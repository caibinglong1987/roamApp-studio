package com.roamtech.telephony.roamapp.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by caibinglong
 * on 2016/12/6.
 */

public class LoginReceiver extends BroadcastReceiver {
    private Context context;

    public LoginReceiver(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ActionValue.ACTION_LOGIN)) {

        }
    }

    public void register(LoginReceiver receiver) {
        if (context != null) {
            context.registerReceiver(receiver, new IntentFilter(ActionValue.ACTION_LOGIN));
        }
    }

    public void unRegister(LoginReceiver receiver) {
        if (context != null) {
            context.unregisterReceiver(receiver);
        }
    }
}
