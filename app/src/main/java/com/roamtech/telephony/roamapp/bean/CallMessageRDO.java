package com.roamtech.telephony.roamapp.bean;


import java.io.Serializable;
import java.util.List;

/**
 * Created by caibinglong
 * on 2016/12/2.
 */

public class CallMessageRDO implements Serializable {
    public List<CallMessageBean> call_records;
    public List<CallMessageBean> message_records;
    public int unread_number = 0;
}
