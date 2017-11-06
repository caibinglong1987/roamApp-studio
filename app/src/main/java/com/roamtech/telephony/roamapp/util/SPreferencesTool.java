package com.roamtech.telephony.roamapp.util;

import android.content.Context;

import com.roamtech.telephony.roamapp.bean.Bell;
import com.roamtech.telephony.roamapp.bean.PaymentModel;
import com.roamtech.telephony.roamapp.bean.ProductModel;
import com.will.common.tool.io.SharedPreferencesTool;

import java.util.List;

/**
 * User: cbl
 * Date: 2016/3/28
 * Time: 17:40
 */
public class SPreferencesTool {
    private static SPreferencesTool mInstance;
    public final static String LOGIN_INFO = "LoginInfo";
    public final static String isFirstOpen = "isFirstOpen";
    public final static String login_userId = "userid";
    public final static String login_sessionId = "sessionid";
    public final static String login_userName = "username";
    public final static String login_password = "password";
    public final static String login_phone = "phone";
    public final static String login_type = "type";
    public final static String login_status = "status";
    public final static String roamPhone = "roamPhone";
    public final static String login_gender = "gender";
    public final static String login_address = "address";
    public final static String login_headUrl = "headUrl";
    public final static String login_caller_phone = "callerPhone";
    public final static String login_caller_type = "callerType"; //2是络漫宝 1是专属号
    public final static String login_badge_miss_call_number = "badge_miss_call_number";
    public final static String login_badge_message_number = "badge_message_number";
    public final static String login_domain = "sip_domain";

    public final static String every_day_update = "every_day_update"; //每天是否更新配置

    public synchronized static SPreferencesTool getInstance() {
        if (mInstance == null) {
            mInstance = new SPreferencesTool();
        }
        return mInstance;
    }

    public SPreferencesTool() {
    }

    public static class AppReleasedConfig {
        public static final String PROFILE_NAME = "AppReleased";
        public static final String VERSION_NAME = "version_name";
        public static final String VERSION = "version";
        public static final String DESCRIPTION = "description";
        public static final String URL = "url";
        public static final String UPGRADE_TIME = "upgrade_time";
        public static final String RELEASE_TIME = "release_time";
    }

    //升级 安装包

    public static class UpgradeCheck {
        static final String PROFILE_NAME = "UpgradeCheck";
        static final String PREFERENCES_UP_APK_INFO_isUp = "isUp";
        static final String PREFERENCES_UP_APK_INFO_VERSION = "version";
        static final String PREFERENCES_UP_APK_INFO_MESSAGE = "message";
        static final String PREFERENCES_UP_APK_INFO_URL = "upLoadApkUrl";
    }

    public static class RoamBoxConfig {
        public static String PROFILE_NAME = "RoamBox";
        public static String DEVID = "devId";
        public static String PHONE = "phone";
        public static String IS_SUBMIT = "isSubmit";
    }

    public static class BellConfig {
        public static String PROFILE_NAME = "Bell";
        public static String BELL = "bell";
    }

    public void saveRoamBoxConfig(Context context, String devId, String phone) {
        SPreferencesTool.getInstance().putValue(context, RoamBoxConfig.PROFILE_NAME, RoamBoxConfig.DEVID, devId);
        SPreferencesTool.getInstance().putValue(context, RoamBoxConfig.PROFILE_NAME, RoamBoxConfig.PHONE, phone);
        SPreferencesTool.getInstance().putValue(context, RoamBoxConfig.PROFILE_NAME, RoamBoxConfig.IS_SUBMIT, false);
    }

    public void saveUpLoadApk(Context context, Boolean isUp, String VersionCode, String message, String URl) {
        SPreferencesTool.getInstance().putValue(context, UpgradeCheck.PROFILE_NAME, UpgradeCheck.PREFERENCES_UP_APK_INFO_isUp, isUp);
        SPreferencesTool.getInstance().putValue(context, UpgradeCheck.PROFILE_NAME, UpgradeCheck.PREFERENCES_UP_APK_INFO_VERSION, VersionCode);
        SPreferencesTool.getInstance().putValue(context, UpgradeCheck.PROFILE_NAME, UpgradeCheck.PREFERENCES_UP_APK_INFO_MESSAGE, message);
        SPreferencesTool.getInstance().putValue(context, UpgradeCheck.PROFILE_NAME, UpgradeCheck.PREFERENCES_UP_APK_INFO_URL, URl);
    }

    public void saveBell(Context context, Bell bell) {
        SharedPreferencesTool.saveObject(context, BellConfig.PROFILE_NAME, BellConfig.BELL, bell);
    }

    public Bell getBell(Context context) {
        return (Bell) SharedPreferencesTool.readObject(context, BellConfig.PROFILE_NAME, BellConfig.BELL);
    }

    public void clearPreferences(Context ctx, String pro_name) {
        SharedPreferencesTool.clearPreferences(ctx, pro_name);
    }

    public void putValue(Context ctx, String pro_name, String key, Object value) {
        SharedPreferencesTool.putValue(ctx, pro_name, key, value);
    }

    public int getIntValue(Context ctx, String pro_name, String key, int defVal) {
        return SharedPreferencesTool.getIntValue(ctx, pro_name, key, defVal);
    }

    public String getStringValue(Context ctx, String pro_name, String key) {
        return SharedPreferencesTool.getStringValue(ctx, pro_name, key);
    }

    public String getStringValue(Context ctx, String pro_name, String key, String value) {
        return SharedPreferencesTool.getStringValue(ctx, pro_name, key, value);
    }

    public boolean getBooleanValue(Context ctx, String pro_name, String key) {
        return SharedPreferencesTool.getBooleanValue(ctx, pro_name, key);
    }

    public boolean getBooleanValue(Context ctx, String pro_name, String key, boolean defVal) {
        return SharedPreferencesTool.getBooleanValue(ctx, pro_name, key, defVal);
    }

    public long getLongValue(Context ctx, String pro_name, String key, long defVal) {
        return SharedPreferencesTool.getLongValue(ctx, pro_name, key, defVal);
    }
}
