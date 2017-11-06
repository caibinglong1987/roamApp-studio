package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.util.Utility;



/**
 * Created by long
 * on 2016/10/12.
 * 络漫宝 配置 wifi 名称 密码设置界面
 */

public class LMBAOWiFiSetActivity extends HeaderBaseActivity {
    private EditText et_wifi_password, et_wifi_last_name;
    private TextView tv_next;
    private String wifi_name = null, wifi_password = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_wifi_set);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_wifi_name_password));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.closeKeyboard(et_wifi_last_name, getApplicationContext());
                finish();
            }
        });
        et_wifi_last_name = (EditText) findViewById(R.id.et_wifi_last_name);
        et_wifi_password = (EditText) findViewById(R.id.et_wifi_password);
        if (RoamApplication.RoamBoxConfigOld != null) {
            et_wifi_last_name.setText(RoamApplication.RoamBoxConfigOld.lan_ssid);
            et_wifi_password.setText(RoamApplication.RoamBoxConfigOld.lan_password);
        }

        et_wifi_password.setFocusable(true);
        et_wifi_password.setFocusableInTouchMode(true);
        et_wifi_password.requestFocus();
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_next:
                wifi_name = et_wifi_last_name.getText().toString();
                wifi_password = et_wifi_password.getText().toString();
                if (wifi_name.length() > 0 && wifi_password.length() >= 8) {//
                    RoamApplication.RoamBoxConfigOld.lan_ssid = wifi_name;
                    RoamApplication.RoamBoxConfigOld.lan_password = wifi_password;
                    toActivity(LMBAOPhoneSettingActivity.class, null); //跳转配置手机号码
                }else{
                    ToastUtils.showToast(this,getString(R.string.str_lan_password_alert));
                }
                break;
        }
    }

}
