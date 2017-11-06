package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.Parameter.KeyValue;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.adapter.DefaultSingleAdapter;
import com.roamtech.telephony.roamapp.adapter.SettingSingleSelectAdapter;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.WifiBean;
import com.roamtech.telephony.roamapp.bean.WifiRDO;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.will.common.tool.view.LibRecyclerView.DividerItemDecoration;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Created by long
 * on 2016/10/13.
 * wifi 列表
 */

public class WifiListActivity extends HeaderBaseActivity {
    private RecyclerView list_recycler_view;
    private List<ScanResult> wifiList;
    private List<WifiBean> wifiBeanList;
    private ArrayList<String> items = new ArrayList<>();
    private DefaultSingleAdapter adapter = null;
    private String wifiName;
    private String systemWifi = "true";
    private String showConnectWifi = "true";
    private int position = 0; //上次选中的位置
    private RoamDialog roamDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_show);
        wifiName = getIntent().getStringExtra(KeyValue.WIFI_NAME);
        systemWifi = getIntent().getStringExtra(KeyValue.SHOW_SYSTEM_WIFI);
        showConnectWifi = getIntent().getStringExtra(KeyValue.SHOW_CONNECT_WIFI);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_wifi_check));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list_recycler_view = (RecyclerView) findViewById(R.id.list_recycler_view);
        list_recycler_view.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于list view
        adapter = new DefaultSingleAdapter(this);
        adapter.setOnItemClickListener(onItemClickListener);
        roamDialog = new RoamDialog(this, getString(R.string.loadinginfo));
        roamDialog.show();
        if (systemWifi != null && systemWifi.equals("false")) {
            getWifiListByRoamBox();
        } else {
            getWifiList();
        }
    }

    /**
     * 检测wifi网络
     */
    private void getWifiList() {
        WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
        wifiAdmin.startScan();
        wifiList = wifiAdmin.getWifiList();
        items.clear();
        for (ScanResult item : wifiList) {
            if (wifiAdmin.getSSID().replace("'", "").equals(item.SSID) && showConnectWifi.equals("false")) {
                //无线中继 需要过滤 已经连接的wifi
                continue;
            } else {
                Collections.addAll(items, item.SSID);
                if (item.SSID.equals(wifiName)) {
                    position = wifiList.indexOf(item); //默认上次选中
                }
            }
        }
        adapter.addItems(items);
        list_recycler_view.setAdapter(adapter);
        adapter.setCurrentSelect(position);
        list_recycler_view.setItemAnimator(new DefaultItemAnimator());
        list_recycler_view.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        roamDialog.dismiss();
    }

    private SettingSingleSelectAdapter.OnItemClickListener onItemClickListener = new SettingSingleSelectAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) { //返回本次选中的wifi
            Bundle bundle = new Bundle();
            wifiName = wifiList == null ? wifiBeanList.get(position).ssId : wifiList.get(position).SSID;
            bundle.putString("wifiName", wifiName);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_GET_ROAM_BOX_WIFI_SUCCESS:
                Log.e("络漫宝wifi获取成功--->", msg.obj.toString());
                wifiBeanList = (List<WifiBean>) msg.obj;
                items.clear();
                for (WifiBean item : wifiBeanList) {
                    Collections.addAll(items, item.ssId);
                    if (item.ssId.equals(wifiName)) {
                        position = wifiBeanList.indexOf(item); //默认上次选中
                    }
                }
                adapter.addItems(items);
                list_recycler_view.setAdapter(adapter);
                adapter.setCurrentSelect(position);
                list_recycler_view.setItemAnimator(new DefaultItemAnimator());
                list_recycler_view.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
                roamDialog.dismiss();
                break;
            case MsgType.MSG_GET_ROAM_BOX_WIFI_ERROR:
                Log.e("络漫宝wifi获取失败", "");
                getWifiList();
                break;
        }
    }

    /**
     * 获取络漫宝发现的wifi
     */
    private void getWifiListByRoamBox() {
        if (RoamApplication.RoamBoxToken == null) {
            uiHandler.sendEmptyMessage(MsgType.MSG_GET_ROAM_BOX_WIFI_ERROR);
            return;
        }
        new RoamBoxFunction(this).getRoamBoxWifiList(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, getHttpUserAgent(), hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_GET_ROAM_BOX_WIFI_ERROR);
            }

            @Override
            public void onSuccess(String response) {
                WifiRDO<WifiBean> result = JsonUtil.fromJson(response, new TypeToken<WifiRDO<WifiBean>>() {
                }.getType());
                if (result != null) {
                    List<WifiBean> list = result.result;
                    if (!list.isEmpty()) {
                        uiHandler.obtainMessage(MsgType.MSG_GET_ROAM_BOX_WIFI_SUCCESS, list).sendToTarget();
                    } else {
                        uiHandler.sendEmptyMessage(MsgType.MSG_GET_ROAM_BOX_WIFI_ERROR);
                    }
                } else {
                    uiHandler.sendEmptyMessage(MsgType.MSG_GET_ROAM_BOX_WIFI_ERROR);
                }
            }
        });
    }
}
