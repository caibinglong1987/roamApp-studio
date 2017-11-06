package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

/**
 * Created by caibinglong
 * on 2016/12/13.
 */

public class CallerNumberBean implements Serializable {
    public String phoneNumber = "";
    public boolean checked = false;
    public int phoneType = 0; //1专属 2 络漫宝
}
