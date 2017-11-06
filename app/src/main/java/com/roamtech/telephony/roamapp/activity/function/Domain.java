package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

/**
 * Created by caibinglong
 * on 2017/2/28.
 */

public class Domain extends HttpFunction {
    public Domain(Context context) {
        super(context);
    }

    public void getDomain(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.GET_DOMAIN, params, hashCode, callback);
    }
}
