package com.lib.mobioptionsads.banner.size;

public class UnityBannerSize {

    private final com.unity3d.services.banners.UnityBannerSize unityBannerSize;


    public UnityBannerSize(int width, int height) {
        this.unityBannerSize = new com.unity3d.services.banners.UnityBannerSize(width, height);
    }

    public com.unity3d.services.banners.UnityBannerSize getUnityBannerSize() {
        return unityBannerSize;
    }

    private int getWidth() {
        if (unityBannerSize != null) {
            return unityBannerSize.getWidth();
        }
        return -1;
    }

    private int getHeight() {
        if (unityBannerSize != null) {
            return unityBannerSize.getHeight();
        }
        return -1;
    }
}
