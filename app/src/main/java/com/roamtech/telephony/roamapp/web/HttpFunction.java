package com.roamtech.telephony.roamapp.web;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.roamtech.telephony.roamapp.util.ErrorHelper;
import com.roamtech.telephony.roamapp.web.jsonhandle.JsonWebBusinessHandler;
import com.will.common.tool.PackageTool;
import com.will.web.HttpManager;
import com.will.web.handle.HttpBusinessCallback;
import com.will.web.okhttp3.OkHttpManager;

import org.json.JSONObject;

import java.util.Map;

import static com.will.common.tool.PackageTool.getPackageInfo;


/**
 * Created by long
 * on 16-3-3.
 */
public class HttpFunction {

    public static int SUC_OK = 0;
    public static int SESSION_TIMEOUT = 1101; //会话超时
    public static final int SOURCES_ANDROID = 2;

    public static final int HEAD_1 = 1;

    public static final int TRUE = 1;
    public static final int FALSE = 0;

    private HttpManager mHttpManager;
    protected Context mContext;

    public HttpFunction(Context context) {
        mContext = context;
        String userAgent = "RoamPhone/" + PackageTool.getVersionName(mContext);
        mHttpManager = new OkHttpManager(userAgent);
    }

    public void httpGet(String url, Map<String, String> params, HttpBusinessCallback callback) {
        JsonWebBusinessHandler jsonWebBusinessHandler = new JsonWebBusinessHandler(mContext);
        callback.setWebHandler(jsonWebBusinessHandler);
        mHttpManager.Request(HttpManager.Method.GET, url, params, callback);
    }

    public void httpPost(String url, Map<String, String> params, HttpBusinessCallback callback) {
//        JsonWebBusinessHandler jsonWebBusinessHandler = new JsonWebBusinessHandler(mContext);
//        callback.setWebHandler(jsonWebBusinessHandler);
        mHttpManager.Request(HttpManager.Method.POST, url, params, callback);
    }

    /**
     * json提交
     */
    public void postJsonRequest(String url, JSONObject requestJson, int hashCodeTag, HttpBusinessCallback callback) {
        //通过FormBody对象添加多个请求参数键值对
//        JsonWebBusinessHandler jsonWebBusinessHandler = new JsonWebBusinessHandler(mContext);
//        callback.setWebHandler(jsonWebBusinessHandler);
        mHttpManager.postJsonRequest(url, requestJson, hashCodeTag, callback);
    }

    /**
     * json提交
     */
    public void postJsonRequest(String url, JSONObject requestJson, int hashCodeTag, int timeout, HttpBusinessCallback callback) {
        //通过FormBody对象添加多个请求参数键值对
//        JsonWebBusinessHandler jsonWebBusinessHandler = new JsonWebBusinessHandler(mContext);
//        callback.setWebHandler(jsonWebBusinessHandler);
        mHttpManager.postJsonRequest(url, requestJson, hashCodeTag, timeout, callback);
    }

    public static boolean isSuc(String code) {
        return (SUC_OK + "").equals(code);
    }

    public static boolean isSuccess(int code) {
        return SUC_OK == code;
    }

    /**
     * 会话失效，重新登录
     * code 1101
     *
     * @return
     */
    public static boolean isSessionTimeout(int code) {
        return code == SESSION_TIMEOUT;
    }


    public String getHttpError(Context context, String code) {
        return ErrorHelper.getErrorHint(context, code);
    }

    /**
     * @param context
     * @param code    提供默认的错误处理
     */
    public void defaultHandleError(Context context, String code) {
        //ToastUtils.showToast(context,getHttpError(context,code));
    }

    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            String versionName = String.valueOf(packageInfo.versionName);
            return versionName;
        }
        return null;
    }
}
