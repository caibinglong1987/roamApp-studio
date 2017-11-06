package com.roamtech.telephony.roamapp.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.roamtech.telephony.roamapp.db.DBConfig;

/**
 * Created by long
 * on 2016/9/29 14:47
 */
@DatabaseTable(tableName = DBConfig.TABLE_USER_ADDRESS)
public class AddressDBModel {
    @DatabaseField(columnName = "address")
    public String address; //具体地址
    @DatabaseField(columnName = "city")
    public String city; //城市
    @DatabaseField(columnName = "consignee")
    public String consignee; //收货人
    @DatabaseField(columnName = "country")
    public String country;
    @DatabaseField(columnName = "district")
    public String district; //区
    @DatabaseField(columnName = "id")
    public int id;
    @DatabaseField(columnName = "mobile")
    public String mobile;
    @DatabaseField(columnName = "province")
    public String province; //省份
    @DatabaseField(columnName = "userid")
    public String userid;
    @DatabaseField(columnName = "zipcode")
    public String zipcode;
}
