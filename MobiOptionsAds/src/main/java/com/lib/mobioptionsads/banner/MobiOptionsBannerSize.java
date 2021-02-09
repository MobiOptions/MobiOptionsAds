package com.lib.mobioptionsads.banner;

import com.lib.mobioptionsads.banner.size.AdmobBannerSize;
import com.lib.mobioptionsads.banner.size.FacebookBannerSize;
import com.lib.mobioptionsads.banner.size.UnityBannerSize;

import org.jetbrains.annotations.NotNull;

public class MobiOptionsBannerSize {

    private final AdmobBannerSize admobBannerSize;
    private final UnityBannerSize unityBannerSize;
    private final FacebookBannerSize facebookBannerSize;


    public MobiOptionsBannerSize(@NotNull AdmobBannerSize admobBannerSize,
                                 @NotNull UnityBannerSize unityBannerSize,
                                 @NotNull FacebookBannerSize facebookBannerSize) {
        this.admobBannerSize = admobBannerSize;
        this.unityBannerSize = unityBannerSize;
        this.facebookBannerSize = facebookBannerSize;
    }

    public AdmobBannerSize getAdmobBannerSize() {
        return admobBannerSize;
    }

    public UnityBannerSize getUnityBannerSize() {
        return unityBannerSize;
    }

    public FacebookBannerSize getFacebookBannerSize() {
        return facebookBannerSize;
    }
}
