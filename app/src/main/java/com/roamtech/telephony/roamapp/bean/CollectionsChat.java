package com.roamtech.telephony.roamapp.bean;

import com.roamtech.telephony.roamapp.db.model.ChatDBModel;

import java.util.Comparator;

/**
 * Created by caibinglong
 * on 2016/12/8.
 */

public class CollectionsChat implements Comparator {

    @Override
    public int compare(Object lhs, Object rhs) {
        ChatDBModel a = (ChatDBModel) lhs;
        ChatDBModel b = (ChatDBModel) rhs;

        return (b.Id - a.Id);
    }
}
