package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by long
 * on 2016/10/19.
 * 络漫宝配置
 */

public class CommonRoamBox<T> implements Serializable {
    public String id;
    @SerializedName("jsonrpc")
    public String jsonRpc;
    @SerializedName("result")
    public T attributes;
    public String error_info;
}
