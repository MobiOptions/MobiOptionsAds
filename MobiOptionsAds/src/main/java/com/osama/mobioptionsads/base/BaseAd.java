package com.osama.mobioptionsads.base;

import android.os.Handler;
import android.os.Looper;

import com.osama.mobioptionsads.data.model.MobiSetting;

import org.jetbrains.annotations.NotNull;

public abstract class BaseAd {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private MobiSetting mobiSetting = new MobiSetting();

    private boolean isTesting = false;

    // facebook video ad => 330311461246322_706645230279608
    // facebook interstitial ad => 330311461246322_706631666947631
    // facebook banner ad => IMG_16_9_APP_INSTALL#330311461246322_706629543614510
    // unity banner id => testing_banner
    // unity interstitial ad id => interstitial_ad_testing
    // unity rewarded ad id => rewarded_test_ad

    private String bannerId = "testing_banner";
    private String rewardedAdId = "rewarded_test_ad";
    private String interstitialAdId = "interstitial_ad_testing";


    // unity game id required for the unity interstitial, rewarded ad, and unity banner
    private String unityGameId = "3871085";


    public void setMobiSetting(MobiSetting mobiSetting) {
        this.mobiSetting = mobiSetting;
    }

    public String getUnityGameId() {
        return unityGameId;
    }

    public void setUnityGameId(String unityGameId) {
        this.unityGameId = unityGameId;
    }

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getRewardedAdId() {
        return rewardedAdId;
    }

    public void setRewardedAdId(String rewardedAdId) {
        this.rewardedAdId = rewardedAdId;
    }

    public String getInterstitialAdId() {
        return interstitialAdId;
    }

    public void setInterstitialAdId(String interstitialAdId) {
        this.interstitialAdId = interstitialAdId;
    }

    public Handler getHandler() {
        return handler;
    }

    protected MobiSetting getMobiSetting() {
        return mobiSetting;
    }

    protected abstract void setupMobiSettings(@NotNull String adName);

    public void destroy() {
        this.handler.removeCallbacks(null);
    }
}
