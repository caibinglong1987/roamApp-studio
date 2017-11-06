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
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.dialog.RoamBoxSettingDialog;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by long
 * on 2016/10/10 14:47
 * 络漫宝 高级设置
 */

public class LMBAOSeniorSettingActivity extends HeaderBaseActivity {
    private LinearLayout layout_channel_select;
    private TextView tv_channel, tv_save_config;
    private final int GO_CHECK_CHANNEL = 100;
    private EditText et_lan_ip;
    private RoamBoxSettingDialog roamBoxSettingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_senior_setting);
        initView();
    }

    public void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_SeniorSetting));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_channel = (TextView) findViewById(R.id.tv_channel);
        et_lan_ip = (EditText) findViewById(R.id.et_lan_ip);
        tv_save_config = (TextView) findViewById(R.id.tv_save_config);
        layout_channel_select = (LinearLayout) findViewById(R.id.layout_channel_select);
        if (RoamApplication.RoamBoxConfigOld != null && RoamApplication.RoamBoxConfigOld.lan_channel != null) {
            tv_channel.setText(RoamApplication.RoamBoxConfigOld.lan_channel);
            et_lan_ip.setText(RoamApplication.RoamBoxConfigOld.lan_ip);
        }
        layout_channel_select.setOnClickListener(this);
        tv_save_config.setOnClickListener(this);
        roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.str_save_config));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.layout_channel_select:
                Bundle bundle = new Bundle();
                bundle.putString("ChannelNumber", tv_channel.getText().toString());
                toActivityForResult(LMBAOChannelActivity.class, GO_CHECK_CHANNEL, bundle);
                break;
            case R.id.tv_save_config:
                String lanIp = et_lan_ip.getText().toString();
                if (lanIp.length() > 0) {
                    roamBoxSettingDialog.show();
                    roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
                    setPhoneWifiInfo(lanIp, tv_channel.getText().toString());
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_CHECK_CHANNEL) {
            if (data != null) {
                tv_channel.setText(data.getStringExtra("ChannelNumber"));
            }
        }
    }

    /**
     * 配置 设置无线配置 信息
     */
    private void setPhoneWifiInfo(String lanIp, String lan_channel) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(RoamApplication.RoamBoxConfigOld.phone);
        jsonArray.put(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId));
        jsonArray.put(RoamApplication.RoamBoxConfigOld.lan_ssid);
        jsonArray.put(RoamApplication.RoamBoxConfigOld.lan_password);
        jsonArray.put(lanIp);
        jsonArray.put(lan_channel);
        Log.e("设置络漫宝LAN口", "地址-》" + Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken + "||lanSSid->" + RoamApplication.RoamBoxConfigOld.lan_ssid
                + "||lan_password->" + RoamApplication.RoamBoxConfigOld.lan_password
                + "||lan_ip->" + lanIp
                + "||lan_channel->" + lan_channel);
        new RoamBoxFunction(this).setRoamBoxPhoneLan(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, jsonArray, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_SET_LAN_WIFI_TIMEOUT);
            }

            @Override
            public void onSuccess(String response) {
                CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                }.getType());
                if (result != null && result.attributes != null && result.attributes.equals("true")) {
                    uiHandler.sendEmptyMessage(MsgType.MSG_SET_LAN_WIFI_SUCCESS);
                } else {
                    uiHandler.sendEmptyMessage(MsgType.MSG_SET_LAN_WIFI_TIMEOUT);
                }
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_SET_LAN_WIFI_SUCCESS:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.config_save_success));
                finish();
                break;
            case MsgType.MSG_SET_LAN_WIFI_TIMEOUT:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.config_save_error));
                break;
        }
    }
}
