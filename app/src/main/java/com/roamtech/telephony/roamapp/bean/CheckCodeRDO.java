package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by admin03 on 2016/8/10.
 */
public class CheckCodeRDO implements Serializable {
    @SerializedName("checkid")
    private Integer checkId;

    public Integer getCheckId() {
        return checkId;
    }

    public void setCheckId(Integer checkId) {
        this.checkId = checkId;
    }
}
