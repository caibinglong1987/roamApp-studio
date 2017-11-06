package com.roamtech.telephony.roamapp.activity;

import com.jungly.gridpasswordview.GridPasswordView;
import com.jungly.gridpasswordview.GridPasswordView.OnPasswordChangedListener;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.util.StringFormatUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class RegisterVerificationPhoneActivity extends BaseActivity {

	private TextView tvResendIdentifyingCode;
	private TextView tvPhoneWarm;
	private String phoneNumber;
	private GridPasswordView gridPasswordView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_verificationphone);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		/**获取手机号**/
		phoneNumber=getIntent().getExtras().getString("phoneNumber");
		tvPhoneWarm=(TextView) findViewById(R.id.tv_phoneWarm);
		String wholeStr = "验证码短息已发送至 "+phoneNumber+"的手机，请查收";
		StringFormatUtil spanStr = new StringFormatUtil(this, wholeStr,
				phoneNumber, R.color.text_black).fillColor();
		tvPhoneWarm.setText(spanStr.getResult());
		gridPasswordView=(GridPasswordView) findViewById(R.id.gridPasswordView);
		gridPasswordView.setPasswordVisibility(true);
		tvResendIdentifyingCode=(TextView) findViewById(R.id.tv_resend_identifying_code);
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		tvResendIdentifyingCode.setOnClickListener(this);
		gridPasswordView.setOnPasswordChangedListener(new OnPasswordChangedListener() {
			
			@Override
			public void onMaxLength(String psw) {
				// TODO Auto-generated method stub
				/**验证码输入完毕  在此验证***/
				toActivity(RegisterInputPasswordActivity.class, null);
			}
			
			@Override
			public void onChanged(String psw) {
				// TODO Auto-generated method stub
			
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == tvResendIdentifyingCode) {
			
		} 
	}
}
