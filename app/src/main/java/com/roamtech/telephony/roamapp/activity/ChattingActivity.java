package com.roamtech.telephony.roamapp.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.Receiver.ActionValue;
import com.roamtech.telephony.roamapp.Receiver.MessageReceiver;
import com.roamtech.telephony.roamapp.adapter.ChatMessageAdapter;
import com.roamtech.telephony.roamapp.adapter.KeyboardSearchAdapter;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.bean.RDContactPhone;
import com.roamtech.telephony.roamapp.db.model.ChatDBModel;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.util.CallUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.util.VerificationUtil;
import com.roamtech.telephony.roamapp.view.FlowLayout;

import org.linphone.LinphoneManager;
import org.linphone.LinphoneService;
import org.linphone.LinphoneUtils;
import org.linphone.compatibility.Compatibility;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneBuffer;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatMessage.State;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.mediastream.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author way
 */
public class ChattingActivity extends HeaderBaseActivity implements LinphoneChatMessage.LinphoneChatMessageListener, OnFocusChangeListener {
    private LinphoneChatRoom chatRoom;
    private String sipUri;
    private String displayName = null;
    private String pictureUri = null;
    private TextView contactName;
    private ImageView ivDial;
    private TextView mBtnSend;// 发送btn
    private ImageView ivAddMessage;
    private EditText mEditTextContent;
    private ListView mListView;
    private RelativeLayout rl_bottom;
    private ChatMessageAdapter mAdapter;// 消息视图的Adapter
    private TextWatcher textWatcher;
    private KeyboardSearchAdapter mKeyboardSearchAdapter;
    private List<RDContact> mAllContactsList;
    private ArrayList<RDContact> mContacts;
    private RDContact mContact;
    private EditText mEtChatTo;
    private ListView mContactListView;
    private RelativeLayout layoutChatto;
    private boolean bNewChat;
    private FlowLayout flowLayout;
    private List<View> selectView = new ArrayList<>();
    private static final int CHAT_MORE_ACTIVITY = 22;
    private static final int CHAT_SELECTCONTACTS_ACTIVITY = 23;

    private String loginPhone;
    private CallMessageUtil callMessageUtil;
    private int pageIndex = 0, pageSize = 50;
    private List<ChatDBModel> historyList = new ArrayList<>();

    private MessageReceiver messageReceiver;
    private RoamDialog roamDialog = null;

    private boolean isNewChat() {
        return bNewChat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        sipUri = getIntent().getExtras().getString("SipUri");
        if (sipUri != null) {
            mContact = RDContactHelper.findContactByPhone(sipUri);
        }
        if (mContact != null) {
            displayName = mContact.getDisplayName();
            Uri uri = RDContactHelper.getContactPictureUri(mContact.getId());
            if (uri != null) {
                pictureUri = uri.toString();
            }
        }
        initView();
        setViewListener();
    }


    /**
     * 初始化view
     */
    private void initView() {
        mContacts = new ArrayList<>();
        //待完成 后期准备 SipBean 对象使用
        mListView = (ListView) findViewById(R.id.listview);
        mBtnSend = (TextView) findViewById(R.id.tv_send);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        contactName = (TextView) findViewById(R.id.contactName);
        ivDial = (ImageView) findViewById(R.id.dial);
        layoutChatto = (RelativeLayout) findViewById(R.id.layout_chatto);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        mEtChatTo = (EditText) findViewById(R.id.et_chatto);
        flowLayout = (FlowLayout) findViewById(R.id.flowlayout);
        ivAddMessage = (ImageView) findViewById(R.id.tv_addmessageContact);
        mContactListView = (ListView) findViewById(android.R.id.list);

        mAllContactsList = RDContactHelper.getAllSystemContacts();
        mKeyboardSearchAdapter = new KeyboardSearchAdapter(this, mAllContactsList);
        mContactListView.setAdapter(mKeyboardSearchAdapter);
        mContactListView.setVisibility(View.GONE);
        loginPhone = loginUserPhone();
        callMessageUtil = new CallMessageUtil(getApplicationContext(), uiHandler);
        if (sipUri == null) {
            bNewChat = true;
        }
        displayChatHeader(displayName);
        if (!bNewChat) {
            roamDialog = new RoamDialog(this, getString(R.string.loadinginfo));
            roamDialog.show();
            loadLocalCache(false, sipUri);
            getUserWebMessage(sipUri);
            if (LinphoneService.isReady()) {
                LinphoneService.instance().resetMessageNotificationCount();
            }
        } else {
            ivDial.setEnabled(false);
        }
        messageReceiver = new MessageReceiver(this, callBack);
        messageReceiver.register(messageReceiver, null);
    }

    private void setViewListener() {
        ivAddMessage.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
        textWatcher = new TextWatcher() {
            public void afterTextChanged(Editable arg0) {
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (mEditTextContent.getText().toString().equals("")) {
                    mBtnSend.setEnabled(false);
                } else {
                    /*if (chatRoom != null)
                        chatRoom.compose();*/
                    mBtnSend.setEnabled(true);
                }
            }
        };
        mEtChatTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                uiHandler.obtainMessage(MsgType.MSG_SEARCH_CONTACT_TEXT, s.toString()).sendToTarget();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }
        });
        mEtChatTo.setOnKeyListener(onKeyListener);
        mEditTextContent.setOnFocusChangeListener(this);
        mContactListView.setOnItemClickListener(this);
        ivDial.setOnClickListener(this);
    }

    private boolean initChatRooms(LinphoneCore lc) {
        Log.e("Tring Chat To: " + sipUri);
        if (VerificationUtil.isChinese(sipUri)) {
            ToastUtils.showToast(this, getString(R.string.str_alert_no_phone_number), Toast.LENGTH_SHORT);
            return false;
        }
        String chatTo = LinphoneActivity.instance().getSipChatTo(CallUtil.getRealToNumber(sipUri));
        if (chatTo != null) {
            chatRoom = lc.getOrCreateChatRoom(chatTo);//sipUri);
            Log.e("Chat To: " + chatRoom.getPeerAddress().asString());
            //Only works if using liblinphone storage
            chatRoom.markAsRead();
            return true;
        }
        return false;
    }

    private OnKeyListener onKeyListener = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL
                    && event.getAction() == KeyEvent.ACTION_DOWN && selectView.size() > 0) {
                /* 隐藏软键盘 */
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    // inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),
                    // 0);
                }
                for (int index = 0; index < selectView.size(); index++) {
                    View view = selectView.get(index);
                    mContacts.remove(view.getTag());
                    flowLayout.removeView(view);
                }
                selectView.clear();
                if (mContacts.size() == 1) {
                    RDContact contact = mContacts.get(0);
                    changeDisplayedChat(sipUri, contact.getDisplayName(), null);
                } else {
                    chatRoom = null;
                }
                //群发 待完成
//                for (SortModel item : mContacts) {
//                    changeDisplayedChat(sipUri, item.getName(), item.getPhotoUri() == null ? null : item.getPhotoUri().toString());
//                }
                return true;
            }
            return false;
        }
    };

    private void addContact(RDContact contact) {
        final LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_flowlayout, flowLayout, false);
        itemLayout.setTag(contact);
        TextView tvUserName = (TextView) itemLayout.findViewById(R.id.tv_username);
        tvUserName.setText(contact.getDisplayName());
        tvUserName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                TextView sTvUserName = (TextView) v;
                sTvUserName.setSelected(!sTvUserName.isSelected());
                if (sTvUserName.isSelected()) {
                    sTvUserName.setTextColor(getResources().getColor(R.color.white));
                    sTvUserName.setBackgroundColor(getResources().getColor(R.color.green));
                    selectView.add(itemLayout);
                } else {
                    sTvUserName.setTextColor(getResources().getColor(R.color.roam_color));
                    sTvUserName.setBackgroundResource(0);
                    selectView.remove(itemLayout);
                }
            }
        });
        flowLayout.addView(itemLayout, flowLayout.getChildCount());
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_send:// 发送按钮点击事件
                sendTextMessage();
                break;
            case R.id.dial:
                dial();
                break;
            case R.id.tv_addmessageContact:
                showSelectContacts();
                break;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("messageDraft", mEditTextContent.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        mEditTextContent.removeTextChangedListener(textWatcher);
//        if (LinphoneService.isReady()) {
//            LinphoneService.instance().removeMessageNotification();
//        }
        LinphoneManager.getInstance().removeListener(this);
        onSaveInstanceState(getIntent().getExtras());
        //Hide keybord
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
        super.onPause();
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onResume() {
        mEditTextContent.addTextChangedListener(textWatcher);
        LinphoneManager.getInstance().addListener(this);
        displayMessageList();
        super.onResume();
    }

    private void dial() {
        if (LinphoneActivity.isInstanciated()) {
            LinphoneCore lc = LinphoneManager.getInstance().getLcIfManagerNotDestroyedOrNull();
            if (lc != null) {
                LinphoneProxyConfig lpc = lc.getDefaultProxyConfig();
                String to;
                if (lpc != null) {
                    String address = sipUri;
                    if (!address.contains("@")) {
                        to = lpc.normalizePhoneNumber(address);
                    } else {
                        to = sipUri;
                    }
                } else {
                    to = sipUri;
                }
                LinphoneActivity.instance().setAddressGoToDialerAndCall(to, displayName, null);
            }
        }
    }

    /**
     * 发送消息
     */
    private void sendTextMessage() {
        sendTextMessage(mEditTextContent.getText().toString());
        mEditTextContent.setText("");
    }

    private void sendTextMessage(String messageToSend) {
        if (sipUri == null) {
            sipUri = mEtChatTo.getText().toString();
        }
        if (sipUri.equals(loginPhone) || sipUri.equals(getRoamPhone())) {
            ToastUtils.showToast(this, getString(R.string.str_no_can_to_self_message));
            return;
        }
        LinphoneCore lc = LinphoneManager.getInstance().getLcIfManagerNotDestroyedOrNull();
        boolean isCreateChatRoom;
        if (chatRoom == null && lc != null) {
            isCreateChatRoom = initChatRooms(lc);
        } else isCreateChatRoom = true;
        if (!isCreateChatRoom) return;
        boolean isNetworkReachable = lc.isNetworkReachable();
        if (chatRoom != null && messageToSend != null && messageToSend.length() > 0 && isNetworkReachable) {
            String callId = UUID.randomUUID().toString();
            LinphoneChatMessage message = chatRoom.createLinphoneChatMessage(messageToSend);
            message.addCustomHeader("Call-ID", callId);
            chatRoom.sendChatMessage(message);
            message.setListener(LinphoneManager.getInstance());
            addMessage(loginPhone, sipUri, messageToSend, callId, true);
            Log.i("Sent message current status: " + message.getStatus());
            noticeUpdateData(loginPhone, sipUri, messageToSend, callId);
            if (bNewChat) { //切换到正常 会话界面
                bNewChat = false;
                String contactName;
                RDContact tempContact = RDContactHelper.findContactByPhone(sipUri);
                if (tempContact != null) {
                    contactName = tempContact.getDisplayName();
                } else {
                    contactName = sipUri;
                }
                displayChatHeader(contactName);
            }
        } else if (!isNetworkReachable && LinphoneActivity.isInstanciated()) {
            ToastUtils.showToast(this, getString(R.string.error_network_unreachable), Toast.LENGTH_LONG);

        } else if (sipUri == null || !VerificationUtil.isMobile(sipUri)) {
            ToastUtils.showToast(this, getString(R.string.str_alert_no_phone_number));
        }
    }

    /**
     * 新消息
     *
     * @param from        from
     * @param to          to
     * @param textMessage message
     *                    isAdd true 添加数据库
     */
    private void addMessage(String from, String to, String textMessage, String callId, boolean isFromSelf) {
        ChatDBModel model = new ChatDBModel();
        if (historyList == null) {
            historyList = new ArrayList<>();
        }

        model.fromContact = from;
        model.toContact = to;
        model.direction = isFromSelf ? 1 : 0;
        model.image = "";
        model.timestamp = System.currentTimeMillis();
        model.status = 0;
        model.messageStatus = 2;
        model.messageType = 0;
        model.message = textMessage;
        model.userId = SPreferencesTool.getInstance().getStringValue(this, SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId);
        model.callId = callId;
        historyList.add(model);
        mAdapter.notifyDataSetChanged();
    }

    public void displayMessageList() {
        mAdapter = new ChatMessageAdapter(this, historyList);
        mAdapter.setContact(mContact);
        mAdapter.setCallback(callback);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mListView.getCount() - 1);
        Log.e("消息数目--》", mAdapter.getCount() + "");
        if (roamDialog != null && roamDialog.isShowing()) {
            roamDialog.dismiss();
        }
    }

    private void displayChatHeader(String displayName) {
        if (isNewChat()) {
            layoutChatto.setVisibility(View.VISIBLE);
            contactName.setText(R.string.newchat);
            if (historyList != null && historyList.size() > 0) {
                historyList.clear();
                mAdapter.refreash(null);
            }
            return;
        }
        layoutChatto.setVisibility(View.GONE);
        RoamApplication.chatNowUserPhone = sipUri;
        if (sipUri != null && sipUri.equals("ucmsg")) {
            contactName.setText(getString(R.string.str_message_system));
            rl_bottom.setVisibility(View.GONE);
            return;
        }
        if (displayName != null && getResources().getBoolean(R.bool.only_display_username_if_unknown) && LinphoneUtils.isSipAddress(sipUri)) {
            contactName.setText(LinphoneUtils.getUsernameFromAddress(sipUri));
        } else if (displayName == null || displayName.isEmpty()) {
            contactName.setText(sipUri);
        } else {
            contactName.setText(displayName);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        RDContact contact = (RDContact) parent.getItemAtPosition(position);
        mContact = contact;
        mContacts.add(contact);
        addContact(contact);
        String sipUri = contact.getDialPhone() != null ? contact.getDialPhone().getNumber() : null;
        mEtChatTo.setText("");
        if (mContacts.size() == 1) {
            changeDisplayedChat(sipUri, contact.getDisplayName(), null);
        } else {
            chatRoom = null;
        }
    }

    public void changeDisplayedChat(String newSipUri, String displayName, String pictureUri) {
        if (newSipUri == null) {
            ToastUtils.showToast(this, getString(R.string.str_alert_no_phone_number));
            return;
        }
        RoamApplication.chatNowUserPhone = newSipUri;
        if (!newSipUri.equals(sipUri)) { //和上次聊天的对象不一样 重新获取数据
            historyList = callMessageUtil.getMessageByPhoneList(false, newSipUri, 0, pageSize);
            if (historyList == null) {
                historyList = new ArrayList<>();
            }
        }
        this.sipUri = newSipUri;
        this.displayName = displayName;
        this.pictureUri = pictureUri;
        String toNumber = CallUtil.getRealToNumber(sipUri);
        if (toNumber == null || toNumber.equals("-1") || !VerificationUtil.isNumber(toNumber)) {
            ToastUtils.showToast(this, getString(R.string.str_alert_no_phone_number));
            return;
        }

        mListView.setVisibility(View.VISIBLE);
        displayChatHeader(displayName);
        displayMessageList();
        ivDial.setEnabled(true);
    }

    private void sendMessageCallback(String callId, State state) {
        for (int i = 0; i < historyList.size(); i++) {
            if (historyList.get(i).callId.equals(callId)) {
                if (State.Delivered == state) {
                    historyList.get(i).messageStatus = 1;
                }
                if (State.NotDelivered == state) {
                    historyList.get(i).messageStatus = 0;
                }
                mAdapter.notifyDataSetChanged();
                Log.e("消息发送完成", "适配器刷新完成");

                break;
            }
        }
    }

    /**
     * 消息 发送 回调
     *
     * @param msg
     * @param state
     */
    @Override
    public void onLinphoneChatMessageStateChanged(LinphoneChatMessage msg, State state) {
        Log.e("消息发送回调---》", msg.getAppData() + "||" + state.toString() + "||" + msg.getCustomHeader("Call-ID"));
        sendMessageCallback(msg.getCustomHeader("Call-ID"), state);
    }

    @Override
    public void onLinphoneChatMessageFileTransferReceived(LinphoneChatMessage msg, LinphoneContent content,
                                                          LinphoneBuffer buffer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLinphoneChatMessageFileTransferSent(LinphoneChatMessage msg, LinphoneContent content, int offset,
                                                      int size, LinphoneBuffer bufferToFill) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLinphoneChatMessageFileTransferProgressChanged(LinphoneChatMessage msg, LinphoneContent content,
                                                                 int offset, int total) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == mEditTextContent && hasFocus) {
            setChangeSipUri();
        }
    }

    private void setChangeSipUri() {
        if (bNewChat) {
            String numberOrName = mEtChatTo.getText().toString();
            if (this.displayName != null && this.displayName.equals(numberOrName)) {
                return;
            }
            if (StringUtil.isTrimBlank(numberOrName)) {
                return;
            }
            try {
                String toNumber = CallUtil.getRealToNumber(numberOrName);
                if (toNumber == null) {
                    ToastUtils.showToast(this, getString(R.string.str_alert_no_phone_number));
                    return;
                }
                LinphoneProxyConfig proxyConfig = LinphoneManager.getLc().getDefaultProxyConfig();
                if (proxyConfig == null) {
                    ToastUtils.showToast(this, getString(R.string.error_io_error));
                    return;
                }
                LinphoneAddress lAddress = LinphoneCoreFactory.instance().createLinphoneAddress(toNumber, proxyConfig.getDomain(), "");
                sipUri = lAddress.getUserName();
            } catch (Exception ex) {
                ToastUtils.showToast(this, getString(R.string.str_alert_no_phone_number));
                return;
            }

            RDContact contact = RDContactHelper.findContactByPhone(sipUri);
            String displayName = contact != null ? contact.getDisplayName() : null;
            mContactListView.setVisibility(View.GONE);
            changeDisplayedChat(sipUri, displayName, null);
        }
    }

    private void showSelectContacts() {
        Bundle extras = new Bundle();
        extras.putSerializable("SelectContacts", mContacts);
        toActivityForResult(SelectContactsActivity.class, CHAT_SELECTCONTACTS_ACTIVITY, extras);
    }

    private PopupWindow mPopupWindow;

    public void showPopWindows(View v, ChatDBModel chatDBModel) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_message_options, null);
        mPopupWindow = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        int popupWidth = view.getMeasuredWidth();
        int popupHeight = view.getMeasuredHeight();
        int[] location = new int[2];
        // 允许点击外部消失
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        view.findViewById(R.id.tv_copy).setOnClickListener(new ChatMsgOptions(v, chatDBModel));
        view.findViewById(R.id.tv_forward).setOnClickListener(new ChatMsgOptions(v, chatDBModel));
        view.findViewById(R.id.tv_delete).setOnClickListener(new ChatMsgOptions(v, chatDBModel));
        view.findViewById(R.id.tv_more).setOnClickListener(new ChatMsgOptions(v, chatDBModel));
        // 获得位置
        v.getLocationOnScreen(location);
        Log.i("showPopWindows " + location[0] + " " + v.getWidth() + " " + popupWidth + " " + location[1] + " " + popupHeight);
        mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
    }

    private void showMore(ChatDBModel chatDBModel) {
        Bundle extras = new Bundle();
        extras.putString("SipUri", sipUri);
        extras.putString("DisplayName", displayName);
        extras.putString("PictureUri", pictureUri);
        extras.putInt("MsgId", (int) chatDBModel.logId);
        toActivityForResult(ChatMoreActivity.class, CHAT_MORE_ACTIVITY, extras);
    }

    class ChatMsgOptions implements OnClickListener {
        private View msgView;
        private ChatDBModel chatDBModel;

        ChatMsgOptions(View view, ChatDBModel chatDBModel) {
            msgView = view;
            this.chatDBModel = chatDBModel;
        }

        @Override
        public void onClick(View v) {
            mPopupWindow.dismiss();
            switch (v.getId()) {
                case R.id.tv_copy:
                    Compatibility.copyTextToClipboard(getApplicationContext(), chatDBModel.message);
                    ToastUtils.showToast(getApplicationContext(), getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT);
                    break;
                case R.id.tv_forward:
                    forward(chatDBModel.message);
                    break;
                case R.id.tv_delete:
                    deleteMsg(chatDBModel);
                    break;
                case R.id.tv_more:
                    showMore(chatDBModel);
                    break;
            }

        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if (arg1 == RESULT_OK) {
            if (arg0 == CHAT_MORE_ACTIVITY) {
                String action = arg2.getStringExtra("Action");
                int[] msgIds = arg2.getIntArrayExtra("MsgIds");
                if (action.equals("copy")) {
                    copyTextMessageToClipboard(msgIds);
                } else if (action.equals("forward")) {
                    forward(buildSelectMessages(msgIds));
//					getIntent().getExtras().putString("messageDraft", buildSelectMessages(msgIds));
                } else if (action.equals("delete")) {
                    deleteMsgList(msgIds);
                }
            } else if (arg0 == CHAT_SELECTCONTACTS_ACTIVITY) {
                mContacts = (ArrayList<RDContact>) arg2.getSerializableExtra("SelectContacts");
                flowLayout.removeAllViews();
                for (RDContact contact : mContacts) {
                    addContact(contact);
                }
                if (mContacts.size() == 1) {
                    mContact = mContacts.get(0);
                    String sipUri = mContact.getDialPhone().getNumber();
                    mEtChatTo.setText("");
                    changeDisplayedChat(sipUri, mContact.getDisplayName(), null);
                } else {
                    chatRoom = null;
                }
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mContact = (RDContact) getBundle().getSerializable(RDContact.class.getName());
        if (mContact != null) {
            addContact(mContact);
            String sipUri = mContact.getDialPhone().getNumber();
            mEtChatTo.setText("");
            changeDisplayedChat(sipUri, mContact.getDisplayName(), null);
        }
    }

    private void copyTextMessageToClipboard(int[] msgIds) {
        String msg = buildSelectMessages(msgIds);//LinphoneActivity.instance().getChatStorage().getTextMessageForId(chatRoom, id);
        if (!StringUtil.isTrimBlank(msg) && LinphoneActivity.isInstanciated()) {
            Compatibility.copyTextToClipboard(this, msg);
            ToastUtils.showToast(getApplicationContext(), getString(R.string.text_copied_to_clipboard), Toast.LENGTH_SHORT);

        }
    }

    private String buildSelectMessages(int[] msgIds) {
        StringBuilder strBuilder = new StringBuilder();
        for (int m = msgIds.length - 1; m >= 0; m--) {
            for (ChatDBModel model : historyList) {
                if (model.logId == msgIds[m]) {
                    strBuilder.append(model.message);
                    if (m > 0) {
                        strBuilder.append("\n");
                    }
                    break;
                }
            }
        }
        return strBuilder.toString();
    }

    private void forward(String msg) {
        mEditTextContent.setText(msg);
        mEditTextContent.setSelection(msg.length());
        bNewChat = true;
        sipUri = null;
        displayChatHeader(null);
        chatRoom = null;
    }

    /**
     * 删除单条消息
     *
     * @param chatDBModel model
     */
    private void deleteMsg(ChatDBModel chatDBModel) {
        ArrayList<String> list = new ArrayList<>();
        list.add(chatDBModel.callId);
        callMessageUtil.deleteMessageMultiple(getAuthJSONObject(), list);
        if (historyList != null && historyList.size() > 0) {
            historyList.remove(chatDBModel);
            mAdapter.notifyDataSetChanged();
        }
        RoamApplication.isLoadGroupMessage = false;
    }

    /**
     * 删除 多条消息
     *
     * @param msgIds
     */
    private void deleteMsgList(int[] msgIds) {
        ArrayList<String> list = new ArrayList<>();
        for (int id : msgIds) {
            for (ChatDBModel model : historyList) {
                if (model.logId == id) {
                    callMessageUtil.deleteLocalMessageById(Integer.parseInt(model.logId + ""));
                    list.add(model.callId);
                    historyList.remove(model);
                    break;
                }
            }
        }
        mAdapter.notifyDataSetChanged();
        callMessageUtil.deleteMessageMultiple(getAuthJSONObject(), list);
        RoamApplication.isLoadGroupMessage = false;
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_SEARCH_CONTACT_TEXT:
                String searchStr = msg.obj.toString();
                if (searchStr == null || searchStr.equals("")) {
                    //titleLayout.setVisibility(View.INVISIBLE);
                    mKeyboardSearchAdapter.refreash(new ArrayList<RDContact>());
                    mListView.setVisibility(View.VISIBLE);
                } else {
                    List<RDContact> filterList = RDContactHelper.searchAllPhone(searchStr);
                    mKeyboardSearchAdapter.refreash(filterList);
                    mKeyboardSearchAdapter.setSearchText(searchStr);
                    mKeyboardSearchAdapter.notifyDataSetChanged();
                    if (filterList.isEmpty()) {
                        mListView.setVisibility(View.VISIBLE);
                    } else {
                        mListView.setVisibility(View.GONE);
                        mContactListView.setVisibility(View.VISIBLE);
                    }
                }
                break;

            case MsgType.MSG_GET_MESSAGE_LIST:
                historyList = callMessageUtil.getMessageByPhoneList(false, sipUri, pageIndex, pageSize);
                if (historyList != null) {
                    Collections.sort(historyList, new Comparator<ChatDBModel>() {
                        public int compare(ChatDBModel arg0, ChatDBModel arg1) {
                            if (arg0.timestamp - arg1.timestamp > 0)
                                return 1;
                            else return -1;
                        }
                    });
                    displayMessageList();
                }
                break;
            case MsgType.MSG_DELETE_ERROR:
                ToastUtils.showToast(this, getString(R.string.str_delete_error));
                break;
        }
    }

    /**
     * adapter 回调
     */
    public ChatMessageAdapter.iCallback callback = new ChatMessageAdapter.iCallback() {
        @Override
        public void longClick(View view, ChatDBModel chatDBModel) {
            showPopWindows(view, chatDBModel);
        }

        @Override
        public void userPhotoClick(ChatDBModel message) {
            Log.e("用户信息---》" + message.fromContact + "||" + message.toContact + "||");
            Bundle extras = new Bundle();
            extras.putBoolean("ChatAddressOnly", false);
            extras.putSerializable(RDContact.class.getName(), getItemContact(message.fromContact, true));
            toActivity(ContactInfoActivity.class, extras);
        }

        @Override
        public void resendMessage(ChatDBModel model) {
            historyList.remove(model);
            model.messageStatus = 2;
            sendTextMessage(model.message);
        }
    };

    /**
     * 获取单个 RDContact
     *
     * @param phone autoAdd true 没有找到联系人 自动添加一个
     * @return
     */

    private RDContact getItemContact(String phone, boolean autoAdd) {
        RDContact contact = RDContactHelper.findContactByPhone(phone);
        if (contact == null && autoAdd) {
            contact = new RDContact();
            RDContactPhone p = new RDContactPhone();
            contact.setPhotoId(-1);
            if (!phone.equals(getLoginInfo().getPhone())) {
                contact.setDisplayName(displayName);
                p.setNumber(sipUri);
            } else {
                contact.setDisplayName("");
                p.setNumber(getLoginInfo().getPhone());
            }
            contact.setDialPhone(p);
            contact.addPhone(p);
        }
        return contact;
    }

    public MessageReceiver.iMessageCallBack callBack = new MessageReceiver.iMessageCallBack() {
        @Override
        public void newMessage(Intent intent) {
            String newMessage = intent.getStringExtra("message");
            String from = intent.getStringExtra("fromSipUri");
            from = CallUtil.getRealToNumber(from, loginPhone, "from");
            String callId = intent.getStringExtra("callId");
            if (from.equals(sipUri)) {
                addMessage(from, loginPhone, newMessage, callId, false);
            }

        }

        @Override
        public void updateGroupMessage(Intent intent) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageReceiver != null) {
            messageReceiver.unRegister(messageReceiver, null);
        }
        RoamApplication.chatNowUserPhone = null;
    }

    /**
     * 加载本地缓存 记录
     *
     * @param ascending false 降序
     * @param phone     对方手机   to
     */
    private void loadLocalCache(boolean ascending, String phone) {
        historyList = callMessageUtil.getMessageByPhoneList(ascending, phone, pageIndex, pageSize);
        if (historyList != null) {
            Collections.sort(historyList, new Comparator<ChatDBModel>() {
                public int compare(ChatDBModel arg0, ChatDBModel arg1) {
                    if (arg0.timestamp - arg1.timestamp > 0)
                        return 1;
                    else return -1;
                }
            });
        }

        if (historyList != null && historyList.size() > 0) {
            displayMessageList();
            Log.e("消息缓存数目", historyList.size() + "");
        }
    }

    /**
     * 获取服务器数据
     *
     * @param to to
     */
    private void getUserWebMessage(String to) {
        callMessageUtil.setPhone(loginUserPhone());
        callMessageUtil.getMessageListByPhone(getAuthJSONObject(), false, to);
    }

    /**
     * 通知刷新 分组界面 消息内容
     *
     * @param from    from
     * @param message message
     */
    private void noticeUpdateData(String from, String to, String message, String callId) {
        Intent intent = new Intent();
        intent.setAction(ActionValue.ACTION_UPDATE_MESSAGE);
        intent.putExtra("from", from);
        intent.putExtra("to", to);
        intent.putExtra("message", message);
        intent.putExtra("callId", callId);
        sendBroadcast(intent);
    }

}