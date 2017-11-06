package com.roamtech.telephony.roamapp.util.Polling;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.GetPayment;
import com.roamtech.telephony.roamapp.activity.function.Product;
import com.roamtech.telephony.roamapp.activity.function.Shipping;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by long
 * on 2016/9/26 09:45
 * 轮询更新 服务
 */

public class PollingService extends Service {
    public static final String ACTION = "com.roamtech.telephony.PollingService";

    private Notification mNotification;
    private NotificationManager mManager;
    private MyBinder myBinder = new MyBinder();

    public class MyBinder extends Binder {
        public PollingService getIService() {
            return PollingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public void onCreate() {
        initNotificationManager();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        new PollingThread().start();
    }

    //初始化通知栏配置
    private void initNotificationManager() {
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int icon = R.drawable.ic_launcher;
        mNotification = new Notification();
        mNotification.icon = icon;
        mNotification.tickerText = "New Message";
        mNotification.defaults |= Notification.DEFAULT_SOUND;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
    }

    //弹出Notification
    private void showNotification() {
//        mNotification.when = System.currentTimeMillis();
//        //Navigator to the new activity when click the notification title
//        Intent i = new Intent(this, MessageActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i,
//                Intent.FLAG_ACTIVITY_NEW_TASK);
//        mNotification.setLatestEventInfo(this,
//                getResources().getString(R.string.app_name), "You have new message!", pendingIntent);
//        mManager.notify(0, mNotification);
    }

    /**
     * Polling thread
     * 模拟向Server轮询的异步线程
     */
    int count = 0;

    class PollingThread extends Thread {
        @Override
        public void run() {
            System.out.println("Polling...");
//            count++;
//            //当计数能被5整除时弹出通知
//            if (count % 5 == 0) {
//                //showNotification();
//                System.out.println("New message!");
//            }
            JSONObject authJson = new JSONObject();
            try {
                authJson.put("userid", SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId));
                authJson.put("sessionid", SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_sessionId));
            } catch (JSONException e) {
                e.printStackTrace();
            }
           // new GetPayment().getPaymentWay(authJson, 1211, null);
           // new Product().getProductList(authJson, 1212, null);
            //new Shipping().getShipList(authJson, 1213, null);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }
}
