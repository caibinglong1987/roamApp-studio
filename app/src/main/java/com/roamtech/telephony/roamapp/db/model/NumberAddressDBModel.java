package com.roamtech.telephony.roamapp.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.roamtech.telephony.roamapp.db.DBConfig;

/**
 * Created by caibinglong
 * on 2017/2/14.
 */
@DatabaseTable(tableName = DBConfig.TABLE_NUMBER_ADDRESS)
public class NumberAddressDBModel {
    public int id;
    public String phone;
    public String province;
    public String city;
    @DatabaseField(columnName = "isp")
    public String operator;
    @DatabaseField(columnName = "city_code")
    public String cityCode;
}
