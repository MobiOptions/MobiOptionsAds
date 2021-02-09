package com.lib.mobioptionsads.rewarded;

public interface MobiRewardAdListener {

    void onRewardedAdOpened(String adsProvider);

    void onRewardedAdClosed(String adProvider);

    void onUserEarnedReward(String adProvider);

    void onRewardedAdError(String adProvider, MobiRewardAdError error);

}
