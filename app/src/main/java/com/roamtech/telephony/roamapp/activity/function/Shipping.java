package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by long
 * on 2016/9/26 09:25
 * 配送信息
 */

public class Shipping extends HttpFunction {
    public Shipping(Context context) {
        super(context);
    }

    /**
     * 获取配送方式 列表
     *
     * @param params   请求参数
     * @param callback 回调函数
     */
    public void getShipList(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.SHOPPING_LIST, params, hashCode, callback);
    }
}
