package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.util.StringFormatUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LMBaoWifiActivity extends BaseActivity {
    private TextView tvConnectTip;
    private Button btnConnectNow;
    private Button btnConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmbao_wifi);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        tvConnectTip = (TextView) findViewById(R.id.tv_connecttip);
        btnConnectNow = (Button) findViewById(R.id.btnConnectNow);
        btnConnected = (Button) findViewById(R.id.btnConnected);
        String wifiName = "Roaming Box";
        String wholeStr = "找到以 " + wifiName + "开头的WiFi并连接";
        StringFormatUtil spanStr = new StringFormatUtil(this, wholeStr,
                wifiName, R.color.green).fillColor();
        tvConnectTip.setText(spanStr.getResult());
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        btnConnected.setOnClickListener(this);
        btnConnectNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == btnConnected) {

        } else if (v == btnConnectNow) {
            toActivity(LMBaoSettingActivity.class, null);
        }
    }
}
