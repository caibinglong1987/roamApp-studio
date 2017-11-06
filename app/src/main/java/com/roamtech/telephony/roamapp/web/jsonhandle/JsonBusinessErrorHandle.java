package com.roamtech.telephony.roamapp.web.jsonhandle;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.roamtech.telephony.roamapp.server.IServiceHelper;
import com.roamtech.telephony.roamapp.server.IServiceValues;
import com.roamtech.telephony.roamapp.util.BroadCastHelper;
import com.roamtech.telephony.roamapp.util.ErrorHelper;


/**
 * Author: long
 * Since: 2016年05月04日 18:13
 * Desc: 业务错误统一处理类
 * FIXME:
 */
public class JsonBusinessErrorHandle implements JsonHandleable {

    private Context mContext;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //ToastUtils.showToast(mContext, ErrorHelper.getErrorHint(mContext,(String)msg.obj));
        }
    };

    public JsonBusinessErrorHandle(Context context){
        this.mContext = context;
    }

    @Override
    public void handle(Object... args) {
        if(args.length < 1){
            return;
        }
        String code = (String)args[0];
        if(mContext == null || code == null){
            return;
        }
        if(ErrorHelper.shouldLogout(code)){
            Intent exitIntent = IServiceHelper.getBroadcastIntent(IServiceValues.ACTION_CMD_WAY, IServiceValues.CMD_EXIT_LOGIN);
            BroadCastHelper.sendBroadcast(mContext, exitIntent);
        }
        else{
            mHandler.obtainMessage(0,code).sendToTarget();
        }
    }
}
