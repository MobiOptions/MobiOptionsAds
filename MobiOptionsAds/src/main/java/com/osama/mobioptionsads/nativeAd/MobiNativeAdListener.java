package com.osama.mobioptionsads.nativeAd;

public interface MobiNativeAdListener {

    void onAdLoaded(String adsProvider);

    void onAdError(String adsProvider, MobiNativeAdError error);

    void onAdClicked(String adsProvider);

}
