package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * 性别编辑 界面
 */
public class SexEditActivity extends BaseActivity {
	private TextView tvMale;
	private TextView tvFemale;
	private String selectGender;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectGender = getIntent().getStringExtra("gender");
		setContentView(R.layout.activity_sex_edit);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		tvMale = (TextView) findViewById(R.id.tv_sex_male);
		tvFemale = (TextView) findViewById(R.id.tv_sex_female);
		if(tvMale.getText().equals(selectGender)) {
			tvMale.setSelected(true);
		} else {
			tvFemale.setSelected(true);
		}
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		tvMale.setOnClickListener(this);
		tvFemale.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == tvMale){
			tvMale.setSelected(true);
			tvFemale.setSelected(false);
			success(tvMale.getText().toString());
		} else if (v == tvFemale) {
			tvMale.setSelected(false);
			tvFemale.setSelected(true);
			success(tvFemale.getText().toString());
		}
	}
	private void success(String gender) {
		Intent i=new Intent();
		i.putExtra("gender", gender);
		setResult(RESULT_OK, i);
		finish();
	}
}
