package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by caibinglong
 * on 2016/12/2.
 */

public class CallMessageBean implements Serializable {
    public String callId;
    public String callee;
    public String caller;
    public String created;
    public int duration; //持续时间
    public boolean direction; //1主叫 0 被叫 callee caller 区分
    public int durationTime;
    @SerializedName("id")
    public long logId;
    public String message;
    public String method;
    public String sipCode;
    public String sipReason;
    public int status;
    public int callState; // 2未接
    public long time;
    public long userId = 0;
    public int unread_number = 0;
}
