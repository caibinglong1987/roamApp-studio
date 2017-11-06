package com.roamtech.telephony.roamapp.event;

/**
 * Created by chenblue23 on 2016/8/29.
 */
public class EventPayResult extends EventBase {
    private boolean paySuccess;

    public EventPayResult(boolean paySuccess) {
        this.paySuccess = paySuccess;
    }

    public boolean isPaySuccess() {
        return paySuccess;
    }

    public void setPaySuccess(boolean paySuccess) {
        this.paySuccess = paySuccess;
    }
}
