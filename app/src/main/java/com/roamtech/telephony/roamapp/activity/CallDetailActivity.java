package com.roamtech.telephony.roamapp.activity;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.Blacklist;
import com.roamtech.telephony.roamapp.adapter.CommonAdapter;
import com.roamtech.telephony.roamapp.adapter.ViewHolder;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CallDetailRecordDBModel;
import com.roamtech.telephony.roamapp.bean.UserHeadBean;
import com.roamtech.telephony.roamapp.db.model.BlacklistDBModel;
import com.roamtech.telephony.roamapp.dialog.PlayerDialog;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.event.EventBlacklist;
import com.roamtech.telephony.roamapp.event.EventCallHistory;
import com.roamtech.telephony.roamapp.event.EventLoadContactEnd;
import com.roamtech.telephony.roamapp.helper.CallLogDataHelper;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.view.RoundImageView;
import com.will.common.tool.time.DateTimeTool;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.LinphoneCallLog;

import java.io.File;
import java.util.ArrayList;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;

import static com.roamtech.telephony.roamapp.util.CallMessageUtil.blackDao;


/**
 * Created by caibinglong
 * on 2017/2/7.
 */

public class CallDetailActivity extends HeaderBaseActivity {
    private ListView lv_call_log;
    private CommonAdapter<CallDetailRecordDBModel> commonAdapter;
    private List<CallDetailRecordDBModel> listData;
    private String phoneNumber;
    private CallMessageUtil callMessageUtil;
    private PlayerDialog.Builder builder;
    private ImageView iv_chat, iv_call, iv_back;
    private RoundImageView id_circle_image;
    private TextView tv_call_number_head, tv_phone_number;
    private TextView tv_add_new_contact, tv_add_old_contact;
    private LinearLayout layout_no_contact, layout_contact;
    private UserHeadBean userHeadBean;
    private String from = "all";
    private final int pageSize = 80;
    private String displayName;
    private int position;
    private LinearLayout layout_blacklist;
    private TextView tv_blacklist;
    private boolean isAddBlacklist = false;
    private BlacklistDBModel queryModel;
    private Blacklist blacklistFun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        phoneNumber = getBundle().getString("phoneNumber");
        if (phoneNumber.equals("-1"))
            phoneNumber = "unknown";
        from = getBundle().getString("from");
        position = getBundle().getInt("position");
        lv_call_log = (ListView) findViewById(R.id.lv_call_log);
        iv_chat = (ImageView) findViewById(R.id.iv_chat);
        iv_call = (ImageView) findViewById(R.id.iv_call);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_call_number_head = (TextView) findViewById(R.id.tv_call_number_head);
        tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
        id_circle_image = (RoundImageView) findViewById(R.id.id_circle_image);
        tv_add_new_contact = (TextView) findViewById(R.id.tv_add_new_contact);
        tv_add_old_contact = (TextView) findViewById(R.id.tv_add_old_contact);
        layout_no_contact = (LinearLayout) findViewById(R.id.layout_no_contact);
        layout_contact = (LinearLayout) findViewById(R.id.layout_contact);
        layout_blacklist = (LinearLayout) findViewById(R.id.layout_blacklist);
        tv_blacklist = (TextView) findViewById(R.id.tv_blacklist);
        callMessageUtil = new CallMessageUtil(getApplicationContext(), uiHandler);
        findCallByPhoneNumber();
        initViewData();
        initViewListener();
    }

    private void initViewData() {
        userHeadBean = RDContactHelper.getUserHeadBean(getApplicationContext(), phoneNumber, position);
        //contact = RDContactHelper.findContactByPhone(phoneNumber);
        displayName = userHeadBean.displayName;
        if (userHeadBean.isContact) {
            layout_no_contact.setVisibility(View.GONE);
            layout_contact.setVisibility(View.VISIBLE);
        } else {
            layout_no_contact.setVisibility(View.VISIBLE);
            layout_contact.setVisibility(View.GONE);
        }
        queryModel = blackDao.queryBlacklistByPhone(phoneNumber, getUserId());
        if (queryModel != null) {
            tv_blacklist.setText(getString(R.string.cancel_blacklist));
            isAddBlacklist = true;
        } else {
            tv_blacklist.setText(getString(R.string.add_blacklist));
            isAddBlacklist = false;
        }
        tv_call_number_head.setText(displayName);
        tv_phone_number.setText(phoneNumber.equals(getString(R.string.str_unknown)) ? getString(R.string.str_unknown_number) : phoneNumber);
        id_circle_image.setText(userHeadBean.headPhotoText, userHeadBean.headTextSize);
        id_circle_image.setImageBitmap(userHeadBean.headPhoto);
        commonAdapter = new CommonAdapter<CallDetailRecordDBModel>(this, listData, R.layout.item_call_log_details) {
            @Override
            public void convert(ViewHolder helper, final CallDetailRecordDBModel item, int position) {
                String callText = null;
                if (item.isIncoming() == 1) {
                    helper.setImageResource(R.id.iv_call, R.drawable.ic_call_out);
                    helper.showView(R.id.tv_duration);
                    helper.setText(R.id.tv_call_state, getString(R.string.call_out)).setTextColor(R.id.tv_call_state, ContextCompat.getColor(getApplicationContext(), R.color.text_black));
                } else {
                    if (item.getStatus() == LinphoneCallLog.CallStatus.Missed.toInt()) {
                        helper.setImageResource(R.id.iv_call, R.drawable.ic_call_no_recieve);
                        helper.hideInVisibleView(R.id.tv_duration);
                        helper.setText(R.id.tv_call_state, getString(R.string.missed_call)).setTextColor(R.id.tv_call_state, ContextCompat.getColor(getApplicationContext(), R.color.color_f92a15));
                    } else if (item.getStatus() == LinphoneCallLog.CallStatus.Declined.toInt()) {
                        helper.setImageResource(R.id.iv_call, R.drawable.ic_call_in);
                        helper.showView(R.id.tv_duration);
                        callText = getString(R.string.call_hang_up);
                        helper.setText(R.id.tv_call_state, getString(R.string.call_receiver)).setTextColor(R.id.tv_call_state, ContextCompat.getColor(getApplicationContext(), R.color.text_black));
                    } else {
                        helper.setImageResource(R.id.iv_call, R.drawable.ic_call_in);
                        helper.showView(R.id.tv_duration);
                        helper.setText(R.id.tv_call_state, getString(R.string.call_receiver)).setTextColor(R.id.tv_call_state, ContextCompat.getColor(getApplicationContext(), R.color.text_black));
                    }
                }
                if (item.getDuration() > 0) {
                    helper.setText(R.id.tv_duration, DateTimeTool.DateFormat(item.getDuration() * 1000, "mm:ss"));
                } else {
                    helper.setText(R.id.tv_duration, callText == null ? getString(R.string.call_no_connected) : callText);
                }
                boolean isHaveRecord = findRecordFile(item.Id + ".wav");
                if (isHaveRecord) {
                    helper.showView(R.id.iv_recording);
                } else {
                    helper.hideView(R.id.iv_recording);
                }

                helper.setText(R.id.tv_time, CallLogDataHelper.timestampToHumanDate(getApplicationContext(), item.getTimestamp()));
                helper.setOnClick(R.id.iv_recording, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //frame_mask_layer.setVisibility(View.VISIBLE);
                        builder = new PlayerDialog.Builder(CallDetailActivity.this, RoamApplication.FILEPATH_RECORD + File.separator + item.Id + ".wav", refreshCall, item);
                        builder.create().show();
                    }
                });
            }
        };
        lv_call_log.setAdapter(commonAdapter);
        blacklistFun = new Blacklist(getApplicationContext());
    }

    private void initViewListener() {
        iv_chat.setOnClickListener(this);
        iv_call.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_add_new_contact.setOnClickListener(this);
        tv_add_old_contact.setOnClickListener(this);
        layout_contact.setOnClickListener(this);
        layout_blacklist.setOnClickListener(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void loadContactSuccess(EventLoadContactEnd eventLoadContactEnd) {
        findCallByPhoneNumber();
        initViewData();
        initViewListener();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_chat:
                if (phoneNumber.equals(getString(R.string.str_unknown)) || phoneNumber.equals("-1") || phoneNumber.equals(getString(R.string.str_unknown_number))) {
                    String alertText = getString(R.string.str_no_can_chat_unknown);
                    TipDialog mKickOutDlg = new TipDialog(this, getString(R.string.prompt), alertText);
                    mKickOutDlg.setLeftButton(getString(R.string.button_ok), null);
                    mKickOutDlg.setRightButton("", null);
                    mKickOutDlg.show();
                    return;
                }
                LinphoneActivity.instance().displayChat(phoneNumber);
                break;
            case R.id.iv_call:
                if (phoneNumber.equals(getString(R.string.str_unknown)) || phoneNumber.equals("-1") || phoneNumber.equals(getString(R.string.str_unknown_number))) {
                    String alertText = getString(R.string.str_no_can_call_unknown);
                    TipDialog mKickOutDlg = new TipDialog(this, getString(R.string.prompt), alertText);
                    mKickOutDlg.setLeftButton(getString(R.string.button_ok), null);
                    mKickOutDlg.setRightButton("", null);
                    mKickOutDlg.show();
                    return;
                }
                LinphoneActivity.instance().setAddressGoToDialerAndCall(phoneNumber, displayName, null);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_add_new_contact:
                Intent addIntent = new Intent(Intent.ACTION_INSERT, Uri.withAppendedPath(Uri.parse("content://com.android.contacts"), "contacts"));
                addIntent.setType("vnd.android.cursor.dir/person");
                addIntent.setType("vnd.android.cursor.dir/contact");
                addIntent.setType("vnd.android.cursor.dir/raw_contact");
                addIntent.putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, phoneNumber);
                startActivity(addIntent);
                break;
            case R.id.tv_add_old_contact:
                Intent oldConstantIntent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
                oldConstantIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                oldConstantIntent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
                oldConstantIntent.putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, 3);
                if (oldConstantIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(oldConstantIntent);
                }
                break;
            case R.id.layout_contact:
                Uri personUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, userHeadBean.contactId);// info.id联系人ID
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(personUri);
                startActivity(intent);
                break;
            case R.id.layout_blacklist:
                confirmDialog();
                break;
        }
    }

    private void confirmDialog() {
        String strConfirm = isAddBlacklist ? getString(R.string.confirm_cancel_blacklist) : getString(R.string.confirm_add_blacklist);
        final TipDialog dialog = new TipDialog(this, strConfirm, "");
        dialog.setRightButton(getString(R.string.button_ok), new TipDialog.OnClickListener() {
            @Override
            public void onClick(int which) {
                if (!isAddBlacklist) {
                    queryModel = new BlacklistDBModel();
                    queryModel.phone = phoneNumber;
                    queryModel.userId = getUserId();
                    queryModel.serverId = -1;
                    blackDao.add(queryModel);
                    tv_blacklist.setText(getString(R.string.cancel_blacklist));
                    isAddBlacklist = true;
                } else {
                    blackDao.delete(queryModel);
                    tv_blacklist.setText(getString(R.string.add_blacklist));
                    isAddBlacklist = false;
                }
                addOrDeleteBlacklist();
                EventBus.getDefault().postSticky(new EventBlacklist());
            }
        });

        dialog.setLeftButton(getString(R.string.button_cancel), new TipDialog.OnClickListener() {
            @Override
            public void onClick(int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void findCallByPhoneNumber() {
        listData = from.equals(CallMessageUtil.DATA_TYPE_ALL) ? callMessageUtil.getAllCallListByPhone(phoneNumber, 0, pageSize)
                : callMessageUtil.getMissCallListByPhone(phoneNumber, 0, pageSize);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getCallByWebService();
            }
        });

    }

    /**
     * 获取 来自服务器的 通话记录数据
     */
    private void getCallByWebService() {
        JSONObject jsonObject = getAuthJSONObject();
        try {
            jsonObject.put("phone", phoneNumber);
            jsonObject.put("is_fetch_new", true);
            jsonObject.put("size", pageSize);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callMessageUtil.getCallListByPhone(jsonObject, true, from);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_GET_ALL_CALL_BY_PHONE:
                listData = from.equals(CallMessageUtil.DATA_TYPE_ALL) ? callMessageUtil.getAllCallListByPhone(phoneNumber, 0, pageSize)
                        : callMessageUtil.getMissCallListByPhone(phoneNumber, 0, pageSize);
                if (listData.size() > 0) {
                    commonAdapter.setData(listData);
                    commonAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 查询 关联通话的录音文件
     *
     * @param fileName file
     * @return bool
     */
    private boolean findRecordFile(String fileName) {
        File file = new File(RoamApplication.FILEPATH_RECORD + File.separator + fileName);
        // 判断文件目录是否存在
        if (file.exists()) {
            return true;
        }
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callHistory(EventCallHistory event) {
        if (event.isInsert()) {
            if (builder != null) {
                builder.closeDialog();
                builder = null;
            }
        }
    }

    public PlayerDialog.iRefreshCall refreshCall = new PlayerDialog.iRefreshCall() {
        @Override
        public void refreshCall() {
            if (builder != null) {
                builder.closeDialog();
                builder = null;
            }
            findCallByPhoneNumber();
            initViewData();
            initViewListener();
        }
    };

    /**
     * 服务端 黑名单操作
     */
    private void addOrDeleteBlacklist() {
        ArrayList<String> phoneList = new ArrayList<>();
        phoneList.add(phoneNumber);
        JSONObject jsonObject = getAuthJSONObject();
        try {
            jsonObject.put("phones", phoneList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (isAddBlacklist) {
            blacklistFun.addBlacklist(jsonObject, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    super.onFailure(errorMap);
                }

                @Override
                public void onSuccess(String response) {
                    super.onSuccess(response);
                }
            });
        } else {
            blacklistFun.deleteBlacklist(jsonObject, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    super.onFailure(errorMap);
                }

                @Override
                public void onSuccess(String response) {
                    super.onSuccess(response);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
