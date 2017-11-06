package com.roamtech.telephony.roamapp.activity;

import java.util.ArrayList;
import java.util.List;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.MinePhoneAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.Phone;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 *  我的号码列表
 */
public class MinePhoneActivity extends BaseActivity {
	private ListView mListView;
	private LinearLayout layoutAddnewphone;
	private String selectPhone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		selectPhone = getIntent().getStringExtra("phone");
		setContentView(R.layout.activity_minephone);
	}

	@Override
	public void initView(Bundle savedInstanceState)  {
		// TODO Auto-generated method stub
		super.initView(savedInstanceState);
		mListView = (ListView) findViewById(android.R.id.list);
		layoutAddnewphone = (LinearLayout) findViewById(R.id.ll_addnewphone);
		List<Phone>  data=new ArrayList<Phone>();
		List<Phone> myPhones=LinphoneActivity.instance().getMyPhones();
		for(Phone myphone:myPhones) {
			Phone phone = new Phone();
			phone.setPhoneNumber(myphone.getPhoneNumber());
			phone.setAreaCode(myphone.getAreaCode());
			if(myphone.getPhoneNumber().equals(selectPhone)) {
				phone.setSelected(true);
			}
			data.add(phone);
		}
		MinePhoneAdapter adapter=new MinePhoneAdapter(this, data);
		mListView.setAdapter(adapter);
	}

	@Override
	public void setListener() {
		// TODO Auto-generated method stub
		super.setListener();
		layoutAddnewphone.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		super.onItemClick(parent, view, position, id);
		Phone selectedPhone=(Phone) mListView.getItemAtPosition(position);
		Intent i=new Intent();
		i.putExtra("phone", selectedPhone);
		setResult(RESULT_OK, i);
		finish();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		if (v == layoutAddnewphone) {
			toActivity(AddNewPhoneActivity.class, null);
		}
	}
}
