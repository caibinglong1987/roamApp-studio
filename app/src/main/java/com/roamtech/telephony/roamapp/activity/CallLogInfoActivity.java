package com.roamtech.telephony.roamapp.activity;

import java.util.ArrayList;
import java.util.List;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.CallLogInfoAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.CallLog;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class CallLogInfoActivity extends BaseActivity {

	private ImageView userPhoto;

	/** 用户名 */
	private TextView tvUsername;

	private TextView tvStopCall;
	private TextView tvAddInCollection;
	private TextView tvAddtoContact;
	private TextView tvAddtoContactHaved;
	private ListView mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calllog_info);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		userPhoto = (ImageView) findViewById(R.id.id_circle_image);
		tvUsername = (TextView) findViewById(R.id.tv_username);

		tvStopCall = (TextView) findViewById(R.id.tvStopCall);
		tvAddInCollection = (TextView) findViewById(R.id.tvaddInCollection);
		tvAddtoContact = (TextView) findViewById(R.id.tvAddtoContact);
		tvAddtoContactHaved = (TextView) findViewById(R.id.tvAddtoContactHaved);
		mListView = (ListView) findViewById(android.R.id.list);
		List<CallLog> logs = new ArrayList<CallLog>();
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
		tvAddtoContact.setOnClickListener(this);
		tvAddtoContactHaved.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == tvStopCall) {

		} else if (v == tvAddInCollection) {

		} else if (v == tvAddtoContact) {

		} else if (v == tvAddtoContactHaved) {

		}
	}
}
