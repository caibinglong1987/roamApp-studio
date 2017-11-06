package com.roamtech.telephony.roamapp.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


/**
 * Created by caibinglong
 * on 2017/2/15.
 */

public class RoamBoxMessageReceiver extends BroadcastReceiver {
    private Context context;
    private iCallback callBack;

    public RoamBoxMessageReceiver(Context context, iCallback callBack) {
        super();
        this.context = context;
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ActionValue.ACTION_ROAM_BOX_MESSAGE) && callBack != null) {
            callBack.newMessageBack(intent);
        }
    }

    public void register(RoamBoxMessageReceiver receiver) {
        if (context != null) {
            if (receiver != null) {
                context.registerReceiver(receiver, new IntentFilter(ActionValue.ACTION_ROAM_BOX_MESSAGE));
            }
        }
    }

    public void unRegister(RoamBoxMessageReceiver receiver) {
        if (context != null) {
            if (receiver != null) {
                context.unregisterReceiver(receiver);
            }
        }
    }

    public interface iCallback {
        void newMessageBack(Intent intent);
    }
}
