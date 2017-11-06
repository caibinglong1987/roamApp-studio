package com.roamtech.telephony.roamapp.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.roamtech.telephony.roamapp.application.AppConfig;

import org.linphone.LinphoneManager;
import org.linphone.LinphoneService;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.ACTION_MAIN;

/**
 * Created by caibinglong
 * on 2017/2/22.
 */

public class SipService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO do some thing what you want..
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        threadTask();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void threadTask() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!LinphoneManager.isServiceWorked(SipService.this.getApplicationContext(), AppConfig.LINPHONE_SERVICE_NAME)) {
                    startService(new Intent(ACTION_MAIN).setClass(SipService.this, LinphoneService.class));
                }
            }
        }, 0, 60 * 1000);
    }

}
