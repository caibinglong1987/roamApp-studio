package com.roamtech.telephony.roamapp.activity;

import android.net.wifi.ScanResult;
import android.os.Bundle;

import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.will.common.tool.wifi.WifiAdmin;

import java.util.List;

/**
 * Created by long
 * on 2016/10/8 09:35
 */

public class WifiActivity extends HeaderBaseActivity {

    private WifiAdmin mWifiAdmin;
    // 扫描结果列表
    private List<ScanResult> list;
    private ScanResult mScanResult;
    private StringBuffer sb = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWifiAdmin = new WifiAdmin(getApplicationContext());
        //getAllNetWorkList();
    }

    public void getAllNetWorkList() {

        // 每次点击扫描之前清空上一次的扫描结果
        if (sb != null) {
            sb = new StringBuffer();
        }
        //开始扫描网络
        mWifiAdmin.startScan();
        List<ScanResult> list = mWifiAdmin.getWifiList();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                //得到扫描结果
                mScanResult = list.get(i);
                sb = sb.append(mScanResult.BSSID + "  ").append(mScanResult.SSID + "   ")
                        .append(mScanResult.capabilities + "   ").append(mScanResult.frequency + "   ")
                        .append(mScanResult.level + "\n\n");
            }
        }
    }
}
