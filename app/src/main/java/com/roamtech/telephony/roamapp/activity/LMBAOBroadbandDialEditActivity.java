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
import com.roamtech.telephony.roamapp.view.ActionSheetDialog;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by long
 * on 2016/10/10 14:50
 * 络漫宝 宽带拨号 上网 编辑 账号密码
 */

public class LMBAOBroadbandDialEditActivity extends HeaderBaseActivity {
    private TextView tv_save_config;
    private EditText et_broadband_password, et_broadband_account;
    private String broadband_account,broadband_password;
    private RoamBoxSettingDialog roamBoxSettingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_boardband_dial_edit);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_broadband));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_save_config = (TextView) findViewById(R.id.tv_save_config);
        et_broadband_password = (EditText) findViewById(R.id.et_broadband_password);
        et_broadband_account = (EditText) findViewById(R.id.et_broadband_account);
        tv_save_config.setOnClickListener(this);

        headerLayout.showRightSubmitButton(R.string.toggle_mode, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(LMBAOBroadbandDialEditActivity.this)
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
                        .addSheetItem(getString(R.string.btn_static_ip), ActionSheetDialog.SheetItemColor.COLOR_888888,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        toActivityWithFinish(LMBAOStaticIpEditActivity.class, null);
                                    }
                                })
//                        .addSheetItem(getString(R.string.btn_wireless_relay), ActionSheetDialog.SheetItemColor.COLOR_888888,
//                                new ActionSheetDialog.OnSheetItemClickListener() {
//                                    @Override
//                                    public void onClick(int which) {
//                                        toActivityWithFinish(LMBAOWirelessRelayEditActivity.class, null);
//                                    }
//                                })
                        .show();
            }
        });
        if (RoamApplication.RoamBoxConfigOld != null){
            et_broadband_account.setText(RoamApplication.RoamBoxConfigOld.pppoe_username);
            et_broadband_password.setText(RoamApplication.RoamBoxConfigOld.pppoe_password);
        }
        roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.str_save_config));
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_save_config:
                broadband_account = et_broadband_account.getText().toString();
                broadband_password = et_broadband_password.getText().toString();
                if (broadband_account.length() > 0 && broadband_password.length() > 0) {
                    roamBoxSettingDialog.show();
                    roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
                    setWanBroadband(broadband_account, broadband_password);
                }
                break;
        }
    }

    /**
     * 设置 络漫宝 宽带 账号 密码
     */
    private void setWanBroadband(String broadband_account, String broadband_password) {
        //获取络漫宝 配置
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("line");
        jsonArray.put("pppoe");
        jsonArray.put(broadband_account);
        jsonArray.put(broadband_password);
        new RoamBoxFunction(this).setRoamBoxWan(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken,
                jsonArray, hashCode(), new HttpBusinessCallback() {
                    @Override
                    public void onFailure(Map<String, ?> errorMap) {
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_BROADBAND_ERROR);
                    }

                    @Override
                    public void onSuccess(String response) {
                        if (response != null) {
                            CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                            }.getType());
                            if (result != null && result.attributes.equals("true")) {
                                Log.e("络漫宝设置宽带账号上网--->", result.attributes);
                                uiHandler.sendEmptyMessage(MsgType.MSG_SET_BROADBAND_SUCCESS);
                            } else {
                                Log.e("络漫宝设置宽带账号上网--->", "false");
                                uiHandler.sendEmptyMessage(MsgType.MSG_SET_BROADBAND_ERROR);
                            }
                        }
                    }
                });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_SET_BROADBAND_SUCCESS:
                roamBoxSettingDialog.dismiss();
                RoamApplication.RoamBoxConfigOld.wan_proto = "pppoe";
                RoamApplication.RoamBoxConfigOld.pppoe_username = broadband_account;
                RoamApplication.RoamBoxConfigOld.pppoe_password = broadband_password;
                ToastUtils.showToast(this,getString(R.string.config_save_success));
                finish();
                break;
            case MsgType.MSG_SET_BROADBAND_ERROR:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.config_save_error));
                break;
        }
    }
}
