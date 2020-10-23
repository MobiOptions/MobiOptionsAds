package com.osama.mobioptionsads.nativeAd.size;

public class NativeAdFacebookSize {

    public static final int WIDTH_280_HEIGHT_250 = 12;
    public static final int WIDTH_500_HEIGHT_500 = 13;

    private int facebookNativeSize;

    public NativeAdFacebookSize(int facebookNativeSize) {
        this.facebookNativeSize = facebookNativeSize;
    }

    public int getFacebookNativeSize() {
        return facebookNativeSize;
    }

    public void setFacebookNativeSize(int facebookNativeSize) {
        this.facebookNativeSize = facebookNativeSize;
    }
}
