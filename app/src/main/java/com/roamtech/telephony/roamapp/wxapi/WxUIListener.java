package com.roamtech.telephony.roamapp.wxapi;

/**
 *
 */
public interface WxUIListener {
    void callBackLogin(String code);
    void callbackBind(String code);
    void callbackShare(String str);
}