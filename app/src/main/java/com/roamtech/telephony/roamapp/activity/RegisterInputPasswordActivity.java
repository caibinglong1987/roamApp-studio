package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterInputPasswordActivity extends BaseActivity {
	private EditText etPassword;
	private Button btnCompleteRegister;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_inputpassword);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		etPassword = (EditText) findViewById(R.id.tv_username);
		btnCompleteRegister = (Button) findViewById(R.id.btnCompleteRegister);

	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		btnCompleteRegister.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == btnCompleteRegister) {
			 toActivity(LMBaoWifiActivity.class, null);
		}
	}
}
