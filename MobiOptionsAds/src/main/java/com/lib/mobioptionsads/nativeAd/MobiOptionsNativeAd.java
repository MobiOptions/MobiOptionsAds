package com.lib.mobioptionsads.nativeAd;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdBase;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeAdView;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.lib.mobioptionsads.MobiConstants;
import com.lib.mobioptionsads.MobiOptionsAdsInit;
import com.lib.mobioptionsads.R;
import com.lib.mobioptionsads.base.BaseAd;
import com.lib.mobioptionsads.data.remote.model.Advertisement;
import com.lib.mobioptionsads.nativeAd.size.NativeAdmobSize;

import java.util.HashMap;
import java.util.Map;

import static com.lib.mobioptionsads.MobiConstants.ADMOB_PROVIDER;
import static com.lib.mobioptionsads.MobiConstants.DEFAULT_PROVIDER;
import static com.lib.mobioptionsads.MobiConstants.FACEBOOK_PROVIDER;
import static com.lib.mobioptionsads.MobiConstants.SETTINGS_ADS_ENABLED;
import static com.lib.mobioptionsads.MobiConstants.TAG;

public class MobiOptionsNativeAd extends BaseAd implements MobiNativeAdListener {

    private AdLoader admobNative;
    private UnifiedNativeAd unifiedNativeAd;
    private NativeAd facebookNativeAd;

    private final Context context;
    private MobiNativeAdListener mobiNativeAdListener;
    private MobiNativeAdListener thisMobiNativeAdListener;

    private final MobiNativeAdSize adSize;

    private final ViewGroup adContainer;

    private boolean admobFailed = false;
    private boolean facebookFailed = false;
    private boolean isDefaultProvider = false;

    private Advertisement advertisement;


    @Override
    protected void setupMobiSettings(String adName) {
        getHandler().post(() -> {
            for (Advertisement ad : getMobiSetting().getAds()) {
                if (ad.getName().equals(adName)) {
                    this.advertisement = ad;
                    switch (getMobiSetting().getAdsProvider()) {
                        case FACEBOOK_PROVIDER:
                            setNativeAdId(ad.getFacebookId());
                            break;
                        case ADMOB_PROVIDER:
                            setNativeAdId(ad.getAdmobId());
                            break;
                        case DEFAULT_PROVIDER:
                            getMobiSetting().setAdsProvider(ADMOB_PROVIDER);
                            isDefaultProvider = true;
                            setNativeAdId(ad.getAdmobId());
                            break;
                    }
                    break;
                }
            }
        });
    }

    public MobiOptionsNativeAd(Context context,
                               String adName,
                               MobiNativeAdSize mobiNativeAdSize,
                               ViewGroup adContainer) {
        if (!(context instanceof AppCompatActivity)) {
            throw new Error("MobiOptionsException: The context should be an instance of AppCompatActivity");
        }
        this.adContainer = adContainer;
        this.context = context;
        this.adSize = mobiNativeAdSize;
        this.setupMobiSettings(adName);
    }


    public void load(MobiNativeAdListener mobiNativeAdListener) {
        getHandler().postDelayed(() -> {
            if (getMobiSetting().getAdsEnabled() != SETTINGS_ADS_ENABLED) {
                Log.d(TAG, "Load ad failed, The ads are disabled from your settings\n" +
                        "Ads Enabled state => " + getMobiSetting().getAdsEnabled());
                return;
            }
            this.mobiNativeAdListener = mobiNativeAdListener;
            this.thisMobiNativeAdListener = MobiOptionsNativeAd.this;
            switch (getMobiSetting().getAdsProvider()) {
                case ADMOB_PROVIDER:
                    admobNative = new AdLoader.Builder(context, getNativeAdId())
                            .forUnifiedNativeAd(unifiedNativeAd1 -> this.unifiedNativeAd = unifiedNativeAd1)
                            .withAdListener(getAdmobListener()).build();
                    admobNative.loadAd(new AdRequest.Builder().build());
                    break;
                case FACEBOOK_PROVIDER:
                    facebookNativeAd = new NativeAd(context, getNativeAdId());
                    facebookNativeAd.loadAd(facebookNativeAd.buildLoadAdConfig()
                            .withAdListener(getFacebookListener())
                            .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                            .build());
                    break;
            }
        }, 500);
    }


    public void show() {
        getHandler().post(() -> {
            Map<String, Object> data = new HashMap<>();
            if (getMobiSetting().getAdsProvider().equals(ADMOB_PROVIDER) && admobNative != null) {
                TemplateView templateView = null;
                LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (adSize.getAdmobSize() != null && adSize.getAdmobSize().getAdmobNativeSize() == NativeAdmobSize.GNT_SMALL_TEMPLATE) {
                    View view = layoutInflater.inflate(R.layout.small_template_view, adContainer);
                    templateView = view.findViewById(R.id.my_template);
                } else if (adSize.getAdmobSize() != null &&
                        adSize.getAdmobSize().getAdmobNativeSize() == NativeAdmobSize.GNT_MEDIUM_TEMPLATE) {
                    View view = layoutInflater.inflate(R.layout.medium_template_view, adContainer);
                    templateView = view.findViewById(R.id.my_template);
                }
                if (templateView != null && unifiedNativeAd != null)
                    templateView.setNativeAd(unifiedNativeAd);
                data.put("an", true);
                MobiOptionsAdsInit.setAppStats(data);
            } else if (getMobiSetting().getAdsProvider().equals(FACEBOOK_PROVIDER)
                    && facebookNativeAd != null) {
                if (facebookNativeAd.isAdLoaded()) {
                    View adView = NativeAdView.render(context, facebookNativeAd);
                    adContainer.addView(adView);
                    data.put("fn", true);
                    MobiOptionsAdsInit.setAppStats(data);
                }
            }
        });
    }

    public void destroy() {
        super.destroy();
        if (((AppCompatActivity) context).isDestroyed()) {
            if (unifiedNativeAd != null)
                unifiedNativeAd.destroy();
        }
    }


    private NativeAdListener getFacebookListener() {
        return new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                if (mobiNativeAdListener != null)
                    mobiNativeAdListener.onAdLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if (mobiNativeAdListener != null)
                    mobiNativeAdListener.onAdError(getMobiSetting().getAdsProvider(), new MobiNativeAdError(adError.getErrorCode(),
                            adError.getErrorMessage()));
                if (thisMobiNativeAdListener != null)
                    thisMobiNativeAdListener.onAdError(getMobiSetting().getAdsProvider(), new MobiNativeAdError(adError.getErrorCode(),
                            adError.getErrorMessage()));
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (mobiNativeAdListener != null)
                    mobiNativeAdListener.onAdLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (mobiNativeAdListener != null)
                    mobiNativeAdListener.onAdClicked(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        };
    }


    private AdListener getAdmobListener() {
        return new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                if (mobiNativeAdListener != null)
                    mobiNativeAdListener.onAdError(getMobiSetting().getAdsProvider(), new MobiNativeAdError(loadAdError.getCode(), loadAdError.getMessage()));
                if (thisMobiNativeAdListener != null)
                    thisMobiNativeAdListener.onAdError(getMobiSetting().getAdsProvider(), new MobiNativeAdError(loadAdError.getCode(), loadAdError.getMessage()));
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                if (mobiNativeAdListener != null)
                    mobiNativeAdListener.onAdLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                if (mobiNativeAdListener != null)
                    mobiNativeAdListener.onAdClicked(getMobiSetting().getAdsProvider());
            }
        };
    }

    // region -------

    @Override
    public void onAdLoaded(String adsProvider) {
        // NO-OP
    }

    @Override
    public void onAdError(String adsProvider, MobiNativeAdError error) {
        if (isDefaultProvider && adsProvider.equals(ADMOB_PROVIDER) && !admobFailed) {
            admobFailed = true;
            Log.d(MobiConstants.TAG, "admob failed, trying to load facebook ad..");
            getMobiSetting().setAdsProvider(FACEBOOK_PROVIDER);
            reloadAd();
        } else if (isDefaultProvider && adsProvider.equals(FACEBOOK_PROVIDER) && !facebookFailed) {
            facebookFailed = true;
            Log.d(MobiConstants.TAG, "facebook failed, trying to load unity ad..");
        }
    }

    @Override
    public void onAdClicked(String adsProvider) {
        // NO-OP
    }

    private void reloadAd() {
        switch (getMobiSetting().getAdsProvider()) {
            case ADMOB_PROVIDER:
                setNativeAdId(advertisement.getAdmobId());
                admobNative = new AdLoader.Builder(context, getNativeAdId())
                        .forUnifiedNativeAd(unifiedNativeAd1 -> this.unifiedNativeAd = unifiedNativeAd1)
                        .withAdListener(getAdmobListener()).build();
                admobNative.loadAd(new AdRequest.Builder().build());
                break;
            case FACEBOOK_PROVIDER:
                setNativeAdId(advertisement.getFacebookId());
                facebookNativeAd = new NativeAd(context, getNativeAdId());
                facebookNativeAd.loadAd(facebookNativeAd.buildLoadAdConfig()
                        .withAdListener(getFacebookListener())
                        .withMediaCacheFlag(NativeAdBase.MediaCacheFlag.ALL)
                        .build());
                break;
        }
    }

    // endregion
}
