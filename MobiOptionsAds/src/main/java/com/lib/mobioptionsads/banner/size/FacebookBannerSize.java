package com.lib.mobioptionsads.banner.size;

import com.facebook.ads.AdSize;

public class FacebookBannerSize {

    @Deprecated
    public static final int FACEBOOK_BANNER_320_50 = 4;
    public static final int FACEBOOK_INTERSTITIAL = 100;
    public static final int FACEBOOK_BANNER_HEIGHT_50 = 5;
    public static final int FACEBOOK_BANNER_HEIGHT_90 = 6;
    public static final int FACEBOOK_RECTANGLE_HEIGHT_250 = 7;

    private final AdSize adSize;


    /**
     * @param sizeType: This size type should be one of above values
     */
    public FacebookBannerSize(int sizeType) {
        if (sizeType == FACEBOOK_BANNER_320_50) {
            this.adSize = AdSize.BANNER_320_50;
        } else if (sizeType == FACEBOOK_INTERSTITIAL) {
            this.adSize = AdSize.INTERSTITIAL;
        } else if (sizeType == FACEBOOK_BANNER_HEIGHT_50) {
            this.adSize = AdSize.BANNER_HEIGHT_50;
        } else if (sizeType == FACEBOOK_BANNER_HEIGHT_90) {
            this.adSize = AdSize.BANNER_HEIGHT_90;
        } else if (sizeType == FACEBOOK_RECTANGLE_HEIGHT_250) {
            this.adSize = AdSize.RECTANGLE_HEIGHT_250;
        } else {
            adSize = null;
            throw new IllegalArgumentException("You provided a wrong sizeType, please refer to the documentation");
        }
    }


    /**
     * @param width:  the facebook banner width
     * @param height: the facebook banner height
     */
    public FacebookBannerSize(int width, int height) {
        adSize = new AdSize(width, height);
    }

    public AdSize getAdSize() {
        return adSize;
    }

    public int getWidth() {
        if (adSize != null) {
            return adSize.getWidth();
        }
        return -1;
    }

    public int getHeight() {
        if (adSize != null) {
            return adSize.getHeight();
        }
        return -1;
    }
}
