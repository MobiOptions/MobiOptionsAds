package com.osama.mobioptionsads.data.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ApiResponse {

    @Expose
    @SerializedName("status")
    private boolean status;


    @Expose
    @SerializedName("code")
    private int code;


    @Expose
    @SerializedName("message")
    private String message;


    @Expose
    @SerializedName("adProject")
    private MobiSetting mobiSetting;


    @SerializedName("adProject")
    public MobiSetting getMobiSetting() {
        return mobiSetting;
    }


    @SerializedName("adProject")
    public void setMobiSetting(MobiSetting mobiSetting) {
        this.mobiSetting = mobiSetting;
    }

    @SerializedName("status")
    public boolean getStatus() {
        return status;
    }


    @SerializedName("status")
    public void setStatus(boolean status) {
        this.status = status;
    }


    @SerializedName("code")
    public int getCode() {
        return code;
    }


    @SerializedName("code")
    public void setCode(int code) {
        this.code = code;
    }


    @SerializedName("message")
    public String getMessage() {
        return message;
    }


    @SerializedName("message")
    public void setMessage(String message) {
        this.message = message;
    }
}
