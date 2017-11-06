package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by roam-caochen on 2017/2/8.
 */

public class BellRDO implements Serializable {

    @SerializedName("bell")
    private Bell bell;

    public Bell getBell() {
        return bell;
    }
}
