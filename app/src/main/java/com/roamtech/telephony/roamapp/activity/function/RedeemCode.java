package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by chenblue23 on 2016/10/26.
 */

public class RedeemCode extends HttpFunction {

    public RedeemCode(Context context) {
        super(context);
    }

    public void exchange(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.REDEEM_CODE, params, hashCode, callback);
    }
}
