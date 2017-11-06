package com.roamtech.telephony.roamapp.activity;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.adapter.MineLMBaoAdapter;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * 我的络漫宝 列表
 */
public class MineLMBaoActivity extends BaseActivity {
    private ListView mListView;
    private LinearLayout layoutAddnewlmbao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minelmbao);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        mListView = (ListView) findViewById(android.R.id.list);
        layoutAddnewlmbao = (LinearLayout) findViewById(R.id.ll_addnewlmbao);
        /*List<Touch>  data=new ArrayList<WiFi>();
		for(int index=0;index<20;index++){
			WiFi wifi=new WiFi();
			wifi.setSsid("Roaming Box-0096"+index);
			wifi.setPhoneNumber(13861236666L+index+"");
			data.add(wifi);
		}*/
        MineLMBaoAdapter adapter = new MineLMBaoAdapter(this, LinphoneActivity.instance().getMyTouchs());
        mListView.setAdapter(adapter);
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        layoutAddnewlmbao.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        Bundle bundle = new Bundle();
        bundle.putSerializable("touch", (TouchDBModel) parent.getItemAtPosition(position));
        toActivity(LMBaoEditActivity.class, bundle);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == layoutAddnewlmbao) {
            toActivity(LMBaoSettingActivity.class, null);
        }
    }
}
