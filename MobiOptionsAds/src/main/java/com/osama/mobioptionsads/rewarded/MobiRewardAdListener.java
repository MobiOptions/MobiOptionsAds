package com.osama.mobioptionsads.rewarded;

import org.jetbrains.annotations.NotNull;

public interface MobiRewardAdListener {

    void onRewardedAdOpened(String adsProvider);

    void onRewardedAdClosed(String adProvider);

    void onUserEarnedReward(String adProvider);

    void onRewardedAdError(String adProvider,@NotNull MobiRewardAdError error);

}
