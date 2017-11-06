package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.ContactSearchAdapter;
import com.roamtech.telephony.roamapp.adapter.SwipeMessageNewAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.bean.RDContactPhone;
import com.roamtech.telephony.roamapp.bean.SimpleMessage;
import com.roamtech.telephony.roamapp.db.model.ChatDBModel;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.util.Utility;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by caibinglong
 * on 2016/12/28.
 * 短信搜索
 */

public class SearchActivity extends BaseActivity {
    private String searchKey = null;
    private EditText search_input;
    private TextView tv_cancel;
    private ImageView iv_delete;
    private LinearLayout layout_no_data;
    private TextView tv_context;

    //消息搜索
    private SwipeMessageNewAdapter messageNewAdapter;
    private SwipeMenuListView mMessageListView;
    private CallMessageUtil callMessageUtil;
    private List<SimpleMessage> simpleMessageList = new ArrayList<>();
    private TranslateAnimation mShowAction, mHiddenAction;
    private final int MESSAGE_SEARCH = 1;
    private final int CONTACTS_SEARCH = 2;
    private final int CONTACTS_SEARCH_TO_MESSAGE = 3; //新建消息 搜索联系人
    private int searchType = 1;

    //通讯录搜索
    private ContactSearchAdapter mAdapter;
    private ListView mSortListView;
    private List<RDContact> sortModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
    }

    protected void setStatusBar() {
        Utility.setColor(this, ContextCompat.getColor(getApplicationContext(), R.color.roam_color));
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        search_input = (EditText) findViewById(R.id.search_input);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        mMessageListView = (SwipeMenuListView) findViewById(R.id.lv_message);
        mSortListView = (ListView) findViewById(R.id.lv_contacts);
        iv_delete = (ImageView) findViewById(R.id.iv_delete);
        tv_context = (TextView) findViewById(R.id.tv_context);
        layout_no_data = (LinearLayout) findViewById(R.id.layout_no_data);
        messageNewAdapter = new SwipeMessageNewAdapter(this, mMessageListView, null);
        mMessageListView.setAdapter(messageNewAdapter);

        /** 给ListView设置adapter **/
        mAdapter = new ContactSearchAdapter(this, sortModelList, sortModelList);
        mSortListView.setAdapter(mAdapter);

        callMessageUtil = new CallMessageUtil(getApplicationContext(), null);
        search_input.setFocusable(true);
        search_input.requestFocus();
        Utility.openKeyboard(search_input, getApplicationContext());

        searchType = getIntent().getIntExtra("searchType", 1);
        if (searchType == MESSAGE_SEARCH) {
            tv_context.setText(getString(R.string.search_content_message));

        }
        if (searchType == CONTACTS_SEARCH || searchType == CONTACTS_SEARCH_TO_MESSAGE) {
            tv_context.setText(getString(R.string.search_content_contact));
        }

        layout_no_data.setVisibility(VISIBLE);
    }

    @Override
    public void setListener() {
        super.setListener();
        tv_cancel.setOnClickListener(this);
        iv_delete.setOnClickListener(this);
        search_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchMessageTask != null) {
                    uiHandler.removeCallbacks(searchMessageTask);
                }
                if (searchContactsTask != null) {
                    uiHandler.removeCallbacks(searchContactsTask);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                searchKey = s.toString();
                uiHandler.sendEmptyMessage(MsgType.MSG_SEARCH_CHANGE);
                if (searchType == MESSAGE_SEARCH) {
                    uiHandler.post(searchMessageTask);
                }
                if (searchType == CONTACTS_SEARCH || searchType == CONTACTS_SEARCH_TO_MESSAGE) {
                    uiHandler.post(searchContactsTask);
                }
            }
        });

        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                SimpleMessage msg = (SimpleMessage) adapterView.getItemAtPosition(position);
                String sipUri = msg.getContact();
                //sipUri = CallUtil.getSipTo()+";to="+CallUtil.getRealToNumber(sipUri);
                if (LinphoneActivity.isInstanciated()) {
                    LinphoneActivity.instance().displayChat(sipUri);
                }
            }
        });

        mSortListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Utility.closeKeyboard(search_input, getApplicationContext());
                if (searchType == CONTACTS_SEARCH) {
                    Bundle extras = new Bundle();
                    RDContact contact = (RDContact) mSortListView.getItemAtPosition(position);
                    extras.putSerializable(RDContact.class.getName(), contact);
                    toActivity(ContactInfoActivity.class, extras);
                }
                if (searchType == CONTACTS_SEARCH_TO_MESSAGE) {
                    Bundle extras = new Bundle();
                    extras.putSerializable(RDContact.class.getName(), (RDContact) mSortListView.getItemAtPosition(position));
                    toActivity(ChattingActivity.class, extras);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_cancel:
                Utility.closeKeyboard(search_input, getApplicationContext());
                finish();
                break;
            case R.id.iv_delete:
                search_input.setText("");
                break;
        }
    }

    public Runnable searchMessageTask = new Runnable() {
        @Override
        public void run() {
            searchLocalMessageData();
        }
    };

    public Runnable searchContactsTask = new Runnable() {
        @Override
        public void run() {
            searchContactsData();
        }
    };

    /**
     * 搜索消息内容
     */
    private void searchLocalMessageData() {
        if (searchKey == null || "".equals(searchKey)) {
            messageNewAdapter.refreash(null);
            return;
        }

        messageNewAdapter.refreash(null);
        List<RDContact> filterDateList = null;
        List<RDContact> mOriginContacts = RDContactHelper.getAllSystemContacts();
        if (!RDContactHelper.isEmptyList(mOriginContacts)) {
            filterDateList = new ArrayList<>();
            for (RDContact contact : mOriginContacts) {
                if (contact.filter(searchKey)) {
                    filterDateList.add(contact);
                }
            }
        }
        List<ChatDBModel> searchList = new ArrayList<>();
        List<RDContactPhone> phoneList;
        String phoneNumber;
        String loginPhone = loginUserPhone();
        if (filterDateList != null) {
            for (int i = 0; i < filterDateList.size(); i++) {
                phoneList = filterDateList.get(i).getPhoneList();
                if (phoneList != null && phoneList.size() > 0) {
                    for (int m = 0; m < phoneList.size(); m++) {
                        phoneNumber = phoneList.get(m).getNumber();
                        phoneNumber = phoneNumber.replace("-", "").replace("+86", "");
                        if (!phoneNumber.equals(loginUserPhone())) {
                            searchList.addAll(callMessageUtil.searchMessage(phoneNumber, loginPhone));
                        }
                    }
                }
            }
        }
        searchList.addAll(callMessageUtil.searchMessage(searchKey, loginPhone));
        searchList = removeDuplicate(searchList);
        Collections.sort(searchList, new Comparator<ChatDBModel>() {
            @Override
            public int compare(ChatDBModel model, ChatDBModel mode2) {
                return (int) (model.timestamp - mode2.timestamp);
            }
        });
        Collections.reverse(searchList);
        uiHandler.obtainMessage(MsgType.MSG_MESSAGE_SEARCH_SUCCESS, searchList).sendToTarget();
    }

    public List<ChatDBModel> removeDuplicate(List<ChatDBModel> list) {
        List<ChatDBModel> newList = new ArrayList<>();
        boolean isHas = false;
        for (ChatDBModel item : list) {
            for (ChatDBModel newItem : newList) {
                isHas = false;
                if (newItem.logId == item.logId || (newItem.fromContact.equals(item.fromContact) &&
                        newItem.toContact.equals(item.toContact)) ||
                        (newItem.toContact.equals(item.fromContact) &&
                                newItem.fromContact.equals(item.toContact))) {
                    isHas = true;
                    break;
                }
            }
            if (!isHas) {
                newList.add(item);
            }
        }
        return newList;
    }

    private void refreshSearchAdapter(List<ChatDBModel> data) {
        String loginPhone = loginUserPhone();
        String touchPhone = getRoamPhone();
        if (data != null && data.size() > 0) {
            simpleMessageList = new ArrayList<>();
            String contact;
            String fromPhone;
            String toPhone;
            String messageText;
            for (int index = 0; index < data.size(); index++) {
                fromPhone = data.get(index).fromContact;
                if (fromPhone.equals(loginPhone) || (touchPhone != null && touchPhone.equals(fromPhone))) {
                    toPhone = data.get(index).fromContact;
                    fromPhone = data.get(index).toContact;
                    contact = fromPhone;
                } else {
                    contact = data.get(index).fromContact;
                    fromPhone = data.get(index).fromContact;
                    toPhone = data.get(index).toContact;
                }
                messageText = data.get(index).message;
                SimpleMessage message = new SimpleMessage(false, true, contact,
                        messageText, data.get(index).timestamp, fromPhone, toPhone);
                message.searchKey = searchKey;
                simpleMessageList.add(message);
            }
            messageNewAdapter.refreash(simpleMessageList);
        }
    }

    private void setShowAction() {
        if (mShowAction == null) {
            mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
            mShowAction.setDuration(500);
        }
    }

    private void setHiddenAction() {
        if (mHiddenAction == null) {
            mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                    -1.0f);
            mHiddenAction.setDuration(500);
        }
    }

    private void searchContactsData() {
        if (TextUtils.isEmpty(searchKey)) {
            mAdapter.refreash(null);
            return;
        } else {
            mAdapter.refreash(null);
            List<RDContact> searchList = RDContactHelper.searchAllPhone(searchKey);
            mAdapter.refreash(searchList);
            mAdapter.setSearchText(searchKey);
            mAdapter.notifyDataSetChanged();
        }
        mSortListView.setSelection(0);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_MESSAGE_SEARCH_SUCCESS:
                List<ChatDBModel> modelList = (List<ChatDBModel>) msg.obj;
                refreshSearchAdapter(modelList);
                break;
            case MsgType.MSG_SEARCH_CHANGE:
                if (!StringUtil.isBlank(searchKey)) {
                    iv_delete.setVisibility(VISIBLE);
                    layout_no_data.setVisibility(GONE);
                    if (searchType == MESSAGE_SEARCH) {
                        mMessageListView.setVisibility(VISIBLE);
                        mSortListView.setVisibility(GONE);
                    }
                    if (searchType == CONTACTS_SEARCH || searchType == CONTACTS_SEARCH_TO_MESSAGE) {
                        mMessageListView.setVisibility(GONE);
                        mSortListView.setVisibility(VISIBLE);
                    }
                } else {
                    mMessageListView.setVisibility(GONE);
                    mSortListView.setVisibility(GONE);
                    iv_delete.setVisibility(GONE);
                    layout_no_data.setVisibility(VISIBLE);
                }
                break;
        }
    }
}
