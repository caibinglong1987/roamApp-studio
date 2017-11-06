package com.roamtech.telephony.roamapp.event;

import com.roamtech.telephony.roamapp.bean.CallDetailRecordDBModel;

/**
 * Created by chenblue23 on 2016/8/29.
 */
public class EventCallHistory extends EventBase {
    private static final int DELETE = 1;
    private static final int INSERT = 2;
    private int id;
    private int type;
    public CallDetailRecordDBModel model;

    public static EventCallHistory getDeleteEvent(int id) {
        return new EventCallHistory(id, DELETE);
    }

    public static EventCallHistory getInsertEvent(int id) {
        return new EventCallHistory(id, INSERT);
    }

    public EventCallHistory(int id, int type) {
        super();
        this.id = id;
        this.type = type;
    }

    public void setModel(CallDetailRecordDBModel model) {
        this.model = model;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isDelete() {
        return type == DELETE;
    }

    public boolean isInsert() {
        return type == INSERT;
    }
}
