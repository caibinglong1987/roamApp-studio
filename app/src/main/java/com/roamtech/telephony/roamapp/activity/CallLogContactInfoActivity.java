package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.CallLogInfoAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.CallLog;

import java.util.ArrayList;
import java.util.List;

public class CallLogContactInfoActivity extends BaseActivity {

	private ImageView userPhoto;

	/** 用户名 */
	private TextView tvUsername;

	private TextView tvStopCall;
	private TextView tvAddInCollection;
	
	
	private ImageView ivsendphonemessage;
	private ImageView ivPhonecall;
	private TextView tvHoursetelnumber;
	
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_calllog_info);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		userPhoto = (ImageView) findViewById(R.id.id_circle_image);
		tvUsername = (TextView) findViewById(R.id.tv_username);

		tvStopCall = (TextView) findViewById(R.id.tvStopCall);
		tvAddInCollection = (TextView) findViewById(R.id.tvaddInCollection);
		ivsendphonemessage=(ImageView) findViewById(R.id.ivsendphonemessage);
		ivPhonecall=(ImageView) findViewById(R.id.ivphonecall);
		tvHoursetelnumber=(TextView) findViewById(R.id.tv_hoursetelnumber);
		
		mListView = (ListView) findViewById(android.R.id.list);
		List<CallLog> logs = new ArrayList<CallLog>();
		logs.add(new CallLog(0,"12:10",false));
		logs.add(new CallLog(2, "02:10", true));
		logs.add(new CallLog(1, "10:10", false));
		logs.add(new CallLog(0,"12:10",false));
		logs.add(new CallLog(2, "02:10", true));
		logs.add(new CallLog(1, "10:10", false));
		logs.add(new CallLog(0,"12:10",false));
		logs.add(new CallLog(2, "02:10", true));
		logs.add(new CallLog(1, "10:10", false));
		logs.add(new CallLog(0,"12:10",false));
		logs.add(new CallLog(2, "02:10", true));
		logs.add(new CallLog(1, "10:10", false));
		logs.add(new CallLog(0,"12:10",false));
		logs.add(new CallLog(2, "02:10", true));
		logs.add(new CallLog(1, "10:10", false));
		logs.add(new CallLog(0,"12:10",false));
		logs.add(new CallLog(2, "02:10", true));
		logs.add(new CallLog(1, "10:10", false));
		mListView.setAdapter(new CallLogInfoAdapter(this, logs));
	}
	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		userPhoto.setOnClickListener(this);
		tvStopCall.setOnClickListener(this);
		tvAddInCollection.setOnClickListener(this);
		ivsendphonemessage.setOnClickListener(this);
		ivPhonecall.setOnClickListener(this);
		tvHoursetelnumber.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == tvStopCall) {

		} else if (v == tvAddInCollection) {

		} else if (v == ivPhonecall) {

		} else if (v == ivsendphonemessage) {

		}else if (v == tvHoursetelnumber) {

		}
	}
}
