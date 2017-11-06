package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class Touch implements Serializable {
    private Long id;
    private String devid;
    @SerializedName("userid")
    private Long userId;
    private String phone;
    private String wifiSsid;
    private String wifiPassword;
    private String host;
    private String domain;
    private Integer devtype;
    private Integer sgroup;
    private Boolean verified;
    private String wan_proto;
    private String wan_type;
    private String wan_ip;
    private String lan_ssid;
    private String lan_password;

    public String getLan_password() {
        return lan_password;
    }

    public void setLan_password(String lan_password) {
        this.lan_password = lan_password;
    }

    public String getWan_proto() {
        return wan_proto;
    }

    public void setWan_proto(String wan_proto) {
        this.wan_proto = wan_proto;
    }

    public String getWan_type() {
        return wan_type;
    }

    public void setWan_type(String wan_type) {
        this.wan_type = wan_type;
    }

    public String getWan_ip() {
        return wan_ip;
    }

    public void setWan_ip(String wan_ip) {
        this.wan_ip = wan_ip;
    }

    public String getLan_ssid() {
        return lan_ssid;
    }

    public void setLan_ssid(String lan_ssid) {
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
        Touch entity = (Touch) other;
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
