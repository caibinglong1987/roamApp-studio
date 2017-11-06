package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ShareActivity extends BaseActivity {
	private TextView tvShareWeixinFriend;
	private TextView tvShareWeixinTimeline;
	private TextView tvShareQQFriend;
	private TextView tvShareQQZone;
	private TextView tvShareTecentBlog;
	private TextView tvShareSinaBlog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
	}
	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		tvShareWeixinFriend = (TextView) findViewById(R.id.tv_shareweixinfriend);
		tvShareWeixinTimeline = (TextView) findViewById(R.id.tv_shareweixintimeline);
		tvShareQQFriend = (TextView) findViewById(R.id.tv_shareqqfrinend);
		tvShareQQZone = (TextView) findViewById(R.id.tv_shareqqzone);
		tvShareTecentBlog = (TextView) findViewById(R.id.tv_sharetecentblog);
		tvShareSinaBlog = (TextView) findViewById(R.id.tv_sharesinablog);
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		tvShareWeixinFriend.setOnClickListener(this);
		tvShareWeixinTimeline.setOnClickListener(this);
		tvShareQQFriend.setOnClickListener(this);
		tvShareQQZone.setOnClickListener(this);
		tvShareTecentBlog.setOnClickListener(this);
		tvShareSinaBlog.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == tvShareWeixinFriend) {
		} else if (v == tvShareWeixinTimeline) {

		} else if (v == tvShareQQFriend) {

		} else if (v == tvShareQQZone) {

		} else if (v == tvShareTecentBlog) {

		} else if (v == tvShareSinaBlog) {

		}
	}
}
