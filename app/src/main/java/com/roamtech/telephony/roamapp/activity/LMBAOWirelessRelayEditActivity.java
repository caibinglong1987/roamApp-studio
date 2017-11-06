package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
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
import com.roamtech.telephony.roamapp.dialog.RoamBoxSettingDialog;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.NetworkTimer;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.util.Utility;
import com.roamtech.telephony.roamapp.view.ActionSheetDialog;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by long
 * on 2016/10/13.
 * 中继模式 编辑
 */

public class LMBAOWirelessRelayEditActivity extends HeaderBaseActivity {
    private LinearLayout layout_wifi_select;
    public final int GO_CHECK_WIFI = 100;
    private EditText et_wifi_password;
    private TextView tv_wifi_name;
    private TextView tv_save_config;
    private String wifi_name, wifi_password;
    private WifiAdmin wifiAdmin;
    private int requestNumber = 0; //请求超时次数
    private RoamBoxFunction roamBoxFunction;
    private int connectNumber = 0;
    private RoamBoxSettingDialog roamBoxSettingDialog;
    private NetworkTimer networkTimer;
    private final int CONNECT_ROAM_BOX_NUMBER = 15;//重连络漫宝次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_wireless_relay_edit);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_wifi));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.closeKeyboard(et_wifi_password, getApplicationContext());
                finish();
            }
        });
        tv_wifi_name = (TextView) findViewById(R.id.tv_wifi_name);
        et_wifi_password = (EditText) findViewById(R.id.et_wifi_password);
        layout_wifi_select = (LinearLayout) findViewById(R.id.layout_wifi_select);
        tv_save_config = (TextView) findViewById(R.id.tv_save_config);
        layout_wifi_select.setOnClickListener(this);
        tv_save_config.setOnClickListener(this);
        headerLayout.showRightSubmitButton(R.string.toggle_mode, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(LMBAOWirelessRelayEditActivity.this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getString(R.string.activity_title_auto_obtain_ip), ActionSheetDialog.SheetItemColor.COLOR_888888,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        toActivityWithFinish(LMBAOAutoInternetEditActivity.class, null);
                                    }
                                })
                        .addSheetItem(getString(R.string.activity_title_broadband), ActionSheetDialog.SheetItemColor.COLOR_888888,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        toActivityWithFinish(LMBAOBroadbandDialEditActivity.class, null);
                                    }
                                })
                        .addSheetItem(getString(R.string.btn_static_ip), ActionSheetDialog.SheetItemColor.COLOR_888888,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        toActivity(LMBAOStaticIpEditActivity.class, null);
                                    }
                                })
                        .show();
            }
        });
        if (RoamApplication.RoamBoxConfigOld != null) {
            tv_wifi_name.setText(RoamApplication.RoamBoxConfigOld.apcli_ssid);
            et_wifi_password.setText(RoamApplication.RoamBoxConfigOld.apcli_password);
        }
        roamBoxFunction = new RoamBoxFunction(this);
        roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.str_save_config));
        wifiAdmin = new WifiAdmin(getApplicationContext());
        networkTimer = new NetworkTimer(this, uiHandler);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layout_wifi_select:
                Bundle bundle = new Bundle();
                bundle.putString(KeyValue.WIFI_NAME, tv_wifi_name.getText().toString());
                bundle.putString(KeyValue.SHOW_SYSTEM_WIFI, "false");
                bundle.putString(KeyValue.SHOW_CONNECT_WIFI, "false");
                toActivityForResult(WifiListActivity.class, GO_CHECK_WIFI, bundle);
                break;
            case R.id.tv_save_config:
                wifi_name = tv_wifi_name.getText().toString();
                wifi_password = et_wifi_password.getText().toString();
                if (wifi_name.length() > 0 && wifi_password.length() >= 8) {
                    Utility.closeKeyboard(et_wifi_password, this);
                    roamBoxSettingDialog.show();
                    wifiAdmin.startScan();
                    RoamApplication.RoamBoxConfigOld.check_ssId = wifi_name;
                    setWanWifiInfo(wifi_name, wifi_password);
                }else{
                    //无线中继的网络 是否需要8位密码限制 提示
                    ToastUtils.showToast(this,getString(R.string.str_lan_password_alert));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_CHECK_WIFI) {
            if (data != null) {
                tv_wifi_name.setText(data.getStringExtra("wifiName"));
            }
        }
    }

    /**
     * 设置无线配置
     *
     * @param wifi_name     ssId
     * @param wifi_password password
     */
    private void setWanWifiInfo(String wifi_name, String wifi_password) {
        //无线中继编辑 判断连接的wifi 是否是上次配置的wifi
        if (!networkTimer.checkRoamBoxWifi(RoamApplication.RoamBoxConfigOld.lan_ssid)) {
            //已经切换到其他网络
            uiHandler.sendEmptyMessage(MsgType.MSG_NO_ROAM_BOX_WIFI);
            return;
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("wireless");
        jsonArray.put("dhcp");
        jsonArray.put(wifi_name);
        jsonArray.put(wifi_password);
        roamBoxFunction.setRoamBoxWan(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, jsonArray, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_WIRELESS_TIMEOUT);
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                    }.getType());
                    if (result != null && result.attributes != null && result.attributes.equals("true")) {
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_WIRELESS_SUCCESS);
                    } else {
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_WIRELESS_ERROR);
                    }
                }
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_SET_WAN_WIRELESS_SUCCESS:
                connectNumber = 0;
                networkTimer.setTimeoutNumber(CONNECT_ROAM_BOX_NUMBER);
                roamBoxSettingDialog.setTitle(getString(R.string.str_lmb_restart));
                roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
                //络漫宝重启中, 请稍候....
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        roamBoxSettingDialog.setTitle(getString(R.string.str_connect_lmb));
                        uiHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                networkTimer.connectCheckSSid(RoamApplication.RoamBoxConfigOld.lan_ssid, null);
                            }
                        }, 3000);
                    }
                }, 8 * 1000);
                break;
            case MsgType.MSG_SET_WAN_WIRELESS_TIMEOUT:
                if (requestNumber >= 3) {
                    requestNumber++;
                    setWanWifiInfo(wifi_name, wifi_password);
                } else {
                    roamBoxSettingDialog.dismiss();
                    ToastUtils.showToast(this, getString(R.string.please_check_has_been_connected_to_roam_box));
                }
                break;
            case MsgType.MSG_SET_WAN_WIRELESS_ERROR:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.config_save_error));
                finish();
                break;
            case MsgType.MSG_CHANGE_NETWORK_SUCCESS:
                requestNumber = 0;
                networkTimer.stopTimer();
                roamBoxSettingDialog.setTitle(getString(R.string.str_detection_internet));
                roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_AUTO_INTERNET);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (WifiAdmin.isNetworkConnected(LMBAOWirelessRelayEditActivity.this) && WifiAdmin.ping()) {
                            uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS);
                        } else {
                            uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_ERROR);
                        }
                    }
                }).start();
                break;
            case MsgType.MSG_CHANGE_NETWORK_ERROR:
                Log.e("连接络漫宝wifi失败--->", "连接络漫宝次数---》" + connectNumber);
                networkTimer.stopTimer();
                roamBoxSettingDialog.dismiss();
                connectNumber = 0;
                ToastUtils.showToast(this, getString(R.string.str_connect_roam_box));
                break;
            case MsgType.MSG_CHANGE_NETWORK_UPDATE:
                //正在连接络漫宝，请稍候
                if (connectNumber >= CONNECT_ROAM_BOX_NUMBER - 1) {
                    Log.e("连接络漫宝wifi失败--->", "连接络漫宝次数---》" + connectNumber);
                    connectNumber = 0;
                    networkTimer.stopTimer();
                    roamBoxSettingDialog.dismiss();
                    ToastUtils.showToast(this, getString(R.string.str_connect_roam_box));
                    return;
                }
                connectNumber++;
                roamBoxSettingDialog.setTitle(getString(R.string.str_connect_lmb));
                break;
            case MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS:
                Log.e("络漫宝无线可以连接--->", "络漫宝无线可以连接并且能上外网");
                roamBoxSettingDialog.dismiss();
                RoamApplication.RoamBoxConfigOld.apcli_ssid = wifi_name;
                RoamApplication.RoamBoxConfigOld.apcli_password = wifi_password;
                RoamApplication.RoamBoxConfigOld.wan_type = "wireless";
                RoamApplication.RoamBoxConfigOld.wan_proto = RoamApplication.WAN_PROTO_DHCP;
                ToastUtils.showToast(this,getString(R.string.config_save_success));
                finish();
                break;
            case MsgType.MSG_ROAM_BOX_NETWORK_ERROR:
                Log.e("络漫宝无线可以连接--->", "络漫宝无线可以连接不能上外网");
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.str_network_no_ok_internnet));
                break;
            case MsgType.MSG_NO_ROAM_BOX_WIFI:
                Log.e("连接络漫宝wifi失败--->", "网络已经切换到其他网络---》" + connectNumber);
                networkTimer.stopTimer();
                roamBoxSettingDialog.dismiss();
                connectNumber = 0;
                ToastUtils.showToast(this, getString(R.string.str_connect_roam_box));
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (roamBoxSettingDialog.isShowing()) {
                roamBoxSettingDialog.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
