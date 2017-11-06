package com.roamtech.telephony.roamapp.activity;

import java.util.ArrayList;
import java.util.List;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.ContactSearchAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.widget.sortlist.ClearEditText;
import com.roamtech.telephony.roamapp.adapter.ContactSearchAdapter.ViewHolder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SelectContactsActivity extends BaseActivity {
    private TextView tvComplete;
    private ListView mListView;
    private ClearEditText mClearEditText;
    private List<RDContact> mContacts;
    private ContactSearchAdapter mContactSearchAdapter;

    @Override
    public void initData() {
        super.initData();
        mContacts = (List<RDContact>) getIntent().getExtras().getSerializable("SelectContacts");
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_contact;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == tvComplete) {
            mContacts = mContactSearchAdapter.getSelectedList();
            if (!mContacts.isEmpty() && mContacts.size() > 1) {
                ToastUtils.showToast(this, getString(R.string.str_alert_support_group_message));
                return;
            }
            Intent i = new Intent();
            Bundle extras = new Bundle();
            extras.putSerializable("SelectContacts", (ArrayList) mContacts);
            i.putExtras(extras);
            setResult(RESULT_OK, i);
            finish();
        }
        if (v == mClearEditText) {
            Bundle bundle = new Bundle();
            bundle.putInt("searchType", 3);
            toActivity(SearchActivity.class, bundle);
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        tvComplete = (TextView) findViewById(R.id.tv_complete);
        mListView = (ListView) findViewById(android.R.id.list);
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

        mContactSearchAdapter = new ContactSearchAdapter(this, RDContactHelper.getAllSystemContacts(), mContacts);
        mContactSearchAdapter.setShowChoose(true);
        mListView.setAdapter(mContactSearchAdapter);
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        tvComplete.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mClearEditText.setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        super.onItemClick(parent, view, position, id);
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        boolean bSelect = mContactSearchAdapter.toggleChecked(position);
        viewHolder.ivSelect.setImageResource(bSelect ? R.drawable.ic_choosed : R.drawable.ic_unchoose);
    }
}
