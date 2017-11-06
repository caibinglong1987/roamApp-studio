package com.roamtech.telephony.roamapp.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.roamtech.telephony.roamapp.db.DBConfig;

import java.io.Serializable;

/**
 * Created by long
 * on 2016/10/23.
 * 首页数据
 */
@DatabaseTable(tableName = DBConfig.TABLE_HOME_PAGE)
public class HomePageDBModel implements Serializable{
    //content,description,id,location,logo,name,sort,type,url,url_title
    @DatabaseField(columnName = "id")
    public int id;
    @DatabaseField(columnName = "logo")
    public String logo;
    @DatabaseField(columnName = "content")
    public String content;
    @DatabaseField(columnName = "description")
    public String description;
    @DatabaseField(columnName = "location")
    public String location;
    @DatabaseField(columnName = "url_title")
    public String url_title;
    @DatabaseField(columnName = "name")
    public String name;
    @DatabaseField(columnName = "url")
    public String url;
    @DatabaseField(columnName = "sort")
    public int sort;
    @DatabaseField(columnName = "type")
    public int type;
}
