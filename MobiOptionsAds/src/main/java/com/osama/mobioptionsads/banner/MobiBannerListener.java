package com.osama.mobioptionsads.banner;


public interface MobiBannerListener {

    void onLoaded(String adsProvider);

    void onClicked(String adsProvider);

    void onFailedToLoad(String adsProvider, MobiOptionBannerError error);

    void onLeftApplication(String adsProvider);

}
