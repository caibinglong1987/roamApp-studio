package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.util.Utility;

/**
 * Created by long
 * on 2016/10/12.
 * 络漫宝配置手机号码
 */

public class LMBAOPhoneSettingActivity extends HeaderBaseActivity {
    private TextView tv_save_config;
    private EditText et_phone_number;
    private String phone_number;
    private ImageView iv_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_num_setting);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_lmb_number));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.closeKeyboard(et_phone_number, getApplicationContext());
                finish();
                startActivity(new Intent(LMBAOPhoneSettingActivity.this, LMBAOWiredModeActivity.class));
            }
        });
        et_phone_number = (EditText) findViewById(R.id.et_phone_number);
        et_phone_number.setText(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_phone));
        tv_save_config = (TextView) findViewById(R.id.tv_save_config);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        tv_save_config.setOnClickListener(this);
        iv_delete.setOnClickListener(this);

        et_phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (et_phone_number.getText().length() > 0) {
                                iv_delete.setVisibility(View.VISIBLE);
                            } else {
                                iv_delete.setVisibility(View.GONE);
                                Utility.openKeyboard(et_phone_number, getApplicationContext());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_save_config:
                phone_number = et_phone_number.getText().toString();
                if (phone_number.length() < 11) {
                    ToastUtils.showToast(this, getString(R.string.str_alert_no_phone_number));
                    return;
                }
                RoamApplication.RoamBoxConfigOld.phone = phone_number;
                toActivity(LMBAOSimCardActivity.class, null);
                break;
            case R.id.iv_delete:
                et_phone_number.setText("");
                break;
        }
    }
}
