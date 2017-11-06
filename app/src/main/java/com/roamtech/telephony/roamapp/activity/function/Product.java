package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by long
 * on 2016/9/26 09:21
 * 产品 信息
 */

public class Product extends HttpFunction {

    public Product(Context context) {
        super(context);
    }

    /**
     * 获取商品列表
     *
     * @param params   请求参数
     * @param callback 回调函数
     */
    public void getProductList(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.PRODUCT_GET, params, hashCode, callback);
    }
}
