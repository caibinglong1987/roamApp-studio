package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by chenblue23 on 2016/8/18.
 */
public class MobileExistRDO implements Serializable{
    @SerializedName("is_exist")
    private boolean isExist;
    @SerializedName("userid")
    private String userId;

    public Boolean getExist() {
        return isExist;
    }

    public void setExist(Boolean exist) {
        isExist = exist;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
