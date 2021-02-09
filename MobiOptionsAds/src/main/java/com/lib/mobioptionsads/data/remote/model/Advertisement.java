package com.lib.mobioptionsads.data.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Advertisement {


    @Expose
    @SerializedName("id")
    private int id;


    @Expose
    @SerializedName("ads_project_id")
    private int adsProjectId;


    @Expose
    @SerializedName("type")
    private String type;


    @Expose
    @SerializedName("admob_id")
    private String admobId;


    @Expose
    @SerializedName("facebook_id")
    private String facebookId;


    @Expose
    @SerializedName("mopub_id")
    private String mopubId;


    @Expose
    @SerializedName("unity_id")
    private String unityId;


    @Expose
    @SerializedName("name")
    private String name;


    @Expose
    @SerializedName("created_at")
    private String createdAt;


    @Expose
    @SerializedName("updated_at")
    private String updatedAt;


    @SerializedName("id")
    public int getId() {
        return id;
    }


    @SerializedName("id")
    public void setId(int id) {
        this.id = id;
    }


    @SerializedName("ads_project_id")
    public int getAdsProjectId() {
        return adsProjectId;
    }


    @SerializedName("ads_project_id")
    public void setAdsProjectId(int adsProjectId) {
        this.adsProjectId = adsProjectId;
    }


    @SerializedName("type")
    public String getType() {
        return type;
    }


    @SerializedName("type")
    public void setType(String type) {
        this.type = type;
    }

    @SerializedName("admob_id")
    public String getAdmobId() {
        return admobId;
    }


    @SerializedName("admob_id")
    public void setAdmobId(String admobId) {
        this.admobId = admobId;
    }


    @SerializedName("facebook_id")
    public String getFacebookId() {
        return facebookId;
    }


    @SerializedName("facebook_id")
    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }


    @SerializedName("mopub_id")
    public String getMopubId() {
        return mopubId;
    }


    @SerializedName("mopub_id")
    public void setMopubId(String mopubId) {
        this.mopubId = mopubId;
    }


    @SerializedName("unity_id")
    public String getUnityId() {
        return unityId;
    }


    @SerializedName("unity_id")
    public void setUnityId(String unityId) {
        this.unityId = unityId;
    }


    @SerializedName("name")
    public String getName() {
        return name;
    }


    @SerializedName("name")
    public void setName(String name) {
        this.name = name;
    }


    @SerializedName("created_at")
    public String getCreatedAt() {
        return createdAt;
    }


    @SerializedName("created_at")
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }


    @SerializedName("updated_at")
    public String getUpdatedAt() {
        return updatedAt;
    }


    @SerializedName("updated_at")
    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
