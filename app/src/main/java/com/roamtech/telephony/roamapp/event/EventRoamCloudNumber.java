package com.roamtech.telephony.roamapp.event;

import com.roamtech.telephony.roamapp.bean.ServicePackage;
import com.roamtech.telephony.roamapp.bean.VoiceTalk;

/**络缦云号码页面接收数据
 * Created by xincheng on 6/8/2016.
 */
public class EventRoamCloudNumber extends EventBase {
    private VoiceTalk voiceTalk;
    //络缦专属好号信息
    private ServicePackage servicePackage;
    public EventRoamCloudNumber(ServicePackage servicePackage,VoiceTalk voiceTalk) {
        this.servicePackage=servicePackage;
        this.voiceTalk=voiceTalk;
    }

    public VoiceTalk getVoiceTalk() {
        return voiceTalk;
    }

    public void setVoiceTalk(VoiceTalk voiceTalk) {
        this.voiceTalk = voiceTalk;
    }

    public ServicePackage getServicePackage() {
        return servicePackage;
    }

    public void setServicePackage(ServicePackage servicePackage) {
        this.servicePackage = servicePackage;
    }
}