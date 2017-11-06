package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by admin03 on 2016/8/5.
 */
public class ServiceRDO implements Serializable {
    @SerializedName("servicepackages")
    private List<ServicePackage> servicePackages;
    @SerializedName("voiceavailable")
    private VoiceTalk voiceTalk;
    @SerializedName("voicenumber")
    private VoiceNumber voiceNumber;

    public List<ServicePackage> getServicePackages() {
        return servicePackages;
    }

    public void setServicePackages(List<ServicePackage> servicePackages) {
        this.servicePackages = servicePackages;
    }

    public VoiceTalk getVoiceTalk() {
        return voiceTalk;
    }

    public void setVoiceTalk(VoiceTalk voiceTalk) {
        this.voiceTalk = voiceTalk;
    }

    public VoiceNumber getVoiceNumber() {
        return voiceNumber;
    }

    public void setVoiceNumber(VoiceNumber voiceNumber) {
        this.voiceNumber = voiceNumber;
    }
}
