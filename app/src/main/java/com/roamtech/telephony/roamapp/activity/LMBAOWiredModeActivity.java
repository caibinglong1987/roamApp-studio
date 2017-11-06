package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.bean.NetworkConfigBean;
import com.roamtech.telephony.roamapp.dialog.RoamBoxSettingDialog;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by long
 * on 2016/10/12.
 * 络漫宝 有线模式 设置
 */

public class LMBAOWiredModeActivity extends HeaderBaseActivity {
    private LinearLayout layout_broadband_dialing, layout_automatic_ip, layout_static_ip;
    private LinearLayout layout_all_wired;
    private RoamBoxSettingDialog roamBoxSettingDialog;
    private RoamBoxFunction roamBoxFunction;
    private int connectNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lbm_wired_mode);
        initView();
    }

    private void initView() {
        RoamApplication.isAutoCancel = getIntent().getBooleanExtra("bDetectionInternet", false);
        headerLayout.showTitle(getString(R.string.connection_internet));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_broadband_dialing = (LinearLayout) findViewById(R.id.layout_broadband_dialing);
        layout_automatic_ip = (LinearLayout) findViewById(R.id.layout_automatic_ip);
        layout_static_ip = (LinearLayout) findViewById(R.id.layout_static_ip);
        layout_all_wired = (LinearLayout) findViewById(R.id.layout_all_wired);

        layout_broadband_dialing.setOnClickListener(this);
        layout_automatic_ip.setOnClickListener(this);
        layout_static_ip.setOnClickListener(this);

        roamBoxFunction = new RoamBoxFunction(this);
        roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.auto_detection_internet_way));
        roamBoxSettingDialog.registerCallback(callback);
        roamBoxSettingDialog.show();
        roamBoxSettingDialog.showCancel(true);
        if (RoamApplication.isAutoCancel) {
            uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_ERROR);
        } else {
            detectionInternetWay();
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layout_broadband_dialing:
                toActivity(LMBAOBroadbandActivity.class, null);
                break;
            case R.id.layout_automatic_ip:
                if (RoamApplication.RoamBoxConfigOld != null && RoamApplication.RoamBoxConfigOld.wan_type.equals("line")
                        && RoamApplication.RoamBoxConfigOld.wan_proto.equals("dhcp")) {
                    toActivity(LMBAOPhoneSettingActivity.class, null);
                } else {
                    //设置 自动 获取IP
                    roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.str_save_config));
                    roamBoxSettingDialog.show();
                    roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
                    setWanAuto();
                }
                break;
            case R.id.layout_static_ip:
                toActivity(LMBAOStaticIpActivity.class, null);
                break;
        }
    }

    private void setWanAuto() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("line");
        jsonArray.put("dhcp");
        new RoamBoxFunction(this).setRoamBoxWan(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, jsonArray, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_DHCP_ERROR);
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                    }.getType());
                    if (result != null && result.attributes != null && result.attributes.equals("true")) {
                        Log.e("络漫宝设置自动上网--->", result.attributes);
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_DHCP_SUCCESS);
                    } else {
                        Log.e("络漫宝设置自动上网--->", "false");
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_DHCP_ERROR);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (RoamApplication.isAutoCancel) {
            layout_all_wired.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_DETECT_NETWORK_SUCCESS:
                if (RoamApplication.isAutoCancel) {
                    roamBoxSettingDialog.dismiss();
                    layout_all_wired.setVisibility(View.VISIBLE);
                    return;
                }
                NetworkConfigBean networkConfigBean = (NetworkConfigBean) msg.obj;
                if (networkConfigBean.wan_type.equals("line")) //有线
                {
                    if (networkConfigBean.wan_proto.equals("dhcp") && networkConfigBean.wan_ip.length() > 0) {
                        //有线 自动获取IP
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (WifiAdmin.isNetworkConnected(LMBAOWiredModeActivity.this) && WifiAdmin.ping()) {
                                    uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS);
                                } else {
                                    uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_ERROR);
                                }
                            }
                        }).start();
                    } else if (networkConfigBean.wan_proto.equals("pppoe")) {
                        //有线 宽带拨号
                        RoamApplication.isAutoCancel = true;
                        roamBoxSettingDialog.dismiss();
                        toActivity(LMBAOBroadbandActivity.class, null);
                    } else if (networkConfigBean.wan_proto.equals("static")) { //
                        //静态IP
                        RoamApplication.isAutoCancel = true;
                        roamBoxSettingDialog.dismiss();
                        toActivity(LMBAOStaticIpActivity.class, null);
                    } else {
                        RoamApplication.isAutoCancel = true;
                        roamBoxSettingDialog.dismiss();
                        layout_all_wired.setVisibility(View.VISIBLE);
                    }
                } else { //无线模式
                    RoamApplication.isAutoCancel = true;
                    roamBoxSettingDialog.dismiss();
                    layout_all_wired.setVisibility(View.VISIBLE);
                }
                break;
            case MsgType.MSG_DETECT_NETWORK_ERROR:
                RoamApplication.isAutoCancel = true;
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.detecting_network_failure));
                layout_all_wired.setVisibility(View.VISIBLE);
                break;
            case MsgType.MSG_DETECT_NETWORK_TIMEOUT:
                if (connectNumber >= 2 || RoamApplication.isAutoCancel) {
                    uiHandler.sendEmptyMessage(MsgType.MSG_DETECT_NETWORK_ERROR);
                    return;
                }
                connectNumber++;
                detectionInternetWay();
                break;
            case MsgType.MSG_ROAM_BOX_NETWORK_ERROR:
                Log.e("络漫宝无线可以连接--->", "络漫宝无线可以连接不能上外网");
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.str_network_no_ok_internnet));
                layout_all_wired.setVisibility(View.VISIBLE);
                RoamApplication.isAutoCancel = true;
                break;
            case MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS:
                roamBoxSettingDialog.dismiss();
                if (RoamApplication.isAutoCancel) {
                    layout_all_wired.setVisibility(View.VISIBLE);
                    return;
                }
                RoamApplication.isAutoCancel = true;
                toActivity(LMBAOAutoInternetActivity.class, null);
                break;
            case MsgType.MSG_SET_WAN_DHCP_SUCCESS:
                //络漫宝重启中, 请稍候....
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        roamBoxSettingDialog.dismiss();
                        toActivity(LMBAOPhoneSettingActivity.class, null);
                    }
                }, 5 * 1000);
                break;
            case MsgType.MSG_SET_WAN_DHCP_ERROR:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.str_auto_obtain_ip_error));
                break;
        }

    }

    public RoamBoxSettingDialog.iCallback callback = new RoamBoxSettingDialog.iCallback() {
        @Override
        public void cancel() {
            layout_all_wired.setVisibility(View.VISIBLE);
            RoamApplication.isAutoCancel = true;
        }
    };

    /**
     * 监测上网方式
     */
    private void detectionInternetWay() {
        roamBoxFunction.getRoamBoxConfigurationInfo(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                if (!RoamApplication.isAutoCancel) {
                    uiHandler.sendEmptyMessage(MsgType.MSG_DETECT_NETWORK_TIMEOUT);
                }
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    CommonRoamBox<NetworkConfigBean> networkConfigBean = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<NetworkConfigBean>>() {
                    }.getType());
                    if (networkConfigBean != null && networkConfigBean.attributes != null) {
                        RoamApplication.RoamBoxConfigOld = networkConfigBean.attributes;
                        Log.e("络漫宝配置--->", RoamApplication.RoamBoxConfigOld.toString());
                        uiHandler.obtainMessage(MsgType.MSG_DETECT_NETWORK_SUCCESS, RoamApplication.RoamBoxConfigOld).sendToTarget();
                    } else {
                        uiHandler.sendEmptyMessage(MsgType.MSG_DETECT_NETWORK_ERROR);
                    }
                }
            }
        });
    }
}
