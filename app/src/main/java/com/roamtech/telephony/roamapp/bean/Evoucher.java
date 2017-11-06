package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xincheng on 2016/7/26.
 */
public class Evoucher implements Serializable, Cloneable {
    private static final long serialVersionUID = 76981136592586767L;
    public static final String STARTTIME_TAG="effect_datetime";
    public static final String ENDTIME_TAG="failure_datetime";
    public static final String USEDTIME_TAG = "used_datetime";
    private int id;
    private int userid;
    @SerializedName(USEDTIME_TAG)
    private Date usedTime;
    @SerializedName(STARTTIME_TAG)
    private Date startTime;
    @SerializedName(ENDTIME_TAG)
    private Date endTime;
    private String logo;
    private String location;
    private String description;
    private Long evoucher_sn;
    private Long evoucherid;
    private String image;
    private double money;
    private String name;
    private Long orderid;
    private boolean repeatable;
    private String areaName;
    private boolean showdetail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEvoucher_sn() {
        return evoucher_sn;
    }

    public void setEvoucher_sn(Long evoucher_sn) {
        this.evoucher_sn = evoucher_sn;
    }

    public Long getEvoucherid() {
        return evoucherid;
    }

    public void setEvoucherid(Long evoucherid) {
        this.evoucherid = evoucherid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getUsedTime() {
        return usedTime;
    }

    public void setUsedTime(Date usedTime) {
        this.usedTime = usedTime;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public boolean isShowdetail() {
        return showdetail;
    }

    public void setShowdetail(boolean showdetail) {
        this.showdetail = showdetail;
    }

    @Override
    public Object clone() {
        Evoucher evoucher = null;
        try {
            evoucher = (Evoucher) super.clone();
            if (usedTime != null) {
                evoucher.usedTime = (Date) usedTime.clone();
            }
            if (startTime != null) {
                evoucher.startTime = (Date) startTime.clone();
            }
            if (endTime != null) {
                evoucher.endTime = (Date) endTime.clone();
            }
        } catch (CloneNotSupportedException e) {

        }
        return evoucher;
    }
}
