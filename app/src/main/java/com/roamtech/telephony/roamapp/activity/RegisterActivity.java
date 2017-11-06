package com.roamtech.telephony.roamapp.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.AccountExistRDO;
import com.roamtech.telephony.roamapp.bean.CheckCodeRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.bean.UserRDO;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.util.VerificationUtil;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneManager;

import java.util.Map;


public class RegisterActivity extends BaseActivity {
    private EditText etUserName;
    private EditText etDentifyingCode;
    private EditText etPassword;
    private Button btnRegister;
    private TextView tvSendDentifyingCode;
    private TextView tvUserAgree;
    private int checkId;
    //	private boolean usernameOk = false;
//	private boolean checkcodeOk = false;
    private SmsContent contentObserver;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //注册短信变化监听
        contentObserver = new SmsContent(new Handler());
        getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true, contentObserver);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        etUserName = (EditText) findViewById(R.id.tv_username);
        etDentifyingCode = (EditText) findViewById(R.id.id_identifying_code);
        etPassword = (EditText) findViewById(R.id.id_password);
        btnRegister = (Button) findViewById(R.id.btn_register);
        tvSendDentifyingCode = (TextView) findViewById(R.id.id_send_identifying_code);
        tvUserAgree = (TextView) findViewById(R.id.tvUserAgree);
        //String webLinkText = "<font color='#0bd3a6'><a href='"+getString(R.string.useragreement_url)+"' style='text-decoration:none; color:#0000FF'>"+getString(R.string.useragreement)+"</a>" ;
        //tvUserAgree.setText(Html.fromHtml(webLinkText));
        //tvUserAgree.setMovementMethod(LinkMovementMethod.getInstance());

    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        btnRegister.setOnClickListener(this);
        tvSendDentifyingCode.setOnClickListener(this);
        tvUserAgree.setOnClickListener(this);
    }

    /**
     * 验证用户名和密码是否存在
     *
     * @return
     */
    private boolean validate() {
        String phoneNumber = etUserName.getText().toString();
        String checkCode = etDentifyingCode.getText().toString();
        String password = etPassword.getText().toString();
        if (StringUtil.isTrimBlank(phoneNumber)) {
            showToast(getString(R.string.not_null_phone_num));
            return false;
        } else if (phoneNumber.length() < 11) {
            showToast(getString(R.string.not_formatted_phone_num));
            return false;
        } else if (StringUtil.isTrimBlank(checkCode)) {
            showToast(getString(R.string.not_null_code));
            return false;
        } else if (StringUtil.isTrimBlank(password)) {
            showToast(getString(R.string.not_null_password));
            return false;
        } else if (password.length() < 6) {
            showToast(getString(R.string.pass_not_less));
            return false;
        } else if (password.length() > 18) {
            showToast(getString(R.string.pass_not_more));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == btnRegister) {
            if (validate()) {
                registerUser();
            }
        } else if (v == tvSendDentifyingCode) {
            String phoneNumber = etUserName.getText().toString();
            if (StringUtil.isTrimBlank(phoneNumber)) {
                showToast(getString(R.string.not_null_phone_num));
                return;
            } else if (phoneNumber.length() < 11) {
                showToast(getString(R.string.not_formatted_phone_num));
                return;
            }
            isAccountExist();
        } else if (v == tvUserAgree) {
            Bundle bundle = new Bundle();
            bundle.putString("title", getString(R.string.user_Agreement));
            bundle.putString("url", getString(R.string.useragreement_url));
            toActivity(WebViewActivity.class, bundle);
        }
    }

    private void isAccountExist() {
        JSONObject json = new JSONObject();
        tvSendDentifyingCode.setEnabled(false);
        try {
            json.put("username", etUserName.getText().toString());
            json.put("phone", etUserName.getText().toString());

            new HttpFunction(this).postJsonRequest(Constant.REGISTER_PRECHECK, json, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onSuccess(String response) {
                    final UCResponse<AccountExistRDO> ucResponse = JsonUtil.fromJson(response, new TypeToken<UCResponse<AccountExistRDO>>() {
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (ucResponse != null && ucResponse.getErrorNo() == 0) {
                                requestCheckCode();
                            } else {
                                showToast(ucResponse.getErrorInfo());
                                tvSendDentifyingCode.setEnabled(true);
                            }
                        }
                    });

                }

                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(getString(R.string.get_fails_network));
                        }
                    });
                }
            });
//            OkHttpUtil.postJsonRequest(Constant.REGISTER_PRECHECK, json, hashCode(), new OKCallback<AccountExistRDO>(new TypeToken<UCResponse<AccountExistRDO>>() {
//            }) {
//                @Override
//                public void onResponse(int statuscode, @Nullable UCResponse<AccountExistRDO> ucResponse) {
//
//                    if (isSucccess()) {
//                        requestCheckCode();
//                    } else {
//                        Toast.makeText(RegisterActivity.this, ucResponse != null ? ucResponse.getErrorInfo() : "status:" + statuscode, Toast.LENGTH_LONG).show();
//                        tvSendDentifyingCode.setEnabled(true);
//                    }
//
//                }
//
//                @Override
//                public void onFailure(IOException e) {
//                    showToast(getString(R.string.get_fails_network));
//                }
//            });
        } catch (JSONException ex) {

        }
    }

    private void registerUser() {
        JSONObject json = getAuthJSONObject();
        try {
            password = etPassword.getText().toString();
            json.put("username", etUserName.getText().toString());
            json.put("password", password);
            json.put("phone", etUserName.getText().toString());
            json.put("checkid", checkId);
            json.put("checkcode", etDentifyingCode.getText().toString());

            new HttpFunction(this).postJsonRequest(Constant.USER_REGISTER, json, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onSuccess(String response) {
                    final UCResponse<UserRDO> ucResponse = JsonUtil.fromJson(response, new TypeToken<UCResponse<UserRDO>>() {
                    });
                    if (ucResponse != null && ucResponse.getErrorNo() == 0) {
                        LinphoneManager.getInstance().setUserSession(ucResponse.getUserId().toString(), ucResponse.getSessionId());
                        setLoginInfo(ucResponse.getUserId().toString(), ucResponse.getSessionId(), password, ucResponse.getAttributes());
                        linphoneLogIn(ucResponse.getUserId().toString(), ucResponse.getSessionId(), ucResponse.getAttributes().getPhone(), getResources().getBoolean(R.bool.setup_account_validation_mandatory));
                        success();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(ucResponse.getErrorInfo());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(getString(R.string.get_fails_network));
                        }
                    });
                }
            });

//            OkHttpUtil.postJsonRequest(Constant.USER_REGISTER, json, hashCode(), new OKCallback<UserRDO>(new TypeToken<UCResponse<UserRDO>>() {
//            }) {
//                @Override
//                public void onResponse(int statuscode, @Nullable UCResponse<UserRDO> ucResponse) {
//                    if (isSucccess()) {
//                        LinphoneManager.getInstance().setUserSession(ucResponse.getUserId().toString(), ucResponse.getSessionId());
//                        setLoginInfo(ucResponse.getUserId().toString(), ucResponse.getSessionId(), password, ucResponse.getAttributes());
//                        linphoneLogIn(ucResponse.getUserId().toString(), ucResponse.getSessionId(), ucResponse.getAttributes().getPhone(), getResources().getBoolean(R.bool.setup_account_validation_mandatory));
//                        success();
//
//                    } else {
//                        Toast.makeText(RegisterActivity.this, ucResponse != null ? ucResponse.getErrorInfo() : "status:" + statuscode, Toast.LENGTH_LONG).show();
//                    }
//
//                }
//
//                @Override
//                public void onFailure(IOException e) {
//                    showToast(getString(R.string.get_fails_network));
//                }
//            });
        } catch (JSONException ex) {

        }
    }

    private void requestCheckCode() {
        JSONObject json = getAuthJSONObject();
        try {
            json.put("phone", etUserName.getText().toString());

            new HttpFunction(this).postJsonRequest(Constant.CHECK_CODE_GET, json, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onSuccess(String response) {
                    final UCResponse<CheckCodeRDO> ucResponse = JsonUtil.fromJson(response, new TypeToken<UCResponse<CheckCodeRDO>>() {
                    });
                    mCodeHandler.sendEmptyMessage(60);
                    if (ucResponse != null && ucResponse.getErrorNo() == 0) {
                        checkId = ucResponse.getAttributes().getCheckId();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showToast(ucResponse.getErrorInfo());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showToast(getString(R.string.get_fails_network));
                        }
                    });
                }
            });

//            OkHttpUtil.postJsonRequest(Constant.CHECK_CODE_GET, json, hashCode(), new OKCallback<CheckCodeRDO>(new TypeToken<UCResponse<CheckCodeRDO>>() {
//            }) {
//                @Override
//                public void onResponse(int statuscode, @Nullable UCResponse<CheckCodeRDO> ucResponse) {
//                    mCodeHandler.sendEmptyMessage(60);
//                    if (isSucccess()) {
//                        checkId = ucResponse.getAttributes().getCheckId();
//                    } else {
//                        Toast.makeText(RegisterActivity.this, ucResponse != null ? ucResponse.getErrorInfo() : "status:" + statuscode, Toast.LENGTH_LONG).show();
//                    }
//
//                }
//
//                @Override
//                public void onFailure(IOException e) {
//                    showToast(getString(R.string.get_fails_network));
//                }
//            });
        } catch (JSONException ex) {

        }
    }

    private Handler mCodeHandler = new Handler() {
        private int mSeconds;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if (what != 2) {
                mSeconds = what;
                tvSendDentifyingCode.setText(String.format(getString(R.string.resend_code), String.valueOf(mSeconds)));
                sendEmptyMessageDelayed(2, 1000);
            } else {
                mSeconds--;
                if (mSeconds == 0) {
                    tvSendDentifyingCode.setText(R.string.send_identifying_code);
                    tvSendDentifyingCode.setEnabled(true);
                } else {
                    tvSendDentifyingCode.setText(String.format(getString(R.string.resend_code), String.valueOf(mSeconds)));
                    sendEmptyMessageDelayed(2, 1000);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCodeHandler.removeMessages(2);
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    public void success() {
        RoamApplication.bSwitchAccount = true;
        RoamApplication.isLoadGroupMessage = false;
        RoamApplication.isLoadCallHistory = false;
        RoamApplication.isLoadMissCallHistory = false;
        RoamApplication.isNewProxyConfig = true;
        startActivity(new Intent(this, LinphoneActivity.class));
    }

    private class SmsContent extends ContentObserver {
        private Cursor cursor = null;

        public SmsContent(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //读取收件箱中指定号码的短信
            Uri outMMS = Uri.parse("content://sms/inbox");
            cursor = getContentResolver().query(outMMS, null, null, null, "date DESC");
            if (cursor != null && cursor.getCount() > 0) {
                ContentValues values = new ContentValues();
                values.put("read", "1"); //修改短信为已读模式
                cursor.moveToNext();
                int bodyColumn = cursor.getColumnIndex("body");
                String smsBody = cursor.getString(bodyColumn);
                etDentifyingCode.setText(VerificationUtil.getDynamicPassword(smsBody));
            }
            //在用managedQuery的时候，不能主动调用close()方法，
            // 否则在Android 4.0+的系统上， 会发生崩溃
            if (Build.VERSION.SDK_INT < 14 && cursor != null) {
                cursor.close();
            }
        }
    }
}
