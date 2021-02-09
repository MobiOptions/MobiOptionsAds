package com.lib.mobioptionsads.banner;


import androidx.annotation.NonNull;


public class MobiOptionBannerError {

    public String errorCode;
    public String errorMessage;

    public MobiOptionBannerError(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @NonNull
    @Override
    public String toString() {
        return "MobiOptionBannerError details => \nCode: " + errorCode + "\nMessage: " + errorMessage;
    }
}
