package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by long
 * on 2016/9/28 15:23
 */

public class OrderBean implements Serializable {
    public String createtime;
    public String discount;
    public Long id;
    public int order_status;
    public int pay_status;
    public String payable_amount;
    public String price;
    public String userid;
    public int shipping_status;
    public int payid;
    public String paytime;
    public String ship_address;
    public String shipping_fee;
    public int shipping_id;
    public String payvoucher;
    public String paidmoney;
    public List<OrderDetailsBean> orderdetails;
}
