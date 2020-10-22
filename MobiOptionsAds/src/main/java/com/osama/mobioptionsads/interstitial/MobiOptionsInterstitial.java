package com.osama.mobioptionsads.interstitial;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.osama.mobioptionsads.base.BaseAd;
import com.osama.mobioptionsads.data.model.MobiSetting;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.osama.mobioptionsads.MobiConstants.*;
import static com.osama.mobioptionsads.MobiConstants.ADMOB_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.FACEBOOK_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.UNITY_PROVIDER;

public class MobiOptionsInterstitial extends BaseAd {

    private MobiInterstitialListener mobiInterstitialListener;

    private InterstitialAd facebookInterstitial;
    private com.google.android.gms.ads.InterstitialAd admobInterstitial;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Context context;

    @Override
    protected void setupMobiSettings(@NotNull String adName) {
        getMobiSetting().setAdsProvider(UNITY_PROVIDER);
        getMobiSetting().setAdsEnabled(1);
    }

    public void setMobiInterstitialListener(MobiInterstitialListener mobiInterstitialListener) {
        handler.postDelayed(() -> {
            if (facebookInterstitial == null && admobInterstitial == null && !getMobiSetting().getAdsProvider().equals(UNITY_PROVIDER)) {
                throw new Error("MobiOptions Exception: You should call the load method before setting up the listener");
            }
            this.mobiInterstitialListener = mobiInterstitialListener;
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

    public MobiOptionsInterstitial(@NotNull Context context, @NotNull String adName) {
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
        handler.post(() -> {
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
                    UnityAds.initialize(context, getUnityGameId(), true);                                 // TODO => change the test mode here
                    break;
            }
        });
    }


    /**
     * The call to this method is done when the interstitial is loaded,
     * check that using the MobiInterstitialListener.
     */
    public void show() {
        handler.post(() -> {
            if (getMobiSetting().getAdsProvider().equals(FACEBOOK_PROVIDER) && facebookInterstitial != null) {
                if (facebookInterstitial.isAdLoaded()) {
                    facebookInterstitial.show();
                } else {
                    Log.d(TAG, "Failed to show Facebook interstitial, not yet loaded");
                }
            } else if (getMobiSetting().getAdsProvider().equals(UNITY_PROVIDER)) {
                if (UnityAds.isReady(getInterstitialAdId())) {
                    UnityAds.show((AppCompatActivity) context, getInterstitialAdId());
                } else {
                    Log.d(TAG, "Failed to show UnityAds interstitial, not yet loaded");
                }
            } else if (getMobiSetting().getAdsProvider().equals(ADMOB_PROVIDER) && admobInterstitial != null) {
                if (admobInterstitial.isLoaded()) {
                    admobInterstitial.show();
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


    @Contract(value = " -> new", pure = true)
    private @NotNull IUnityAdsListener getUnityListener() {
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
            }
        };
    }


    @Contract(value = " -> new", pure = true)
    private @NotNull AdListener getAdmobListener() {
        return new AdListener() {
            @Override
            public void onAdClosed() {
                mobiInterstitialListener.onClosed(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                MobiInterstitialError error = new MobiInterstitialError(String.valueOf(loadAdError.getCode()), loadAdError.getMessage());
                mobiInterstitialListener.onError(getMobiSetting().getAdsProvider(), error);
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


    @Contract(value = " -> new", pure = true)
    private @NotNull InterstitialAdListener getFacebookListener() {
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
}
