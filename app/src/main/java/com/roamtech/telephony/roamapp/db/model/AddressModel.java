package com.roamtech.telephony.roamapp.db.model;

import com.roamtech.telephony.roamapp.bean.CityBean;

import java.util.List;

/**
 * Created by long
 * on 2016/9/29 14:23
 */

public class AddressModel {
    public String address;
    public List<CityBean> cities;
    public String city; //城市id
    public String consignee; //收货人
    public String country;
    public String district; //区id
    public int id;
    public String mobile;
    public String province; //省份id
    public String userid;
    public String zipcode;
}
