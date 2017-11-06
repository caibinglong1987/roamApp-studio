package com.roamtech.telephony.roamapp.activity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ResetPasswordByBindPhoneActivity extends BaseActivity {
	private boolean isFromSafeAndAccount;
	private TextView mTitleText;
	private EditText etUserName;
	private EditText etDentifyingCode;
	private Button btnSunmitOk;
	private TextView tvSendDentifyingCode;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword_bindphone);
	}
	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		mTitleText=(TextView) findViewById(R.id.id_titletext);
		Bundle b=getIntent().getExtras();
		if(b!=null){
			isFromSafeAndAccount=b.getBoolean("isFromSafeAndAccount");
			if(isFromSafeAndAccount){
				mTitleText.setText(getText(R.string.bindPhone));
			}else{
				mTitleText.setText(getText(R.string.resetbyBindPhone));
			}
		}
		etUserName=(EditText) findViewById(R.id.tv_username);
		etDentifyingCode=(EditText) findViewById(R.id.id_identifying_code);
		btnSunmitOk=(Button) findViewById(R.id.id_submit_ok);
		tvSendDentifyingCode=(TextView) findViewById(R.id.id_send_identifying_code);
	}
	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		btnSunmitOk.setOnClickListener(this);
		tvSendDentifyingCode.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if(v==btnSunmitOk){
			if(isFromSafeAndAccount){
				toActivity(UnbindPhoneActivity.class, null);
			}else{
				
			}
		}else if(v==tvSendDentifyingCode){
			
		}
	}
}
