package com.roamtech.telephony.roamapp.jsbridge;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/***
 * 核心类
 * T为当前注册方法的接收对象
 * 1、T中注册的方法必须是public
 * 2、T中注册的方法必须要@JsInterface
 * 3、参数类型必须是基础数据类型 JSONOBJECT,JsCallback
 *
 * **/
public class JsCallJava{
    private final static String TAG = "JsCallJava";
    private final static String RETURN_RESULT_FORMAT = "{\"code\": %d, \"result\": %s}";
    private HashMap<String, Method> mMethodsMap;
    private String mInjectedName;
    private String mPreloadInterfaceJS;
    private Gson mGson;
    //当前对象
    private Object mInstance;

    public JsCallJava(String injectedName, Object instance) {
        try {
            if (TextUtils.isEmpty(injectedName)) {
                throw new Exception("injected name can not be null");
            }
            mInstance = instance;
            mInjectedName = injectedName;
            mMethodsMap = new HashMap<String, Method>();
            //获取自身声明的所有方法（包括public private protected）， getMethods会获得所有继承与非继承的方法
            Method[] methods = instance.getClass().getMethods();//getDeclaredMethods();
            StringBuilder sb = new StringBuilder("javascript:(function(b){console.log(\"");
            sb.append(mInjectedName);
            sb.append(" initialization begin\");var a={queue:[],callback:function(){var d=Array.prototype.slice.call(arguments,0);var c=d.shift();var e=d.shift();this.queue[c].apply(this,d);if(!e){delete this.queue[c]}}};");
            for (Method method : methods) {
                String sign;
                // Log.e("print",method.getName()+"==isPublic:"+);
                if (!Modifier.isPublic(method.getModifiers()) || (sign = genJavaMethodSign(method)) == null) {
                    continue;
                }
                mMethodsMap.put(sign, method);
                sb.append(String.format("a.%s=", method.getName()));
            }

            sb.append("function(){var f=Array.prototype.slice.call(arguments,0);if(f.length<1){throw\"");
            sb.append(mInjectedName);
            sb.append(" call error, message:miss method name\"}var e=[];for(var h=1;h<f.length;h++){var c=f[h];var j=typeof c;e[e.length]=j;if(j==\"function\"){var d=a.queue.length;a.queue[d]=c;f[h]=d}}var g=JSON.parse(prompt(JSON.stringify({method:f.shift(),types:e,args:f})));if(g.code!=200){throw\"");
            sb.append(mInjectedName);
            sb.append(" call error, code:\"+g.code+\", message:\"+g.result}return g.result};Object.getOwnPropertyNames(a).forEach(function(d){var c=a[d];if(typeof c===\"function\"&&d!==\"callback\"){a[d]=function(){return c.apply(a,[d].concat(Array.prototype.slice.call(arguments,0)))}}});b.");
            sb.append(mInjectedName);
            sb.append("=a;console.log(\"");
            sb.append(mInjectedName);
            sb.append(" initialization end\");");
            sb.append(";try{" + "(function(doc){ var readyEvent = doc.createEvent('Event');readyEvent.initEvent(\"nativeready\", true, true); doc.dispatchEvent(readyEvent); })(b.document)}catch(ex){}");
            sb.append("})(window)");
          //  sb.append(mInjectedName);

            mPreloadInterfaceJS = sb.toString();
        } catch (Exception e) {
            Log.e(TAG, "init js error:" + e.getMessage());
        }
    }

    private String genJavaMethodSign(Method method) {
        String sign = method.getName();
        Class[] argsTypes = method.getParameterTypes();
        int len = argsTypes.length;
      //Log.e("print", len + "===" + method.toString());
        //包含该注解的方法才可以使用的
        JsInterface jsMeta = method.getAnnotation(JsInterface.class);
        if (jsMeta == null) {
            Log.w(TAG, "method(" + sign + ") must have annotation @JsInterface ,or will be pass");
            return null;
        }
        //Log.e("print","meta:"+meta);

        // Log.e("print","isSameClass"+(argsTypes[0]==mInstance.getClass().getSuperclass()));
//        if (len < 1 || argsTypes[0] != mInstance.getClass()) {
//            Log.w(TAG, "method(" + sign + ") must use webview to be first parameter, will be pass");
//          //  return null;
//        }
//        if (len < 1 || argsTypes[0] != mInstance.getClass()) {
//            Log.w(TAG, "method(" + sign + ") must use webview to be first parameter, will be pass");
//            //  return null;
//        }
        for (int k = 0; k < len; k++) {
            Class cls = argsTypes[k];
            if (cls == String.class) {
                sign += "_S";
            } else if (cls == int.class ||
                    cls == long.class ||
                    cls == Long.class ||
                    cls == Integer.class ||
                    cls == float.class ||
                    cls == Float.class ||
                    cls == double.class ||
                    cls == Double.class) {
                sign += "_N";
            } else if (cls == boolean.class ||
                    cls == Boolean.class) {
                sign += "_B";
            } else if (cls == JSONObject.class) {
                sign += "_O";
            } else if (cls == JsCallback.class) {
                sign += "_F";
            } else {
                sign += "_P";
            }
        }
        return sign;
    }

    public String getPreloadInterfaceJS() {
        return mPreloadInterfaceJS;
    }

    public String call(WebView webView, String jsonStr) {
        if (!TextUtils.isEmpty(jsonStr)) {
            try {
                JSONObject callJson = new JSONObject(jsonStr);
                String methodName = callJson.getString("method");
                JSONArray argsTypes = callJson.getJSONArray("types");
                JSONArray argsVals = callJson.getJSONArray("args");
                //Log.e("print",methodName+"="+argsTypes+"="+argsVals);

                String sign = methodName;
                int len = argsTypes.length();
                Object[] values = new Object[len];
                int numIndex = 0;
                String currType;
                for (int k = 0; k < len; k++) {
                    currType = argsTypes.optString(k);
                    if ("string".equals(currType)) {
                        sign += "_S";
                        values[k] = argsVals.isNull(k) ? null : argsVals.getString(k);
                    } else if ("number".equals(currType)) {
                        sign += "_N";
                        //为了标记for循环的判断 如果第一位就是number类型排除0
                        numIndex = numIndex * 10 + k + 1;
                        Log.e("print", "numIndex:" + numIndex);
                    } else if ("boolean".equals(currType)) {
                        sign += "_B";
                        values[k] = argsVals.getBoolean(k);
                    } else if ("object".equals(currType)) {
                        sign += "_O";
                        values[k] = argsVals.isNull(k) ? null : argsVals.getJSONObject(k);
                    } else if ("function".equals(currType)) {
                        sign += "_F";
                        values[k] = new JsCallback(webView, mInjectedName, argsVals.getInt(k));
                    } else {
                        sign += "_P";
                    }
                }

                Method currMethod = mMethodsMap.get(sign);
                // 方法匹配失败
                if (currMethod == null) {
                    return getReturn(jsonStr, 500, "not found method(" + sign + ") with valid parameters");
                }
                // 数字类型细分匹配
                if (numIndex > 0) {
                    Class[] methodTypes = currMethod.getParameterTypes();
                    int currIndex;
                    Class currCls;
                    while (numIndex > 0) {
                        currIndex = (numIndex - numIndex / 10 * 10) - 1;
                        currCls = methodTypes[currIndex];
                        if (currCls == int.class||currCls == Integer.class) {
                            values[currIndex] = argsVals.getInt(currIndex);
                        } else if (currCls == long.class||currCls == Long.class) {
                            //WARN: argsJson.getLong(k + defValue) will return a bigger incorrect number
                            values[currIndex] = Long.parseLong(argsVals.getString(currIndex));
                        } else {
                            values[currIndex] = argsVals.getDouble(currIndex);
                        }
                        // Log.e("print",currIndex+"=numIndex="+numIndex+" value:"+ values[currIndex]);
                        numIndex /= 10;
                    }
                }
                return getReturn(jsonStr, 200, currMethod.invoke(mInstance, values));
            } catch (Exception e) {
                //优先返回详细的错误信息
                if (e.getCause() != null) {
                    return getReturn(jsonStr, 500, "method execute error:" + e.getCause().getMessage());
                }
                return getReturn(jsonStr, 500, "method execute error:" + e.getMessage());
            }
        } else {
            return getReturn(jsonStr, 500, "call data empty");
        }
    }

    private String getReturn(String reqJson, int stateCode, Object result) {
        String insertRes;
        if (result == null) {
            insertRes = "null";
        } else if (result instanceof String) {
            result = ((String) result).replace("\"", "\\\"");
            insertRes = "\"" + result + "\"";
        } else if (!(result instanceof Integer)
                && !(result instanceof Long)
                && !(result instanceof Boolean)
                && !(result instanceof Float)
                && !(result instanceof Double)
                && !(result instanceof JSONObject)) {    // 非数字或者非字符串的构造对象类型都要序列化后再拼接
            if (mGson == null) {
                mGson = new Gson();
            }
            insertRes = mGson.toJson(result);
        } else {  //数字直接转化
            insertRes = String.valueOf(result);
        }
        String resStr = String.format(RETURN_RESULT_FORMAT, stateCode, insertRes);
        Log.d(TAG, mInjectedName + " call json: " + reqJson + " result:" + resStr);
        return resStr;
    }
}