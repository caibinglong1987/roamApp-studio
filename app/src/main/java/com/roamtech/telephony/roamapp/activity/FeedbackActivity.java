package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FeedbackActivity extends HeaderBaseActivity {
	private EditText etFeedback;
	private TextView tvTextLimit;
	private Button btnSubmit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		initView();
	}

	private void initView()  {
		headerLayout.showTitle(getString(R.string.feedback));
		headerLayout.showLeftBackButton();
		etFeedback=(EditText) findViewById(R.id.et_feedback);
		tvTextLimit=(TextView) findViewById(R.id.tv_textlimit);
		btnSubmit=(Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == btnSubmit) {
		} 
	}
}
