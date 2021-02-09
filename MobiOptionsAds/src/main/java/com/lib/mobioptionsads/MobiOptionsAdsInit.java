package com.lib.mobioptionsads;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.BuildConfig;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.lib.mobioptionsads.data.DataManger;
import com.lib.mobioptionsads.data.remote.model.Advertisement;
import com.lib.mobioptionsads.data.remote.model.ApiResponse;
import com.lib.mobioptionsads.data.remote.model.MobiSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.ads.AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CRASH_DEBUG_MODE;
import static com.lib.mobioptionsads.MobiConstants.ADMOB_PROVIDER;
import static com.lib.mobioptionsads.MobiConstants.FACEBOOK_PROVIDER;
import static com.lib.mobioptionsads.MobiConstants.ROTATION_PROVIDER;
import static com.lib.mobioptionsads.MobiConstants.TIME_UNTIL_THE_APP_IS_STARTED;
import static com.lib.mobioptionsads.MobiConstants.UNITY_PROVIDER;

public class MobiOptionsAdsInit {

    private final MobiInitializationListener mobiInitializationListener;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static DataManger dataManger;
    public static MobiSetting mobiSetting;
    public static boolean testingMode = false;
    private static boolean isSetUpDone = false;
    private static List<String> admobTestDevices = new ArrayList<>();
    public static boolean appIsStartedAfterDelay = false;
    private static boolean playStoreCheck = false;

    public boolean isInitialized() {
        return isSetUpDone;
    }

    public static void setAdmobTestDevices(List<String> admobTestDevices) {
        MobiOptionsAdsInit.admobTestDevices = admobTestDevices;
    }

    private MobiOptionsAdsInit(Context context,
                               String applicationToken,
                               MobiInitializationListener mobiInitializationListener) {
        this.mobiInitializationListener = mobiInitializationListener;
        setUpDataManager(context);
        Call<ApiResponse> verifyCall = dataManger.verifyAppToken(applicationToken);
        verifyCall.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                ApiResponse apiResponse = response.body();
                if (apiResponse != null) {
                    if (apiResponse.getCode() == 404 && !apiResponse.getStatus()) {
                        mobiInitializationListener.onInitializationFailed("MobiOptionsAds: Initialization error: Invalid token");
                        isSetUpDone = false;
                    } else if (apiResponse.getMobiSetting() != null) {
                        mobiSetting = apiResponse.getMobiSetting();

                        dataManger.setAppToken(apiResponse.getMobiSetting().getToken());
                        dataManger.setProjectId(apiResponse.getMobiSetting().getId());

                        if (dataManger.getLaunchedFirstTime())
                            setAppLaunchedFirstTime(context);
                        setupSDKs(context);
                        setAppLaunched();
                        if (mobiSetting.getAdsProvider().equals(ROTATION_PROVIDER))
                            setUpRotationProviders(context);
                        else
                            setUpSingleProviders(context);
                        isSetUpDone = true;
                        delayUntilAppRun();
                        Log.d(MobiConstants.TAG, "onResponse: => all data is here");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                mobiInitializationListener.onInitializationFailed("MobiOptionsAds: Initialization error: " + t.getMessage());
            }
        });
    }


    public static synchronized MobiOptionsAdsInit build(Context context,
                                                        String applicationToken,
                                                        boolean isTesting,
                                                        MobiInitializationListener mobiInitializationListener) {
        testingMode = isTesting;
        return new MobiOptionsAdsInit(context, applicationToken, mobiInitializationListener);
    }

    private static synchronized void setUpDataManager(Context context) {
        if (dataManger == null) {
            dataManger = new DataManger(context);
        }
    }

    public static DataManger getDataManger() {
        return dataManger;
    }


    private OnInitializationCompleteListener getAdmobInitListener() {
        return initializationStatus -> mobiInitializationListener.onInitializationSuccess();
    }


    private void setupSDKs(Context context) {
        handler.post(() -> {
            if (BuildConfig.DEBUG) {
                AdSettings.setIntegrationErrorMode(INTEGRATION_ERROR_CRASH_DEBUG_MODE);
            }
            AudienceNetworkAds.initialize(context);
            MobileAds.initialize(context, getAdmobInitListener());
            if (testingMode) {
                RequestConfiguration requestConfiguration = new RequestConfiguration
                        .Builder()
                        .setTestDeviceIds(admobTestDevices).build();
                MobileAds.setRequestConfiguration(requestConfiguration);
            }
        });
    }

    private void setAppLaunchedFirstTime(Context context) {
        handler.post(() -> {
            Map<String, Object> fields = new HashMap<>();
            fields.put("ads_projects_id", dataManger.getProjectId());
            if (isInstalledFromPlayStore(context))
                fields.put("playstore", true);
            Call<ApiResponse> apiResponseCall = dataManger.setAppLaunchedFirstTime(fields);
            apiResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    dataManger.setLaunchedFirstTime(false);
                    Log.d(MobiConstants.TAG, "onResponse: => check the response");
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.d(MobiConstants.TAG, "onFailure: setAppLaunchedFirstTime errors => " + t.getMessage());
                }
            });
        });
    }

    private void setUpRotationProviders(Context context) {
        if (isInstalledFromPlayStore(context)) {
            if (!Objects.requireNonNull(dataManger.getLastProvidersShown().get(MobiConstants.FACEBOOK_PROVIDER))
                    && !Objects.requireNonNull(dataManger.getLastProvidersShown().get(MobiConstants.UNITY_PROVIDER))
                    && !Objects.requireNonNull(dataManger.getLastProvidersShown().get(MobiConstants.ADMOB_PROVIDER))) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(MobiConstants.ADMOB_PROVIDER, false);
                map.put(MobiConstants.FACEBOOK_PROVIDER, false);
                map.put(MobiConstants.UNITY_PROVIDER, true);
                dataManger.setLastProvidersShown(map);
                mobiSetting.setAdsProvider(ADMOB_PROVIDER);
                return;
            }
            if (Objects.requireNonNull(MobiOptionsAdsInit.getDataManger().getLastProvidersShown().get(UNITY_PROVIDER))) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(ADMOB_PROVIDER, true);
                map.put(UNITY_PROVIDER, false);
                map.put(FACEBOOK_PROVIDER, false);
                mobiSetting.setAdsProvider(ADMOB_PROVIDER);
                MobiOptionsAdsInit.setShownProviders(map);
            } else if (Objects.requireNonNull(MobiOptionsAdsInit.getDataManger().getLastProvidersShown().get(ADMOB_PROVIDER))) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(FACEBOOK_PROVIDER, true);
                map.put(ADMOB_PROVIDER, false);
                map.put(UNITY_PROVIDER, false);
                mobiSetting.setAdsProvider(FACEBOOK_PROVIDER);
                MobiOptionsAdsInit.setShownProviders(map);
            } else if (Objects.requireNonNull(MobiOptionsAdsInit.getDataManger().getLastProvidersShown().get(FACEBOOK_PROVIDER))) {
                Map<String, Boolean> map = new HashMap<>();
                map.put(FACEBOOK_PROVIDER, false);
                map.put(ADMOB_PROVIDER, false);
                map.put(UNITY_PROVIDER, true);
                mobiSetting.setAdsProvider(UNITY_PROVIDER);
                MobiOptionsAdsInit.setShownProviders(map);
            }
        } else {
            mobiSetting.setAdsProvider(UNITY_PROVIDER);                 // Show only the unity ads
        }
    }


    private void setUpSingleProviders(Context context) {
        if (isInstalledFromPlayStore(context)) {
            mobiSetting.setSingle(true);
        } else {
            mobiSetting.setSingle(true);
            mobiSetting.setAdsProvider(UNITY_PROVIDER);                 // Show only the unity ads
        }
    }


    private void delayUntilAppRun() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> appIsStartedAfterDelay = true, TIME_UNTIL_THE_APP_IS_STARTED);
    }

    private void setAppLaunched() {
        handler.post(() -> {
            Map<String, Object> fields = new HashMap<>();
            fields.put("ads_projects_id", dataManger.getProjectId());
            if (isAppLaunchedInLast24H()) {
                dataManger.setAppLaunchedAt(System.currentTimeMillis());
                fields.put("unique", true);
            }
            if (isAppLaunchedMoreThanOneTime()) {
                fields.put("second", true);
            }
            Call<ApiResponse> apiResponseCall = dataManger.setAppInitialized(fields);
            apiResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Log.d(MobiConstants.TAG, "onResponse: setAppLaunched success");
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.d(MobiConstants.TAG, "onFailure: setAppLaunched errors " + t.getMessage());
                }
            });
        });
    }

    private boolean isInstalledFromPlayStore(Context context) {
        if (playStoreCheck)
            return true;
        else {
            List<String> providers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));
            final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());
            return installer != null && providers.contains(installer);
        }
    }

    private boolean isAppLaunchedInLast24H() {
        return System.currentTimeMillis() - dataManger.getAppLaunchedAt() >= MobiConstants.TWENTY_HOUR_MILLS;
    }

    private boolean isAppLaunchedMoreThanOneTime() {
        if (isAppLaunchedInLast24H()) {
            int currentTimes = dataManger.getNumTimesLaunched();
            dataManger.setNumTimesLaunched(++currentTimes);
            return currentTimes > 1;
        }
        dataManger.setNumTimesLaunched(0);
        return false;
    }

    public static void setAppStats(Map<String, Object> fields) {
        Handler handlerOne = new Handler(Looper.getMainLooper());
        handlerOne.post(() -> {
            fields.put("ads_projects_id", dataManger.getProjectId());
            Call<ApiResponse> apiResponseCall = dataManger.setAdsStats(fields);
            apiResponseCall.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    Log.d(MobiConstants.TAG, "onResponse: setAppStats => success");
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Log.d(MobiConstants.TAG, "onFailure: setAppStats => " + t.getMessage());
                }
            });
        });
    }

    public static void setShownProviders(Map<String, Boolean> shownProviders) {
        dataManger.setLastProvidersShown(shownProviders);
    }

    public static void setDisableStoreCheck(boolean disableStoreCheck) {
        playStoreCheck = disableStoreCheck;
    }

    private MobiSetting getFakeMobiSettings() {
        MobiSetting mobiSetting = new MobiSetting();
        mobiSetting.setAdsProvider(ROTATION_PROVIDER);
        mobiSetting.setAdsEnabled(MobiConstants.SETTINGS_ADS_ENABLED);
        Advertisement ad1 = new Advertisement();

        ad1.setId(10);
        ad1.setAdmobId("ca-app-pub-3940256099942544/6300978111");
        ad1.setFacebookId("IMG_16_9_APP_INSTALL#330311461246322_706629543614510");
        ad1.setUnityId("testing_banner");
        ad1.setName("Banner_testing");

        Advertisement ad2 = new Advertisement();
        ad2.setName("Interstitial_testing");
        ad2.setUnityId("interstitial_ad_testing");
        ad2.setFacebookId("330311461246322_706631666947631");
        ad2.setAdmobId("ca-app-pub-3940256099942544/1033173712");
        ad2.setId(11);

        Advertisement ad3 = new Advertisement();
        ad3.setId(12);
        ad3.setName("Rewarded_testing");
        ad3.setAdmobId("ca-app-pub-3940256099942544/5224354917");
        ad3.setFacebookId("330311461246322_706645230279608");
        ad3.setUnityId("rewarded_test_ad");

        Advertisement ad4 = new Advertisement();
        ad4.setId(13);
        ad4.setName("Native_testing");
        ad4.setAdmobId("ca-app-pub-3940256099942544/2247696110");
        ad4.setFacebookId("330311461246322_709355306675267");

        List<Advertisement> ads = new ArrayList<>();
        ads.add(ad1);
        ads.add(ad2);
        ads.add(ad3);
        ads.add(ad4);
        mobiSetting.setAds(ads);
        return mobiSetting;
    }
}
