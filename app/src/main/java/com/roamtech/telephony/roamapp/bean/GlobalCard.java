package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xincheng on 2016/7/19.
 * 全球上网卡
 */
public class GlobalCard implements Serializable {
    private static final long serialVersionUID = 6712905332391590808L;
    private int id;
    private String iccid;
    private int userid;
    private List<ServicePackage> servicePackages;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getIccid() {
        return iccid;
    }

    public void setIccid(String iccid) {
        this.iccid = iccid;
    }

    public List<ServicePackage> getServicePackages() {
        return servicePackages;
    }

    public void setServicePackages(List<ServicePackage> servicePackages) {
        this.servicePackages = servicePackages;
    }

    public void addServicePackage(ServicePackage servicePackage) {
        if (servicePackages == null) {
            servicePackages = new ArrayList<>();
        }
        servicePackages.add(servicePackage);
    }

    public void removeServicePackage(ServicePackage servicePackage) {
        if (servicePackages != null) {
            servicePackages.remove(servicePackage);
        }
    }

    public ServicePackage getFirstPackage() {
        if (servicePackages != null && servicePackages.size() > 0) {
            return servicePackages.get(0);
        }
        return null;
    }
}
