package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.StringFormatUtil;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneManager;

import java.io.IOException;

public class ChangePasswordActivity extends BaseActivity {
    private TextView tvSafeLevel;
    private EditText etOldPassword;
    private EditText etNewPassword;
    private Button btnSubmitChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        tvSafeLevel = (TextView) findViewById(R.id.tv_safelevel);
        etOldPassword = (EditText) findViewById(R.id.et_oldpassword);
        etNewPassword = (EditText) findViewById(R.id.et_newpassword);
        btnSubmitChange = (Button) findViewById(R.id.btn_submitchange);
        /***设置textView 字体显示两种颜色*/
        String level = "中";
        String wholeStr = "当前密码强度: " + level;
        StringFormatUtil spanStr = new StringFormatUtil(this, wholeStr,
                level, R.color.yellow).fillColor();
        tvSafeLevel.setText(spanStr.getResult());
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        btnSubmitChange.setOnClickListener(this);
    }

    private boolean validate() {
        String oldPassword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        if (StringUtil.isTrimBlank(oldPassword)) {
            showToast(getString(R.string.not_null_old_pass));
            return false;
        } else if (StringUtil.isTrimBlank(newPassword)) {
            showToast(getString(R.string.not_null_new_pass));
            return false;
        } else if (newPassword.length() < 6) {
            showToast(getString(R.string.pass_not_less));
            return false;
        } else if (newPassword.length() > 18) {
            showToast(getString(R.string.pass_not_more));
            return false;
        } else if (oldPassword.equals(newPassword)) {
            showToast(getString(R.string.new_pass_not_old_pass));
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == btnSubmitChange) {
            if (validate()) {
                changePassword();
            }
        }
    }

    private void changePassword() {
        JSONObject json = getAuthJSONObject();
        try {
            json.put("opassword", etOldPassword.getText().toString());
            json.put("npassword", etNewPassword.getText().toString());
            OkHttpUtil.postJsonRequest(Constant.CHANGE_PASSWORD, json, hashCode(), new OKCallback<String>(new TypeToken<UCResponse<String>>() {
            }) {
                @Override
                public void onResponse(int statuscode, @Nullable UCResponse<String> ucResponse) {
                    if (isSucccess()) {
                        showToast(getString(R.string.change_pass_success));
                        MobclickAgent.onProfileSignOff();
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ChangePasswordActivity.this, ucResponse != null ? ucResponse.getErrorInfo() : "status:" + statuscode, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(IOException e) {
                    showToast(getString(R.string.get_fails_network));
                }
            });
        } catch (JSONException ex) {

        }
    }
}
