package com.lib.mobioptionsads.rewarded;

import androidx.annotation.NonNull;

public class MobiRewardAdError {

    public int code;
    public String message;

    public MobiRewardAdError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @NonNull
    @Override
    public String toString() {
        return "MobiRewardAdError: details: Code: " + code + "\nMessage" + message;
    }
}
