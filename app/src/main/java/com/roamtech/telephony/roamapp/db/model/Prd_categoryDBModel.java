package com.roamtech.telephony.roamapp.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.roamtech.telephony.roamapp.db.DBConfig;

/**
 * Created by long
 * on 2016/9/27 20:54
 */
@DatabaseTable(tableName = DBConfig.TABLE_PRD_CATEGORY)
public class Prd_categoryDBModel {
    @DatabaseField(columnName = "id")
    public int id;
    @DatabaseField(columnName = "pid")
    public int pid;
    @DatabaseField(columnName = "path")
    public String path;
    @DatabaseField(columnName = "name")
    public String name;
    @DatabaseField(columnName = "subname")
    public String subname;
    @DatabaseField(columnName = "logo")
    public String logo;
    @DatabaseField(columnName = "description")
    public String description;
    @DatabaseField(columnName = "sort")
    public int sort;
    @DatabaseField(columnName = "createtime")
    public String createtime;
    @DatabaseField(columnName = "modifytime")
    public String modifytime;
}
