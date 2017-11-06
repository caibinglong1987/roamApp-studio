package com.roamtech.telephony.roamapp.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;

import com.roamtech.telephony.roamapp.wxapi.WXInterface;

/**
 * Created by long
 * on 2016/10/8 11:15
 * 微信付款 广播监听
 */

public class WXPayResultReceiver extends BroadcastReceiver {
    private Handler uiHandler = null;
    private Context context = null;

    /**
     * @param context context
     * @param handler handler
     */
    public WXPayResultReceiver(Context context, Handler handler) {
        this.context = context;
        this.uiHandler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WXInterface.WEIXIN_PAY_RESULT_ACTION) && uiHandler != null) {
            uiHandler.sendEmptyMessageDelayed(intent.getIntExtra("wxPayResult", -1), 1000);
        }
    }

    /**
     * 注册广播
     */
    public void register(WXPayResultReceiver receiver) {
        if (receiver != null && context != null) {
            context.registerReceiver(receiver, new IntentFilter(WXInterface.WEIXIN_PAY_RESULT_ACTION));
        }
    }

    /**
     * 取消注册 广播
     */
    public void unRegister(WXPayResultReceiver receiver) {
        if (receiver != null && context != null) {
            context.unregisterReceiver(receiver);
        }
    }
}
