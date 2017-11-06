package com.roamtech.telephony.roamapp.activity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ResetPasswordByBindEmailActivity extends BaseActivity {
	private EditText etEmail;
	private Button btnSunmitOk;
	private boolean isFromSafeAndAccount;
	private TextView mTitleText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword_bindemail);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		mTitleText = (TextView) findViewById(R.id.id_titletext);
		Bundle b = getIntent().getExtras();
		if (b != null) {
			isFromSafeAndAccount = b.getBoolean("isFromSafeAndAccount");
			if (isFromSafeAndAccount) {
				mTitleText.setText(getText(R.string.bindEmail));
			} else {
				mTitleText.setText(getText(R.string.resetByBindEmail));
			}
		}
		etEmail = (EditText) findViewById(R.id.id_email);
		btnSunmitOk = (Button) findViewById(R.id.id_submit_ok);
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		btnSunmitOk.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == btnSunmitOk) {
			if(isFromSafeAndAccount){
				toActivity(UnbindEmailActivity.class, null);
			}else{
				
			}
		}
	}
}
