package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

/**
 * Created by long
 * on 2016/10/21.
 * 首页 配置
 */

public class HomePager extends HttpFunction {
    public HomePager(Context context) {
        super(context);
    }

    /**
     * 获取 首页banner 配置
     *
     * @param jsonObject  json
     * @param hashCodeTag hashCode
     * @param callback    callback
     */
    public void getListBanner(JSONObject jsonObject, int hashCodeTag, HttpBusinessCallback callback) {
        postJsonRequest(Constant.GET_HOME_BANNER, jsonObject, hashCodeTag, callback);
    }

    /**
     * 应用升级检查
     *
     * @param jsonObject  json
     * @param hashCodeTag hashCode
     * @param callback    callback
     */
    public void validApp(JSONObject jsonObject, int hashCodeTag, HttpBusinessCallback callback) {
        postJsonRequest(Constant.UPGRADE_CHECK, jsonObject, hashCodeTag, callback);
    }
}
