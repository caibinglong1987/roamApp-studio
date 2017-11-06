package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

/**
 * Created by LONG
 * on 2016/10/28.
 * 用户设置操作
 */

public class Setting extends HttpFunction {
    public Setting(Context context) {
        super(context);
    }

    /**
     * 退出账号
     *
     * @param jsonObject json
     * @param hashCode hashCode
     * @param callback 回调
     */
    public void loginOut(JSONObject jsonObject, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.LOGOUT, jsonObject, hashCode, callback);
    }
}
