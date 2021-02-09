package com.lib.mobioptionsads.interstitial;

public interface MobiInterstitialListener {

    void onDisplayed(String adsProvider);

    void onClosed(String adsProvider);

    void onError(String adsProvider, MobiInterstitialError error);

    void onLoaded(String adsProvider);

    void onClicked(String adsProvider);

}
