package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

/**
 * Created by xincheng on 2016/7/12.
 */
public class LoginInfo implements Serializable {
    //    {
//        "error_info": "成功",
//            "error_no": 0,
//            "result": {
//        "phone": "13967124998",
//                "status": 1,
//                "type": 0,
//                "username": "zhengsj"
//    },
//        "sessionid": "4e72be4e-1601-416c-8aa3-20e189c0396f",
//            "userid": 1
//    }
    public final static int NORMAL_USER = 0;
    public final static int CLERK_USER = 1;
    public final static int EMPLOYEE_USER = 2;
    public final static int OPERATOR_USER = 3;
    public final static int ADMIN_USER = 4;
    private String sessionId;
    private String userId;
    private String phone;
    private int status;
    private int type;
    private String username;
    private String user_photo;
    private String user_gender;
    private String user_address;
    private String roamPhone;

    public String getRoamPhone() {
        return roamPhone;
    }

    public void setRoamPhone(String roamPhone) {
        this.roamPhone = roamPhone;
    }

    public String getUserAddress() {
        return user_address;
    }

    public void setUseraddress(String user_address) {
        this.user_address = user_address;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }

    public String getUser_gender() {
        return user_gender;
    }

    public void setUser_gender(String user_gender) {
        this.user_gender = user_gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
