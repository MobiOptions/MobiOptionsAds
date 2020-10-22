package com.osama.mobioptionsads.interstitial;

public class MobiInterstitialError {

    public String code;
    public String message;

    public MobiInterstitialError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "MobiInterstitialError details: code: " + code + "\nmessage: " + message;
    }
}
