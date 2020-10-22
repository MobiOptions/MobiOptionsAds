package com.osama.mobioptionsads.rewarded;

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
import com.osama.mobioptionsads.MobiConstants;
import com.osama.mobioptionsads.base.BaseAd;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.osama.mobioptionsads.MobiConstants.ADMOB_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.FACEBOOK_PROVIDER;
import static com.osama.mobioptionsads.MobiConstants.UNITY_PROVIDER;

public class MobiOptionRewardedAd extends BaseAd {

    private Context context;

    private RewardedAd admobRewardedAd;
    private RewardedVideoAd facebookRewardedVideoAd;

    private MobiRewardAdLoadListener loadListener;
    private MobiRewardAdListener rewardAdListener;

    @Override
    protected void setupMobiSettings(@NotNull String adName) {
        getMobiSetting().setAdsEnabled(1);
        getMobiSetting().setAdsProvider(UNITY_PROVIDER);
    }

    public MobiOptionRewardedAd(@NotNull Context context, @NotNull String adName) {
        if (!(context instanceof AppCompatActivity)) {
            throw new Error("MobiOptionsException: The context should be an instance of AppCompatActivity");
        }
        this.context = context;
        this.setupMobiSettings(adName);
    }


    // region public functions

    public void load(@NonNull MobiRewardAdLoadListener rewardAdLoadListener) {
        this.loadListener = rewardAdLoadListener;
        switch (getMobiSetting().getAdsProvider()) {
            case ADMOB_PROVIDER:
                admobRewardedAd = new RewardedAd(context, getRewardedAdId());
                admobRewardedAd.loadAd(new AdRequest.Builder().build(), getAdmobLoadListener());
                break;
            case UNITY_PROVIDER:
                UnityAds.initialize(context, getUnityGameId(), true);                                 // TODO => change the test mode here

                UnityAds.addListener(getUnityListener());
                break;
            case FACEBOOK_PROVIDER:
                facebookRewardedVideoAd = new RewardedVideoAd(context, getRewardedAdId());
                facebookRewardedVideoAd.buildLoadAdConfig().withAdListener(getFacebookListener()).build();
                facebookRewardedVideoAd.loadAd();
                break;
        }
    }


    public void show(@NotNull MobiRewardAdListener rewardAdListener) {
        this.rewardAdListener = rewardAdListener;
        if (getMobiSetting().getAdsProvider().equals(ADMOB_PROVIDER) && admobRewardedAd != null) {
            admobRewardedAd.show((AppCompatActivity) context, getAdmobRewardListener());
        } else if (getMobiSetting().getAdsProvider().equals(UNITY_PROVIDER)) {
            if (UnityAds.isReady(getRewardedAdId())) {               // this is just a double check
                UnityAds.show((AppCompatActivity) context, getRewardedAdId());
            }
        } else if (getMobiSetting().getAdsProvider().equals(FACEBOOK_PROVIDER)) {
            if (facebookRewardedVideoAd.isAdLoaded()) {
                facebookRewardedVideoAd.show();
            }
        }
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


    @Contract(value = " -> new", pure = true)
    private @NotNull RewardedAdLoadCallback getAdmobLoadListener() {
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
            }
        };
    }


    private @NotNull RewardedAdCallback getAdmobRewardListener() {
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


    private @NotNull IUnityAdsListener getUnityListener() {
        return new IUnityAdsListener() {
            @Override
            public void onUnityAdsReady(String s) {
                loadListener.onRewardedAdLoaded(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onUnityAdsStart(String s) {
                rewardAdListener.onRewardedAdOpened(getMobiSetting().getAdsProvider());
            }

            @Override
            public void onUnityAdsFinish(String s, UnityAds.FinishState finishState) {
                if (finishState.equals(UnityAds.FinishState.COMPLETED)) {
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
            }
        };
    }


    @Contract(value = " -> new", pure = true)
    private @NotNull RewardedVideoAdListener getFacebookListener() {
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
                rewardAdListener.onRewardedAdError(getMobiSetting().getAdsProvider(), new MobiRewardAdError(adError.getErrorCode(), adError.getErrorMessage()));
                loadListener.onRewardedAdFailedToLoad(getMobiSetting().getAdsProvider(), new MobiRewardAdError(adError.getErrorCode(), adError.getErrorMessage()));
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

}
