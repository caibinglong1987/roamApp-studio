package com.roamtech.telephony.roamapp.db.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.roamtech.telephony.roamapp.db.DBConfig;

/**
 * Created by long
 * on 2016/9/27 11:42
 */
@DatabaseTable(tableName = DBConfig.TABLE_CHAT)
public class ChatDBModel {
    @DatabaseField(columnName = "id", generatedId = true)
    public int Id;
    @DatabaseField(columnName = "logId")
    @SerializedName("id")
    public long logId;
    @DatabaseField(columnName = "callid")
    public String callId;
    @DatabaseField(columnName = "fromContact")
    public String fromContact;
    @DatabaseField(columnName = "toContact")
    public String toContact;
    @DatabaseField(columnName = "direction")
    public int direction;
    @DatabaseField(columnName = "message")
    public String message;
    @DatabaseField(columnName = "image")
    public String image;
    @DatabaseField(columnName = "timestamp")
    public long timestamp;
    @DatabaseField(columnName = "read")
    public int read;//1是 已读 0未读
    @DatabaseField(columnName = "status")
    public int status;
    @DatabaseField(columnName = "user_id")
    public String userId;
    @DatabaseField(columnName = "message_status")
    public int messageStatus; //消息状态  1 发送成功  0 发送失败
    @DatabaseField(columnName = "message_type")
    public int messageType = 0; //1是系统消息
    @DatabaseField(columnName = "parent")
    public int parent = 0; // 1是一级目录
    @DatabaseField(columnName = "login_user_id")
    public String loginUserId;
    @DatabaseField(columnName = "del_status")
    public int deleteStatus = 0; //1是 已经删除
    @DatabaseField(columnName = "unread_num")
    public int unreadNumber = 0; //未读数目
}
