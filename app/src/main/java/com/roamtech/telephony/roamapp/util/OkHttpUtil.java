package com.roamtech.telephony.roamapp.util;

import com.roamtech.telephony.roamapp.base.OKCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

/**
 * OKhttp 帮助类
 * Created by xincheng on 2016/7/21.
 */
public class OkHttpUtil {
    private static String CER_ROAMTECH = "-----BEGIN CERTIFICATE-----\n" +
            "MIIHRzCCBi+gAwIBAgIHBr+YPq76tzANBgkqhkiG9w0BAQsFADCBjDELMAkGA1UE\n" +
            "BhMCSUwxFjAUBgNVBAoTDVN0YXJ0Q29tIEx0ZC4xKzApBgNVBAsTIlNlY3VyZSBE\n" +
            "aWdpdGFsIENlcnRpZmljYXRlIFNpZ25pbmcxODA2BgNVBAMTL1N0YXJ0Q29tIENs\n" +
            "YXNzIDEgUHJpbWFyeSBJbnRlcm1lZGlhdGUgU2VydmVyIENBMB4XDTE1MTIxNDE1\n" +
            "MDE1MFoXDTE2MTIxNTEwMTYxOFowUjELMAkGA1UEBhMCQ04xGjAYBgNVBAMTEXd3\n" +
            "dy5yb2FtLXRlY2guY29tMScwJQYJKoZIhvcNAQkBFhhwb3N0bWFzdGVyQHJvYW0t\n" +
            "dGVjaC5jb20wggIiMA0GCSqGSIb3DQEBAQUAA4ICDwAwggIKAoICAQD+6KryW7JA\n" +
            "0C8hTxGwXkb1NIbAL1QptnOCT3vgaqND+YLMke5KDnjO3YiRO2SWr0zJE2/+ugX0\n" +
            "1dhstNdB+Hp0SQOiInnQZRb5kWcZMYOE3LirD216YKAHmThhDnfXxl4QN8qHGy+1\n" +
            "2FzhOiCpdXHkwQB9vEHkMlD1kkGkVlxr3AttcHOoyG/7wlbr0VzAN7zNtQsCJ3Wm\n" +
            "Qg018eNyuIXNe5i6/47splBeko0nggpxkyw+Mf60NveyevVSaeY14von2aD83CUG\n" +
            "qxbeDoiAi8KIho+86kdmh8nff8yDRAMct5hOso9IcIzRVlSSI+AcY/Dba3xiTx96\n" +
            "an7YKJLbqqLcdwXN/krUBh/lq78gZO3PqznF5+xyTztFBjchFtWZC8lx580WcnlW\n" +
            "CSc3DlBnHPMbRD9Yy7XO19n482RuJwRdVtSi3hnW7Cle8vMaJFM5G9xCkvI8sOpU\n" +
            "5KDxnTJx3lctR8oXOhdgCRAdhH27EM95d68EUbFyZ4toCgOsh2iDgyOm40WKAbKo\n" +
            "fvretVqLt/qfDJEC+0qBRhHVHpWum2bn4uKyjSG9v/WKH2uTNAnHmUTOns2L0niU\n" +
            "4fiP1Q+90fStLi5+ZVLd2uGzO4d6a4FIvkk5vOgu9iGGPUnTDGcPO1r1OqU+C8+/\n" +
            "xn+lgneVLv+YjDgKniPJWm9TAhe+3vJF6QIDAQABo4IC5TCCAuEwCQYDVR0TBAIw\n" +
            "ADALBgNVHQ8EBAMCA6gwEwYDVR0lBAwwCgYIKwYBBQUHAwEwHQYDVR0OBBYEFPWx\n" +
            "ZaKO8+U6fVwY0sgsA16HdCZRMB8GA1UdIwQYMBaAFOtCNNCYsKuf9BtrCPfMZC7v\n" +
            "DixFMCsGA1UdEQQkMCKCEXd3dy5yb2FtLXRlY2guY29tgg1yb2FtLXRlY2guY29t\n" +
            "MIIBVgYDVR0gBIIBTTCCAUkwCAYGZ4EMAQIBMIIBOwYLKwYBBAGBtTcBAgMwggEq\n" +
            "MC4GCCsGAQUFBwIBFiJodHRwOi8vd3d3LnN0YXJ0c3NsLmNvbS9wb2xpY3kucGRm\n" +
            "MIH3BggrBgEFBQcCAjCB6jAnFiBTdGFydENvbSBDZXJ0aWZpY2F0aW9uIEF1dGhv\n" +
            "cml0eTADAgEBGoG+VGhpcyBjZXJ0aWZpY2F0ZSB3YXMgaXNzdWVkIGFjY29yZGlu\n" +
            "ZyB0byB0aGUgQ2xhc3MgMSBWYWxpZGF0aW9uIHJlcXVpcmVtZW50cyBvZiB0aGUg\n" +
            "U3RhcnRDb20gQ0EgcG9saWN5LCByZWxpYW5jZSBvbmx5IGZvciB0aGUgaW50ZW5k\n" +
            "ZWQgcHVycG9zZSBpbiBjb21wbGlhbmNlIG9mIHRoZSByZWx5aW5nIHBhcnR5IG9i\n" +
            "bGlnYXRpb25zLjA1BgNVHR8ELjAsMCqgKKAmhiRodHRwOi8vY3JsLnN0YXJ0c3Ns\n" +
            "LmNvbS9jcnQxLWNybC5jcmwwgY4GCCsGAQUFBwEBBIGBMH8wOQYIKwYBBQUHMAGG\n" +
            "LWh0dHA6Ly9vY3NwLnN0YXJ0c3NsLmNvbS9zdWIvY2xhc3MxL3NlcnZlci9jYTBC\n" +
            "BggrBgEFBQcwAoY2aHR0cDovL2FpYS5zdGFydHNzbC5jb20vY2VydHMvc3ViLmNs\n" +
            "YXNzMS5zZXJ2ZXIuY2EuY3J0MCMGA1UdEgQcMBqGGGh0dHA6Ly93d3cuc3RhcnRz\n" +
            "c2wuY29tLzANBgkqhkiG9w0BAQsFAAOCAQEAcrjKhjEZQcwWZPGOUwJUbVGLzesb\n" +
            "ljBUiwvAHw/KyC9OQR4RKKUcvIfOBe65m+UBY+HOBBMEH1rGDEDdrRqk3ZgclCPv\n" +
            "TxJFuDE6n+dWeK+nmTeLILquMIe5aCGPOlqVN9V+x/Flzym+Ah3t4qcIkXfndrjM\n" +
            "Hua92etcgz63+/vN731+UbRM5nGQhcfu/K/s+/Q+2+qPnYzT1eE3xDFLK6UgeK6Q\n" +
            "nB97RTYD5tBRHBdIg3DMBTXnUfh6zUWygmtbCy3JAtDrp4d6Aam6f6iGKinksG+q\n" +
            "F5OukWVk24J0ifh2cowHNfLBpXxJRSPQ/nxqX0475rtfloQObHZuBeyA9w==\n" +
            "-----END CERTIFICATE-----";
    private static final OkHttpClient mOkHttpClient;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    static {
        //创建OkHttpClient对象，用于稍后发起请求
       // HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(new InputStream[]{new Buffer()
                //.writeUtf8(CER_ROAMTECH)
              //  .inputStream()}, null, null);
        mOkHttpClient = new OkHttpClient.Builder()
                //.sslSocketFactory(sslParams.sSLSocketFactory)// sslParams.trustManager
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static void getRequest(String url, int hashCodeTag, OKCallback callback) {
        //根据请求URL创建一个Request对象
        Request request = new Request.Builder().tag(hashCodeTag).url(url).build();
        //根据Request对象发起Get异步Http请求，并添加请求回调
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * form表单提交
     */
    public static void postRequest(String url, JSONObject requestJson, int hashCodeTag, OKCallback callback) {
        String userAgent = requestJson.optString("versionName");
        requestJson.remove("versionName");
        //通过FormBody对象添加多个请求参数键值对
        RequestBody formBody = getRequestBody(requestJson);
        //通过请求地址和请求体构造Post请求对象Request
        Request request = new Request.Builder().tag(hashCodeTag).addHeader("User-Agent", userAgent).url(url).post(formBody).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }

    /**
     * json提交
     */
    public static void postJsonRequest(String url, JSONObject requestJson, int hashCodeTag, OKCallback callback) {
        String userAgent = requestJson.optString("versionName");
        requestJson.remove("versionName");
        //通过FormBody对象添加多个请求参数键值对
        RequestBody jsonBody = RequestBody.create(JSON, requestJson.toString());
        //通过请求地址和请求体构造Post请求对象Request
        Request request = new Request.Builder().tag(hashCodeTag).addHeader("User-Agent", userAgent).url(url).post(jsonBody).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }
    /**
     * json提交
     */
    /*public static void postJsonRequest(String url, JSONObject requestJson, int hashCodeTag, OKJsonCallback callback) {
        //通过FormBody对象添加多个请求参数键值对
        RequestBody jsonBody = RequestBody.create(JSON, requestJson.toString());
        //通过请求地址和请求体构造Post请求对象Request
        Request request = new Request.Builder().tag(hashCodeTag).url(url).post(jsonBody).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(callback);
    }*/

    /**
     * 生成Post请求的body
     *
     * @param requestJson 请求的json数据
     * @return 返回RequestBody
     */
    public static RequestBody getRequestBody(JSONObject requestJson) {
        FormBody.Builder builder = new FormBody.Builder();
        try {
            //先遍历整个 requestJson 对象
            for (Iterator<String> iter = requestJson.keys(); iter.hasNext(); ) {
                String key = iter.next();
                builder.add(key, requestJson.getString(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return builder.build();
    }

    //取消请求
    public static void cancel(Object tag) {
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }
}
