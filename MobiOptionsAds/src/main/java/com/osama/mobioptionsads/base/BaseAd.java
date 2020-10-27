package com.osama.mobioptionsads.base;

import android.os.Handler;
import android.os.Looper;

import com.osama.mobioptionsads.MobiOptionsAdsInit;
import com.osama.mobioptionsads.data.remote.model.MobiSetting;

import org.jetbrains.annotations.NotNull;

public abstract class BaseAd {

    private final Handler handler = new Handler(Looper.getMainLooper());
    private MobiSetting mobiSetting = MobiOptionsAdsInit.mobiSetting;

    private boolean isTesting = true;

    public boolean isTesting() {
        return isTesting;
    }

    public void setTesting(boolean testing) {
        isTesting = testing;
    }

    // facebook video ad => 330311461246322_706645230279608
    // facebook interstitial ad => 330311461246322_706631666947631
    // facebook banner ad => IMG_16_9_APP_INSTALL#330311461246322_706629543614510
    // unity banner id => testing_banner
    // unity interstitial ad id => interstitial_ad_testing
    // unity rewarded ad id => rewarded_test_ad

    private String bannerId = "IMG_16_9_APP_INSTALL#330311461246322_706629543614510";
    private String rewardedAdId = "330311461246322_706645230279608";
    private String interstitialAdId = "330311461246322_706631666947631";


    // the admob test id => ca-app-pub-3940256099942544/2247696110
    private String nativeAdId = "330311461246322_709355306675267";


    // unity game id required for the unity interstitial, rewarded ad, and unity banner
    // unity game id => 3871085
    private String unityGameId = MobiOptionsAdsInit.mobiSetting.getUnityGameId();


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

    public String getNativeAdId() {
        return nativeAdId;
    }

    public void setNativeAdId(String nativeAdId) {
        this.nativeAdId = nativeAdId;
    }

    public Handler getHandler() {
        return handler;
    }

    protected MobiSetting getMobiSetting() {
        return mobiSetting;
    }

    protected abstract void setupMobiSettings(String adName);

    protected void destroy() {
        this.handler.removeCallbacks(null);
    }
}
