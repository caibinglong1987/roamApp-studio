package com.roamtech.telephony.roamapp.event;

/**
 * Created by caibinglong
 * on 2017/3/13.
 */

public class EventUpPay {
    private String payResult;

    public EventUpPay() {
        super();
    }

    public void setPayResult(String payResult) {
        this.payResult = payResult;
    }

    public String getPayResult() {
        return payResult;
    }
}
