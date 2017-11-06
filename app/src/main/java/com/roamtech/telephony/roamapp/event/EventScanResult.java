package com.roamtech.telephony.roamapp.event;

/**
 * Created by chenblue23 on 2016/10/21.
 */

public class EventScanResult extends EventBase {
    private String iccid;

    public EventScanResult(String iccid) {
        this.iccid = iccid;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }
}
