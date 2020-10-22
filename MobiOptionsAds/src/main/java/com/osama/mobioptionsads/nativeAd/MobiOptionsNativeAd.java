package com.osama.mobioptionsads.nativeAd;

import com.osama.mobioptionsads.base.BaseAd;

import org.jetbrains.annotations.NotNull;

public class MobiOptionsNativeAd extends BaseAd {


    @Override
    protected void setupMobiSettings(@NotNull String adName) {

    }

    public MobiOptionsNativeAd(@NotNull String adName) {
        //
        this.setupMobiSettings(adName);
    }
}
