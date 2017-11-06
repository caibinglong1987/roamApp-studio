package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin03 on 2016/8/10.
 */
public class AccountExistRDO implements Serializable {
    @SerializedName("is_exist")
    private Boolean isExists;

    public Boolean getExists() {
        return isExists;
    }

    public void setExists(Boolean exists) {
        isExists = exists;
    }

}
