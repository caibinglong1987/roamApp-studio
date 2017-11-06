package com.roamtech.telephony.roamapp.util;

import com.roamtech.telephony.roamapp.bean.SimpleMessage;

import java.util.Comparator;

/**
 * Created by caibinglong
 * on 2016/12/22.
 */

public class SortComparator implements Comparator {
    @Override
    public int compare(Object arg0, Object arg1) {
        SimpleMessage a = (SimpleMessage) arg0;
        SimpleMessage b = (SimpleMessage) arg1;
        return (int) (a.time - b.time);
    }
}
