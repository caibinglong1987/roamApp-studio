package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


public class UnbindEmailActivity extends BaseActivity {
	private TextView tvBindedEmailTip;
	private TextView tvUnBindEmail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unbindemail);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		tvBindedEmailTip = (TextView) findViewById(R.id.tv_bindedEmailtip);
		tvUnBindEmail = (TextView) findViewById(R.id.tv_unbindemail);
		tvBindedEmailTip.setText("已绑定邮箱：zhu**@roam-tech.com");
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		tvUnBindEmail.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == tvUnBindEmail) {
			
		}
	}
}
