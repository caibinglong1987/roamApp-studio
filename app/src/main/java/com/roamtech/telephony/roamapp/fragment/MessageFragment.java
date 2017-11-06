package com.roamtech.telephony.roamapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.Receiver.MessageReceiver;
import com.roamtech.telephony.roamapp.activity.ChattingActivity;
import com.roamtech.telephony.roamapp.activity.MainNewActivity;
import com.roamtech.telephony.roamapp.activity.SearchActivity;
import com.roamtech.telephony.roamapp.activity.function.CallMessage;
import com.roamtech.telephony.roamapp.adapter.SwipeMessageNewAdapter;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.BaseFragment;
import com.roamtech.telephony.roamapp.bean.SimpleMessage;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.db.model.BlacklistDBModel;
import com.roamtech.telephony.roamapp.db.model.ChatDBModel;
import com.roamtech.telephony.roamapp.event.EventBlacklist;
import com.roamtech.telephony.roamapp.event.EventNetworkConnect;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.util.CallUtil;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.LocalDisplay;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.util.UriHelper;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenu;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuCreator;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuItem;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuListView;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.FragmentsAvailable;
import org.linphone.mediastream.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MessageFragment extends BaseFragment {
    private ImageView ivUserIcon;
    private ImageView ivAddMessage;
    private TextView tvEdit;

    private SwipeMenuListView mMessageListView;

    private MainNewActivity mMainActivity;
    private TextView tvSelectAll, tvDelete;
    private LinearLayout mMessageEditLayout;
    private LinearLayout bottomLinearMenu;
    //protected FragmentTabHost mTabHost;

    private EditText search_input;
    private CallMessageUtil callMessageUtil;

    private List<ChatDBModel> chatGroupDBModels = new ArrayList<>();
    private List<SimpleMessage> simpleMessageList = new ArrayList<>();
    private SwipeMessageNewAdapter messageNewAdapter;
    private int pageIndex = 0;
    private final int pageSize = 100;
    private String loginPhone;
    private MessageReceiver messageReceiver = null;
    private MessageReceiver updateReceiver = null;
    private int sum_message_number = 0;

    private LinearLayout layout_search;

    private String userId = null;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_message;
    }

    //监听选中的短信条数的辩护
    public void onSelectSizeChange() {
        int selectCount = getSelectMessageList().size();
        //全部选中
        if (selectCount == messageNewAdapter.getCount()) {
            tvSelectAll.setText("反选");
        } else {
            tvSelectAll.setText("全选");
        }
        if (selectCount > 0) {
            tvDelete.setEnabled(true);
        } else {
            tvDelete.setEnabled(false);
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mMainActivity = (MainNewActivity) getBaseActivity();
        mMessageEditLayout = (LinearLayout) mMainActivity.findViewById(R.id.llyt_message_tool);
        bottomLinearMenu = (LinearLayout) mMainActivity.findViewById(R.id.bottomLinear);
        tvSelectAll = (TextView) mMessageEditLayout.findViewById(R.id.tv_selectall);
        tvDelete = (TextView) mMessageEditLayout.findViewById(R.id.tv_delete);
        search_input = (EditText) findViewById(R.id.search_input);
        ivUserIcon = (ImageView) findViewById(R.id.id_circle_image);
        ivAddMessage = (ImageView) findViewById(R.id.iv_addmessage);
        tvEdit = (TextView) findViewById(R.id.id_edit);
        mMessageListView = (SwipeMenuListView) findViewById(R.id.lv_message);
        layout_search = (LinearLayout) findViewById(R.id.layout_search);
        if (RoamApplication.isDebug) {
            ivAddMessage.setVisibility(View.VISIBLE);
            tvEdit.setVisibility(View.VISIBLE);
        } else {
            ivAddMessage.setVisibility(View.GONE);
            tvEdit.setVisibility(View.GONE);
        }
        //初始化一次就OK了 避免后面使用出现空指针的现象
        messageNewAdapter = new SwipeMessageNewAdapter(mMainActivity, mMessageListView, null);
        mMessageListView.setAdapter(messageNewAdapter);
        mMessageListView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(mMainActivity);
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                openItem.setWidth(LocalDisplay.dp2px(90));
                // set item title
                openItem.setTitle(getString(R.string.delete));
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        });
        mMessageListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                String phoneNumber = messageNewAdapter.getItem(position).getContact();
                removeMessageLog(phoneNumber);
                callMessageUtil.deleteLocalMessageByPhone(phoneNumber);
                ArrayList<String> list = new ArrayList<>();
                list.add(phoneNumber);
                removeItem(list);
                if (messageNewAdapter != null) {
                    messageNewAdapter.notifyDataSetChanged();
                }
            }
        });
        userId = mMainActivity.getUserId();
        callMessageUtil = new CallMessageUtil(mMainActivity.getApplicationContext(), fragmentHandler);
        messageReceiver = new MessageReceiver(mMainActivity.getApplicationContext(), callBack);
        updateReceiver = new MessageReceiver(mMainActivity.getApplicationContext(), callBack);
        messageReceiver.register(messageReceiver, updateReceiver);

    }

    /**
     * 删除 服务端 消息 记录数据
     *
     * @param list list
     */
    private void removeItem(ArrayList<String> list) {
        CallMessage callMessage = new CallMessage(mMainActivity.getApplicationContext());
        JSONObject jsonObject = mMainActivity.getAuthJSONObject();
        try {
            jsonObject.put("phones", new JSONArray(list));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        callMessage.deleteGroupMessage(jsonObject, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
                fragmentHandler.sendEmptyMessage(MsgType.MSG_DELETE_ERROR);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                UCResponse<String> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<String>>() {
                }.getType());
                if (result != null && HttpFunction.isSuccess(result.getErrorNo())) {
                    fragmentHandler.sendEmptyMessage(MsgType.MSG_DELETE_SUCCESS);
                }
            }
        });
    }

    /**
     * 消息 删除
     */
    private void removeMessageLogs() {
        ArrayList<String> list = new ArrayList<>();
        for (SimpleMessage msg : getSelectMessageList()) {
            callMessageUtil.deleteLocalMessageByPhone(msg.getContact());
            removeMessageLog(msg.getContact());
            list.add(msg.getContact());
        }
        removeItem(list);
    }

    /**
     * 刷新 适配器数据
     *
     * @param phoneNumber phone
     */
    private void removeMessageLog(String phoneNumber) {
        if (messageNewAdapter != null && messageNewAdapter.getCount() > 0) {
            for (SimpleMessage item : messageNewAdapter.getData()) {
                if (item.getContact().equals(phoneNumber)) {
                    messageNewAdapter.remove(item);
                    simpleMessageList.remove(item);
                    break;
                }
            }
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        ivUserIcon.setOnClickListener(this);
        ivAddMessage.setOnClickListener(this);
        tvEdit.setOnClickListener(this);
        mMessageListView.setOnItemClickListener(this);
        layout_search.setOnClickListener(this);
        search_input.setOnClickListener(this);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (WifiAdmin.isNetworkConnected(getActivity())) {
            if (LinphoneActivity.isInstanciated()) {
                LinphoneActivity.instance().selectMenu(FragmentsAvailable.CHATLIST);
            }
            String head_url = SPreferencesTool.getInstance().getStringValue(getActivity().getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_headUrl);
            if (head_url == null || "".equals(head_url)) {
                ivUserIcon.setImageResource(R.drawable.nav_user_default);
            } else {
                ivUserIcon.setImageURI(UriHelper.obtainUri(head_url));
            }
            if (!RoamApplication.isLoadGroupMessage) {
                RoamApplication.badgeSumNumber -= sum_message_number;
                sum_message_number = 0;
                RoamApplication.isLoadGroupMessage = true;
                loadLocalCache();
                long delayTime = 3000;
                if (RoamApplication.loginTouchPhone != null) {
                    delayTime = 0; //说明数据请求结束了
                }
                fragmentHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded()) {
                            Log.e("消息重新加载--》", "消息重新加载");
                            JSONObject jsonObject = mMainActivity.getAuthJSONObject();
                            callMessageUtil.getServiceMessageGroupList(jsonObject);
                        }
                    }
                }, delayTime);
            } else {
                setRefreshByTime();
            }
            if (sum_message_number <= 0) {
                sum_message_number = 0;
                mMainActivity.setMessageNumber(0);
            }
        } else {
            loadLocalCache();
            ToastUtils.showToast(getActivity(), getString(R.string.error_io_error));
        }
    }

    /**
     * 获取未读数目
     *
     * @return int
     */
    private int getNotReadNumber() {
        int number = 0;
        for (SimpleMessage item : simpleMessageList) {
            number += item.number;
        }
        return number;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        super.onItemClick(parent, view, position, id);
        SimpleMessage msg = (SimpleMessage) parent.getItemAtPosition(position);

        //如果为编辑状态 设置选中
        if (messageNewAdapter.isEdit()) {
            //如果为编辑状态
            msg.setSelect(!msg.isSelect());
            onSelectSizeChange();
            //每次都刷新影响效率
            ImageView handleView = (ImageView) view.findViewById(R.id.id_handle_item);
            handleView.setImageResource(msg.isSelect() ? R.drawable.ic_choosed : R.drawable.ic_unchoose);
            return;
        }
        String sipUri = msg.getContact();
        //sipUri = CallUtil.getSipTo()+";to="+CallUtil.getRealToNumber(sipUri);
        if (LinphoneActivity.isInstanciated()) {
            int cutBackNumber = simpleMessageList.get(position).number;
            sum_message_number -= cutBackNumber;
            simpleMessageList.get(position).number = 0;
            LinphoneActivity.instance().displayChat(sipUri);
            callMessageUtil.updateChatUnreadNumber(simpleMessageList.get(position).getContact(), 0, true);
            setRedDot(true);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == ivAddMessage) {
            mMainActivity.toActivity(ChattingActivity.class, new Bundle());
        }
        if (v == tvEdit) {
            //没有短息状态禁止编辑
            if (messageNewAdapter.isEmpty()) {
                mMainActivity.showToast("暂无信息");
                return;
            }
            setEditState(!messageNewAdapter.isEdit());
        } else if (v == ivUserIcon) {
            mMainActivity.getDrawerLayout().openDrawer(GravityCompat.START);
        } else if (v == tvDelete) {
            removeMessageLogs();
            setEditState(false);
            if (messageNewAdapter != null) {
                messageNewAdapter.notifyDataSetChanged();
            }
        } else if (v == tvSelectAll) {
            if (tvSelectAll.getText().equals("全选")) {
                setAllMessageSelect(true);
            } else {
                setAllMessageSelect(false);
            }
            messageNewAdapter.notifyDataSetChanged();
        } else if (layout_search == v || search_input == v) {
            mMainActivity.toActivity(SearchActivity.class, null);
        }
    }

    /**
     * 顶部按钮状态变化
     *
     * @param isEdit
     */
    private void setEditState(boolean isEdit) {

        if (isEdit) {
            /** 编辑状态 点击 全部还原不选中状态 ***/
            setAllMessageSelect(false);
        }
        if (mMessageEditLayout.getVisibility() != View.VISIBLE) {
            mMessageEditLayout.setVisibility(View.VISIBLE);
        } else {
            mMessageEditLayout.setVisibility(View.GONE);
        }
        if (isEdit) {
            /***可能在Keybord被重新设置了 每次编辑的时候再次设置 in case**/
            tvDelete.setOnClickListener(this);
            tvSelectAll.setOnClickListener(this);
        }
        translationAnimRun(mMessageEditLayout, isEdit, 200);
        translationAnimRun(bottomLinearMenu, !isEdit, 200);
        messageNewAdapter.setEdit(isEdit);
        messageNewAdapter.notifyDataSetChanged();
        tvEdit.setText(!isEdit ? R.string.edit : R.string.cancel);
    }


    /**
     * 获取选中的通短信记录
     */
    private List<SimpleMessage> getSelectMessageList() {
        List<SimpleMessage> selectMessageList = new ArrayList<>();
        for (SimpleMessage message : messageNewAdapter.getData()) {
            if (message.isSelect()) {
                selectMessageList.add(message);
            }
        }
        return selectMessageList;
    }

    /**
     * 设置message 为isSelelct
     *
     * @param isSelect
     */
    private void setAllMessageSelect(boolean isSelect) {
        for (SimpleMessage message : messageNewAdapter.getData()) {
            if (message.isSelect() != isSelect) {
                message.setSelect(isSelect);
            }
        }
        onSelectSizeChange();
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_NO_DATA:
            case MsgType.MSG_GET_GROUP_MESSAGE_LIST:
                loadLocalCache();
                break;
            case MsgType.MSG_DELETE_ERROR:
                ToastUtils.showToast(mMainActivity, getString(R.string.str_delete_error));
                break;
            case MsgType.MSG_DELETE_SUCCESS:
                callMessageUtil.getServiceMessageGroupList(mMainActivity.getAuthJSONObject());
                break;
        }
    }

    /**
     * 刷新 适配器
     */
    private void refreshAdapter() {
        if (chatGroupDBModels != null && chatGroupDBModels.size() > 0) {
            List<BlacklistDBModel> blacklistDBModels = CallMessageUtil.blackDao.queryAll();
            if (blacklistDBModels != null && blacklistDBModels.size() > 0) {
                for (BlacklistDBModel item : blacklistDBModels) {
                    for (ChatDBModel model : chatGroupDBModels) {
                        if (model.fromContact.equals(item.phone)) {
                            chatGroupDBModels.remove(model);
                            break;
                        }
                    }
                }
            }

            simpleMessageList = new ArrayList<>();
            String contact;
            String fromPhone;
            String toPhone;
            for (int index = 0; index < chatGroupDBModels.size(); index++) {
                contact = chatGroupDBModels.get(index).fromContact;
                fromPhone = chatGroupDBModels.get(index).fromContact;
                toPhone = chatGroupDBModels.get(index).toContact;
                SimpleMessage message = new SimpleMessage(false, true, contact,
                        chatGroupDBModels.get(index).message, chatGroupDBModels.get(index).timestamp, fromPhone, toPhone);
                message.searchKey = null;
                message.callId = chatGroupDBModels.get(index).callId;
                message.number = chatGroupDBModels.get(index).unreadNumber;
                simpleMessageList.add(message);
            }
            messageNewAdapter.refreash(simpleMessageList);
            setRedDot(true);
        } else {
            simpleMessageList = new ArrayList<>();
            messageNewAdapter.refreash(null);
        }
    }

    public void updateMessage(String from, String to, String message, String callId) {
        String userTouchPhone = mMainActivity.getRoamPhone();
        loginPhone = mMainActivity.loginUserPhone();

        int direction = from.equals(loginPhone) || from.equals(userTouchPhone) ? 1 : 0;
        String fromContact = direction == 0 ? from : to;
        long messageTime = System.currentTimeMillis();
        ChatDBModel addModel = new ChatDBModel();
        addModel.fromContact = from;
        addModel.toContact = to;
        addModel.direction = direction;
        addModel.callId = callId;
        addModel.message = message;
        addModel.timestamp = messageTime;
        addModel.read = 1;
        addModel.messageStatus = 2;
        addModel.deleteStatus = 0;
        addModel.loginUserId = mMainActivity.getUserId();
        callMessageUtil.addOrUpdateMessageToDB(addModel);
        ChatDBModel updateModel = new ChatDBModel();
        boolean isShow = false; //列表是否已经加载过这个 新消息的 对象
        boolean isDot = true; //是否需要加小红点提醒
        if (simpleMessageList == null) {
            simpleMessageList = new ArrayList<>();
        }

        if (simpleMessageList.size() > 0) {
            for (int index = 0; index < simpleMessageList.size(); index++) {
                if (simpleMessageList.get(index).from.equals(fromContact)) {
                    simpleMessageList.get(index).message = message;
                    simpleMessageList.get(index).time = messageTime;
                    simpleMessageList.get(index).callId = callId;
                    if (RoamApplication.chatNowUserPhone != null && RoamApplication.chatNowUserPhone.equals(simpleMessageList.get(index).from)) {
                        simpleMessageList.get(index).number = 0;
                        isDot = false;
                    } else {
                        simpleMessageList.get(index).number += 1;
                        callMessageUtil.updateChatUnreadNumber(fromContact, 1, false);
                    }
                    isShow = true;
                    break;
                }
            }
        }

        if (!isShow) {
            int number;
            String callerPhone = from;
            String calleePhone = to;
            if (from.equals(loginPhone) || from.equals(userTouchPhone)) {
                calleePhone = from;
                callerPhone = to;
            }
            if (RoamApplication.chatNowUserPhone != null && (RoamApplication.chatNowUserPhone.equals(from)
                    || RoamApplication.chatNowUserPhone.equals(to))) {
                isDot = false;
                number = 0;
            } else {
                number = 1;
            }
            SimpleMessage simpleMessage = new SimpleMessage(false, true, callerPhone, message, messageTime,
                    callerPhone, calleePhone);
            simpleMessage.number = number;
            simpleMessageList.add(simpleMessage);
            if (loginPhone.equals(from) || from.equals(userTouchPhone)) {
                updateModel.fromContact = to;
                updateModel.toContact = from;
            } else {
                updateModel.fromContact = from;
                updateModel.toContact = to;
            }
            updateModel.direction = direction;
            updateModel.message = message;
            updateModel.read = 1;
            updateModel.deleteStatus = 0;
            updateModel.timestamp = messageTime;
            updateModel.parent = 1;
            updateModel.callId = callId;
            updateModel.messageStatus = 1;
            updateModel.loginUserId = userId;
            updateModel.unreadNumber = 1;
            callMessageUtil.updateGroupMessage(updateModel);
            messageNewAdapter.insert(simpleMessage, 0);
        }
        setRefreshByTime();
        setRedDot(isDot);
    }

    /**
     * 设置 消息小红点提示
     *
     * @param isDot true 显示
     */
    private void setRedDot(boolean isDot) {
        if (isDot && isAdded()) {
            sum_message_number = getNotReadNumber();
            mMainActivity.setMessageNumber(sum_message_number);
            mMainActivity.updateBadgeNumber(SPreferencesTool.login_badge_message_number, sum_message_number);
            android.util.Log.e("收到消息", sum_message_number + "");
        }
    }

    /**
     * 时间重新排序
     */
    private void setRefreshByTime() {
        Collections.sort(simpleMessageList, new Comparator<SimpleMessage>() {
            public int compare(SimpleMessage arg0, SimpleMessage arg1) {
                if (arg1.time - arg0.time > 0)
                    return 1;
                else return -1;
            }
        });
        messageNewAdapter.refreash(simpleMessageList);
    }

    private void loadLocalCache() {
        chatGroupDBModels = callMessageUtil.getLocalMessageGroupList(false, pageIndex, pageSize);
        refreshAdapter();
    }

    public MessageReceiver.iMessageCallBack callBack = new MessageReceiver.iMessageCallBack() {
        @Override
        public void newMessage(Intent intent) {
            if (isAdded()) {
                loginPhone = mMainActivity.loginUserPhone();
                String newMessage = intent.getStringExtra("message");
                String from = intent.getStringExtra("fromSipUri");
                from = CallUtil.getRealToNumber(from, loginPhone, "from");
                String callId = intent.getStringExtra("callId");
                updateMessage(from, loginPhone, newMessage, callId);
            }
        }

        @Override
        public void updateGroupMessage(Intent intent) {
            if (isAdded()) {
                String newMessage = intent.getStringExtra("message");
                String from = intent.getStringExtra("from");
                String to = intent.getStringExtra("to");
                String callId = intent.getStringExtra("callId");
                updateMessage(from, to, newMessage, callId);
            }
        }
    };

    /**
     * 网络切换 获取最新的消息 记录
     *
     * @param networkConnect 网络变化
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshGroupMessage(EventNetworkConnect networkConnect) {
        if (isAdded()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (WifiAdmin.isNetworkConnected(mMainActivity.getApplicationContext())) {
                        Log.w("消息定时刷新数据服务");
                        callMessageUtil.getServiceMessageGroupList(mMainActivity.getAuthJSONObject());
                    }
                }
            }).start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshGroupMessage(EventBlacklist eventBlacklist) {
        loadLocalCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (messageReceiver != null) {
            messageReceiver.unRegister(messageReceiver, updateReceiver);
        }
        EventBus.getDefault().unregister(this);
    }
}