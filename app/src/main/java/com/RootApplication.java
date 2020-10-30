package com;

import android.app.Application;
import android.util.Log;


import com.osama.mobioptionsads.MobiInitializationListener;
import com.osama.mobioptionsads.MobiOptionsAdsInit;
import com.osama.mobioptionsads.interstitial.MobiInterstitialListener;
import com.osama.mobioptionsads.nativeAd.MobiOptionsNativeAd;

import java.util.Collections;

public class RootApplication extends Application {

    private static MobiOptionsAdsInit mobiOptionsAdsInit;

    public static RootApplication rootApplication;


    public static synchronized void setupMobiOptionsAds(MobiInitializationListener listener) {
        if (mobiOptionsAdsInit == null) {
            MobiOptionsAdsInit.setAdmobTestDevices(Collections.singletonList("YOU-TEST-DEVICE-ID-PLACED-HERE"));
            mobiOptionsAdsInit = MobiOptionsAdsInit.build(rootApplication,
                    "TJ6N6Wy8aZsc9oWW92TuXlIZwsGtj7",
                    true,                                               // If you set this to false, the list of the test devices will be ignored.
                    listener);
        } else if (mobiOptionsAdsInit.isInitialized()) {
            listener.onInitializationSuccess();
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
