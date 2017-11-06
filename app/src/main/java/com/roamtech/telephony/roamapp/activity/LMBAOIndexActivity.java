package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.bean.NetworkConfigBean;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.will.common.tool.wifi.MacAddressUtils;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import java.util.Map;

/**
 * Created by caibinglong
 * on 2017/1/2.
 * 络漫宝设置 跳过自动连接WIFI设置
 */

public class LMBAOIndexActivity extends HeaderBaseActivity {
    private LinearLayout layout_wifi_change, layout_go_wifi;
    private TextView tv_wifi_name, tv_next;
    private RoamDialog roamDialog;
    private RoamBoxFunction roamBoxFunction;
    private WifiAdmin wifiAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_no_config);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_lbm_connection));
        layout_wifi_change = (LinearLayout) findViewById(R.id.layout_wifi_change);
        layout_go_wifi = (LinearLayout) findViewById(R.id.layout_go_wifi);
        tv_wifi_name = (TextView) findViewById(R.id.tv_wifi_name);
        tv_next = (TextView) findViewById(R.id.tv_next);
        layout_wifi_change.setVisibility(View.GONE);
        layout_go_wifi.setVisibility(View.VISIBLE);
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_next.setOnClickListener(this);
        layout_go_wifi.setOnClickListener(this);
        roamBoxFunction = new RoamBoxFunction(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                roamDialog = new RoamDialog(this, getString(R.string.auto_connection));
                roamDialog.show();
                wifiAdmin = new WifiAdmin(getApplicationContext());
                String IpAddress = MacAddressUtils.intToIpAddress(wifiAdmin.getIpAddress());
                IpAddress = IpAddress.substring(0, IpAddress.lastIndexOf(".") + 1) + "1";
                Constant.ROAM_BOX_AUTH = Constant.LMBAO_AUTH_START + IpAddress + Constant.LMBAO_AUTH_END;
                Constant.ROAM_BOX_CONFIG = Constant.LMBAO_AUTH_START + IpAddress + Constant.LMBAO_CONFIG_END;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (WifiAdmin.isNetworkConnected(getApplicationContext()) && WifiAdmin.isNetworkOnline()) {
                            getAuthTokenNew();
                        } else {
                            uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_ERROR);
                        }
                    }
                }).start();
                break;
            case R.id.layout_go_wifi:
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
                break;
        }
    }

    /**
     * 获取 token
     */
    private void getAuthTokenNew() {
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
                } else {
                    uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_ERROR);
                }
            }

            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_TIMEOUT);
            }
        });
    }

    /**
     * 监测上网方式
     */
    private void getRoamBoxConfig() {
        roamBoxFunction.getRoamBoxConfigurationInfo(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken,
                hashCode(), new HttpBusinessCallback() {
                    @Override
                    public void onFailure(Map<String, ?> errorMap) {
                        uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_ERROR);
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            CommonRoamBox<NetworkConfigBean> networkConfigBean = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<NetworkConfigBean>>() {
                            }.getType());
                            if (networkConfigBean != null && networkConfigBean.attributes != null) {
                                RoamApplication.RoamBoxConfigOld = networkConfigBean.attributes;
                                uiHandler.obtainMessage(MsgType.MSG_DETECT_NETWORK_SUCCESS, RoamApplication.RoamBoxConfigOld).sendToTarget();
                            } else {
                                uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_ERROR);
                            }
                        }
                    }
                });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_ROAM_BOX_TOKEN_SUCCESS:
                //获取络漫宝 Token 成功
                getRoamBoxConfig();
                break;
            case MsgType.MSG_ROAM_BOX_TOKEN_ERROR:
            case MsgType.MSG_ROAM_BOX_TOKEN_TIMEOUT:
                roamDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.connect_error_lmb), Toast.LENGTH_LONG);
                break;
            case MsgType.MSG_DETECT_NETWORK_SUCCESS:
                roamDialog.dismiss();
                RoamApplication.RoamBoxConfigOld.lan_ssid = wifiAdmin.getSSID().replace("\"",""); //保存现在的络漫宝wifi名称
                RoamApplication.RoamBoxConfigOld.wan_proto = RoamApplication.WAN_PROTO_DHCP;
                RoamApplication.RoamBoxConfigOld.wan_type = "line";
                toActivity(LMBAOPhoneSettingActivity.class, null);
                break;
            case MsgType.MSG_ROAM_BOX_NETWORK_ERROR:
                roamDialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("bDetectionInternet", true);
                intent.setClass(this, LMBAOWiredModeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
