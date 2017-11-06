package com.roamtech.telephony.roamapp.base;

import com.roamtech.telephony.roamapp.bean.ServicePackage;
import com.roamtech.telephony.roamapp.bean.VoiceTalk;

import java.util.List;

/**
 * Created by admin03 on 2016/7/30.
 */
public interface ServicePackageCallback {
    void handle(List<ServicePackage> sps, VoiceTalk vt);
}
