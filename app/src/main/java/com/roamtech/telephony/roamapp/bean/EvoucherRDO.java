package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin03 on 2016/8/5.
 */
public class EvoucherRDO implements Serializable {
    List<Evoucher> evouchers;

    public List<Evoucher> getEvouchers() {
        return evouchers;
    }

    public void setEvouchers(List<Evoucher> evouchers) {
        this.evouchers = evouchers;
    }
}
