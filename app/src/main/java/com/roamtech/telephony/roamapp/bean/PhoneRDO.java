package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin03 on 2016/8/10.
 */
public class PhoneRDO implements Serializable {
    private List<Phone> phones;

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

}
