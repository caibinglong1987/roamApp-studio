package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;

/**
 * Created by long
 * on 2016/10/21.
 * 检测结果 可以自动获取IP
 */

public class LMBAOAutoInternetActivity extends HeaderBaseActivity {
    private TextView tv_save_config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_auto_internet);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.connection_internet));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_save_config = (TextView) findViewById(R.id.tv_save_config);
        tv_save_config.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save_config:
                //自动检测 已经可以上网 不用重启路由器设置
                RoamApplication.RoamBoxConfigOld.wan_proto = RoamApplication.WAN_PROTO_DHCP;
                RoamApplication.RoamBoxConfigOld.wan_type = "line";
                toActivity(LMBAOPhoneSettingActivity.class, null);
                break;
        }
    }
}
