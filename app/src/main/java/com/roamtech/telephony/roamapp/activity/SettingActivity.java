package com.roamtech.telephony.roamapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.Setting;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.db.dao.CommonDao;
import com.roamtech.telephony.roamapp.db.model.AddressDBModel;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.view.ToggleButton;
import com.roamtech.telephony.roamapp.view.ToggleButton.OnToggleChanged;

import org.json.JSONObject;
import org.linphone.LinphonePreferences;

import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import java.io.IOException;
import java.util.Map;

import static com.roamtech.telephony.roamapp.enums.LoadingState.SESSION_TIME_OUT;

public class SettingActivity extends HeaderBaseActivity {
    private TextView tvPresskeySound;
    private ToggleButton togglePresskeySound;

    private TextView tvRing;
    private TextView tvMultilanguage;
    private TextView tvOneClickCall;
    private TextView tvIpCallFirst;
    private TextView tvChangePassword;
    private ToggleButton toggleIpCallFirst;
    private ToggleButton toggleAgcEnable;
    private TextView btnExitAccount;
    private TextView tvSetCallerNumber;
    private RoamDialog roamDialog;
    private LinphonePreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        mPrefs = LinphonePreferences.instance();
        roamDialog = new RoamDialog(this, getString(R.string.logouting));
        headerLayout.showTitle(getString(R.string.menu_settings));
        headerLayout.showLeftBackButton();
        tvPresskeySound = (TextView) findViewById(R.id.tv_keypresssound);
        togglePresskeySound = (ToggleButton) findViewById(R.id.toggle_keypresssound);
        tvRing = (TextView) findViewById(R.id.tv_ring);
        tvMultilanguage = (TextView) findViewById(R.id.tv_multilanguage);
        tvOneClickCall = (TextView) findViewById(R.id.tv_oneclickcall);
        tvIpCallFirst = (TextView) findViewById(R.id.tv_ipcallfirst);
        tvChangePassword = (TextView) findViewById(R.id.tv_change_password);
        tvSetCallerNumber = (TextView) findViewById(R.id.tv_set_caller_number);
        toggleIpCallFirst = (ToggleButton) findViewById(R.id.toggle_ipcallfirst);
        toggleAgcEnable = (ToggleButton) findViewById(R.id.toggle_agcenable);
        btnExitAccount = (TextView) findViewById(R.id.btnExit);
        tvPresskeySound.setOnClickListener(this);
        tvRing.setOnClickListener(this);
        tvMultilanguage.setOnClickListener(this);
        tvOneClickCall.setOnClickListener(this);
        tvIpCallFirst.setOnClickListener(this);
        tvChangePassword.setOnClickListener(this);
        tvSetCallerNumber.setOnClickListener(this);
        togglePresskeySound.setOnToggleChanged(new OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                // TODO Auto-generated method stub
            }
        });
        toggleIpCallFirst.setOnToggleChanged(new OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                // TODO Auto-generated method stub
            }
        });
        if (mPrefs.isAgcEnabled()) {
            toggleAgcEnable.setToggleOn();
        }
        toggleAgcEnable.setOnToggleChanged(new OnToggleChanged() {

            @Override
            public void onToggle(boolean on) {
                mPrefs.enableAgc(on);
            }
        });
        btnExitAccount.setOnClickListener(this);
        if (getLoginInfo().getSessionId() == null || getLoginInfo().getUserId() == null) {
            btnExitAccount.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_set_caller_number:
                if (LinphoneActivity.isInstanciated()) {
                    if ((LinphoneActivity.instance().getVoiceNumber() != null && LinphoneActivity.instance().getVoiceNumber().getPhone() != null) ||
                            (LinphoneActivity.instance().getMyTouchs() != null && LinphoneActivity.instance().getMyTouchs().size() > 0)) {
                        toActivity(CallerNumberActivity.class, null);
                    } else {
                        ToastUtils.showToast(this, getString(R.string.str_no_can_set_caller_number), Toast.LENGTH_LONG);
                    }
                } else {
                    ToastUtils.showToast(this, getString(R.string.str_no_can_set_caller_number), Toast.LENGTH_LONG);
                }
                break;
            case R.id.tv_change_password:
                toActivity(ChangePasswordActivity.class, null);
                break;
        }
        if (v == tvPresskeySound) {

        } else if (v == tvRing) {

        } else if (v == tvMultilanguage) {

        } else if (v == tvOneClickCall) {

        } else if (v == tvIpCallFirst) {

        } else if (v == btnExitAccount) {
            final TipDialog dialog = new TipDialog(this, getString(R.string.setting_exit_dialog), "");
            dialog.setRightButton(getString(R.string.button_ok), new TipDialog.OnClickListener() {
                @Override
                public void onClick(int which) {
                    roamDialog.show();
                    btnExitAccount.setEnabled(false);
                    logOut();
                }
            });

            dialog.setLeftButton(getString(R.string.button_cancel), new TipDialog.OnClickListener() {
                @Override
                public void onClick(int which) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }


    public void logOut() {
        new Setting(this).loginOut(getAuthJSONObject(), hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_LOGOUT_ERROR);
            }

            @Override
            public void onSuccess(String response) {
                UCResponse<String> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<String>>() {
                });
                uiHandler.obtainMessage(MsgType.MSG_LOGOUT_SUCCESS, result).sendToTarget();
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_LOGOUT_SUCCESS:
                UCResponse<String> result = (UCResponse<String>) msg.obj;
                if (HttpFunction.isSuccess(result.getErrorNo())) {
                    roamDialog.dismiss();
                    success();
                } else if (HttpFunction.isSessionTimeout(result.getErrorNo())) {
                    roamDialog.dismiss();
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                } else {
                    roamDialog.dismiss();
                    btnExitAccount.setEnabled(true);
                    Toast.makeText(SettingActivity.this, result.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
                break;
            case MsgType.MSG_LOGOUT_ERROR:
                roamDialog.dismiss();
                btnExitAccount.setEnabled(true);
                break;
        }
    }

    public void success() {
        setResult(Activity.RESULT_OK);
        finish();
        clearLoginInfo();
        LinphoneActivity.instance().toActivityClearTopWithState(LoginActivity.class, LinphoneActivity.FIRST_LOGIN_ACTIVITY, null);
    }
}
