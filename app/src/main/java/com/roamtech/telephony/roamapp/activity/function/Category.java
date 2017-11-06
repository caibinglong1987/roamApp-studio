package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by long
 * on 2016/9/27 21:00
 */

public class Category extends HttpFunction {
    public Category(Context context) {
        super(context);
    }

    /**
     * 获取 产品类目表
     *
     * @param params   请求参数
     * @param callback 回调
     */
    public void getCategoryList(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.PRD_CATEGORY, params, hashCode, callback);
    }
}
