package com.lib.mobioptionsads.banner.size;

import com.google.android.gms.ads.AdSize;

public class AdmobBannerSize {


    public static final int ADMOB_SIZE_BANNER = 1;
    public static final int ADMOB_SIZE_FULL_BANNER = 2;
    public static final int ADMOB_LARGE_BANNER = 3;
    public static final int ADMOB_LEADER_BOARD = 4;
    public static final int ADMOB_MEDIUM_RECTANGLE = 6;
    public static final int ADMOB_WIDE_SKYSCRAPER = 7;
    public static final int ADMOB_SMART_BANNER = 8;
    public static final int ADMOB_FLUID = 9;
    public static final int ADMOB_SEARCH = 10;


    private final AdSize adSize;

    public AdmobBannerSize(int sizeType) {
        if (sizeType == ADMOB_SIZE_BANNER) {
            this.adSize = AdSize.BANNER;
        } else if (sizeType == ADMOB_SIZE_FULL_BANNER) {
            this.adSize = AdSize.FULL_BANNER;
        } else if (sizeType == ADMOB_LARGE_BANNER) {
            this.adSize = AdSize.LARGE_BANNER;
        } else if (sizeType == ADMOB_LEADER_BOARD) {
            this.adSize = AdSize.LEADERBOARD;
        } else if (sizeType == ADMOB_MEDIUM_RECTANGLE) {
            this.adSize = AdSize.MEDIUM_RECTANGLE;
        } else if (sizeType == ADMOB_WIDE_SKYSCRAPER) {
            this.adSize = AdSize.WIDE_SKYSCRAPER;
        } else if (sizeType == ADMOB_SMART_BANNER) {
            this.adSize = AdSize.SMART_BANNER;
        } else if (sizeType == ADMOB_FLUID) {
            this.adSize = AdSize.FLUID;
        } else if (sizeType == ADMOB_SEARCH) {
            this.adSize = AdSize.SEARCH;
        } else {
            this.adSize = null;
            throw new IllegalArgumentException("You provided a wrong sizeType, please refer the the documentation");
        }
    }

    public AdmobBannerSize(int width, int height) {
        this.adSize = new AdSize(width, height);
    }

    public AdSize getAdSize() {
        return adSize;
    }

    public int getWidth() {
        if (this.adSize != null) {
            return this.adSize.getWidth();
        }
        return -1;
    }

    public int getHeight() {
        if (adSize != null) {
            return this.adSize.getHeight();
        }
        return -1;
    }
}
