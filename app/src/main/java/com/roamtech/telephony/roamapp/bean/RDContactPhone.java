package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

/**
 * Created by xincheng
 * on 6/22/2016.
 */
public class RDContactPhone implements Serializable {
    public static final int TYPE_HOME = 1;
    public static final int TYPE_MOBILE = 2;
    public static final int TYPE_WORK = 3;

    private static final long serialVersionUID = -4916355296090759733L;
    private long id;
    private String number;
    private int type;/*1 -- home, 2 -- mobile, 3 -- work, 4 -- fax*/

    public RDContactPhone(long id, int type, String number) {
        this.id = id;
        this.type = type;
        this.number = number;
    }

    public RDContactPhone() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "id:" + id + " number:" + number + " type:" + type ;
    }
}
