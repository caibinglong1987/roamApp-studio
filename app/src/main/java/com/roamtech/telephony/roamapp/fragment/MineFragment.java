package com.roamtech.telephony.roamapp.fragment;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.AboutActivity;
import com.roamtech.telephony.roamapp.activity.AccountSafeActivity;
import com.roamtech.telephony.roamapp.activity.MineLMBaoActivity;
import com.roamtech.telephony.roamapp.activity.SettingActivity;
import com.roamtech.telephony.roamapp.activity.ShareActivity;
import com.roamtech.telephony.roamapp.activity.UserInfoActivity;
import com.roamtech.telephony.roamapp.base.BaseFragment;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.StringFormatUtil;
import com.roamtech.telephony.roamapp.util.UriHelper;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author 纸票 电票 小票 商票的fragment
 * 
 */
public class MineFragment extends BaseFragment {
	/** 用户名 */
	private TextView tvUserName;
	private ImageView userPhoto;
	private TextView tvUserInfo;
	private TextView tvAcountSafe;
	private TextView tvMylmbao;
	private TextView tvSetting;
	private TextView tvAbout;
	private TextView tvSharemanhua;
	
	private TextView tvSafeLevel;
	private TextView tvLmbaoCount;
	private TextView tvNewVerison;

	@Override
	public int getLayoutId() {
		return R.layout.fragment_mine;
	}

	@Override
	public void initView(Bundle savedInstanceState) {
		super.initView(savedInstanceState);
		tvUserName = (TextView) findViewById(R.id.tv_username);
		userPhoto = (ImageView) findViewById(R.id.id_circle_image);
		userPhoto.setOnClickListener(this);
		tvUserInfo = (TextView) findViewById(R.id.tv_userinfo);
		tvUserInfo.setOnClickListener(this);
		tvAcountSafe = (TextView) findViewById(R.id.tv_account_safe);
		tvAcountSafe.setOnClickListener(this);
		tvMylmbao = (TextView) findViewById(R.id.tv_mylmbao);
		tvMylmbao.setOnClickListener(this);
		tvSetting = (TextView) findViewById(R.id.tv_setting);
		tvSetting.setOnClickListener(this);
		tvAbout = (TextView) findViewById(R.id.tv_about);
		tvAbout.setOnClickListener(this);
		tvSharemanhua = (TextView) findViewById(R.id.tv_sharemanhua);
		tvSharemanhua.setOnClickListener(this);
		/***悬浮的text 获取控件 根据请求结果控制*/
		tvSafeLevel = (TextView) findViewById(R.id.tv_safelevel);
		tvLmbaoCount = (TextView) findViewById(R.id.tv_mylmbao_count);
		tvNewVerison = (TextView) findViewById(R.id.tv_newversion);

		/***设置textView 字体显示两种颜色*/
		String level="中";
		String wholeStr = "安全等级: "+level;
		StringFormatUtil spanStr = new StringFormatUtil(getActivity(), wholeStr,
				level, R.color.yellow).fillColor();
		tvSafeLevel.setText(spanStr.getResult());
		tvLmbaoCount.setText(""+LinphoneActivity.instance().getMyTouchs().size());
		tvUserName.setText(LinphoneActivity.instance().getMyPhones().get(0).getPhoneNumber());
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == userPhoto) {

		} else if (v == tvUserInfo) {
			getBaseActivity().toActivity(UserInfoActivity.class, null);
		} else if (v == tvAcountSafe) {
			getBaseActivity().toActivity(AccountSafeActivity.class, null);
		} else if (v == tvMylmbao) {
			getBaseActivity().toActivity(MineLMBaoActivity.class, null);
		} else if (v == tvSetting) {
			getBaseActivity().toActivity(SettingActivity.class, null);
		} else if (v == tvAbout) {
			getBaseActivity().toActivity(AboutActivity.class, null);
		} else if (v == tvSharemanhua) {
			getBaseActivity().toActivity(ShareActivity.class, null);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
		tvLmbaoCount.setText(""+LinphoneActivity.instance().getMyTouchs().size());
		tvUserName.setText(LinphoneActivity.instance().getMyPhones().get(0).getPhoneNumber());
		String head_url = SPreferencesTool.getInstance().getStringValue(getActivity().getApplicationContext(),SPreferencesTool.LOGIN_INFO,SPreferencesTool.login_headUrl);
		if (head_url == null || "".equals(head_url)){
			userPhoto.setImageResource(R.drawable.logo_default_userphoto);
		}else{
			userPhoto.setImageURI(UriHelper.obtainUri(head_url));
		}
	}


}