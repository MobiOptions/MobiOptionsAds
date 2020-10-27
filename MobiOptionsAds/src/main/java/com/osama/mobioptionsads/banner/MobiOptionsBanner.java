package com.osama.mobioptionsads.banner;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.osama.mobioptionsads.MobiConstants;
import com.osama.mobioptionsads.MobiOptionsAdsInit;
import com.osama.mobioptionsads.base.BaseAd;
import com.osama.mobioptionsads.data.remote.model.Advertisement;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.osama.mobioptionsads.MobiConstants.ADMOB_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.DEFAULT_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.FACEBOOK_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.ROTATION_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.SETTINGS_ADS_ENABLED;
import static com.osama.mobioptionsads.MobiConstants.TAG;
import static com.osama.mobioptionsads.MobiConstants.UNITY_PROVIDER;

public class MobiOptionsBanner extends BaseAd implements MobiBannerListener {

    private AdView facebookBanner = null;
    private com.google.android.gms.ads.AdView admobBanner = null;
    private BannerView unityBanner = null;

    private MobiBannerListener mobiBannerListener;
    private MobiBannerListener thisBannerListener;
    private boolean isDefaultProvider = false;

    private boolean admobFailed = false;
    private boolean facebookFailed = false;
    private boolean unityFailed = false;

    private final ViewGroup bannerContainer;
    private final MobiOptionsBannerSize size;

    private Advertisement advertisement;

    @Override
    protected void setupMobiSettings(String adName) {
        getHandler().post(() -> {
            for (Advertisement ad : getMobiSetting().getAds()) {
                if (ad.getName().equals(adName)) {
                    advertisement = ad;
                    switch (getMobiSetting().getAdsProvider()) {
                        case FACEBOOK_PROVIDER:
                            setBannerId(ad.getFacebookId());
                            break;
                        case ADMOB_PROVIDER:
                            setBannerId(ad.getAdmobId());
                            break;
                        case UNITY_PROVIDER:
                            setBannerId(ad.getUnityId());
                            break;
                        case DEFAULT_PROVIDER:
                            getMobiSetting().setAdsProvider(ADMOB_PROVIDER);
                            setBannerId(ad.getAdmobId());
                            isDefaultProvider = true;
                            break;
                    }
                    break;
                }
            }
        });
    }

    public void setMobiBannerListener(MobiBannerListener mobiBannerListener) {
        getHandler().postDelayed(() -> {
            this.mobiBannerListener = mobiBannerListener;
            this.thisBannerListener = MobiOptionsBanner.this;
            if (getMobiSetting().getAdsProvider().equals(FACEBOOK_PROVIDER) && facebookBanner != null) {
                facebookBanner.loadAd(facebookBanner.buildLoadAdConfig().withAdListener(setUpFacebookListener()).build());
            } else if (getMobiSetting().getAdsProvider().equals(ADMOB_PROVIDER) && admobBanner != null) {
                admobBanner.setAdListener(setUpAdmobListener());
            } else if (getMobiSetting().getAdsProvider().equals(UNITY_PROVIDER) && unityBanner != null) {
                unityBanner.setListener(setUpUnityListener());
            }
        }, 300);
    }


    public MobiOptionsBanner(@NotNull LinearLayout container, MobiOptionsBannerSize size, String adName) {
        this.size = size;
        this.bannerContainer = container;
        this.setupMobiSettings(adName);
    }

    // region public functions

    public void load() {
        getHandler().postDelayed(() -> {
            if (getMobiSetting().getAdsEnabled() != SETTINGS_ADS_ENABLED) {
                Log.d(TAG, "Load ad failed, The ads are disabled from your settings\n" +
                        "Ads Enabled state => " + getMobiSetting().getAdsEnabled());
                return;
            }
            Map<String, Object> data = new HashMap<>();
            switch (getMobiSetting().getAdsProvider()) {
                case FACEBOOK_PROVIDER:
                    if (size.getFacebookBannerSize() == null) {
                        Log.d(TAG, "The size of the banner should not be null");
                        return;
                    }
                    facebookBanner = new AdView(bannerContainer.getContext(), getBannerId(), size.getFacebookBannerSize().getAdSize());
                    facebookBanner.loadAd();
                    bannerContainer.addView(facebookBanner);
                    data.put("fb", true);
                    MobiOptionsAdsInit.setAppStats(data);
                    break;
                case ADMOB_PROVIDER:
                    if (size.getAdmobBannerSize() == null) {
                        Log.d(TAG, "The size of the banner should not be null");
                        return;
                    }
                    admobBanner = new com.google.android.gms.ads.AdView(bannerContainer.getContext());
                    admobBanner.setAdUnitId(getBannerId());
                    admobBanner.setAdSize(size.getAdmobBannerSize().getAdSize());
                    admobBanner.loadAd(new AdRequest.Builder().build());
                    bannerContainer.addView(admobBanner);
                    data.put("ab", true);
                    MobiOptionsAdsInit.setAppStats(data);
                    break;
                case UNITY_PROVIDER:
                    if (size.getUnityBannerSize() == null) {
                        Log.d(TAG, "The size of the banner should not be null");
                        return;
                    }
                    UnityAds.initialize(bannerContainer.getContext(), getUnityGameId(), isTesting());
                    unityBanner = new BannerView((AppCompatActivity) bannerContainer.getContext(), getBannerId(),
                            size.getUnityBannerSize().getUnityBannerSize());
                    unityBanner.load();
                    bannerContainer.addView(unityBanner);
                    data.put("ub", true);
                    MobiOptionsAdsInit.setAppStats(data);
                    break;
            }
        }, 500);
    }


    @Override
    public void destroy() {
        super.destroy();
        if (facebookBanner != null) {
            facebookBanner.destroy();
            facebookBanner = null;
        }
        if (admobBanner != null) {
            admobBanner.destroy();
            admobBanner = null;
        }
        if (unityBanner != null) {
            unityBanner.destroy();
            unityBanner = null;
        }
    }

    // endregion


    private AdListener setUpFacebookListener() {
        return new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                MobiOptionBannerError error = new MobiOptionBannerError(String.valueOf(adError.getErrorCode()), adError.getErrorMessage());
                mobiBannerListener.onFailedToLoad(getMobiSetting().getAdsProvider(), error);
                thisBannerListener.onFailedToLoad(getMobiSetting().getAdsProvider(), error);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                mobiBannerListener.onLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdClicked(Ad ad) {
                mobiBannerListener.onClicked(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // NO-OP
            }
        };
    }


    private com.google.android.gms.ads.AdListener setUpAdmobListener() {
        return new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdClosed() {
                // Not implemented for the moment
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                MobiOptionBannerError error = new MobiOptionBannerError(String.valueOf(loadAdError.getCode()), loadAdError.getMessage());
                mobiBannerListener.onFailedToLoad(getMobiSetting().getAdsProvider(), error);
                thisBannerListener.onFailedToLoad(getMobiSetting().getAdsProvider(), error);
            }

            @Override
            public void onAdLeftApplication() {
                mobiBannerListener.onLeftApplication(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                mobiBannerListener.onLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdClicked() {
                mobiBannerListener.onClicked(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        };
    }


    private BannerView.IListener setUpUnityListener() {
        return new BannerView.IListener() {
            @Override
            public void onBannerLoaded(BannerView bannerView) {
                mobiBannerListener.onLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onBannerClick(BannerView bannerView) {
                mobiBannerListener.onClicked(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onBannerFailedToLoad(BannerView bannerView, BannerErrorInfo bannerErrorInfo) {
                MobiOptionBannerError errors = new MobiOptionBannerError(bannerErrorInfo.errorCode.toString(), bannerErrorInfo.errorMessage);
                mobiBannerListener.onFailedToLoad(getMobiSetting().getAdsProvider(), errors);
                thisBannerListener.onFailedToLoad(getMobiSetting().getAdsProvider(), errors);
            }

            @Override
            public void onBannerLeftApplication(BannerView bannerView) {
                mobiBannerListener.onLeftApplication(getMobiSetting().getAdsProvider());
            }
        };
    }

    // region ------------

    @Override
    public void onLoaded(String adsProvider) {
        // NO-OP
    }

    @Override
    public void onClicked(String adsProvider) {
        // NO-OP
    }

    @Override
    public void onFailedToLoad(String adsProvider, MobiOptionBannerError error) {
        if (isDefaultProvider && adsProvider.equals(ADMOB_PROVIDER) && !admobFailed) {
            admobFailed = true;
            Log.d(MobiConstants.TAG, "admob failed, trying to load facebook ad..");
            getMobiSetting().setAdsProvider(FACEBOOK_PROVIDER);
            reloadAd();
        } else if (isDefaultProvider && adsProvider.equals(UNITY_PROVIDER) && !unityFailed) {
            unityFailed = true;
            Log.d(MobiConstants.TAG, "All ads failed to load");
        } else if (isDefaultProvider && adsProvider.equals(FACEBOOK_PROVIDER) && !facebookFailed) {
            facebookFailed = true;
            Log.d(MobiConstants.TAG, "facebook failed, trying to load unity ad..");
            getMobiSetting().setAdsProvider(UNITY_PROVIDER);
            reloadAd();
        }
    }

    @Override
    public void onLeftApplication(String adsProvider) {
        // NO-OP
    }

    private void reloadAd() {
        getHandler().post(() -> {
            Map<String, Object> data = new HashMap<>();
            switch (getMobiSetting().getAdsProvider()) {
                case FACEBOOK_PROVIDER:
                    if (size.getFacebookBannerSize() == null) {
                        Log.d(TAG, "The size of the banner should not be null");
                        return;
                    }
                    setBannerId(advertisement.getFacebookId());
                    facebookBanner = new AdView(bannerContainer.getContext(), getBannerId(), size.getFacebookBannerSize().getAdSize());
                    facebookBanner.loadAd();
                    bannerContainer.addView(facebookBanner);
                    data.put("fb", true);
                    MobiOptionsAdsInit.setAppStats(data);
                    break;
                case ADMOB_PROVIDER:
                    if (size.getAdmobBannerSize() == null) {
                        Log.d(TAG, "The size of the banner should not be null");
                        return;
                    }
                    setBannerId(advertisement.getAdmobId());
                    admobBanner = new com.google.android.gms.ads.AdView(bannerContainer.getContext());
                    admobBanner.setAdUnitId(getBannerId());
                    admobBanner.setAdSize(size.getAdmobBannerSize().getAdSize());
                    admobBanner.loadAd(new AdRequest.Builder().build());
                    bannerContainer.addView(admobBanner);
                    data.put("ab", true);
                    MobiOptionsAdsInit.setAppStats(data);
                    break;
                case UNITY_PROVIDER:
                    if (size.getUnityBannerSize() == null) {
                        Log.d(TAG, "The size of the banner should not be null");
                        return;
                    }
                    setBannerId(advertisement.getUnityId());
                    UnityAds.initialize(bannerContainer.getContext(), getUnityGameId(), isTesting());
                    unityBanner = new BannerView((AppCompatActivity) bannerContainer.getContext(), getBannerId(),
                            size.getUnityBannerSize().getUnityBannerSize());
                    unityBanner.load();
                    bannerContainer.addView(unityBanner);
                    data.put("ub", true);
                    MobiOptionsAdsInit.setAppStats(data);
                    break;
            }
        });
    }

    // endregion
}
