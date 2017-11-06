package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by caibinglong
 * on 2016/11/17.
 */

public class WifiBean implements Serializable{
    public String signal;
    @SerializedName("ssid")
    public String ssId;
    public String channel;
}
