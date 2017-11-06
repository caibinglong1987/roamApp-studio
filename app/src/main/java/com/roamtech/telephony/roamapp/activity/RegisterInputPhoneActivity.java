package com.roamtech.telephony.roamapp.activity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.util.StringUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterInputPhoneActivity extends BaseActivity {
	private EditText etUserName;
	private Button btnNext;
	private TextView tvConutryselect;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_inputphone);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		etUserName=(EditText) findViewById(R.id.tv_username);
		btnNext=(Button) findViewById(R.id.btnNext);
		tvConutryselect=(TextView) findViewById(R.id.tv_conutryselect);
		
	}
	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		btnNext.setOnClickListener(this);
		tvConutryselect.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		 if(v==tvConutryselect){
			//toActivity(ResetPasswordActivity.class, null);
		}else if(v==btnNext){
			Bundle bundle=new Bundle();
			String number=getEditTextValue(etUserName);
			/**判断防止下一界面空指针*/
			if(StringUtil.isTrimBlank(number)){
				number="134****9041";
			}
			bundle.putString("phoneNumber",number );
			toActivity(RegisterVerificationPhoneActivity.class, bundle);
		}
	}
}
