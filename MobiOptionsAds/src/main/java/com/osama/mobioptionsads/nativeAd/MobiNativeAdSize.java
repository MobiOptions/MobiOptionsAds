package com.osama.mobioptionsads.nativeAd;

import com.osama.mobioptionsads.nativeAd.size.NativeAdFacebookSize;
import com.osama.mobioptionsads.nativeAd.size.NativeAdmobSize;

public class MobiNativeAdSize {

    private NativeAdFacebookSize nativeAdFacebookSize;
    private NativeAdmobSize admobSize;

    public MobiNativeAdSize(int admobNativeAdSize, int facebookNativeAdSize) {
        this.nativeAdFacebookSize = new NativeAdFacebookSize(facebookNativeAdSize);
        this.admobSize = new NativeAdmobSize(admobNativeAdSize);
    }

    public NativeAdFacebookSize getNativeAdFacebookSize() {
        return nativeAdFacebookSize;
    }

    public void setNativeAdFacebookSize(NativeAdFacebookSize nativeAdFacebookSize) {
        this.nativeAdFacebookSize = nativeAdFacebookSize;
    }

    public NativeAdmobSize getAdmobSize() {
        return admobSize;
    }

    public void setAdmobSize(NativeAdmobSize admobSize) {
        this.admobSize = admobSize;
    }
}
