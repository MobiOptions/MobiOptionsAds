package com;

import android.app.Application;


import com.lib.mobioptionsads.MobiInitializationListener;
import com.lib.mobioptionsads.MobiOptionsAdsInit;

import java.util.Collections;

public class RootApplication extends Application {

    private static MobiOptionsAdsInit mobiOptionsAdsInit;

    public static RootApplication rootApplication;


    public static synchronized void setupMobiOptionsAds(MobiInitializationListener listener) {
        if (mobiOptionsAdsInit == null) {
            MobiOptionsAdsInit.setAdmobTestDevices(Collections.singletonList("YOU-TEST-DEVICE-ID-PLACED-HERE"));
            MobiOptionsAdsInit.setDisableStoreCheck(true);                                                          // true to simulate the play sotre behaviour
            mobiOptionsAdsInit = MobiOptionsAdsInit.build(rootApplication,
                    "59VXT7z2sIUcjY8h8KSU9nYJuWvSiU",               // TJ6N6Wy8aZsc9oWW92TuXlIZwsGtj7
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
