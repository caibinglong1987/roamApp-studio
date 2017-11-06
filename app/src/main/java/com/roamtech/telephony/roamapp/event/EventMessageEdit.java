package com.roamtech.telephony.roamapp.event;

/**
 * Created by user on 6/8/2016.
 */
public class EventMessageEdit extends EventBase{
    //是否选中了全部
    private int selectSize;
    public EventMessageEdit(int selectSize) {
        this.selectSize=selectSize;
    }

    public int getSelectSize() {
        return selectSize;
    }

}
