package com.roamtech.telephony.roamapp.bean;

/*
ChatMessage.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.roamtech.telephony.roamapp.db.DBConfig;

import java.io.Serializable;

/**
 * @author Sylvain Berfini
 */
@DatabaseTable(tableName = DBConfig.TABLE_CALL_DETAIL)
public class CallDetailRecordDBModel implements Serializable {
    @DatabaseField(columnName = "id", generatedId = true)
    public int Id;
    @DatabaseField(columnName = "caller")
    private String caller;
    @DatabaseField(columnName = "callee")
    private String callee;
    @DatabaseField(columnName = "timestamp")
    private long timestamp;
    @DatabaseField(columnName = "duration")
    private int duration;
    @DatabaseField(columnName = "direction")
    private int incoming; //1 是呼出  0是呼入
    @DatabaseField(columnName = "status")
    private int status;
    @DatabaseField(columnName = "logId")
    private long id;
    @DatabaseField(columnName = "quality")
    private int quality;
    @DatabaseField(columnName = "user_id")
    private String userId;
    @DatabaseField(columnName = "state")
    public int state = 0;
    @DatabaseField(columnName = "callid")
    public String callId;
    @DatabaseField(columnName = "login_user_id")
    public String loginUserId;
    @DatabaseField(columnName = "del_status")
    public int deleteStatus = 0; //1是 已经删除
    @DatabaseField(columnName = "parent")
    public int parent = 0; // 1是一级目录
    @DatabaseField(columnName = "call_status")
    public int callStatus = 0; // 2是未接 0 是已结或未打通记录
    @DatabaseField(columnName = "from")
    public String from = "all";
    @DatabaseField(columnName = "read")
    public int read;//1是 已读 0未读
    @DatabaseField(columnName = "show_number")
    public String showNumber;//列表显示号码

    public int getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(int callStatus) {
        this.callStatus = callStatus;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public CallDetailRecordDBModel() {
    }

    public CallDetailRecordDBModel(long id, String caller, String callee, long timestamp, int duration,
                                   int incoming, int state, int quality, String userId, String callId, String loginUserId) {
        super();
        this.id = id;
        this.caller = caller;
        this.callee = callee;
        this.timestamp = timestamp;
        this.duration = duration;
        this.incoming = incoming;
        this.state = state;
        this.quality = quality;
        this.userId = userId;
        this.callId = callId;
        this.loginUserId = loginUserId;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int isIncoming() {
        return incoming;
    }

    public void setIncoming(int incoming) {
        this.incoming = incoming;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String toString() {
        return this.id + " : " + this.caller + " " + this.callee + " " + this.timestamp + ", duration= " + this.duration + ", incoming= " + this.incoming + ", status = " + this.status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof CallDetailRecordDBModel)) {
            return false;
        }
        if (((CallDetailRecordDBModel) o).getId() == this.getId()) {
            return true;
        }
        return false;
    }
}
