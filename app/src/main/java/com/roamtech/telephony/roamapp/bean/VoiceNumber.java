package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by admin03 on 2016/7/29.
 */
public class VoiceNumber implements Serializable {
    private static final long serialVersionUID = 1L;
    @SerializedName("effect_datetime")
    private Date startTime;
    @SerializedName("failure_datetime")
    private Date endTime;
    @SerializedName("remaindertime")
    private Double remainder;//天数
    private String phone;
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
    public Double getRemainder() {
        return remainder;
    }
    public void setRemainder(Double remainder) {
        this.remainder = remainder;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "VoiceNumber{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", remainder=" + remainder +
                ", phone='" + phone + '\'' +
                '}';
    }
}
