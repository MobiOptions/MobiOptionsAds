package com;

import android.app.Application;

import androidx.annotation.Nullable;

import com.osama.mobioptionsads.MobiInitializationListener;
import com.osama.mobioptionsads.MobiOptionsAdsInit;

import org.jetbrains.annotations.NotNull;

public class RootApplication extends Application {

    private static MobiOptionsAdsInit mobiOptionsAdsInit;

    public static RootApplication rootApplication;


    public static synchronized void setupMobiOptionsAds(@NotNull MobiInitializationListener listener) {
        if (mobiOptionsAdsInit == null) {
            mobiOptionsAdsInit = new MobiOptionsAdsInit(rootApplication, "5QkzBKobuLjiypdKXg1f5LGMyC0xh6", listener);
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
