package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by long
 * on 2016/10/18.
 * 络漫宝 函数
 */

public class RoamBoxFunction extends HttpFunction {
    private final int timeout = 30;

    public RoamBoxFunction(Context context) {
        super(context);
    }

    /**
     * 获取我的络漫宝
     *
     * @param url         地址
     * @param jsonObject  参数
     * @param hashCodeTag hashCode
     * @param callback    回调
     */
    public void getRoamBoxList(String url, JSONObject jsonObject, int hashCodeTag, HttpBusinessCallback callback) {
        postJsonRequest(url, jsonObject, hashCodeTag, callback);
    }

    /**
     * 获取 token
     *
     * @param url
     * @param hashCodeTag hashCode
     * @param callback    回调
     */
    public void getRoamBoxAuthToken(String url, int hashCodeTag, HttpBusinessCallback callback) {
        if (url == null || url.length() == 0) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("roamuser");
        jsonArray.put("roampass123");
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", "login");
            jsonObject.put("params", jsonArray);
            jsonObject.put("id", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(url, jsonObject, hashCodeTag, timeout, callback);
    }

    /**
     * 获取当前络漫宝的所有配置信息
     *
     * @param url         地址
     * @param hashCodeTag hashCode
     * @param callback    回调
     */
    public void getRoamBoxConfigurationInfo(String url, int hashCodeTag, HttpBusinessCallback callback) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", "get_all");
            jsonObject.put("params", jsonArray);
            jsonObject.put("id", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(url, jsonObject, hashCodeTag, timeout, callback);
    }


    /**
     * 设置无线配置 LAN 口
     *
     * @param url         url
     * @param jsonArray   数组参数
     * @param hashCodeTag hashCode
     * @param callback    回调
     */
    public void setRoamBoxLan(String url, JSONArray jsonArray, int hashCodeTag, HttpBusinessCallback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", "set_lan");
            jsonObject.put("params", jsonArray);
            jsonObject.put("id", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(url, jsonObject, hashCodeTag, timeout, callback);
    }

    /**
     * 设置络漫宝配置 手机号码和 LAN 口
     *
     * @param url         url
     * @param jsonArray   数组参数
     * @param hashCodeTag hashCode
     * @param callback    回调
     */
    public void setRoamBoxPhoneLan(String url, JSONArray jsonArray, int hashCodeTag, HttpBusinessCallback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", "set_lanphone");
            jsonObject.put("params", jsonArray);
            jsonObject.put("id", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(url, jsonObject, hashCodeTag, timeout, callback);
    }

    /**
     * 设置络漫宝 WAN 口
     * 宽带拨号 静态IP 自动获取 无线中继
     *
     * @param url         url
     * @param jsonArray   数组参数
     * @param hashCodeTag hashCode
     * @param callback    回调
     */
    public void setRoamBoxWan(String url, JSONArray jsonArray, int hashCodeTag, HttpBusinessCallback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", "set_wan");
            jsonObject.put("params", jsonArray);
            jsonObject.put("id", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(url, jsonObject, hashCodeTag, timeout, callback);
    }

    /**
     * 设置络漫宝 手机号码
     *
     * @param url         url
     * @param jsonArray   数组参数
     * @param hashCodeTag hashCode
     * @param callback    回调
     */
    public void setRoamBoxPhone(String url, JSONArray jsonArray, int hashCodeTag, HttpBusinessCallback callback) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", "set_phone");
            jsonObject.put("params", jsonArray);
            jsonObject.put("id", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(url, jsonObject, hashCodeTag, timeout, callback);
    }

    /**
     * 获取络漫宝扫描的wifi
     * {"id":null,"jsonrpc":"2.0","result":[
     * {"signal":"26","ssid":"luomanchi01","channel": "1"},
     * {"signal":"24","ssid":"RoamBox-69cc4d6b5e2c6699","channel":"1"}]}
     *
     * @param url         url
     * @param userAgent   userAgent
     * @param hashCodeTag hashcode
     * @param callback    callback
     */
    public void getRoamBoxWifiList(String url, String userAgent, int hashCodeTag, HttpBusinessCallback callback) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", "get_wifis");
            jsonObject.put("params", jsonArray);
            jsonObject.put("id", "1");
            jsonObject.put(Constant.ROAM_VERSION_USER_AGENT, userAgent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(url, jsonObject, hashCodeTag, timeout, callback);
    }

    /**
     * 设置络漫宝 重启
     *
     * @param url         url
     * @param hashCodeTag hashCode
     * @param callback    回调
     */
    public void restartRoamBox(String url, int hashCodeTag, HttpBusinessCallback callback) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("jsonrpc", "2.0");
            jsonObject.put("method", "apply");
            jsonObject.put("params", jsonArray);
            jsonObject.put("id", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(url, jsonObject, hashCodeTag, timeout, callback);
    }

    /**
     * 绑定 络漫宝
     */
    public void bindRoamBox(final Context context, JSONObject json) {
        if (!SPreferencesTool.getInstance().getBooleanValue(context, SPreferencesTool.RoamBoxConfig.PROFILE_NAME, SPreferencesTool.RoamBoxConfig.IS_SUBMIT)) {
            try {
                json.put("devid", SPreferencesTool.getInstance().getStringValue(context, SPreferencesTool.RoamBoxConfig.PROFILE_NAME, SPreferencesTool.RoamBoxConfig.DEVID));
                json.put("phone", SPreferencesTool.getInstance().getStringValue(context, SPreferencesTool.RoamBoxConfig.PROFILE_NAME, SPreferencesTool.RoamBoxConfig.PHONE));
                OkHttpUtil.postJsonRequest(Constant.ROAM_BOX_BIND, json, hashCode(), new OKCallback<String>(new TypeToken<UCResponse<String>>() {
                }) {
                    @Override
                    public void onResponse(int statusCode, @Nullable UCResponse<String> ucResponse) {
                        if (isSucccess()) {
                            SPreferencesTool.getInstance().clearPreferences(context, SPreferencesTool.RoamBoxConfig.PROFILE_NAME);
                        }
                    }

                    @Override
                    public void onFailure(IOException e) {
                    }
                });
            } catch (JSONException ex) {
            }
        }
    }

    /**
     * 绑定络漫宝
     *
     * @param jsonObject json user
     * @param hashCode hashCode
     * @param callback callback
     */
    public void bindRoamBox(JSONObject jsonObject, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.ROAM_BOX_BIND, jsonObject, hashCode, timeout, callback);
    }
}
