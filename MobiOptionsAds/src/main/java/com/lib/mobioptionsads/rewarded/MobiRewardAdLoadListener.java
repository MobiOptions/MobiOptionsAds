package com.lib.mobioptionsads.rewarded;

public interface MobiRewardAdLoadListener {

    void onRewardedAdLoaded(String adsProvider);

    void onRewardedAdFailedToLoad(String adsProvider, MobiRewardAdError error);

}
