package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by long
 * on 2016/10/13.
 * 络漫宝
 */

public class UCResponeRoamBox<T>  implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("jsonarpc")
    private String jsonarpc;
    @SerializedName("result")
    private T attributes;
}
