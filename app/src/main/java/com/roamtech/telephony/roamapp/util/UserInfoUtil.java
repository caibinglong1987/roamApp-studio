package com.roamtech.telephony.roamapp.util;

import android.content.Context;

import com.roamtech.telephony.roamapp.bean.LoginInfo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by caibinglong
 * on 2017/3/13.
 */

public class UserInfoUtil {
    private static UserInfoUtil instance;

    public static UserInfoUtil getInstance() {
        if (instance == null) {
            instance = new UserInfoUtil();
        }
        return instance;
    }

    /**
     * 获取用户信息
     *
     * @param context ctx
     * @return json
     */
    public JSONObject getJsonUser(Context context) {
        JSONObject jsonObject = new JSONObject();
        String userId = SPreferencesTool.getInstance().getStringValue(context, SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, "0");
        String sessionId = SPreferencesTool.getInstance().getStringValue(context, SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_sessionId, "0");
        if (userId.equals("0") || sessionId.equals("0")) {
            return null;
        }
        try {
            jsonObject.put("userid", userId);
            jsonObject.put("sessionid", sessionId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 清除未接电话数据
     *
     * @param context ctx
     */
    public void clearMissCallNumber(Context context) {
        SPreferencesTool.getInstance().putValue(context, SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_badge_miss_call_number, 0);
    }

    /**
     * 获取用户信息
     *
     * @param context ctx
     * @return LoginInfo
     */
    public LoginInfo getLoginInfo(Context context) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setPhone(SPreferencesTool.getInstance().getStringValue(context, SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_phone, "0"));
        loginInfo.setRoamPhone(SPreferencesTool.getInstance().getStringValue(context, SPreferencesTool.LOGIN_INFO, SPreferencesTool.roamPhone, "-1"));
        loginInfo.setUserId(SPreferencesTool.getInstance().getStringValue(context, SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, "0"));
        return null;
    }
}
