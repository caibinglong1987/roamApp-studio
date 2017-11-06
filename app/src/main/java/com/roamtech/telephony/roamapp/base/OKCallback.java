package com.roamtech.telephony.roamapp.base;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.util.JsonUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by xincheng on 2016/7/20.
 */
public abstract class OKCallback<T> implements Callback {
    private static final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private UCResponse<T> ucResp;
    private TypeToken<UCResponse<T>> token;

    public OKCallback(TypeToken<UCResponse<T>> token) {
        this.token = token;
    }
    @Override
    public final void onResponse(final Call call, final Response response) throws IOException {
        final String jsonStr = response.body().string();

        //ucResp = JSONUtils.fromJson(jsonStr,new TypeToken<UCResponse<T>>(){});
        ucResp = JsonUtil.fromJson(jsonStr,token);
        Log.e("print",jsonStr);
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                onResponse(response.code(), ucResp);
            }
        });
    }

    @Override
    public final void onFailure(Call call, final IOException e) {
        mUiHandler.post(new Runnable() {
            @Override
            public void run() {
                onFailure(e);
            }
        });
    }

    /**
     * @param statuscode http状态码
     * @param ucResponse http请求成功返回的JSON结果 ,失败的话可能为 null
     */
    public abstract void onResponse(int statuscode, @Nullable UCResponse<T> ucResponse);

    public abstract void onFailure(IOException e);

    public boolean isSucccess() {
        if (ucResp != null && ucResp.getErrorNo() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 会话失效，重新登录
     *
     * @return
     */
    public boolean isSessionTimeout() {
        if (ucResp != null && ucResp.getErrorNo() == 1101) {
            return true;
        }
        return false;
    }
}
