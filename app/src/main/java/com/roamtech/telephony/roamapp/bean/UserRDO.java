package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

/**
 * Created by admin03 on 2016/8/5.
 */
public class UserRDO implements Serializable {
    private String username;
    private String phone = "";
    private String email;
    private Integer status;
    private Integer type;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone == null ? "0" : phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

}
