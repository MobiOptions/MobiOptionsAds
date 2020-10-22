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
import com.osama.mobioptionsads.base.BaseAd;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.osama.mobioptionsads.MobiConstants.ADMOB_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.FACEBOOK_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.ROTATION_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.SETTINGS_ADS_ENABLED;
import static com.osama.mobioptionsads.MobiConstants.TAG;
import static com.osama.mobioptionsads.MobiConstants.UNITY_PROVIDER;

public class MobiOptionsBanner extends BaseAd {

    private AdView facebookBanner = null;
    private com.google.android.gms.ads.AdView admobBanner = null;
    private BannerView unityBanner = null;

    private MobiBannerListener mobiBannerListener;


    private final ViewGroup bannerContainer;
    private final MobiOptionsBannerSize size;

    @Override
    protected void setupMobiSettings(@NotNull String adName) {
        getMobiSetting().setAdsProvider(UNITY_PROVIDER);
        getMobiSetting().setAdsEnabled(SETTINGS_ADS_ENABLED);
    }

    public void setMobiBannerListener(MobiBannerListener mobiBannerListener) {
        getHandler().postDelayed(() -> {
            if (admobBanner == null && facebookBanner == null && unityBanner == null) {
                throw new Error("MobiOptionsAds Exception: You have to call the load() method before setting up a listener");
            }
            this.mobiBannerListener = mobiBannerListener;
            if (getMobiSetting().getAdsProvider().equals(FACEBOOK_PROVIDER) && facebookBanner != null) {
                facebookBanner.loadAd(facebookBanner.buildLoadAdConfig().withAdListener(setUpFacebookListener()).build());
            } else if (getMobiSetting().getAdsProvider().equals(ADMOB_PROVIDER) && admobBanner != null) {
                admobBanner.setAdListener(setUpAdmobListener());
            } else if (getMobiSetting().getAdsProvider().equals(UNITY_PROVIDER) && unityBanner != null) {
                unityBanner.setListener(setUpUnityListener());
            } else if (getMobiSetting().getAdsProvider().equals(ROTATION_PROVIDER)) {

            /*
                    TODO => handle here the rotation things, check which one should provide the ads and don't forgot
                        to set the provider name in the getMobiSetting() object because it's used in the callbacks.
            */

            }
        }, 300);
    }


    public MobiOptionsBanner(@NotNull LinearLayout container, @NotNull MobiOptionsBannerSize size, String adName) {
        this.size = size;
        this.bannerContainer = container;
        this.setupMobiSettings(adName);
    }

    // region public functions

    public void load() {
        getHandler().post(() -> {
            if (getMobiSetting().getAdsEnabled() != SETTINGS_ADS_ENABLED) {
                Log.d(TAG, "Load ad failed, The ads are disabled from your settings, try to enable them \n" +
                        "Ads Enabled state => " + getMobiSetting().getAdsEnabled());
                return;
            }
            switch (getMobiSetting().getAdsProvider()) {
                case FACEBOOK_PROVIDER:
                    if (size.getFacebookBannerSize() == null) {
                        Log.d(TAG, "The size of the banner should not be null");
                        return;
                    }
                    facebookBanner = new AdView(bannerContainer.getContext(), getBannerId(), size.getFacebookBannerSize().getAdSize());
                    facebookBanner.loadAd();
                    bannerContainer.addView(facebookBanner);
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
                    break;
                case UNITY_PROVIDER:
                    if (size.getUnityBannerSize() == null) {
                        Log.d(TAG, "The size of the banner should not be null");
                        return;
                    }
                    UnityAds.initialize(bannerContainer.getContext(), getUnityGameId(), true);
                    unityBanner = new BannerView((AppCompatActivity) bannerContainer.getContext(), getBannerId(),
                            size.getUnityBannerSize().getUnityBannerSize());
                    unityBanner.load();
                    bannerContainer.addView(unityBanner);
                    break;
            }
        });
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

    @Contract(pure = true)
    private @NotNull AdListener setUpFacebookListener() {
        return new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                MobiOptionBannerError error = new MobiOptionBannerError(String.valueOf(adError.getErrorCode()), adError.getErrorMessage());
                mobiBannerListener.onFailedToLoad(getMobiSetting().getAdsProvider(), error);
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

    @Contract(value = " -> new", pure = true)
    private com.google.android.gms.ads.@NotNull AdListener setUpAdmobListener() {
        return new com.google.android.gms.ads.AdListener() {
            @Override
            public void onAdClosed() {
                // Not implemented for the moment
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                MobiOptionBannerError error = new MobiOptionBannerError(String.valueOf(loadAdError.getCode()), loadAdError.getMessage());
                mobiBannerListener.onFailedToLoad(getMobiSetting().getAdsProvider(), error);
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

    @Contract(value = " -> new", pure = true)
    private BannerView.@NotNull IListener setUpUnityListener() {
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
            }

            @Override
            public void onBannerLeftApplication(BannerView bannerView) {
                mobiBannerListener.onLeftApplication(getMobiSetting().getAdsProvider());
            }
        };
    }
}
