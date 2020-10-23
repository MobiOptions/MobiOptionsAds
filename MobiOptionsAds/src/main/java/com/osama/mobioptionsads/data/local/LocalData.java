package com.osama.mobioptionsads.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.osama.mobioptionsads.MobiConstants;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LocalData implements ILocalData {

    private static final String SHARED_PRES_NAME = "mobioptionsads.Prefs";
    public static final String APP_LAUNCHED_FIRST_TIME = "mobioptionsads.Launched_First_Time";
    public static final String APP_TOKEN = "mobioptionsads.App_Token";
    public static final String APP_LAUNCHED_AT = "mobioptionsads.App_Launched_At";
    public static final String APP_LAUNCHED_TIMES = "mobioptionsads.App_Launched_For";

    public static final String FACEBOOK_PROVIDER_KEY = "mobioptionsads.Facebook_provider_key";
    public static final String UNITY_PROVIDER_KEY = "mobioptionsads.Unity_provider_key";
    public static final String ADMOB_PROVIDER_KEY = "mobioptionsads.Admob_provider_key";

    private final SharedPreferences sharedPreferences;

    public LocalData(@NotNull Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PRES_NAME, Context.MODE_PRIVATE);
    }


    @Override
    public void setAppToken(String appToken) {
        sharedPreferences.edit().putString(APP_TOKEN, appToken).apply();
    }

    @Override
    public String getAppToken() {
        return sharedPreferences.getString(APP_TOKEN, null);
    }

    @Override
    public void setLaunchedFirstTime(boolean launchedFirstTime) {
        sharedPreferences.edit().putBoolean(APP_LAUNCHED_FIRST_TIME, false).apply();
    }

    @Override
    public boolean getLaunchedFirstTime() {
        return sharedPreferences.getBoolean(APP_LAUNCHED_FIRST_TIME, true);
    }

    @Override
    public void setAppLaunchedAt(Long launchedAt) {
        sharedPreferences.edit().putLong(APP_LAUNCHED_AT, launchedAt).apply();
    }

    @Override
    public Long getAppLaunchedAt() {
        return sharedPreferences.getLong(APP_LAUNCHED_AT, 0L);
    }

    @Override
    public void setNumTimesLaunched(int numTimesLaunched) {
        sharedPreferences.edit().putInt(APP_LAUNCHED_TIMES, numTimesLaunched).apply();
    }

    @Override
    public int getNumTimesLaunched() {
        return sharedPreferences.getInt(APP_LAUNCHED_TIMES, 0);
    }

    @Override
    public void setLastProvidersShown(@NotNull Map<String, Boolean> map) {
        sharedPreferences.edit().putBoolean(FACEBOOK_PROVIDER_KEY, Objects.requireNonNull(map.get(MobiConstants.FACEBOOK_PROVIDER))).apply();
        sharedPreferences.edit().putBoolean(ADMOB_PROVIDER_KEY, Objects.requireNonNull(map.get(MobiConstants.ADMOB_PROVIDER))).apply();
        sharedPreferences.edit().putBoolean(UNITY_PROVIDER_KEY, Objects.requireNonNull(map.get(MobiConstants.UNITY_PROVIDER))).apply();
    }

    @Override
    public Map<String, Boolean> getLastProvidersShown() {
        Map<String, Boolean> map = new HashMap<>();
        map.put(MobiConstants.FACEBOOK_PROVIDER, sharedPreferences.getBoolean(FACEBOOK_PROVIDER_KEY, false));
        map.put(MobiConstants.UNITY_PROVIDER, sharedPreferences.getBoolean(UNITY_PROVIDER_KEY, false));
        map.put(MobiConstants.ADMOB_PROVIDER, sharedPreferences.getBoolean(ADMOB_PROVIDER_KEY, false));
        return map;
    }
}
