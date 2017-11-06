package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin03 on 2016/7/29.
 */
public class UCResponse<T>  implements Serializable {
    @SerializedName("error_no")
    private int errorNo;
    @SerializedName("error_info")
    private String errorInfo;
    @SerializedName("userid")
    private Long userId;
    @SerializedName("sessionid")
    private String sessionId;
    @SerializedName("devid")
    private String devId;
    //com.google.gson.internal.LinkedTreeMap
    @SerializedName("result")
    private T attributes;

    public int getErrorNo() {
        return errorNo;
    }

    public void setErrorNo(int errorNo) {
        this.errorNo = errorNo;
    }


    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public T getAttributes() {
        return attributes;
    }

    public void setAttributes(T attributes) {
        this.attributes = attributes;
    }

    /*public <T> T getResultData(String name, TypeToken<T> token) {
        return JSONUtils.fromJson(JSONUtils.toJson(attributes.get(name)),token);
    }

    public <T> T getResultData(String name, Class<T> clazz) {
        return JSONUtils.fromJson(JSONUtils.toJson(attributes.get(name)),clazz);
    }
    public String getResultData(String name) {
        return attributes.get(name).toString();
    }*/

    @Override
    public String toString() {
        return "UCResponse{" +
                "errorNo=" + errorNo +
                ", errorInfo='" + errorInfo + '\'' +
                ", userId=" + userId +
                ", sessionId='" + sessionId + '\'' +
                ", devId='" + devId + '\'' +
                ", attributes=" + attributes +
                '}';
    }
}
