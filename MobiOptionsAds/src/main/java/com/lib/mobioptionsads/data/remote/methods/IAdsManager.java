package com.lib.mobioptionsads.data.remote.methods;

import com.lib.mobioptionsads.data.remote.model.ApiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IAdsManager {


    @GET("adsproject/get/{token}")
    Call<ApiResponse> verifyAppToken(@Path("token") String appToken);


    @FormUrlEncoded
    @POST("adsprojectopen/add")
    Call<ApiResponse> setAppInitialized(@FieldMap Map<String, Object> fields);


    @FormUrlEncoded
    @POST("adsprojectinstalls/add")
    Call<ApiResponse> setAppLaunchedFirstTime(@FieldMap Map<String, Object> fields);


    @FormUrlEncoded
    @POST("adsprojectadstats/add")
    Call<ApiResponse> setAdsStats(@FieldMap Map<String, Object>fields);


}
