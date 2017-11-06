package com.roamtech.telephony.roamapp.db.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.roamtech.telephony.roamapp.db.DBConfig;

import java.io.Serializable;

/**
 * Created by caibinglong
 * on 2017/3/8.
 */
@DatabaseTable(tableName = DBConfig.TABLE_BLACKLIST)
public class BlacklistDBModel implements Serializable{
    @DatabaseField(columnName = "id", generatedId = true)
    public int Id;
    @DatabaseField(columnName = "server_id")
    @SerializedName("id")
    public int serverId;
    @SerializedName("userid")
    @DatabaseField(columnName = "user_id")
    public String userId;
    @DatabaseField(columnName = "phone")
    public String phone;
    public boolean isSelect = false;
    public String area;
    public boolean isShow = false;
}
