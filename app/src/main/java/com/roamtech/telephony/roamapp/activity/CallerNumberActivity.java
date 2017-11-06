package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.CommonAdapter;
import com.roamtech.telephony.roamapp.adapter.ViewHolder;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CallerNumberBean;
import com.roamtech.telephony.roamapp.bean.VoiceNumber;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caibinglong
 * on 2016/12/12.
 * 主叫号码 设置
 */

public class CallerNumberActivity extends HeaderBaseActivity {
    private CommonAdapter<CallerNumberBean> adapter = null;
    private ListView listView;
    private List<CallerNumberBean> list = new ArrayList<>();
    private ImageView iv_caller;
    private TextView tv_phone_number;
    private String defaultPhone = null;
    private int defaultType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_caller_number);
        initView();
        initListData();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_caller));
        listView = (ListView) findViewById(R.id.listView_caller);
        iv_caller = (ImageView) findViewById(R.id.iv_caller);
        tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter = new CommonAdapter<CallerNumberBean>(CallerNumberActivity.this, list, R.layout.item_caller_phone) {
            @Override
            public void convert(ViewHolder helper, CallerNumberBean item, int position) {
                helper.setText(R.id.tv_phone_number, item.phoneNumber);
                if (item.phoneType == 1) {
                    helper.setImageResource(R.id.iv_caller, R.drawable.rd_number);
                } else {
                    helper.setImageResource(R.id.iv_caller, R.drawable.my_box);
                }
                if (item.checked) {
                    helper.showView(R.id.iv_check);
                } else {
                    helper.hideView(R.id.iv_check);
                }
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                for (CallerNumberBean item : list) {
                    if (item.phoneNumber.equals(list.get(i).phoneNumber)) {
                        item.checked = true;
                        tv_phone_number.setText(item.phoneNumber);
                        if (item.phoneType == 1) {
                            iv_caller.setImageResource(R.drawable.rd_number);
                        } else {
                            iv_caller.setImageResource(R.drawable.my_box);
                        }
                        setCallerNumber(item.phoneNumber, item.phoneType);
                    } else {
                        item.checked = false;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initListData() {
        defaultPhone = SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_caller_phone, "0");
        defaultType = SPreferencesTool.getInstance().getIntValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_caller_type, 0);
        if (LinphoneActivity.isInstanciated()) {
            VoiceNumber voiceNumber = LinphoneActivity.instance().getVoiceNumber();
            List<TouchDBModel> touchDBModelList = LinphoneActivity.instance().getMyTouchs();
            CallerNumberBean model;

            if (touchDBModelList != null && touchDBModelList.size() > 0) {
                for (int i = 0; i < touchDBModelList.size(); i++) {
                    model = new CallerNumberBean();
                    model.phoneNumber = touchDBModelList.get(i).getPhone();
                    model.checked = false;
                    model.phoneType = 2;

                    if (defaultPhone.equals("0")) {
                        defaultPhone = model.phoneNumber;
                        defaultType = model.phoneType;
                        setCallerNumber(defaultPhone, defaultType);
                    }
                    if (defaultPhone.equals(model.phoneNumber)) {
                        model.checked = true;
                    }
                    list.add(model);
                }
            }

            if (voiceNumber != null && voiceNumber.getPhone() != null) {
                model = new CallerNumberBean();
                model.phoneNumber = voiceNumber.getPhone();
                model.checked = false;
                model.phoneType = 1;
                if (defaultPhone.equals("0")) {
                    defaultPhone = model.phoneNumber;
                    defaultType = model.phoneType;
                    setCallerNumber(defaultPhone, defaultType);
                }
                if (defaultPhone.equals(model.phoneNumber)) {
                    model.checked = true;
                }
                list.add(model);
            }

            if (list.size() > 0) {
                adapter.setData(list);
                adapter.notifyDataSetChanged();
            }
        }

        tv_phone_number.setText(defaultPhone);
        if (defaultType == 1) {
            iv_caller.setImageResource(R.drawable.rd_number);
        } else {
            iv_caller.setImageResource(R.drawable.my_box);
        }
    }

    private void setCallerNumber(String phoneNumber, int callerType) {
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO,
                SPreferencesTool.login_caller_phone, phoneNumber);
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO,
                SPreferencesTool.login_caller_type, callerType);
    }
}
