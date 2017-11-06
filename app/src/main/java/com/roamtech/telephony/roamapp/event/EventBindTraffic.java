package com.roamtech.telephony.roamapp.event;

import com.roamtech.telephony.roamapp.bean.ServicePackage;

/**绑卡成功向前一个页面发送通知
 * Created by xincheng on 6/8/2016.
 */
public class EventBindTraffic extends EventBase {
    private ServicePackage servicePackage;
    public EventBindTraffic(ServicePackage servicePackage) {
        this.servicePackage=servicePackage;
    }

    public ServicePackage getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(ServicePackage servicePackage) {
        this.servicePackage = servicePackage;
    }
}