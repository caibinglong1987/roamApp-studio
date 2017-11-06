package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * Created by admin03 on 2016/7/29.
 */
public class ServicePackage implements Serializable,Comparable<ServicePackage> {
    private static final long serialVersionUID = -8316373398571711858L;
    public static final String STARTTIME_TAG="effect_datetime";
    public static final String ENDTIME_TAG="failure_datetime";
    public static final String SIMID_TAG="simid";
    private String name;
    private String logo;
    private Integer type;//1 全球上网卡，2 流量套餐， 3 语音套餐
    @SerializedName("productid")
    private Long productId;
    @SerializedName("orderid")
    private Long orderId;
    @SerializedName("orderdetailid")
    private Long orderdetailId;
    @SerializedName(STARTTIME_TAG)
    private Date startTime;
    @SerializedName(ENDTIME_TAG)
    private Date endTime;
    private Double remainder;
    private String areaname;
    private String simid;
    private String phone;
    private Integer quantity;
    private Set<String> simids;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getLogo() {
        return logo;
    }
    public void setLogo(String logo) {
        this.logo = logo;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public Long getProductId() {
        return productId;
    }
    public void setProductId(Long productId) {
        this.productId = productId;
    }
    public Long getOrderId() {
        return orderId;
    }
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    public Long getOrderdetailId() {
        return orderdetailId;
    }
    public void setOrderdetailId(Long orderdetailId) {
        this.orderdetailId = orderdetailId;
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
    public Double getRemainder() {
        return remainder;
    }
    public void setRemainder(Double remainder) {
        this.remainder = remainder;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    public String getAreaname() {
        return areaname;
    }
    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }
    public String getSimid() {
        return simid;
    }
    public void setSimid(String simid) {
        this.simid = simid;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public Integer getQuantity() {
        return quantity;
    }
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    public Set<String> getSimids() {
        return simids;
    }
    public void setSimids(Set<String> simids) {
        this.simids = simids;
    }

    @Override
    public String toString() {
        return "ServicePackage{" +
                "name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                ", type=" + type +
                ", productId=" + productId +
                ", orderId=" + orderId +
                ", orderdetailId=" + orderdetailId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", remainder=" + remainder +
                ", areaname='" + areaname + '\'' +
                ", simid='" + simid + '\'' +
                ", phone='" + phone + '\'' +
                ", quantity=" + quantity +
                ", Simids=" + simids +
                '}';
    }

    @Override
    public int compareTo(ServicePackage another) {
        if(getStartTime()==null) {
            if(another.getStartTime() == null) {
                return 0;
            } else {
                return -1;
            }
        }
        if(another.getStartTime() == null) {
            return 1;
        }
        return getStartTime().compareTo(another.getStartTime());
    }
}

