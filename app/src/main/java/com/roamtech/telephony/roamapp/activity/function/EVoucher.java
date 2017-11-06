package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by caibinglong
 * on 2017/1/2.
 */

public class EVoucher extends HttpFunction {
    public EVoucher(Context context) {
        super(context);
    }

    public void getEVoucher(JSONObject jsonObject, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.EVOUCHER, jsonObject, hashCode, callback);
    }
}
