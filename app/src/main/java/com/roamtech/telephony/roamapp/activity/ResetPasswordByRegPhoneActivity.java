package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.bean.CheckCodeRDO;
import com.roamtech.telephony.roamapp.bean.MobileExistRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ResetPasswordByRegPhoneActivity extends BaseActivity {
	private EditText etUserName;
	private EditText etDentifyingCode;
	private EditText etPassword;
	private Button btnSunmitOk;
	private TextView tvSendDentifyingCode;
	private int checkId;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_resetpassword_regphone);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		etUserName = (EditText) findViewById(R.id.tv_username);
		etDentifyingCode = (EditText) findViewById(R.id.id_identifying_code);
		etPassword = (EditText) findViewById(R.id.id_password);
		btnSunmitOk = (Button) findViewById(R.id.id_submit_ok);
		tvSendDentifyingCode = (TextView) findViewById(R.id.id_send_identifying_code);
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		btnSunmitOk.setOnClickListener(this);
		tvSendDentifyingCode.setOnClickListener(this);
	}

	private boolean validate() {
		String phoneNumber = etUserName.getText().toString();
		String checkCode = etDentifyingCode.getText().toString();
		String password = etPassword.getText().toString();
		if (StringUtil.isTrimBlank(phoneNumber)) {
			showToast("手机号码不能为空");
			return false;
		} else if (StringUtil.isTrimBlank(checkCode)) {
			showToast("验证码不能为空");
			return false;
		} else if (StringUtil.isTrimBlank(password)) {
			showToast("密码不能为空");
			return false;
		} else if (password.length() < 6) {
			showToast("密码不能少于6位");
			return false;
		} else if (password.length() > 18) {
			showToast("密码不能大于18位");
			return false;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == btnSunmitOk) {
			if (validate()) {
				resetPassword();
			}
		} else if (v == tvSendDentifyingCode) {
			String phoneNumber = etUserName.getText().toString();
			if (StringUtil.isTrimBlank(phoneNumber)) {
				showToast("手机号码不能为空");
				return;
			}
			isMobileExist(phoneNumber);
		}
	}

	private void isMobileExist(final String phoneNumber) {
		JSONObject json = new JSONObject();
		tvSendDentifyingCode.setEnabled(false);
		try {
			json.put("phone", phoneNumber);
			OkHttpUtil.postJsonRequest(Constant.IS_MOBILE_EXIST, json, hashCode(), new OKCallback<MobileExistRDO>(new TypeToken<UCResponse<MobileExistRDO>>() {
			}) {
				@Override
				public void onResponse(int statuscode, @Nullable UCResponse<MobileExistRDO> ucResponse) {

					if (isSucccess()) {
						MobileExistRDO mobileExistRDO = ucResponse.getAttributes();
						if (mobileExistRDO != null) {
							if (mobileExistRDO.getExist()) {
								userId = mobileExistRDO.getUserId();
								requestCheckCode(phoneNumber, mobileExistRDO.getUserId());
							} else {
								showToast("该用户不存在");
								tvSendDentifyingCode.setEnabled(true);
							}
						}
					} else {
						Toast.makeText(ResetPasswordByRegPhoneActivity.this, ucResponse != null ? ucResponse.getErrorInfo() : "status:" + statuscode, Toast.LENGTH_LONG).show();
						tvSendDentifyingCode.setEnabled(true);
					}

				}

				@Override
				public void onFailure(IOException e) {
					tvSendDentifyingCode.setEnabled(true);
					showToast("获取失败,确认网络后重试");
				}
			});
		} catch (JSONException ex) {
			tvSendDentifyingCode.setEnabled(true);
		}
	}

	private void requestCheckCode(String phoneNumber, String userId) {
		JSONObject json = new JSONObject();
		try {
			json.put("phone", phoneNumber);
			json.put("userid", userId);
			OkHttpUtil.postJsonRequest(Constant.CHECK_CODE_GET, json, hashCode(), new OKCallback<CheckCodeRDO>(new TypeToken<UCResponse<CheckCodeRDO>>() {
			}) {
				@Override
				public void onResponse(int statuscode, @Nullable UCResponse<CheckCodeRDO> ucResponse) {
					new CountDownTimer(60000, 1000) {
						@Override
						public void onTick(long millisUntilFinished) {
							tvSendDentifyingCode.setText("重新发送(" + millisUntilFinished / 1000 + ")");
						}

						@Override
						public void onFinish() {
							tvSendDentifyingCode.setText(R.string.send_identifying_code);
							tvSendDentifyingCode.setEnabled(true);
						}
					}.start();
					if (isSucccess()) {
						checkId = ucResponse.getAttributes().getCheckId();
					} else {
						Toast.makeText(ResetPasswordByRegPhoneActivity.this, ucResponse != null ? ucResponse.getErrorInfo() : "status:" + statuscode, Toast.LENGTH_LONG).show();
					}

				}

				@Override
				public void onFailure(IOException e) {
					tvSendDentifyingCode.setEnabled(true);
					showToast("获取失败,确认网络后重试");
				}
			});
		} catch (JSONException ex) {
			tvSendDentifyingCode.setEnabled(true);
		}
	}

	private void resetPassword() {
		JSONObject json = new JSONObject();
		try {
			json.put("phone", etUserName.getText().toString().trim());
			json.put("userid", userId);
			json.put("checkid", checkId);
			json.put("checkcode", etDentifyingCode.getText().toString());
			json.put("npassword", etPassword.getText().toString());
			OkHttpUtil.postJsonRequest(Constant.RESET_PASSWORD, json, hashCode(), new OKCallback<String>(new TypeToken<UCResponse<String>>() {
			}) {
				@Override
				public void onResponse(int statuscode, @Nullable UCResponse<String> ucResponse) {
					if (isSucccess()) {
						showToast("重置密码成功");
						finish();
					} else {
						Toast.makeText(ResetPasswordByRegPhoneActivity.this, ucResponse != null ? ucResponse.getErrorInfo() : "status:" + statuscode, Toast.LENGTH_LONG).show();
					}

				}

				@Override
				public void onFailure(IOException e) {
					showToast("获取失败,确认网络后重试");
				}
			});
		} catch (JSONException ex) {

		}
	}

}
