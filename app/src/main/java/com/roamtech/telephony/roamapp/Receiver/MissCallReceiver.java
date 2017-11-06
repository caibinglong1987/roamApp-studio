package com.roamtech.telephony.roamapp.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by caibinglong
 * on 2016/12/22.
 */

public class MissCallReceiver extends BroadcastReceiver {
    private Context context;
    private iMissCallBack callBack;
    public MissCallReceiver(Context context,iMissCallBack callBack) {
        super();
        this.context = context;
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ActionValue.ACTION_NEW_MISS_CALL) && callBack != null) {
            callBack.newCall(intent);
        }
    }
    public void register(MissCallReceiver receiver) {
        if (context != null) {
            if (receiver != null) {
                context.registerReceiver(receiver, new IntentFilter(ActionValue.ACTION_NEW_MISS_CALL));
            }
        }
    }

    public void unRegister(MissCallReceiver receiver) {
        if (context != null) {
            if (receiver != null) {
                context.unregisterReceiver(receiver);
            }
        }
    }


    public interface iMissCallBack {
        void newCall(Intent intent);
    }
}
