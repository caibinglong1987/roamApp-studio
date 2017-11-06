package com.roamtech.telephony.roamapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;

import org.linphone.mediastream.Log;

/**
 * @author yangyu 功能描述：主程序入口类
 */
public class StartActivity extends BaseActivity {

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Used to change for the lifetime of the app the name used to tag the logs
        new Log(getResources().getString(R.string.app_name), !getResources().getBoolean(R.bool.disable_every_log));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_start);
        onServiceReady();
    }

    protected void onServiceReady() {
        final Class<? extends Activity> classToStart;
        boolean isFirstOpen = SPreferencesTool.getInstance().getBooleanValue(getApplicationContext(), SPreferencesTool.isFirstOpen, SPreferencesTool.isFirstOpen, false);
        if (!isFirstOpen) {
            SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.isFirstOpen, SPreferencesTool.isFirstOpen, true);
            classToStart = GuideActivity.class;
        } else {
            String userId = SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, "0");
            if (userId.equals("0")) {
                classToStart = LoginActivity.class;
            } else {
                classToStart = LinphoneActivity.class;
            }
        }
        startActivity(new Intent().setClass(StartActivity.this, classToStart).setData(getIntent().getData()));
    }
}
