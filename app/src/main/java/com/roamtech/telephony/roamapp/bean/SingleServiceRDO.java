package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;


/**
 * Created by admin03 on 2016/8/5.
 */
public class SingleServiceRDO {
    @SerializedName("servicepackage")
    private ServicePackage servicePackage;

    public ServicePackage getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(ServicePackage servicePackage) {
        this.servicePackage = servicePackage;
    }
}
