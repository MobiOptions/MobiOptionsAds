package com.lib.mobioptionsads.data.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MobiSetting {


    @Expose
    @SerializedName("id")
    private int id;


    @Expose
    @SerializedName("app_id")
    private String appId;


    @Expose
    @SerializedName("token")
    private String token;


    @Expose
    @SerializedName("user_id")
    private int userId;


    @Expose
    @SerializedName("ads_activated")
    private int adsEnabled;


    @Expose
    @SerializedName("ads_provider")
    private String adsProvider;


    @Expose
    @SerializedName("auto_disable")
    private int autoDisable;


    @Expose
    @SerializedName("serve_perc")
    private Double serveAdPer;


    private boolean isSingle = false;


    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    @Expose
    @SerializedName("unity_game_id")
    String unityGameId;


    @Expose
    @SerializedName("ads")
    private List<Advertisement> ads;


    @SerializedName("id")
    public int getId() {
        return id;
    }


    @SerializedName("id")
    public void setId(int id) {
        this.id = id;
    }


    @SerializedName("ads_activated")
    public int getAdsEnabled() {
        return adsEnabled;
    }


    @SerializedName("ads_activated")
    public void setAdsEnabled(int adsEnabled) {
        this.adsEnabled = adsEnabled;
    }


    @SerializedName("ads_provider")
    public String getAdsProvider() {
        return adsProvider;
    }


    @SerializedName("ads_provider")
    public void setAdsProvider(String adsProvider) {
        this.adsProvider = adsProvider;
    }


    @SerializedName("serve_perc")
    public Double getServeAdPer() {
        return serveAdPer;
    }


    @SerializedName("serve_perc")
    public void setServeAdPer(Double serveAdPer) {
        this.serveAdPer = serveAdPer;
    }


    @SerializedName("ads")
    public List<Advertisement> getAds() {
        return ads;
    }


    @SerializedName("ads")
    public void setAds(List<Advertisement> ads) {
        this.ads = ads;
    }


    @SerializedName("app_id")
    public String getAppId() {
        return appId;
    }


    @SerializedName("app_id")
    public void setAppId(String appId) {
        this.appId = appId;
    }


    @SerializedName("token")
    public String getToken() {
        return token;
    }


    @SerializedName("token")
    public void setToken(String token) {
        this.token = token;
    }


    @SerializedName("user_id")
    public int getUserId() {
        return userId;
    }


    @SerializedName("user_id")
    public void setUserId(int userId) {
        this.userId = userId;
    }


    @SerializedName("auto_disable")
    public int getAutoDisable() {
        return autoDisable;
    }


    @SerializedName("auto_disable")
    public void setAutoDisable(int autoDisable) {
        this.autoDisable = autoDisable;
    }


    @SerializedName("unity_game_id")
    public String getUnityGameId() {
        return unityGameId;
    }


    @SerializedName("unity_game_id")
    public void setUnityGameId(String unityGameId) {
        this.unityGameId = unityGameId;
    }
}
