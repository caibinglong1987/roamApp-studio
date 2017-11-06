package com.roamtech.telephony.roamapp.activity.function;

import android.content.Context;

import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

/**
 * Created by caibinglong
 * on 2017/3/13.
 */

public class Blacklist extends HttpFunction {
    public Blacklist(Context context) {
        super(context);
    }

    public void getBlacklist(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.GET_BLACKLIST, params, hashCode, callback);
    }

    public void addBlacklist(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.ADD_BLACKLIST, params, hashCode, callback);
    }

    public void deleteBlacklist(JSONObject params, int hashCode, HttpBusinessCallback callback) {
        postJsonRequest(Constant.DELETE_BLACKLIST, params, hashCode, callback);
    }
}
