package com.roamtech.telephony.roamapp.event;

/**
 * Created by user on 6/8/2016.
 */
public class EventBase {
    //消息
    private String message;

    public EventBase() {
    }
    public EventBase(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
