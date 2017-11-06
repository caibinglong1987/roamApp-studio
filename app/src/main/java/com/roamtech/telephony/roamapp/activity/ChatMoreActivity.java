package com.roamtech.telephony.roamapp.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.ChatMessageAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.db.model.ChatDBModel;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import org.linphone.LinphoneService;
import org.linphone.LinphoneUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author way
 */
public class ChatMoreActivity extends BaseActivity {
    private String sipUri;
    private String displayName;
    private TextView contactName;
    private TextView selectAll;
    private boolean bSelectAll;
    private ListView mListView;
    private ChatMessageAdapter mAdapter;// 消息视图的Adapter
    private int mSelectId;
    private List<ChatDBModel> historyList = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatmore);
    }

    /**
     * 初始化view
     */
    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        sipUri = getIntent().getExtras().getString("SipUri");
        displayName = getIntent().getExtras().getString("DisplayName");
        mSelectId = getIntent().getExtras().getInt("MsgId");
        mListView = (ListView) findViewById(R.id.listview);
        contactName = (TextView) findViewById(R.id.contactName);
        selectAll = (TextView) findViewById(R.id.tv_selectall);

        bSelectAll = false;
        displayChatHeader(displayName);
        loadLocalCache(false, sipUri);

        selectAll.setOnClickListener(this);
        findViewById(R.id.iv_copy).setOnClickListener(this);
        findViewById(R.id.iv_delete).setOnClickListener(this);
        findViewById(R.id.iv_forward).setOnClickListener(this);
        // Force hide keyboard
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.tv_selectall) {
            bSelectAll = !bSelectAll;
            selectAll.setText(bSelectAll ? R.string.unselect_all : R.string.select_all);
            mAdapter.selectAll(bSelectAll);
            mAdapter.notifyDataSetChanged();
            return;
        }
        if (mAdapter.getSelectedList().isEmpty()) {
            return;
        }
        switch (v.getId()) {
            case R.id.iv_copy:
                copy();
                break;
            case R.id.iv_forward:
                forward();
                break;
            case R.id.iv_delete:
                delete();
                break;
        }
    }

    @Override
    public void onPause() {
        LinphoneService.instance().removeMessageNotification();
        if (LinphoneActivity.isInstanciated()) {
            LinphoneActivity.instance().updateChatFragment(null);
        }
        onSaveInstanceState(getIntent().getExtras());
        super.onPause();
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onResume() {

//		invalidate();
        super.onResume();
    }

    private void displayChatHeader(String displayName) {
        if (displayName == null && getResources().getBoolean(R.bool.only_display_username_if_unknown) && LinphoneUtils.isSipAddress(sipUri)) {
            contactName.setText(LinphoneUtils.getUsernameFromAddress(sipUri));
        } else if (displayName == null || displayName.isEmpty()) {
            contactName.setText(sipUri);
        } else {
            contactName.setText(displayName);
        }

    }

    private int[] buildSelectMsgId(List<ChatDBModel> selectList) {
        int[] results = new int[selectList.size()];
        int i = 0;
        for (ChatDBModel model : selectList) {
            results[i++] = Integer.parseInt(String.valueOf(model.logId));
        }
        return results;
    }

    private void copy() {
        Intent i = new Intent();
        i.putExtra("Action", "copy");
        i.putExtra("MsgIds", buildSelectMsgId(mAdapter.getSelectedList()));
        setResult(RESULT_OK, i);
        finish();
    }

    private void forward() {
        Intent i = new Intent();
        i.putExtra("Action", "forward");
        i.putExtra("MsgIds", buildSelectMsgId(mAdapter.getSelectedList()));
        setResult(RESULT_OK, i);
        finish();
    }

    private void delete() {
        Intent i = new Intent();
        i.putExtra("Action", "delete");
        i.putExtra("MsgIds", buildSelectMsgId(mAdapter.getSelectedList()));
        setResult(RESULT_OK, i);
        finish();
    }

    /**
     * 加载本地缓存 记录
     *
     * @param ascending false 降序
     * @param to        to
     */
    private void loadLocalCache(boolean ascending, String to) {
        CallMessageUtil callMessageUtil = new CallMessageUtil(getApplicationContext(), null);
        historyList = callMessageUtil.getMessageByPhoneList(ascending, to, 0, 200);
        if (historyList != null) {
            Collections.sort(historyList, new Comparator<ChatDBModel>() {
                public int compare(ChatDBModel arg0, ChatDBModel arg1) {
                    return Integer.parseInt(String.valueOf(arg0.logId)) - Integer.parseInt(String.valueOf(arg1.logId));
                }
            });
            initDataListView();
        }
    }

    private void initDataListView() {
        mAdapter = new ChatMessageAdapter(this, historyList);
        mAdapter.setEditing(true, mSelectId);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mAdapter.getSelectPos());
    }
}