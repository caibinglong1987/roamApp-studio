package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

/**
 * Created by long
 * on 2016/9/26 14:46
 * 支付方式model
 */

public class PaymentModel implements Serializable {
    public String code;
    public String description;
    public boolean enabled;
    public String fee;
    public int id;
    public String name;
    public int sort;
    public int terminaltype;
}
