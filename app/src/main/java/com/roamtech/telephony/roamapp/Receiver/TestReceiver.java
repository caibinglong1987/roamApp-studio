package com.roamtech.telephony.roamapp.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.linphone.mediastream.Log;

/**
 * Created by caibinglong
 * on 2017/2/24.
 */

public class TestReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {

            Log.w("广播接收----BROADCAST_ACTION");

    }
}
