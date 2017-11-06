package com.roamtech.telephony.roamapp.activity;

import java.util.ArrayList;
import java.util.List;

import com.roamtech.telephony.roamapp.util.TelephonyUtils;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.WiFiAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.WiFi;

import android.os.Bundle;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class SelectWiFiActivity extends BaseActivity {
    private ListView mListView;
    private String selectSsid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectSsid = getIntent().getStringExtra("wifi");
        setContentView(R.layout.activity_selectwifi);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        mListView = (ListView) findViewById(android.R.id.list);
        List<WiFi> data = new ArrayList<WiFi>();
        List<WifiConfiguration> configureNetworks = TelephonyUtils.getInstance().getConfiguredNetworks();

        if (configureNetworks != null) {
            for (WifiConfiguration config : configureNetworks) {
                String wifissid = config.SSID;
                if (wifissid.equals("roamtouchAP") || wifissid.equals("\"roamtouchAP\"")) {
                    continue;
                }
                if (wifissid.startsWith("\"") && wifissid.endsWith("\"")) {
                    wifissid = wifissid.substring(1, wifissid.length() - 1);
                }
                WiFi wifi = new WiFi();
                if (wifissid.equals(selectSsid)) {
                    wifi.setSelected(true);
                }
                wifi.setSsid(wifissid);
                data.add(wifi);
            }
        }
        WiFiAdapter adapter = new WiFiAdapter(this, data);
        mListView.setAdapter(adapter);
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        super.onItemClick(parent, view, position, id);
        WiFi selectedWiFi = (WiFi) mListView.getItemAtPosition(position);
        Intent i = new Intent();
        i.putExtra("wifi", selectedWiFi);
        setResult(RESULT_OK, i);
        finish();
    }
}
