package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.util.StringFormatUtil;

public class AccountSafeActivity extends BaseActivity {
    private TextView tvSafelevel;
    private TextView tvChangePassword;
    private TextView tvBindPhone;
    private TextView tvBindEmail;

    @Override
    public int getLayoutId() {
        return R.layout.activity_account_safe;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        tvSafelevel = (TextView) findViewById(R.id.tv_safelevel);
        tvChangePassword = (TextView) findViewById(R.id.tv_changepassword);
        tvBindPhone = (TextView) findViewById(R.id.tv_bindPhone);
        tvBindEmail = (TextView) findViewById(R.id.tv_bindEmail);

        /***设置textView 字体显示两种颜色*/
        String level = "中";
        //String wholeStr = "当前安全等级: " + level;
        String wholeStr = String.format(getString(R.string.safe_now_level),String.valueOf(level));
        StringFormatUtil spanStr = new StringFormatUtil(this, wholeStr,
                level, R.color.yellow).fillColor();
        tvSafelevel.setText(spanStr.getResult());

    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        tvChangePassword.setOnClickListener(this);
        tvBindEmail.setOnClickListener(this);
        tvBindPhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == tvChangePassword) {
            toActivity(ChangePasswordActivity.class, null);
        } else if (v == tvBindPhone) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isFromSafeAndAccount", true);
            toActivity(ResetPasswordByBindPhoneActivity.class, bundle);
        } else if (v == tvBindEmail) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isFromSafeAndAccount", true);
            toActivity(ResetPasswordByBindEmailActivity.class, bundle);
        }
    }
}
