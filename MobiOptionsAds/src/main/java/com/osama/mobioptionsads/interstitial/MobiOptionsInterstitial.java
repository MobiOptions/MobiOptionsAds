package com.osama.mobioptionsads.interstitial;

import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.osama.mobioptionsads.MobiConstants;
import com.osama.mobioptionsads.MobiOptionsAdsInit;
import com.osama.mobioptionsads.base.BaseAd;
import com.osama.mobioptionsads.data.remote.model.Advertisement;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import java.util.HashMap;
import java.util.Map;

import static com.osama.mobioptionsads.MobiConstants.*;
import static com.osama.mobioptionsads.MobiConstants.ADMOB_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.FACEBOOK_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.UNITY_PROVIDER;

public class MobiOptionsInterstitial extends BaseAd implements MobiInterstitialListener {

    private MobiInterstitialListener mobiInterstitialListener;
    private MobiInterstitialListener thisMobiInterstitialListener;

    private InterstitialAd facebookInterstitial;
    private com.google.android.gms.ads.InterstitialAd admobInterstitial;

    private final Context context;

    private boolean isDefaultProvider = false;

    private boolean admobFailed = false;
    private boolean facebookFailed = false;
    private boolean unityFailed = false;

    private Advertisement advertisement;

    @Override
    protected void setupMobiSettings(String adName) {
        getHandler().post(() -> {
            for (Advertisement ad : getMobiSetting().getAds()) {
                if (ad.getName().equals(adName)) {
                    this.advertisement = ad;
                    switch (getMobiSetting().getAdsProvider()) {
                        case FACEBOOK_PROVIDER:
                            setInterstitialAdId(ad.getFacebookId());
                            break;
                        case ADMOB_PROVIDER:
                            setInterstitialAdId(ad.getAdmobId());
                            break;
                        case UNITY_PROVIDER:
                            setInterstitialAdId(ad.getUnityId());
                            break;
                        case DEFAULT_PROVIDER:
                            getMobiSetting().setAdsProvider(ADMOB_PROVIDER);
                            setInterstitialAdId(ad.getAdmobId());
                            isDefaultProvider = true;
                            break;
                    }
                    break;
                }
            }
        });
    }

    public void setMobiInterstitialListener(MobiInterstitialListener mobiInterstitialListener) {
        getHandler().postDelayed(() -> {
            this.mobiInterstitialListener = mobiInterstitialListener;
            this.thisMobiInterstitialListener = MobiOptionsInterstitial.this;
            if (getMobiSetting().getAdsProvider().equals(UNITY_PROVIDER)) {
                UnityAds.addListener(this.getUnityListener());
            } else if (getMobiSetting().getAdsProvider().equals(ADMOB_PROVIDER) && admobInterstitial != null) {
                admobInterstitial.setAdListener(this.getAdmobListener());
            } else if (getMobiSetting().getAdsProvider().equals(FACEBOOK_PROVIDER) && facebookInterstitial != null) {
                facebookInterstitial.loadAd(facebookInterstitial.buildLoadAdConfig().withAdListener(this.getFacebookListener()).build());
            } else if (getMobiSetting().getAdsProvider().equals(ROTATION_PROVIDER)) {
                // TODO: Same as banner
            }
        }, 200);
    }

    public MobiOptionsInterstitial(Context context, String adName) {
        if (!(context instanceof AppCompatActivity)) {
            throw new Error("MobiOptions Error: The context should be an instance of an Activity");
        }
        this.context = context;
        this.setupMobiSettings(adName);
    }


    // region public functions

    /**
     * Call this method before setting up a listener for the interstitial
     */
    public void loadAd() {
        getHandler().postDelayed(() -> {
            if (getMobiSetting().getAdsEnabled() != SETTINGS_ADS_ENABLED) {
                Log.d(TAG, "Load ad failed, The ads are disabled from your settings\n" +
                        "Ads Enabled state => " + getMobiSetting().getAdsEnabled());
                return;
            }
            switch (getMobiSetting().getAdsProvider()) {
                case FACEBOOK_PROVIDER:
                    facebookInterstitial = new InterstitialAd(context, getInterstitialAdId());
                    facebookInterstitial.loadAd();
                    break;
                case ADMOB_PROVIDER:
                    admobInterstitial = new com.google.android.gms.ads.InterstitialAd(context);
                    admobInterstitial.setAdUnitId(getInterstitialAdId());
                    admobInterstitial.loadAd(new AdRequest.Builder().build());
                    break;
                case UNITY_PROVIDER:
                    UnityAds.initialize(context, getUnityGameId(), isTesting());
                    break;
            }
        }, 500);
    }


    /**
     * The call to this method is done when the interstitial is loaded,
     * check that using the MobiInterstitialListener.
     */
    public void show() {
        getHandler().post(() -> {
            Map<String, Object> data = new HashMap<>();
            if (getMobiSetting().getAdsProvider().equals(FACEBOOK_PROVIDER) && facebookInterstitial != null) {
                if (facebookInterstitial.isAdLoaded()) {
                    facebookInterstitial.show();
                    data.put("fi", true);
                    MobiOptionsAdsInit.setAppStats(data);
                } else {
                    Log.d(TAG, "Failed to show Facebook interstitial, not yet loaded");
                }
            } else if (getMobiSetting().getAdsProvider().equals(UNITY_PROVIDER)) {
                if (UnityAds.isReady(getInterstitialAdId())) {
                    UnityAds.show((AppCompatActivity) context, getInterstitialAdId());
                    data.put("ui", true);
                    MobiOptionsAdsInit.setAppStats(data);
                } else {
                    Log.d(TAG, "Failed to show UnityAds interstitial, not yet loaded");
                }
            } else if (getMobiSetting().getAdsProvider().equals(ADMOB_PROVIDER) && admobInterstitial != null) {
                if (admobInterstitial.isLoaded()) {
                    admobInterstitial.show();
                    data.put("ai", true);
                    MobiOptionsAdsInit.setAppStats(data);
                } else {
                    Log.d(TAG, "Failed to show Admob interstitial, not yet loaded");
                }
            }
        });
    }

    public boolean isLoaded() {
        switch (getMobiSetting().getAdsProvider()) {
            case FACEBOOK_PROVIDER:
                return facebookInterstitial != null && facebookInterstitial.isAdLoaded();
            case ADMOB_PROVIDER:
                return admobInterstitial != null && admobInterstitial.isLoaded();
            case UNITY_PROVIDER:
                return UnityAds.isReady(getInterstitialAdId());
        }
        Log.d(TAG, "isLoaded: None of the sdk was initialised please check your configuration");
        return false;
    }

    // endregion


    private IUnityAdsListener getUnityListener() {
        return new IUnityAdsListener() {

            @Override
            public void onUnityAdsReady(String s) {
                mobiInterstitialListener.onLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onUnityAdsStart(String s) {
                // not implemented for the moment
            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
                mobiInterstitialListener.onDisplayed(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
                mobiInterstitialListener.onError(getMobiSetting().getAdsProvider(),
                        new MobiInterstitialError(unityAdsError.name(), "Message: ".concat(unityAdsError.name())));
                thisMobiInterstitialListener.onError(getMobiSetting().getAdsProvider(),
                        new MobiInterstitialError(unityAdsError.name(), "Message: ".concat(unityAdsError.name())));
            }
        };
    }


    private AdListener getAdmobListener() {
        return new AdListener() {
            @Override
            public void onAdClosed() {
                mobiInterstitialListener.onClosed(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                MobiInterstitialError error = new MobiInterstitialError(String.valueOf(loadAdError.getCode()), loadAdError.getMessage());
                mobiInterstitialListener.onError(getMobiSetting().getAdsProvider(), error);
                thisMobiInterstitialListener.onError(getMobiSetting().getAdsProvider(), error);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                mobiInterstitialListener.onLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdClicked() {
                mobiInterstitialListener.onClicked(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }
        };
    }


    private InterstitialAdListener getFacebookListener() {
        return new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                mobiInterstitialListener.onDisplayed(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                mobiInterstitialListener.onClosed(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                MobiInterstitialError error = new MobiInterstitialError(String.valueOf(adError.getErrorCode()), adError.getErrorMessage());
                mobiInterstitialListener.onError(getMobiSetting().getAdsProvider(), error);
                thisMobiInterstitialListener.onError(getMobiSetting().getAdsProvider(), error);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                mobiInterstitialListener.onLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdClicked(Ad ad) {
                mobiInterstitialListener.onClosed(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Not implemented for the moment
            }
        };
    }

    // region -----

    @Override
    public void onDisplayed(String adsProvider) {
        // NO-OP
    }

    @Override
    public void onClosed(String adsProvider) {
        // NO-OP
    }

    @Override
    public void onError(String adsProvider, MobiInterstitialError error) {
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
    public void onLoaded(String adsProvider) {
        // NO-OP
    }

    @Override
    public void onClicked(String adsProvider) {
        // NO-OP
    }

    private void reloadAd() {
        getHandler().post(() -> {
            switch (getMobiSetting().getAdsProvider()) {
                case FACEBOOK_PROVIDER:
                    setInterstitialAdId(advertisement.getFacebookId());
                    facebookInterstitial = new InterstitialAd(context, getInterstitialAdId());
                    facebookInterstitial.loadAd();
                    break;
                case ADMOB_PROVIDER:
                    setInterstitialAdId(advertisement.getAdmobId());
                    admobInterstitial = new com.google.android.gms.ads.InterstitialAd(context);
                    admobInterstitial.setAdUnitId(getInterstitialAdId());
                    admobInterstitial.loadAd(new AdRequest.Builder().build());
                    break;
                case UNITY_PROVIDER:
                    setInterstitialAdId(advertisement.getUnityId());
                    UnityAds.initialize(context, getUnityGameId(), isTesting());
                    break;
            }
        });
    }

    // endregion
}
