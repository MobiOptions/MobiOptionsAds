package com.osama.mobioptionsads;

import android.content.Context;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.BuildConfig;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE;

public class MobiOptionsAdsInit {

    private MobiInitializationListener mobiInitializationListener;

    public MobiOptionsAdsInit(@NotNull Context context,
                              @NotNull String applicationToken,
                              @Nullable MobiInitializationListener mobiInitializationListener) {

        // facebook initialization
        if (BuildConfig.DEBUG) {
            AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CRASH_DEBUG_MODE);
        }
        AudienceNetworkAds.initialize(context);

        // Admob Initialization
        if (mobiInitializationListener == null)
            MobileAds.initialize(context);
        else {
            this.mobiInitializationListener = mobiInitializationListener;
            MobileAds.initialize(context, getAdmobInitListener());
        }


    }


    @Contract(value = " -> new", pure = true)
    private @NotNull OnInitializationCompleteListener getAdmobInitListener() {
        return initializationStatus -> mobiInitializationListener.onInitializationSuccess();
    }
}
