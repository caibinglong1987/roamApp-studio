package com.roamtech.telephony.roamapp.base;

import com.roamtech.telephony.roamapp.bean.VoiceNumber;
import com.roamtech.telephony.roamapp.bean.VoiceTalk;

/**
 * Created by admin03 on 2016/8/25.
 */
public interface VoiceAvailableCallback {
    void handle(VoiceNumber vn, VoiceTalk vt);
}
