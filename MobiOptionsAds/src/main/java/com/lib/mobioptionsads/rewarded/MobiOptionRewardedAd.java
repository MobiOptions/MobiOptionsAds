package com.lib.mobioptionsads.rewarded;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.ads.Ad;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.lib.mobioptionsads.MobiConstants;
import com.lib.mobioptionsads.MobiOptionsAdsInit;
import com.lib.mobioptionsads.base.BaseAd;
import com.lib.mobioptionsads.data.remote.model.Advertisement;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;


import java.util.HashMap;
import java.util.Map;

import static com.lib.mobioptionsads.MobiConstants.ADMOB_PROVIDER;
import static com.lib.mobioptionsads.MobiConstants.DEFAULT_PROVIDER;
import static com.lib.mobioptionsads.MobiConstants.FACEBOOK_PROVIDER;
import static com.lib.mobioptionsads.MobiConstants.SETTINGS_ADS_ENABLED;
import static com.lib.mobioptionsads.MobiConstants.TAG;
import static com.lib.mobioptionsads.MobiConstants.UNITY_PROVIDER;

public class MobiOptionRewardedAd extends BaseAd implements MobiRewardAdLoadListener {

    private final Context context;

    private RewardedAd admobRewardedAd;
    private RewardedVideoAd facebookRewardedVideoAd;

    private MobiRewardAdLoadListener loadListener;
    private MobiRewardAdListener rewardAdListener;
    private MobiRewardAdLoadListener thisLoadListener;

    private boolean admobFailed = false;
    private boolean facebookFailed = false;
    private boolean unityFailed = false;
    private boolean isDefaultProvider = false;

    private Advertisement advertisement;

    @Override
    protected void setupMobiSettings(String adName) {
        for (Advertisement ad : getMobiSetting().getAds()) {
            if (ad.getName().equals(adName)) {
                this.advertisement = ad;
                switch (getMobiSetting().getAdsProvider()) {
                    case FACEBOOK_PROVIDER:
                        setRewardedAdId(ad.getFacebookId());
                        break;
                    case ADMOB_PROVIDER:
                        setRewardedAdId(ad.getAdmobId());
                        break;
                    case UNITY_PROVIDER:
                        setRewardedAdId(ad.getUnityId());
                        break;
                    case DEFAULT_PROVIDER:
                        getMobiSetting().setAdsProvider(ADMOB_PROVIDER);
                        isDefaultProvider = true;
                        setRewardedAdId(ad.getAdmobId());
                        break;
                }
                break;
            }
        }
    }

    public MobiOptionRewardedAd(Context context, String adName) {
        if (!(context instanceof AppCompatActivity)) {
            throw new Error("MobiOptionsException: The context should be an instance of AppCompatActivity");
        }
        this.context = context;
        this.setupMobiSettings(adName);
    }


    // region public functions

    public void load(MobiRewardAdLoadListener rewardAdLoadListener) {
        if (!isAppIsAfterDelay())
            return;
        getHandler().postDelayed(() -> {
            if (getMobiSetting().getAdsEnabled() != SETTINGS_ADS_ENABLED) {
                Log.d(TAG, "Load ad failed, The ads are disabled from your settings\n" +
                        "Ads Enabled state => " + getMobiSetting().getAdsEnabled());
                return;
            }
            this.loadListener = rewardAdLoadListener;
            this.thisLoadListener = MobiOptionRewardedAd.this;
            switch (getMobiSetting().getAdsProvider()) {
                case ADMOB_PROVIDER:
                    admobRewardedAd = new RewardedAd(context, getRewardedAdId());
                    admobRewardedAd.loadAd(new AdRequest.Builder().build(), getAdmobLoadListener());
                    break;
                case UNITY_PROVIDER:
                    UnityAds.initialize(context, getUnityGameId(), isTesting());
                    UnityAds.addListener(getUnityListener());
                    break;
                case FACEBOOK_PROVIDER:
                    facebookRewardedVideoAd = new RewardedVideoAd(context, getRewardedAdId());
                    facebookRewardedVideoAd.buildLoadAdConfig().withAdListener(getFacebookListener()).build();
                    facebookRewardedVideoAd.loadAd();
                    break;
            }
        }, 500);
    }


    public void show(MobiRewardAdListener rewardAdListener) {
        getHandler().post(() -> {
            this.rewardAdListener = rewardAdListener;
            Map<String, Object> data = new HashMap<>();
            if (getMobiSetting().getAdsProvider().equals(ADMOB_PROVIDER) && admobRewardedAd != null) {
                if (admobRewardedAd.isLoaded()) {
                    admobRewardedAd.show((AppCompatActivity) context, getAdmobRewardListener());
                    data.put("ar", true);
                    MobiOptionsAdsInit.setAppStats(data);
                }
            } else if (getMobiSetting().getAdsProvider().equals(UNITY_PROVIDER)) {
                if (UnityAds.isReady(getRewardedAdId())) {
                    UnityAds.show((AppCompatActivity) context, getRewardedAdId());
                    data.put("ur", true);
                    MobiOptionsAdsInit.setAppStats(data);
                }
            } else if (getMobiSetting().getAdsProvider().equals(FACEBOOK_PROVIDER)) {
                if (facebookRewardedVideoAd.isAdLoaded()) {
                    facebookRewardedVideoAd.show();
                    data.put("fr", true);
                    MobiOptionsAdsInit.setAppStats(data);
                }
            }
        });
    }

    public boolean isLoaded() {
        if (getMobiSetting().getAdsProvider().equals(ADMOB_PROVIDER) && admobRewardedAd != null) {
            return admobRewardedAd.isLoaded();
        } else if (getMobiSetting().getAdsProvider().equals(UNITY_PROVIDER)) {
            return UnityAds.isReady(getRewardedAdId());
        } else if (getMobiSetting().getAdsProvider().equals(FACEBOOK_PROVIDER) && facebookRewardedVideoAd != null) {
            return facebookRewardedVideoAd.isAdLoaded();
        }
        Log.d(MobiConstants.TAG, "isLoaded: No ads provider is initialised, check your settings");
        return false;
    }

    // endregion


    private RewardedAdLoadCallback getAdmobLoadListener() {
        return new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
                super.onRewardedAdLoaded();
                loadListener.onRewardedAdLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                super.onRewardedAdFailedToLoad(loadAdError);
                MobiRewardAdError error = new MobiRewardAdError(loadAdError.getCode(), loadAdError.getMessage());
                loadListener.onRewardedAdFailedToLoad(getMobiSetting().getAdsProvider(), error);
                thisLoadListener.onRewardedAdFailedToLoad(getMobiSetting().getAdsProvider(), error);
            }
        };
    }


    private RewardedAdCallback getAdmobRewardListener() {
        return new RewardedAdCallback() {
            @Override
            public void onRewardedAdOpened() {
                super.onRewardedAdOpened();
                rewardAdListener.onRewardedAdOpened(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onRewardedAdClosed() {
                super.onRewardedAdClosed();
                rewardAdListener.onRewardedAdClosed(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onRewardedAdFailedToShow(AdError adError) {
                super.onRewardedAdFailedToShow(adError);
                MobiRewardAdError error = new MobiRewardAdError(adError.getCode(), adError.getMessage());
                rewardAdListener.onRewardedAdError(getMobiSetting().getAdsProvider(), error);
            }

            @Override
            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                rewardAdListener.onUserEarnedReward(getMobiSetting().getAdsProvider());
            }
        };
    }


    private IUnityAdsListener getUnityListener() {
        return new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String s) {
                loadListener.onRewardedAdLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onUnityAdsStart(String s) {
                if (rewardAdListener == null)
                    return;
                rewardAdListener.onRewardedAdOpened(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
                if (finishState.equals(UnityAds.FinishState.COMPLETED)) {
                    if (rewardAdListener != null)
                        rewardAdListener.onUserEarnedReward(getMobiSetting().getAdsProvider());
                } else if (finishState.equals(UnityAds.FinishState.SKIPPED)) {
                    rewardAdListener.onRewardedAdClosed(getMobiSetting().getAdsProvider());
                } else if (finishState.equals(UnityAds.FinishState.ERROR)) {
                    MobiRewardAdError error = new MobiRewardAdError(-1, "Unknown error happened while finishing Unity rewarded ad");
                    rewardAdListener.onRewardedAdError(getMobiSetting().getAdsProvider(), error);
                }
            }

            @Override
            public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {
                rewardAdListener.onRewardedAdError(getMobiSetting().getAdsProvider(), new MobiRewardAdError(-1, unityAdsError.name()));
                thisLoadListener.onRewardedAdFailedToLoad(getMobiSetting().getAdsProvider(), new MobiRewardAdError(-1, unityAdsError.name()));
            }
        };
    }


    private RewardedVideoAdListener getFacebookListener() {
        return new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoCompleted() {
                rewardAdListener.onUserEarnedReward(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // not implemented for the moment
            }

            @Override
            public void onRewardedVideoClosed() {
                rewardAdListener.onRewardedAdClosed(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onError(Ad ad, com.facebook.ads.AdError adError) {
                loadListener.onRewardedAdFailedToLoad(getMobiSetting().getAdsProvider(), new MobiRewardAdError(adError.getErrorCode(), adError.getErrorMessage()));

                if (rewardAdListener != null)
                    rewardAdListener.onRewardedAdError(getMobiSetting().getAdsProvider(), new MobiRewardAdError(adError.getErrorCode(), adError.getErrorMessage()));

                if (thisLoadListener != null)
                    thisLoadListener.onRewardedAdFailedToLoad(getMobiSetting().getAdsProvider(), new MobiRewardAdError(adError.getErrorCode(), adError.getErrorMessage()));
            }

            @Override
            public void onAdLoaded(Ad ad) {
                loadListener.onRewardedAdLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onAdClicked(Ad ad) {
                // not implemented for the moment
            }
        };
    }


    // region -----

    @Override
    public void onRewardedAdLoaded(String adsProvider) {
        Log.d(MobiConstants.TAG, "onRewardedAdLoaded: Ad loaded successfully");
    }

    @Override
    public void onRewardedAdFailedToLoad(String adsProvider, MobiRewardAdError error) {
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

    private void reloadAd() {
        switch (getMobiSetting().getAdsProvider()) {
            case ADMOB_PROVIDER:
                setRewardedAdId(advertisement.getAdmobId());
                admobRewardedAd = new RewardedAd(context, getRewardedAdId());
                admobRewardedAd.loadAd(new AdRequest.Builder().build(), getAdmobLoadListener());
                break;
            case UNITY_PROVIDER:
                setRewardedAdId(advertisement.getUnityId());
                UnityAds.initialize(context, getUnityGameId(), isTesting());
                UnityAds.addListener(getUnityListener());
                break;
            case FACEBOOK_PROVIDER:
                setRewardedAdId(advertisement.getFacebookId());
                facebookRewardedVideoAd = new RewardedVideoAd(context, getRewardedAdId());
                facebookRewardedVideoAd.buildLoadAdConfig().withAdListener(getFacebookListener()).build();
                facebookRewardedVideoAd.loadAd();
                break;
        }
    }

    // endregion

}
