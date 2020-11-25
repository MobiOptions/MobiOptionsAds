package com.osama.mobioptionsads.data;

import android.content.Context;

import com.osama.mobioptionsads.data.local.LocalData;
import com.osama.mobioptionsads.data.remote.ApiManager;
import com.osama.mobioptionsads.data.remote.model.ApiResponse;

import java.util.Map;

import retrofit2.Call;

public class DataManger implements IDataManager {

    private final ApiManager apiManager;
    private final LocalData localData;

    public DataManger(Context context) {
        apiManager = new ApiManager();
        localData = new LocalData(context);
    }

    @Override
    public Call<ApiResponse> verifyAppToken(String appToken) {
        return apiManager.verifyAppToken(appToken);
    }

    @Override
    public Call<ApiResponse> setAppInitialized(Map<String, Object> fields) {
        return apiManager.setAppInitialized(fields);
    }

    @Override
    public Call<ApiResponse> setAppLaunchedFirstTime(Map<String, Object> fields) {
        return apiManager.setAppLaunchedFirstTime(fields);
    }

    @Override
    public Call<ApiResponse> setAdsStats(Map<String, Object> fields) {
        return apiManager.setAdsStats(fields);
    }

    @Override
    public void setAppToken(String appToken) {
        localData.setAppToken(appToken);
    }

    @Override
    public String getAppToken() {
        return localData.getAppToken();
    }

    @Override
    public void setLaunchedFirstTime(boolean launchedFirstTime) {
        localData.setLaunchedFirstTime(launchedFirstTime);
    }

    @Override
    public boolean getLaunchedFirstTime() {
        return localData.getLaunchedFirstTime();
    }

    @Override
    public void setAppLaunchedAt(Long launchedAt) {
        localData.setAppLaunchedAt(launchedAt);
    }

    @Override
    public Long getAppLaunchedAt() {
        return localData.getAppLaunchedAt();
    }

    @Override
    public void setNumTimesLaunched(int numTimesLaunched) {
        localData.setNumTimesLaunched(numTimesLaunched);
    }

    @Override
    public int getNumTimesLaunched() {
        return localData.getNumTimesLaunched();
    }

    @Override
    public void setLastProvidersShown(Map<String, Boolean> map) {
        localData.setLastProvidersShown(map);
    }

    @Override
    public Map<String, Boolean> getLastProvidersShown() {
        return localData.getLastProvidersShown();
    }

    @Override
    public void setProjectId(int projectId) {
        localData.setProjectId(projectId);
    }

    @Override
    public int getProjectId() {
        return localData.getProjectId();
    }
}
