package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by caibinglong
 * on 2016/12/1.
 * 获取 通话记录 未接来电 消息记录
 */

public class CallMessage extends HttpFunction {
    public CallMessage(Context context) {
        super(context);
    }

    /**
     * 获取 分组通话记录
     *
     * @param params   params
     * @param hashCode hashCode
     * @param callback callback
     */
    public void getGroupAllCall(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        try {
            params.put("call_status", "all");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(Constant.GET_CALL_GROUP, params, hashCode, callback);
    }

    public void getGroupMessage(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.GET_MESSAGE_GROUP, params, hashCode, callback);
    }

    /**
     * 获取未接来电
     * 分页
     *
     * @param params
     * @param hashCode
     * @param callback
     */
    public void getGroupMissCallHistory(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        try {
            params.put("call_status", "missed");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        postJsonRequest(Constant.GET_CALL_GROUP, params, hashCode, callback);
    }

    /**
     * 获取 单组 通话记录
     *
     * @param params   params
     * @param hashCode hashCode
     * @param callback callback
     */
    public void getCallListByPhone(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.GET_CALL, params, hashCode, callback);
    }

    /**
     * 获取 单组 消息记录
     *
     * @param params   params
     * @param hashCode hashCode
     * @param callback callback
     */
    public void getMessageListByPhone(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.GET_ALL_MESSAGE, params, hashCode, callback);
    }


    /**
     * 更新操作
     *
     * @param params   params
     * @param hashCode hashCode
     * @param callback callback
     */
    public void updateCallMessage(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.DELETE_CALL_MESSAGE, params, hashCode, callback);
    }

    public void updateMessageByListCallId(JSONObject params, int hashCode, HttpBusinessCallback callback){
        postJsonRequest(Constant.DELETE_MESSAGE, params, hashCode, callback);
    }

    /**
     * 删除 多组会话接口
     *
     * @param params   params
     * @param hashCode hashCode
     * @param callback callback
     */
    public void deleteGroupMessage(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.DELETE_MESSAGE_GROUP, params, hashCode, callback);
    }

    /**
     * 删除 多组通话接口
     *
     * @param params   params
     * @param hashCode hashCode
     * @param callback callback
     */
    public void deleteGroupCall(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.DELETE_CALL_GROUP, params, hashCode, callback);
    }
}
