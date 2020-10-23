package com.osama.mobioptionsads.nativeAd.size;

public class NativeAdmobSize {

    public static final int GNT_SMALL_TEMPLATE = 10;
    public static final int GNT_MEDIUM_TEMPLATE = 11;

    private int admobNativeSize;

    public NativeAdmobSize(int admobNativeSize) {
        this.admobNativeSize = admobNativeSize;
    }

    public int getAdmobNativeSize() {
        return admobNativeSize;
    }

    public void setAdmobNativeSize(int admobNativeSize) {
        this.admobNativeSize = admobNativeSize;
    }
}
