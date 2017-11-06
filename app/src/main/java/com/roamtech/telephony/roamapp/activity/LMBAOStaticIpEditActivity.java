package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
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
import com.roamtech.telephony.roamapp.view.ActionSheetDialog;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by long
 * on 2016/10/17.
 * 静态IP上网 编辑
 */

public class LMBAOStaticIpEditActivity extends HeaderBaseActivity {
    private EditText et_ip, et_subnet_mask, et_gateway, et_dns_one, et_dns_two;
    private TextView tv_save_config;
    private String ip, subnet_mask, gateway, dnsOne, dnsTwo;
    private RoamBoxSettingDialog roamDialog;
    private int connectNumber = 0;
    private RoamBoxFunction roamBoxFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_static_ip_edit);
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
        headerLayout.showRightSubmitButton(R.string.toggle_mode, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(LMBAOStaticIpEditActivity.this)
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
//                        .addSheetItem(getString(R.string.btn_wireless_relay), ActionSheetDialog.SheetItemColor.COLOR_888888,
//                                new ActionSheetDialog.OnSheetItemClickListener() {
//                                    @Override
//                                    public void onClick(int which) {
//                                        toActivityWithFinish(LMBAOWirelessRelayEditActivity.class, null);
//                                    }
//                                })
                        .addSheetItem(getString(R.string.activity_title_broadband), ActionSheetDialog.SheetItemColor.COLOR_888888,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        toActivityWithFinish(LMBAOBroadbandDialEditActivity.class, null);
                                    }
                                })
                        .show();
            }
        });

        et_ip = (EditText) findViewById(R.id.et_ip);
        et_subnet_mask = (EditText) findViewById(R.id.et_subnet_mask);
        et_gateway = (EditText) findViewById(R.id.et_gateway);
        et_dns_one = (EditText) findViewById(R.id.et_dns_one);
        et_dns_two = (EditText) findViewById(R.id.et_dns_two);
        tv_save_config = (TextView) findViewById(R.id.tv_save_config);
        if (RoamApplication.RoamBoxConfigOld != null) {
            et_ip.setText(RoamApplication.RoamBoxConfigOld.wan_ip);
            et_subnet_mask.setText(RoamApplication.RoamBoxConfigOld.wan_netmask);
            et_gateway.setText(RoamApplication.RoamBoxConfigOld.wan_gateway);
            if (RoamApplication.RoamBoxConfigOld.wan_dnsaddrs != null && RoamApplication.RoamBoxConfigOld.wan_dnsaddrs.size() > 0)
                et_dns_one.setText(RoamApplication.RoamBoxConfigOld.wan_dnsaddrs.get(0));
        }
        tv_save_config.setOnClickListener(this);
        roamDialog = new RoamBoxSettingDialog(this, getString(R.string.str_save_config));
        roamBoxFunction = new RoamBoxFunction(this);
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
                Utility.closeKeyboard(et_dns_one, getApplicationContext());
                roamDialog.show();
                roamDialog.setAnimationType(roamDialog.TYPE_ROAM_SET);
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
                        Log.e("络漫宝设置静态IP上网--->", "true");
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_STATIC_IP_SUCCESS);
                    } else {
                        Log.e("络漫宝设置静态IP上网--->", "false");
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_STATIC_IP_ERROR);
                    }
                }
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_SET_WAN_STATIC_IP_SUCCESS:
                roamDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.config_save_success));
                RoamApplication.RoamBoxConfigOld.wan_proto = "static";
                finish();
                break;
            case MsgType.MSG_SET_WAN_STATIC_IP_ERROR:
                roamDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.config_save_error));
                break;
            case MsgType.MSG_SET_WAN_STATIC_IP_TIMEOUT:
                if (connectNumber >= 4) {
                    roamDialog.dismiss();
                    ToastUtils.showToast(this, getString(R.string.config_save_error));
                    return;
                }
                connectNumber++;
                setStaticIp(ip, subnet_mask, gateway, dnsOne, dnsTwo);
                break;
        }
    }
}
