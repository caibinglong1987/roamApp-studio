package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.DefaultSingleAdapter;
import com.roamtech.telephony.roamapp.adapter.SettingSingleSelectAdapter;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.will.common.tool.view.LibRecyclerView.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by caibinglong
 * on 2016/11/8.
 * 信道选择
 */

public class LMBAOChannelActivity extends HeaderBaseActivity {
    private String ChannelNumber;
    private RecyclerView list_recycler_view;
    private ArrayList<String> items = new ArrayList<>();
    private DefaultSingleAdapter adapter = null;
    private int position = 0; //上次选中的位置
    private RoamDialog roamDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi_show);
        ChannelNumber = getIntent().getStringExtra("ChannelNumber");
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_wifi_check));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        list_recycler_view = (RecyclerView) findViewById(R.id.list_recycler_view);
        list_recycler_view.setLayoutManager(new LinearLayoutManager(this));//这里用线性显示 类似于list view
        adapter = new DefaultSingleAdapter(this);
        adapter.setOnItemClickListener(onItemClickListener);
        roamDialog = new RoamDialog(this, getString(R.string.loadinginfo));
        roamDialog.show();
        for (int i = 1; i < 14; i++) {
            items.add(i - 1, i + "");
            if ((i + "").equals(ChannelNumber)) {
                position = items.indexOf(ChannelNumber); //默认上次选中
            }
        }

        adapter.addItems(items);
        list_recycler_view.setAdapter(adapter);
        adapter.setCurrentSelect(position);
        list_recycler_view.setItemAnimator(new DefaultItemAnimator());
        list_recycler_view.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        roamDialog.dismiss();
    }

    private SettingSingleSelectAdapter.OnItemClickListener onItemClickListener = new SettingSingleSelectAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) { //返回本次选中的信道
            Bundle bundle = new Bundle();
            bundle.putString("ChannelNumber", items.get(position));
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    };
}
