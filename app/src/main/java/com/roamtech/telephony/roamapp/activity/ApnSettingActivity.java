package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.helper.DeviceHelper;

import org.linphone.compatibility.Compatibility;

public class ApnSettingActivity extends BaseActivity {

    private Button btn_setApn_no_southeast_asia; //非东南亚
    private Button btn_setApn_southeast_asia; //东南亚设置
    @Override
    public int getLayoutId() {
        return R.layout.activity_apn_setting;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        btn_setApn_no_southeast_asia = (Button) findViewById(R.id.btn_setApn_no_southeast_asia);
        btn_setApn_southeast_asia = (Button) findViewById(R.id.btn_setApn_southeast_asia);
    }

    @Override
    public void setListener() {
        super.setListener();
        btn_setApn_no_southeast_asia.setOnClickListener(this);
        btn_setApn_southeast_asia.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == btn_setApn_no_southeast_asia) {
            Compatibility.copyTextToClipboard(this, "dashi.cloudsim-internet");
            DeviceHelper.toApnSetting(this);
        }
        if (v == btn_setApn_southeast_asia) {
            Compatibility.copyTextToClipboard(this, "3gnet");
            DeviceHelper.toApnSetting(this);
        }
    }
}
