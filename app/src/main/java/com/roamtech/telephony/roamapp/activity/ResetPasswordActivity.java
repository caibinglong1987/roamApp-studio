package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResetPasswordActivity extends BaseActivity {

	private TextView tvRegisterPhone;
	private TextView tvBindPhone;
	private TextView tvBindEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		tvRegisterPhone = (TextView) findViewById(R.id.tv_registerPhone);
		tvBindPhone = (TextView) findViewById(R.id.tv_bindPhone);
		tvBindEmail = (TextView) findViewById(R.id.tv_bindEmail);
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		tvBindEmail.setOnClickListener(this);
		tvBindPhone.setOnClickListener(this);
		tvRegisterPhone.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == tvRegisterPhone) {
			toActivity(ResetPasswordByRegPhoneActivity.class, null);
		} else if (v == tvBindPhone) {
			toActivity(ResetPasswordByBindPhoneActivity.class, null);
		} else if (v == tvBindEmail) {
			toActivity(ResetPasswordByBindEmailActivity.class, null);
		}
	}
}
