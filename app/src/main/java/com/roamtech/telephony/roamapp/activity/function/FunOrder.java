package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by long
 * on 2016/9/26 09:26
 * 订单信息
 */

public class FunOrder extends HttpFunction {

    public FunOrder(Context context) {
        super(context);
    }

    /**
     * 获取购买的订单列表
     *
     * @param params   请求参数
     * @param callback 回调函数
     */
    public void getOrderList(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.ORDER_LIST, params, hashCode, callback);
    }

    /**
     * 取消订单
     *
     * @param params   请求参数
     * @param callback 回调函数
     */
    public void cancelOrder(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.ORDER_CANCEL, params, hashCode, callback);
    }

    /**
     * 获取收货地址
     *
     * @param params   请求参数
     * @param callback 回调函数
     */
    public void getAddress(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.GET_ADDRESS, params, hashCode, callback);
    }

    /**
     * 确认收货
     *
     * @param params   请求参数
     * @param callback 回调函数
     */
    public void receivedOrder(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.ORDER_RECEIVED, params, hashCode, callback);
    }

    /**
     * 支付订单 参数
     *
     * @param params   请求参数
     * @param callback 回调函数
     */
    public void getPayParams(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.PAY_ORDER, params, hashCode, callback);
    }
}
