package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UnbindPhoneActivity extends BaseActivity {
	private TextView tvBindedPhoneTip;
	private TextView tvUnBindPhone;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unbindphone);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		tvBindedPhoneTip = (TextView) findViewById(R.id.tv_bindedphonetip);
		tvUnBindPhone = (TextView) findViewById(R.id.tv_unbindphone);
		tvBindedPhoneTip.setText("已绑手机：+86 188****1224");
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		tvUnBindPhone.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == tvUnBindPhone) {
			
		}
	}
}
