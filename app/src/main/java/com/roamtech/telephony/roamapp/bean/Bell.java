package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by roam-caochen on 2017/2/8.
 */

public class Bell implements Serializable {

    @SerializedName("starttime")
    private Date startTime;
    @SerializedName("endtime")
    private Date endTime;
    @SerializedName("url")
    private String url;

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && obj instanceof Bell) {
            Bell bell = (Bell) obj;
            if (this.url.equals(bell.url) && this.startTime.equals(bell.startTime) && this.endTime.equals(bell.endTime)) {
                return true;
            }
        }
        return false;
    }
}
