package com.lib.mobioptionsads.data.remote;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lib.mobioptionsads.data.remote.methods.IAdsManager;
import com.lib.mobioptionsads.data.remote.model.ApiResponse;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.lib.mobioptionsads.MobiConstants.MOBI_BASE_URL;

public class ApiManager implements IApiManager {

    private final IAdsManager adsManager;

    public ApiManager() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    Request request = chain.request()
                            .newBuilder()
                            .addHeader("Accept", "application/json")
                            .build();
                    return chain.proceed(request);
                }).build();
        Gson gson = new GsonBuilder()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MOBI_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        adsManager = retrofit.create(IAdsManager.class);
    }

    @Override
    public Call<ApiResponse> verifyAppToken(String appToken) {
        return adsManager.verifyAppToken(appToken);
    }

    @Override
    public Call<ApiResponse> setAppInitialized(Map<String, Object> fields) {
        return adsManager.setAppInitialized(fields);
    }

    @Override
    public Call<ApiResponse> setAppLaunchedFirstTime(Map<String, Object> fields) {
        return adsManager.setAppLaunchedFirstTime(fields);
    }

    @Override
    public Call<ApiResponse> setAdsStats(Map<String, Object> fields) {
        return adsManager.setAdsStats(fields);
    }
}
