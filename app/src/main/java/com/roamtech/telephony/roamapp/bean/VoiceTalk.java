package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;
/**
 * Created by admin03 on 2016/7/29.
 */
public class VoiceTalk implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long totaltime;//分钟
    private Long usedtime;
    private Long remaindertime;
    public Long getTotaltime() {
        return totaltime;
    }
    public void setTotaltime(Long totaltime) {
        this.totaltime = totaltime;
    }
    public Long getUsedtime() {
        return usedtime;
    }
    public void setUsedtime(Long usedtime) {
        this.usedtime = usedtime;
    }
    public Long getRemaindertime() {
        return remaindertime;
    }
    public void setRemaindertime(Long remaindertime) {
        this.remaindertime = remaindertime;
    }

    @Override
    public String toString() {
        return "VoiceTalk{" +
                "totaltime=" + totaltime +
                ", usedtime=" + usedtime +
                ", remaindertime=" + remaindertime +
                '}';
    }
}

