package com.roamtech.telephony.roamapp.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.roamtech.telephony.roamapp.db.DBConfig;

/**
 * Created by long
 * on 2016/9/27 09:31
 * 配送 model
 */
@DatabaseTable(tableName = DBConfig.TABLE_SHIPPING)
public class ShippingDBModel {
    @DatabaseField(columnName = "id")
    public int id;
    @DatabaseField(columnName = "code")
    public String code;
    @DatabaseField(columnName = "descriptio")
    public String descriptio;
    @DatabaseField(columnName = "enabled")
    public boolean enabled;
    @DatabaseField(columnName = "insure")
    public String insure;
    @DatabaseField(columnName = "name")
    public String name;
    @DatabaseField(columnName = "support_cod")
    public boolean support_cod;
}
