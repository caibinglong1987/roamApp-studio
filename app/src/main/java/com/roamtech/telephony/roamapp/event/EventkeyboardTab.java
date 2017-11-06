package com.roamtech.telephony.roamapp.event;

/**
 * Created by user on 6/8/2016.
 */
public class EventkeyboardTab extends EventBase {
    //是否显示
    private boolean isShow;

    public EventkeyboardTab(boolean show) {
        isShow = show;
    }

    public boolean isShow() {
        return isShow;
    }
}
