package com.roamtech.telephony.roamapp.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by caibinglong
 * on 2017/2/3.
 * sip 服务
 */

public class ServiceGuard extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
