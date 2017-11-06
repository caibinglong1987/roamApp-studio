package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by caibinglong
 * on 2017/1/3.
 */

public class EVoucherBean implements Serializable {
    public String createtime;
    @SerializedName("description")
    public String descContext;
    public String effect_datetime;
    public long evoucher_sn;
    public long evoucherid;
    public String failure_datetime;
    @SerializedName("image")
    public String imageUrl;
    public double money;
    @SerializedName("name")
    public String name;
    public long orderid;
    public boolean repeatable;
    public String used_datetime;
}
