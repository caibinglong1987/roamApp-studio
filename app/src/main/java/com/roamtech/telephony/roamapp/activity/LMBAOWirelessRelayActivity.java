package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
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
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by long
 * on 2016/10/12.
 * 无线中继 网络设置
 */

public class LMBAOWirelessRelayActivity extends HeaderBaseActivity {
    public final int GO_CHECK_WIFI = 100;
    private TextView tv_next, et_wifi_name;
    private LinearLayout layout_wifi_select;
    private EditText et_wifi_password;
    private String wifi_name, wifi_password;
    private int requestNumber = 0; //请求超时次数
    private RoamBoxFunction roamBoxFunction;
    private int connectNumber = 0;
    private RoamBoxSettingDialog roamBoxSettingDialog;
    private NetworkTimer networkTimer;
    private final int CONNECT_ROAM_BOX_NUMBER = 15;//重连络漫宝次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_wireless_relay);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_relay_internet));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_next = (TextView) findViewById(R.id.tv_next);
        layout_wifi_select = (LinearLayout) findViewById(R.id.layout_wifi_select);
        et_wifi_name = (TextView) findViewById(R.id.et_wifi_name);
        et_wifi_password = (EditText) findViewById(R.id.et_wifi_password);
        layout_wifi_select.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        roamBoxFunction = new RoamBoxFunction(this);
        roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.str_save_config));
        networkTimer = new NetworkTimer(this, uiHandler);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                //连接网络
                connectNumber = 0;
                wifi_name = et_wifi_name.getText().toString();
                wifi_password = et_wifi_password.getText().toString();
                if (wifi_name.length() > 0 && wifi_password.length() >= 8) {
                    setWanWireless(wifi_name, wifi_password);
                } else {
                    //无线中继的网络 是否需要8位密码限制 提示
                    ToastUtils.showToast(this, getString(R.string.str_lan_password_alert));
                }
                break;
            case R.id.layout_wifi_select:
                Bundle bundle = new Bundle();
                bundle.putString(KeyValue.WIFI_NAME, et_wifi_name.getText().toString());
                bundle.putString(KeyValue.SHOW_SYSTEM_WIFI, "false");
                bundle.putString(KeyValue.SHOW_CONNECT_WIFI, "false");
                toActivityForResult(WifiListActivity.class, GO_CHECK_WIFI, bundle);
                break;
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_SET_WAN_WIRELESS_SUCCESS:
                //需要连接网络才能进行下一步
                RoamApplication.RoamBoxConfigOld.lan_ssid = wifi_name;
                RoamApplication.RoamBoxConfigOld.lan_password = wifi_password;
                connectNumber = 0;
                networkTimer.setTimeoutNumber(CONNECT_ROAM_BOX_NUMBER);
                roamBoxSettingDialog.setTitle(getString(R.string.str_lmb_restart));
                roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
                //络漫宝重启中, 请稍候....
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //startTimer();
                        roamBoxSettingDialog.setTitle(getString(R.string.str_connect_lmb));
                        uiHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                networkTimer.connectCheckSSid(RoamApplication.RoamBoxConfigOld.check_ssId, wifi_password);
                            }
                        }, 3000);
                    }
                }, 8 * 1000);
                break;
            case MsgType.MSG_SET_WAN_WIRELESS_TIMEOUT:
                if (requestNumber >= 3) {
                    connectNumber = 0;
                    requestNumber = 0;
                    roamBoxSettingDialog.dismiss();
                    ToastUtils.showToast(this, getString(R.string.please_check_has_been_connected_to_roam_box));
                    return;
                }
                requestNumber++;
                setWanWireless(wifi_name, wifi_password);
                break;
            case MsgType.MSG_SET_WAN_WIRELESS_ERROR:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.config_save_error));
                break;
            case MsgType.MSG_CHANGE_NETWORK_SUCCESS:
                Log.e("络漫宝无线可以连接--->", "准备重启络漫宝");
                networkTimer.stopTimer();
                requestNumber = 0;
                connectNumber = 0;
                roamBoxSettingDialog.setTitle(getString(R.string.str_detection_internet));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (WifiAdmin.isNetworkConnected(LMBAOWirelessRelayActivity.this) && WifiAdmin.ping()) {
                            uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS);
                        } else {
                            uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_ERROR);
                        }
                    }
                }).start();
                //getHomePageData();
                break;
            case MsgType.MSG_CHANGE_NETWORK_ERROR:
                roamBoxSettingDialog.dismiss();
                connectNumber = 0;
                networkTimer.stopTimer();
                ToastUtils.showToast(this, getString(R.string.str_connect_roam_box));
                break;
            case MsgType.MSG_CHANGE_NETWORK_UPDATE:
                if (connectNumber >= CONNECT_ROAM_BOX_NUMBER - 1) {
                    Log.e("连接络漫宝wifi失败--->", "连接络漫宝次数---》" + connectNumber);
                    roamBoxSettingDialog.dismiss();
                    connectNumber = 0;
                    networkTimer.stopTimer();
                    ToastUtils.showToast(this, getString(R.string.str_connect_roam_box));
                    return;
                }
                connectNumber++;
                roamBoxSettingDialog.setTitle(getString(R.string.str_connect_lmb));
                break;
            case MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS:
                Log.e("络漫宝无线可以连接--->", "络漫宝无线可以连接并且能上外网");
                RoamApplication.RoamBoxConfigOld.wan_proto = RoamApplication.WAN_PROTO_WIRELESS;
                RoamApplication.RoamBoxConfigOld.wan_type = "wireless";
                roamBoxSettingDialog.dismiss();
                toActivity(LMBAOWiFiSetActivity.class, null);
                break;
            case MsgType.MSG_ROAM_BOX_NETWORK_ERROR:
                Log.e("络漫宝无线可以连接--->", "络漫宝无线可以连接不能上外网");
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.str_network_no_ok_internnet));
                break;
            case MsgType.MSG_NO_ROAM_BOX_WIFI:
                Log.e("连接络漫宝wifi失败--->", "网络已经切换到其他网络---》" + connectNumber);
                roamBoxSettingDialog.dismiss();
                connectNumber = 0;
                networkTimer.stopTimer();
                ToastUtils.showToast(this, getString(R.string.str_connect_roam_box));
                break;
        }
    }

    /**
     * 设置无线配置
     *
     * @param wifi_name     ssId
     * @param wifi_password password
     */
    private void setWanWireless(String wifi_name, String wifi_password) {
        if (!networkTimer.checkRoamBoxWifi(RoamApplication.RoamBoxConfigOld.check_ssId)) {
            //已经切换到其他网络
            uiHandler.sendEmptyMessage(MsgType.MSG_NO_ROAM_BOX_WIFI);
            return;
        }
        if (RoamApplication.RoamBoxToken != null) {
            roamBoxSettingDialog.show();
            roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("wireless");
            jsonArray.put("dhcp");
            jsonArray.put(wifi_name);
            jsonArray.put(wifi_password);
            roamBoxFunction.setRoamBoxWan(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, jsonArray, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    //uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_WIRELESS_TIMEOUT);
                    uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_WIRELESS_SUCCESS);
                }

                @Override
                public void onSuccess(String response) {
                    if (response != null) {
                        CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                        }.getType());
                        if (result != null && result.attributes != null && result.attributes.equals("true")) {
                            Log.e("络漫宝设置无线中继结果--->", result.attributes);
                            uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_WIRELESS_SUCCESS);
                        } else {
                            Log.e("络漫宝设置无线中继结果--->", "false");
                            uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_WIRELESS_ERROR);
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_CHECK_WIFI) {
            if (data != null) {
                et_wifi_name.setText(data.getStringExtra("wifiName"));
            }
        }
    }

    /**
     * 测试是否能获取服务器数据
     * 间接证明网络是否正常
     */
//    private void getHomePageData() {
//        new HomePager(this).getListBanner(getAuthJSONObject(), hashCode(), new HttpBusinessCallback() {
//            @Override
//            public void onFailure(Map<String, ?> errorMap) {
//                uiHandler.sendEmptyMessage(MsgType.MSG_GET_HOME_HEAD_LINE_TIMEOUT);
//            }
//
//            @Override
//            public void onSuccess(String response) {
//                if (response != null) {
//                    UCResponse<HomePageRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<HomePageRDO>>() {
//                    }.getType());
//                    if (result != null && result.getAttributes() != null) {
//                        //listHeadLine = result.getAttributes().homepages;
//                        uiHandler.sendEmptyMessage(MsgType.MSG_GET_HOME_HEAD_LINE_SUCCESS);
//                    }else{
//                        uiHandler.sendEmptyMessage(MsgType.MSG_GET_HOME_HEAD_LINE_ERROR);
//                    }
//                }
//            }
//        });
//    }
}
