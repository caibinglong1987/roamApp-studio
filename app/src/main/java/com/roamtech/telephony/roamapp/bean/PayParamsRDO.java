package com.roamtech.telephony.roamapp.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by chenblue23 on 2016/8/23.
 */
public class PayParamsRDO implements Serializable {
    @SerializedName("payparams")
    private String payParams;
    private String html;

    public String getHtml() {
        return html;
    }
    public void setHtml(String html) {
        this.html = html;
    }

    public String getPayParams() {
        return payParams;
    }

    public void setPayParams(String payParams) {
        this.payParams = payParams;
    }
}
