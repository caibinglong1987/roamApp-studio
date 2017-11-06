package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.dialog.RoamBoxSettingDialog;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.util.Utility;
import com.roamtech.telephony.roamapp.util.VerificationUtil;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by long
 * on 2016/10/17.
 * 静态IP上网
 */

public class LMBAOStaticIpActivity extends HeaderBaseActivity {
    private EditText et_ip, et_subnet_mask, et_gateway, et_dns_one, et_dns_two;
    private TextView tv_save_config;
    private String ip, subnet_mask, gateway, dnsOne, dnsTwo;

    private RoamBoxFunction roamBoxFunction;
    private RoamBoxSettingDialog roamBoxSettingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_lmb_static_ip);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_static_ip));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.closeKeyboard(et_ip, getApplicationContext());
                finish();
            }
        });
        et_ip = (EditText) findViewById(R.id.et_ip);
        et_subnet_mask = (EditText) findViewById(R.id.et_subnet_mask);
        et_gateway = (EditText) findViewById(R.id.et_gateway);
        et_dns_one = (EditText) findViewById(R.id.et_dns_one);
        et_dns_two = (EditText) findViewById(R.id.et_dns_two);
        tv_save_config = (TextView) findViewById(R.id.tv_save_config);
        if (RoamApplication.RoamBoxConfigOld != null && RoamApplication.RoamBoxConfigOld.wan_ip != null
                && RoamApplication.RoamBoxConfigOld.wan_netmask != null && RoamApplication.RoamBoxConfigOld.wan_gateway != null) {
            et_ip.setText(RoamApplication.RoamBoxConfigOld.wan_ip);
            et_subnet_mask.setText(RoamApplication.RoamBoxConfigOld.wan_netmask);
            et_gateway.setText(RoamApplication.RoamBoxConfigOld.wan_gateway);
            if (RoamApplication.RoamBoxConfigOld.wan_dnsaddrs != null && RoamApplication.RoamBoxConfigOld.wan_dnsaddrs.size() > 0)
                et_dns_one.setText(RoamApplication.RoamBoxConfigOld.wan_dnsaddrs.get(0));
        }
        tv_save_config.setOnClickListener(this);
        roamBoxFunction = new RoamBoxFunction(this);
        roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.str_save_config));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save_config:
                ip = et_ip.getText().toString();
                subnet_mask = et_subnet_mask.getText().toString();
                gateway = et_gateway.getText().toString();
                dnsOne = et_dns_one.getText().toString();
                dnsTwo = et_dns_two.getText().toString();
                if (!VerificationUtil.matches(ip)) {
                    ToastUtils.showToast(this, String.format(getString(R.string.data_error), getString(R.string.str_ip)));
                    return;
                }
                if (subnet_mask.length() == 0) {
                    ToastUtils.showToast(this, String.format(getString(R.string.data_error), getString(R.string.str_sub_netmask)));
                    return;
                }
                if (!VerificationUtil.matches(gateway)) {
                    ToastUtils.showToast(this, String.format(getString(R.string.data_error), getString(R.string.str_gateway)));
                    return;
                }
                if (dnsOne.length() == 0) {
                    ToastUtils.showToast(this, String.format(getString(R.string.data_error), getString(R.string.str_dns)));
                    return;
                }
                roamBoxSettingDialog.show();
                roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
                setStaticIp(ip, subnet_mask, gateway, dnsOne, dnsTwo);
                break;
        }
    }

    /**
     * 设置 静态IP 上网
     */
    private void setStaticIp(String ip, String subnet_mask, String gateway, String dnsOne, String dnsTwo) {
        //获取络漫宝 配置
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("line");
        jsonArray.put("static");
        jsonArray.put(ip);
        jsonArray.put(gateway);
        jsonArray.put(subnet_mask);
        jsonArray.put(dnsOne);
        //jsonArray.put(dnsTwo);
        if (Constant.ROAM_BOX_CONFIG.length()>0 && RoamApplication.RoamBoxToken != null) {
            roamBoxFunction.setRoamBoxWan(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, jsonArray, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_STATIC_IP_TIMEOUT);
                }

                @Override
                public void onSuccess(String response) {
                    if (response != null) {
                        CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                        }.getType());
                        if (result != null && result.attributes != null && result.attributes.equals("true")) {
                            Log.e("络漫宝设置静态IP上网--->", result.attributes);
                            uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_STATIC_IP_SUCCESS);
                        } else {
                            Log.e("络漫宝设置静态IP上网--->", "false");
                            uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_STATIC_IP_ERROR);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_SET_WAN_STATIC_IP_SUCCESS:
                roamBoxSettingDialog.setTitle(getString(R.string.str_detection_internet));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (WifiAdmin.isNetworkConnected(LMBAOStaticIpActivity.this) && WifiAdmin.ping()) {
                            uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS);
                        } else {
                            uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_ERROR);
                        }
                    }
                }).start();
                break;
            case MsgType.MSG_SET_WAN_STATIC_IP_TIMEOUT:
            case MsgType.MSG_SET_WAN_STATIC_IP_ERROR:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.config_save_error));
                break;
            case MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS:
                Log.e("络漫宝静态IP可以连接--->", "络漫宝无线可以连接并且能上外网");
                RoamApplication.RoamBoxConfigOld.wan_proto = RoamApplication.WAN_PROTO_STATIC;
                RoamApplication.RoamBoxConfigOld.wan_type = "line";
                roamBoxSettingDialog.dismiss();
                toActivity(LMBAOPhoneSettingActivity.class, null);
                break;
            case MsgType.MSG_ROAM_BOX_NETWORK_ERROR:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.str_network_no_ok_internnet));
                Log.e("络漫宝静态IP可以连接--->", "络漫宝无线可以连接不能上外网");
                break;
        }
    }
}
