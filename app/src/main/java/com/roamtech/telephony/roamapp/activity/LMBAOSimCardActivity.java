package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.will.common.tool.wifi.WifiAdmin;

/**
 * Created by long
 * on 2016/10/18.
 */

public class LMBAOSimCardActivity extends HeaderBaseActivity {
    private TextView tv_save_config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_sim_card);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_insert_sim));
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
                if (WifiAdmin.isWifiConnected(this)) {
                    toActivity(LMBAOSuccessfulActivity.class, null);
                } else {
                    ToastUtils.showToast(this,getString(R.string.auto_internet_error));
                }
                break;
        }
    }
}
