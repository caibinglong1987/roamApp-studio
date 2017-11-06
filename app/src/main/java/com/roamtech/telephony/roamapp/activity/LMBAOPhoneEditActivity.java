package com.roamtech.telephony.roamapp.activity;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
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
import com.roamtech.telephony.roamapp.util.Utility;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by long
 * on 2016/10/10 11:20
 * 络漫宝 号码配置
 */

public class LMBAOPhoneEditActivity extends HeaderBaseActivity {
    private TextView tv_later_set, tv_save_config;
    private LinearLayout layout_select_number;
    private EditText et_phone_number;
    private String phone_number;
    private RoamBoxFunction roamBoxFunction;
    private RoamBoxSettingDialog roamBoxSettingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_num_edit);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_lmb_number));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        layout_select_number = (LinearLayout) findViewById(R.id.layout_select_number);
        tv_later_set = (TextView) findViewById(R.id.tv_later_set);
        tv_save_config = (TextView) findViewById(R.id.tv_save_config);
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        tv_later_set.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_later_set.getPaint().setAntiAlias(true);//抗锯
        layout_select_number.setOnClickListener(this);
        tv_later_set.setOnClickListener(this);
        tv_save_config.setOnClickListener(this);
        et_phone_number.setText(RoamApplication.RoamBoxConfigOld.phone);
        et_phone_number.setSelection(et_phone_number.getText().length());
        roamBoxFunction = new RoamBoxFunction(this);
        roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.str_save_config));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_later_set:
                break;
//            case R.id.layout_select_number:
//                break;
            case R.id.tv_save_config:
                //配置手机号码
                phone_number = et_phone_number.getText().toString();
                if (phone_number.length() == 11) {
                    Utility.closeKeyboard(et_phone_number, getApplicationContext());
                    roamBoxSettingDialog.show();
                    roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
                    setPhoneInfo();
                }else {
                    ToastUtils.showToast(this,getString(R.string.str_alert_no_phone_number));
                }
                break;
        }
    }

    /**
     * 设置 络漫宝号码
     */
    private void setPhoneInfo() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(phone_number);
        jsonArray.put(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId));
        roamBoxFunction.setRoamBoxPhone(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, jsonArray, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_PHONE_TIMEOUT);
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                    }.getType());
                    if (result != null && result.attributes != null && result.attributes.equals("true")) {
                        Log.e("络漫宝设置手机号码--->", "true");
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_PHONE_SUCCESS);
                    } else {
                        Log.e("络漫宝设置手机号码--->", "false");
                        uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_PHONE_ERROR);
                    }
                }
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_SET_WAN_PHONE_SUCCESS:
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        roamBoxSettingDialog.setTitle(getString(R.string.str_now_register_new_phone));
                        uiHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                roamBoxSettingDialog.dismiss();
                                RoamApplication.RoamBoxConfigOld.phone = phone_number;
                                if (LinphoneActivity.isInstanciated()) {
                                    RoamApplication.bGoConfig = true;
                                    LinphoneActivity.instance().requestMyTouchDBModels();
                                }
                                ToastUtils.showToast(LMBAOPhoneEditActivity.this, getString(R.string.config_save_success));
                                finish();
                            }
                        }, 3000);
                    }
                }, 4000);
                break;
            case MsgType.MSG_SET_WAN_PHONE_TIMEOUT:
            case MsgType.MSG_SET_WAN_PHONE_ERROR:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.config_save_error));
                break;
        }
    }
}
