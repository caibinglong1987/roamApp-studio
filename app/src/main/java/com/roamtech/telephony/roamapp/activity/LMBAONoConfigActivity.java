package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.Parameter.KeyValue;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.NetworkTimer;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.will.common.tool.Location.LocationUtil;
import com.will.common.tool.wifi.MacAddressUtils;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by long
 * on 2016/10/11.
 * 没有找到配置过的络漫宝
 */

public class LMBAONoConfigActivity extends HeaderBaseActivity {

    public final int GO_CHECK_WIFI = 100;
    private LinearLayout layout_wifi_change;
    private TextView tv_wifi_name, tv_next, tv_find_text;
    private RoamDialog roamDialog;
    private List<ScanResult> wifiList = new ArrayList<>();
    private int btn_type = 0; //0是取消 1 是已经找到wifi 立即连接
    private WifiAdmin wifiAdmin;
    private String IpAddress;
    private int connectNumber = 0;
    private NetworkTimer networkTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_no_config);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_lbm_connection));
        layout_wifi_change = (LinearLayout) findViewById(R.id.layout_wifi_change);
        tv_wifi_name = (TextView) findViewById(R.id.tv_wifi_name);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_find_text = (TextView) findViewById(R.id.tv_find_text);
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_wifi_change.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        networkTimer = new NetworkTimer(this, uiHandler);
    }

    /**
     * 检测网络
     */
    private void detectionInternet() {
        roamDialog = new RoamDialog(this, getString(R.string.auto_detection_internet_way));
        roamDialog.show();
        wifiList.clear();
        wifiAdmin.startScan();
        wifiList = wifiAdmin.getWifiList();
        if (!wifiAdmin.openWifi()) { //验证无线网络是否开启
            roamDialog.dismiss();
            btn_type = 4;
            tv_wifi_name.setText("");
            tv_next.setText(getString(R.string.str_restart_detectionInternet));
            ToastUtils.showToast(this, getString(R.string.str_wifi_error));
            return;
        }
        for (ScanResult item : wifiList) {
            if (item.SSID.contains("RoamBox")) { //RoamBox
                layout_wifi_change.setVisibility(View.VISIBLE);
                tv_wifi_name.setText(item.SSID);
                btn_type = 1;
                tv_next.setText(getString(R.string.connectNow));
                break;
            }
        }
        roamDialog.dismiss();
        if (tv_wifi_name.getText().length() <= 0) {
            if (!LocationUtil.isOPen(this) && wifiList.isEmpty()) { //判断是否开启GPS 6.0系统需要开启GPS 权限 才能获取wifi
                ToastUtils.showToast(this, getString(R.string.str_gps_error));
                btn_type = 3;
                tv_wifi_name.setText("");
                tv_next.setText(getString(R.string.str_restart_detectionInternet));
            } else {
                btn_type = 0;
                layout_wifi_change.setVisibility(View.VISIBLE);
                tv_find_text.setVisibility(View.INVISIBLE);
                tv_wifi_name.setText(getString(R.string.goto_wifi));
                tv_wifi_name.setTextColor(ContextCompat.getColor(this, R.color.red_dark));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                if (btn_type == 1) {
                    connectNumber = 0;
                    roamDialog.setTitle(getString(R.string.auto_connection));
                    roamDialog.show();
                    networkTimer.connectCheckSSid(tv_wifi_name.getText().toString(), null);
                }
                if (btn_type == 0 || btn_type == 3 || btn_type == 4) {
                    detectionInternet();
                }
                //Log.e("络漫宝设置WIFI列表-->", wifiAdmin.lookUpScan().toString());
                break;
            case R.id.layout_wifi_change:
                Bundle bundle = new Bundle();
                bundle.putString(KeyValue.WIFI_NAME, tv_wifi_name.getText().toString());
                //toActivity(WifiListActivity.class, bundle);
                bundle.putString(KeyValue.SHOW_CONNECT_WIFI, "true");
                bundle.putString(KeyValue.SHOW_SYSTEM_WIFI, "true");
                toActivityForResult(WifiListActivity.class, GO_CHECK_WIFI, bundle);
                break;
        }

    }

    /**
     * 获取 token
     */
    private void getAuthTokenNew() {
        RoamBoxFunction roamBoxFunction = new RoamBoxFunction(this);
        roamBoxFunction.getRoamBoxAuthToken(Constant.ROAM_BOX_AUTH, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                if (response != null) {
                    CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                    }.getType());
                    if (result != null && result.attributes != null) {
                        RoamApplication.RoamBoxToken = result.attributes;
                        Log.e("络漫宝Token--->", RoamApplication.RoamBoxToken);
                        uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_SUCCESS);
                    } else {
                        uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_ERROR);
                    }
                }
            }

            @Override
            public void onFailure(Map<String, ?> errorMap) {
                connectNumber++;
                uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_TIMEOUT);
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_ROAM_BOX_TOKEN_SUCCESS:
                //获取络漫宝 Token 成功
                roamDialog.dismiss();
                RoamApplication.RoamBoxConfigOld.check_ssId = tv_wifi_name.getText().toString(); //保存现在的络漫宝wifi名称
                //toActivity(LMBAOManualConfigActivity.class, null);
                toActivity(LMBAOWiredModeActivity.class, null);
                break;
            case MsgType.MSG_ROAM_BOX_TOKEN_ERROR:
                roamDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.connect_error_lmb));
                break;
            case MsgType.MSG_ROAM_BOX_TOKEN_TIMEOUT:
                if (connectNumber >= 3) {
                    uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_ERROR);
                    return;
                }
                getAuthTokenNew();
                break;
            case MsgType.MSG_CHANGE_NETWORK_SUCCESS:
                networkTimer.stopTimer();
                connectNumber = 0;
                roamDialog.setTitle(getString(R.string.str_connect_lmb));
                wifiAdmin = new WifiAdmin(getApplicationContext());
                IpAddress = MacAddressUtils.intToIpAddress(wifiAdmin.getIpAddress());
                IpAddress = IpAddress.substring(0, IpAddress.lastIndexOf(".") + 1) + "1";
                Constant.ROAM_BOX_AUTH = Constant.LMBAO_AUTH_START + IpAddress + Constant.LMBAO_AUTH_END;
                Constant.ROAM_BOX_CONFIG = Constant.LMBAO_AUTH_START + IpAddress + Constant.LMBAO_CONFIG_END;
                getAuthTokenNew();
                break;
            case MsgType.MSG_CHANGE_NETWORK_UPDATE:
                if (connectNumber >= 4) {
                    networkTimer.stopTimer();
                    roamDialog.dismiss();
                    ToastUtils.showToast(this, getString(R.string.auto_internet_error));
                    connectNumber = 0;
                    return;
                }
                connectNumber++;
                break;
            case MsgType.MSG_CHANGE_NETWORK_ERROR:
                connectNumber = 5;
                uiHandler.sendEmptyMessage(MsgType.MSG_CHANGE_NETWORK_UPDATE);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiAdmin = new WifiAdmin(getApplicationContext());
        if (btn_type != 1) {
            detectionInternet();
        }
        connectNumber = 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_CHECK_WIFI) {
            if (data != null) {
                tv_wifi_name.setText(data.getStringExtra("wifiName"));
                tv_wifi_name.setTextColor(ContextCompat.getColor(this, R.color.roam_color));
                tv_next.setText(getString(R.string.connectNow));
                tv_find_text.setText(getString(R.string.str_you_select_wifi));
                tv_find_text.setVisibility(View.VISIBLE);
                btn_type = 1;
            }
        }
    }

}
