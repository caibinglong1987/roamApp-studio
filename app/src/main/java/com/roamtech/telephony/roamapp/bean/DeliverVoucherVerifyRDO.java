package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

/**
 * Created by admin03 on 2016/8/5.
 */
public class DeliverVoucherVerifyRDO implements Serializable {
    private Integer quantity;
    //private FunOrder order;
    //private User user;
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
