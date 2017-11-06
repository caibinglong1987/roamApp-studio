package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.os.Message;
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
import com.roamtech.telephony.roamapp.util.NetworkTimer;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.util.Utility;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by long
 * on 2016/10/10 12:43
 * 络漫宝 无线网络 配置 编辑
 */

public class LMBAOWifiEditActivity extends HeaderBaseActivity {
    private TextView tv_save_config;
    private EditText et_wifi_password, et_wifi_last_name;
    private String wifi_name, wifi_password;
    private RoamBoxFunction roamBoxFunction;
    private RoamBoxSettingDialog roamBoxSettingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_wifi_edit);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_wifi_set));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.closeKeyboard(et_wifi_password, getApplicationContext());
                finish();
            }
        });
        tv_save_config = (TextView) findViewById(R.id.tv_save_config);
        et_wifi_password = (EditText) findViewById(R.id.et_wifi_password);
        et_wifi_last_name = (EditText) findViewById(R.id.et_wifi_last_name);
        tv_save_config.setOnClickListener(this);

        if (RoamApplication.RoamBoxConfigOld != null) {
            et_wifi_last_name.setText(RoamApplication.RoamBoxConfigOld.lan_ssid);
            et_wifi_password.setText(RoamApplication.RoamBoxConfigOld.lan_password);
        }
        roamBoxFunction = new RoamBoxFunction(this);
        roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.str_save_config));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save_config:
                wifi_name = et_wifi_last_name.getText().toString();
                wifi_password = et_wifi_password.getText().toString();
                if (wifi_name.length() > 0 && wifi_password.length() >= 8) {
                    Utility.closeKeyboard(et_wifi_password, getApplicationContext());
                    setLanWifiInfo(wifi_name, wifi_password);
                } else {
                    ToastUtils.showToast(this, getString(R.string.str_lan_password_alert));
                }
                break;
        }
    }

    /**
     * 设置无线配置
     *
     * @param wifi_name     ssId
     * @param wifi_password password
     */
    private void setLanWifiInfo(String wifi_name, String wifi_password) {
        roamBoxSettingDialog.show();
        roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(wifi_name);
        jsonArray.put(wifi_password);
        jsonArray.put(RoamApplication.RoamBoxConfigOld.lan_ip);
        jsonArray.put(RoamApplication.RoamBoxConfigOld.lan_channel);
        roamBoxFunction.setRoamBoxLan(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, jsonArray, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                //uiHandler.sendEmptyMessage(MsgType.MSG_SET_LAN_WIFI_TIMEOUT);
                uiHandler.sendEmptyMessage(MsgType.MSG_SET_LAN_WIFI_ERROR);
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                    }.getType());
                    if (result != null && result.attributes != null && result.attributes.equals("true")) {
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_LAN_WIFI_SUCCESS);
                    } else {
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_LAN_WIFI_ERROR);
                    }
                }
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_SET_LAN_WIFI_SUCCESS:
                roamBoxSettingDialog.setTitle(getString(R.string.str_lmb_restart));
                //络漫宝重启中, 请稍候....
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        roamBoxSettingDialog.setTitle(getString(R.string.str_connect_lmb));
                        uiHandler.sendEmptyMessage(MsgType.MSG_RESTART_ROAM_BOX_SUCCESS);
                    }
                }, 8 * 1000);
                break;
            case MsgType.MSG_SET_LAN_WIFI_ERROR:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.config_save_error));
                break;
            case MsgType.MSG_RESTART_ROAM_BOX_SUCCESS:
                roamBoxSettingDialog.dismiss();
                RoamApplication.RoamBoxConfigOld.lan_ssid = wifi_name;
                RoamApplication.RoamBoxConfigOld.lan_password = wifi_password;
                ToastUtils.showToast(this, getString(R.string.config_save_success));
                finish();
                break;
        }
    }
}
