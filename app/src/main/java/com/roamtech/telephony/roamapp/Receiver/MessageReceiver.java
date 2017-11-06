package com.roamtech.telephony.roamapp.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


/**
 * Created by caibinglong
 * on 2016/12/8.
 */

public class MessageReceiver extends BroadcastReceiver {
    private iMessageCallBack callBack = null;
    private Context context;

    public MessageReceiver(Context context, iMessageCallBack callBack) {
        super();
        this.context = context;
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ActionValue.ACTION_NEW_MESSAGE) && callBack != null) {
            callBack.newMessage(intent);
        }
        if (intent.getAction().equals(ActionValue.ACTION_UPDATE_MESSAGE) && callBack != null) {
            callBack.updateGroupMessage(intent);
        }
    }

    public void register(MessageReceiver receiver, MessageReceiver receiver2) {
        if (context != null) {
            if (receiver != null) {
                context.registerReceiver(receiver, new IntentFilter(ActionValue.ACTION_NEW_MESSAGE));
            }
            if (receiver2 != null) {
                context.registerReceiver(receiver2, new IntentFilter(ActionValue.ACTION_UPDATE_MESSAGE));
            }
        }
    }

    public void unRegister(MessageReceiver receiver, MessageReceiver receiver2) {
        if (context != null) {
            if (receiver != null) {
                context.unregisterReceiver(receiver);
            }
            if (receiver2 != null) {
                context.unregisterReceiver(receiver2);
            }
        }
    }

    public interface iMessageCallBack {
        void newMessage(Intent intent);

        void updateGroupMessage(Intent intent);
    }
}
