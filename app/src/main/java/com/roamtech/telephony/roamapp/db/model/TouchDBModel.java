package com.roamtech.telephony.roamapp.db.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.roamtech.telephony.roamapp.db.DBConfig;

import java.io.Serializable;

@DatabaseTable(tableName = DBConfig.TABLE_ROAM_BOX_TOUCH)
public class TouchDBModel implements Serializable {
    @DatabaseField(columnName = "id")
    private Long id;
    @DatabaseField(columnName = "devid")
    private String devid;
    @DatabaseField(columnName = "userId")
    @SerializedName("userid")
    private Long userId;
    @DatabaseField(columnName = "phone")
    private String phone = "";
    @DatabaseField(columnName = "wifiSsid")
    private String wifiSsid = "";
    @DatabaseField(columnName = "wifiPassword")
    private String wifiPassword = "";
    @DatabaseField(columnName = "host")
    private String host = "";
    @DatabaseField(columnName = "domain")
    private String domain = "";
    @DatabaseField(columnName = "devtype")
    private Integer devtype;
    @DatabaseField(columnName = "sgroup")
    private Integer sgroup;
    @DatabaseField(columnName = "verified")
    private Boolean verified = false;
    @DatabaseField(columnName = "wan_proto")
    private String wan_proto = "";
    @DatabaseField(columnName = "wan_type")
    private String wan_type = "";
    @DatabaseField(columnName = "wan_ip")
    private String wan_ip = "";
    @DatabaseField(columnName = "lan_ssid")
    private String lan_ssid = "";
    @DatabaseField(columnName = "lan_password")
    private String lan_password = "";
    private String operator = ""; //运营商
    private boolean boolNetwork = false;

    public boolean isBoolNetwork() {
        return boolNetwork;
    }

    public void setBoolNetwork(boolean boolNetwork) {
        this.boolNetwork = boolNetwork;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getLan_password() {
        return lan_password;
    }

    public void setLan_password(String lan_password) {
        this.lan_password = lan_password;
    }

    public String getWanProto() {
        return wan_proto;
    }

    public void setWanProto(String wan_proto) {
        this.wan_proto = wan_proto;
    }

    public String getWanType() {
        return wan_type;
    }

    public void setWanType(String wan_type) {
        this.wan_type = wan_type;
    }

    public String getWan_ip() {
        return wan_ip;
    }

    public void setWan_ip(String wan_ip) {
        this.wan_ip = wan_ip;
    }

    public String getLanSsid() {
        return lan_ssid;
    }

    public void setLanSsid(String lan_ssid) {
        this.lan_ssid = lan_ssid;
    }

    @Override
    public int hashCode() {
        return (devid == null) ? super.hashCode() : devid
                .hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || !getClass().equals(other.getClass())) {
            return false;
        }
        TouchDBModel entity = (TouchDBModel) other;
        if (devid == null && entity.devid == null) {
            return super.equals(entity);
        }
        if ((devid != null && entity.devid == null)
                || (devid == null && entity.devid != null)) {
            return false;
        }
        return devid.equals(entity.devid);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevid() {
        return devid;
    }

    public void setDevid(String devid) {
        this.devid = devid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWifiSsid() {
        return wifiSsid;
    }

    public void setWifiSsid(String wifiSsid) {
        this.wifiSsid = wifiSsid;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getDevtype() {
        return devtype;
    }

    public void setDevtype(Integer devtype) {
        this.devtype = devtype;
    }

    public Integer getSgroup() {
        return sgroup;
    }

    public void setSgroup(Integer sgroup) {
        this.sgroup = sgroup;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
