package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

/**
 * Created by admin03 on 2016/8/12.
 */
public class CredtripOpenRDO implements Serializable {
    String html;
    UserThirdParty credtripaccount;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public UserThirdParty getCredtripaccount() {
        return credtripaccount;
    }

    public void setCredtripaccount(UserThirdParty credtripaccount) {
        this.credtripaccount = credtripaccount;
    }
}
