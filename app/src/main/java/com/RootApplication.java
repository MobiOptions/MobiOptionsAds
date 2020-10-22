package com;

import android.app.Application;

import androidx.annotation.Nullable;

import com.osama.mobioptionsads.MobiInitializationListener;
import com.osama.mobioptionsads.MobiOptionsAdsInit;

public class RootApplication extends Application {

    private static MobiOptionsAdsInit mobiOptionsAdsInit;

    public static RootApplication rootApplication;


    public static synchronized void setupMobiOptionsAds(@Nullable MobiInitializationListener listener) {
        if (mobiOptionsAdsInit == null) {
            mobiOptionsAdsInit = new MobiOptionsAdsInit(rootApplication, "PLACE_YOUR_APP_TOKEN_HERE", listener);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    public RootApplication() {
        super();
        rootApplication = this;
    }
}
