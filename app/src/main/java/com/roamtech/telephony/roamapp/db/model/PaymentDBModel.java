package com.roamtech.telephony.roamapp.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.roamtech.telephony.roamapp.db.DBConfig;

/**
 * Created by long
 * on 2016/9/27 09:26
 */
@DatabaseTable(tableName = DBConfig.TABLE_PAYMENT)
public class PaymentDBModel {
    @DatabaseField(columnName = "id")
    public int id;
    @DatabaseField(columnName = "code")
    public String code;
    @DatabaseField(columnName = "description")
    public String description;
    @DatabaseField(columnName = "enabled")
    public boolean enabled;
    @DatabaseField(columnName = "fee")
    public String fee;
    @DatabaseField(columnName = "name")
    public String name;
    @DatabaseField(columnName = "sort")
    public int sort;
    @DatabaseField(columnName = "terminaltype")
    public int terminaltype;
}
