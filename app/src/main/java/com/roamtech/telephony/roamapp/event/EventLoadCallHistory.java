package com.roamtech.telephony.roamapp.event;

/**
 * Created by caibinglong
 * on 2017/3/1.
 */

public class EventLoadCallHistory extends EventBase {
    private int unreadNumber;

    public EventLoadCallHistory() {
        super();
    }

    public void setUnreadNumber(int number) {
        unreadNumber = number;
    }

    public int getUnreadNumber() {
        return unreadNumber;
    }
}
