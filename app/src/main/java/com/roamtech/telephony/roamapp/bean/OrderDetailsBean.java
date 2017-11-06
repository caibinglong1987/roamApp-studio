package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by long
 * on 2016/9/28 14:35
 */

public class OrderDetailsBean implements Serializable {

    public String discount;
    public String effect_datetime;
    public String failure_datetime;
    public int id;
    public List<OdprdattrsBean> odprdattrs;
    public int orderid;
    public String phone;
    public int productid;
    public int quantity;
    public int quantity_per_unit;
    public String source;
    public int status;
    public String areaname;
    public String unit_price;
    public String userid;
    public Integer call_duration;
    //public List<SimidsBean> simids;

    @Override
    public String toString() {
        return "OrderDetailsBean{" +
                "discount='" + discount + '\'' +
                ", effect_datetime='" + effect_datetime + '\'' +
                ", failure_datetime='" + failure_datetime + '\'' +
                ", id=" + id +
                ", odprdattrs=" + odprdattrs +
                ", orderid=" + orderid +
                ", phone='" + phone + '\'' +
                ", productid=" + productid +
                ", quantity=" + quantity +
                ", quantity_per_unit=" + quantity_per_unit +
                ", source='" + source + '\'' +
                ", status=" + status +
                ", areaname='" + areaname + '\'' +
                ", unit_price='" + unit_price + '\'' +
                ", userid='" + userid + '\'' +
                ", simids=" + "" +
                ", call_duration=" + call_duration +
                '}';
    }
}
