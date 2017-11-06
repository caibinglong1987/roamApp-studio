package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.Blacklist;
import com.roamtech.telephony.roamapp.activity.function.GetBell;
import com.roamtech.telephony.roamapp.application.AppInterfaceImpl;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.bean.UserRDO;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.roamtech.telephony.roamapp.helper.ActivityCollector;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.umeng.analytics.MobclickAgent;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneService;

import java.util.Map;

public class LoginActivity extends BaseActivity {
    private EditText etUserName;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvFrogetPassword;
    private TextView tvRegister;
    private static final int REQUEST_CODE_REGISTER = 1;
    private RoamDialog roamDialog;
    private String password;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        etUserName = (EditText) findViewById(R.id.tv_username);
        etPassword = (EditText) findViewById(R.id.id_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvFrogetPassword = (TextView) findViewById(R.id.tv_forgetpassword);
        tvRegister = (TextView) findViewById(R.id.tv_register);
        tvRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
        tvRegister.getPaint().setAntiAlias(true);//抗锯齿
        roamDialog = new RoamDialog(this, getString(R.string.loginning));
        resetForLogin(false);
        autoLogin();
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        btnLogin.setOnClickListener(this);
        tvFrogetPassword.setOnClickListener(this);
        tvRegister.setOnClickListener(this);
    }

    /**
     * 验证用户名和密码是否存在
     *
     * @return
     */
    private boolean validate() {
        username = etUserName.getText().toString();
        password = etPassword.getText().toString();
        if (StringUtil.isTrimBlank(username)) {
            showToast(getString(R.string.not_null_userId));
            return false;
        } else if (StringUtil.isTrimBlank(password)) {
            showToast(getString(R.string.not_null_password));
            return false;
        } else if (password.length() < 6) {
            showToast(getString(R.string.pass_not_less));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == btnLogin) {
            if (validate()) {
                requestLogin();
                roamDialog.show();
            }
        } else if (v == tvFrogetPassword) {
            toActivity(ResetPasswordByRegPhoneActivity.class, null);
        } else if (v == tvRegister) {
            toActivityForResult(RegisterActivity.class, REQUEST_CODE_REGISTER, null);
        }
    }

    private void requestLogin() {
        JSONObject json = getAuthJSONObject();
        try {
            json.put("username", username);
            json.put("password", password);
            new HttpFunction(this).postJsonRequest(Constant.USER_LOGIN, json, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onSuccess(final String response) {
                    final UCResponse<UserRDO> ucResponse = JsonUtil.fromJson(response, new TypeToken<UCResponse<UserRDO>>() {
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ucResponse != null && ucResponse.getErrorNo() == 0) {
                                String userId = ucResponse.getUserId().toString();
                                String sessionId = ucResponse.getSessionId();
                                clearAccountData(userId, sessionId, ucResponse.getAttributes());
                            } else {
                                showToast(ucResponse.getErrorInfo());
                            }
                            roamDialog.dismiss();
                        }
                    });

                }

                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(R.string.login_error);
                            roamDialog.dismiss();
                        }
                    });
                }
            });
        } catch (JSONException ex) {

        }
    }


    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if (arg1 == RESULT_OK) {
            if (arg0 == REQUEST_CODE_REGISTER) {
                RoamApplication.isNewProxyConfig = true;
                startActivity(new Intent(this, LinphoneActivity.class));
            }
        }
    }

    @Override
    public void onBackPressed() {
        ActivityCollector.finishAll();
        System.exit(0);
    }

    /**
     * 重置数据 登录 操作 注册 lin_phone
     */
    private void clearAccountData(final String loginUserId, final String sessionId, final UserRDO userRDO) {
        String oldAccountUserId = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, "0");
        setLoginInfo(loginUserId, sessionId, password, userRDO);
        long delayTime = 0;
        if (!LinphoneManager.isInstanciated()) {
            delayTime = 2000;
        }
        uiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (LinphoneManager.isInstanciated()) {
                    LinphoneManager.getInstance().setUserSession(loginUserId, sessionId);
                    linphoneLogIn(loginUserId, sessionId, userRDO.getPhone(), getResources().getBoolean(R.bool.setup_account_validation_mandatory));
                }
            }
        }, delayTime);
        if (oldAccountUserId == null || !loginUserId.equals(oldAccountUserId)) {
            //跟上次登录账号不一样  清除数据
            RoamApplication.isLoadCallHistory = false;
            RoamApplication.isLoadMissCallHistory = false;
            RoamApplication.bSwitchAccount = true;
        }

        RoamApplication.isLoadGroupMessage = false;
        RoamApplication.isNewProxyConfig = true;
        new AppInterfaceImpl().initCallHistory(getApplicationContext());
        MobclickAgent.onProfileSignIn(loginUserId);
        Log.i("onResponse", "userId" + loginUserId + "sessionId" + sessionId);
        if (LinphoneActivity.isInstanciated()) {
            LinphoneActivity.instance().dismissKickOut();
        }
        startActivity(new Intent(this, LinphoneActivity.class));
    }

    /**
     * 自动登录
     */
    private void autoLogin() {
        username = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userName, "0");
        password = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_password, "0");
        if (!username.equals("0") && !password.equals("0")) {
            requestLogin();
            roamDialog.show();
        }
    }
}
