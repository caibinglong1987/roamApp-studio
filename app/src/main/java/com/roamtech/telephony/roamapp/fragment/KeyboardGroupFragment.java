package com.roamtech.telephony.roamapp.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.Receiver.MissCallReceiver;
import com.roamtech.telephony.roamapp.activity.MainNewActivity;
import com.roamtech.telephony.roamapp.adapter.PhoneNumberAdapter;
import com.roamtech.telephony.roamapp.adapter.SwipeCallLogGroupAdapter;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.BaseFragment;
import com.roamtech.telephony.roamapp.bean.CallDetailRecordDBModel;
import com.roamtech.telephony.roamapp.bean.CallLogData;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.db.model.BlacklistDBModel;
import com.roamtech.telephony.roamapp.event.EventBlacklist;
import com.roamtech.telephony.roamapp.event.EventCallHistory;
import com.roamtech.telephony.roamapp.event.EventLoadCallHistory;
import com.roamtech.telephony.roamapp.event.EventLoadContactEnd;
import com.roamtech.telephony.roamapp.event.EventNetworkConnect;
import com.roamtech.telephony.roamapp.event.EventkeyboardTab;
import com.roamtech.telephony.roamapp.helper.AsyBlurHelper;
import com.roamtech.telephony.roamapp.helper.CallLogDataHelper;
import com.roamtech.telephony.roamapp.helper.KeyboardHelper;
import com.roamtech.telephony.roamapp.helper.KeyboardHelper.OnKeybordListener;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.util.CallUtil;
import com.roamtech.telephony.roamapp.util.LocalDisplay;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.util.UriHelper;
import com.roamtech.telephony.roamapp.view.SwipyRefreshLayout;
import com.roamtech.telephony.roamapp.view.SwipyRefreshLayoutDirection;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenu;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuCreator;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuItem;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuListView;
import com.will.common.tool.time.DateTimeTool;
import com.will.common.tool.wifi.WifiAdmin;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.ContactsManager;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneService;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneCallLog;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dialer fragment
 */
public class KeyboardGroupFragment extends BaseFragment {
    //信息联系人
    private View mNumberContainer;

    private EditText mNumberEditText;
    private ImageView mIvAddNumber;
    private ListView mDialNumberListView;
    private PhoneNumberAdapter mNumberAdapter;
    private List<RDContact> mAllDialContacts;

    //通话记录
    private View mCallLogContainer;

    private SwipeMenuListView mAllCallListView;
    private SwipeMenuListView mMissedCallListView;
    private SwipeCallLogGroupAdapter mAllCallAdapter;
    private SwipeCallLogGroupAdapter mMissedCallAdapter;

    private RadioGroup mRadioGroup;
    private TextView tvEdit;

    private TextView tvSelectAll, tvDelete;
    private LinearLayout mMessageEditLayout;
    private LinearLayout bottomLinearMenu;
    // protected FragmentTabHost mTabHost;
    /****
     * 键盘控件
     **/
    private LinearLayout mLayoutKeyboard;

    private KeyboardHelper mKeyboardHelper;
    //高斯模糊偏移量 此参数和布局有关
    private int mBlurOffset;
    private MainNewActivity mMainActivity;
    private ImageView ivUserIcon, ivNumberHead;

    private SwipyRefreshLayout pullToRefreshView, pullToRefreshViewMiss;

    private final int ALL_CALL_TYPE = 1;
    private final int MISS_CALL_TYPE = 2;
    private int callViewType = ALL_CALL_TYPE;

    private int pageIndex = 0, pageSize = 15; //全部通话记录分页
    private int missPageIndex = 0, missPageSize = 15; //未接电话 分页
    private CallMessageUtil callMessageUtil;

    private MissCallReceiver missCallReceiver = null;
    private List<BlacklistDBModel> blacklistDBModels = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.fragment_keyboard;
    }

    /**
     * 获取选中的通话记录
     */
    private List<CallLogData> getSelectMessageList() {
        List<CallLogData> selectMessageList = new ArrayList<>();
        for (CallLogData data : getCurrentCallLogAdapter().getData()) {
            if (data.isSelect()) {
                selectMessageList.add(data);
            }
        }
        return selectMessageList;
    }

    private SwipeCallLogGroupAdapter getCurrentCallLogAdapter() {
        Log.e("GetCacheAdapter-->", callViewType + "");
        return callViewType == ALL_CALL_TYPE ? mAllCallAdapter : mMissedCallAdapter;
    }

    /**
     * 设置message 为isSelelct
     *
     * @param isSelect
     */
    private void setAllMessageSelect(boolean isSelect) {
        for (CallLogData data : getCurrentCallLogAdapter().getData()) {
            if (data.isSelect() != isSelect) {
                data.setSelect(isSelect);
            }
        }
        onSelectSizeChange();
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
//        Log.e("通话记录界面--》", DateTimeTool.GetDateTimeNow("yyyy-MM-dd HH:mm:ss"));
        mMainActivity = (MainNewActivity) getBaseActivity();
        //mTabHost = mMainActivity.getFragmentTabHost();
        bottomLinearMenu = (LinearLayout) mMainActivity.findViewById(R.id.bottomLinear);
        mBlurOffset = getResources().getDimensionPixelOffset(R.dimen.title_height) + getResources().getDimensionPixelOffset(R.dimen.fitStatusPadding);
        ivUserIcon = (ImageView) findViewById(R.id.id_circle_image_tab);
        ivNumberHead = (ImageView) findViewById(R.id.id_circle_image_number);
        ivNumberHead.setOnClickListener(this);
        ivUserIcon.setOnClickListener(this);
        initCallLogView();
        initNumberView();

        missCallReceiver = new MissCallReceiver(mMainActivity.getApplicationContext(), iMissCallBack);
        missCallReceiver.register(missCallReceiver);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    private void initCallLogView() {
        mMessageEditLayout = (LinearLayout) mMainActivity.findViewById(R.id.llyt_message_tool);
        pullToRefreshView = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        pullToRefreshViewMiss = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshViewMiss);
        tvSelectAll = (TextView) mMessageEditLayout.findViewById(R.id.tv_selectall);
        tvDelete = (TextView) mMessageEditLayout.findViewById(R.id.tv_delete);
        mCallLogContainer = findViewById(R.id.rlyt_calllog_container);
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_callLog);
        tvEdit = (TextView) findViewById(R.id.id_edit);

        tvEdit.setOnClickListener(this);
        pullToRefreshView.setOnRefreshListener(onRefreshListener);
        pullToRefreshView.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        pullToRefreshViewMiss.setOnRefreshListener(onRefreshListener);
        pullToRefreshViewMiss.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_all) {
                    callViewType = ALL_CALL_TYPE;
                    mAllCallListView.setVisibility(View.VISIBLE);
                    mMissedCallListView.setVisibility(View.GONE);
                    pullToRefreshView.setVisibility(View.VISIBLE);
                    pullToRefreshViewMiss.setVisibility(View.INVISIBLE);
                    if (mMissedCallAdapter.isEdit()) {
                        mMissedCallAdapter.setEdit(false);
                        setEditText(false);
                    }
                } else {
                    callViewType = MISS_CALL_TYPE;
                    mAllCallListView.setVisibility(View.GONE);
                    mMissedCallListView.setVisibility(View.VISIBLE);
                    pullToRefreshView.setVisibility(View.INVISIBLE);
                    pullToRefreshViewMiss.setVisibility(View.VISIBLE);
                    if (mAllCallAdapter.isEdit()) {
                        mAllCallAdapter.setEdit(false);
                        setEditText(false);
                    }
                    callMessageUtil.getGroupMissAllCallList(mMainActivity.getAuthJSONObject(), false);
                }
            }
        });
        initAllCall();
        initMissedCall();
        callMessageUtil = new CallMessageUtil(mMainActivity.getApplicationContext(), fragmentHandler);
    }

    private void setEditText(boolean isEdit) {
        tvEdit.setText(!isEdit ? R.string.edit : R.string.cancel);
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
        translationAnimRun(mMessageEditLayout, isEdit, 150);
        translationAnimRun(bottomLinearMenu, !isEdit, 150);
    }

    private void initAllCall() {
        mAllCallListView = (SwipeMenuListView) findViewById(R.id.lv_allcall);
        mAllCallListView.setMenuCreator(new SwipeMenuCreator() {
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
        mAllCallListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                CallLogData data = mAllCallAdapter.getItem(position);
                ArrayList<String> list = new ArrayList<>();
                if (!StringUtil.isBlank(data.getToNumber())) {
                    list.add(data.getToNumber());
                    callMessageUtil.deleteLocalCallByPhone(data.getToNumber(), data.getFrom());
                    deleteCallData(data.getId(), data.getFrom());
                } else {
                    callMessageUtil.deleteLocalCallHistory(String.valueOf(data.getId()));
                    mAllCallAdapter.remove(data);
                    mMissedCallAdapter.remove(data);
                }
                deleteWebServer(list, data.getFrom());
            }
        });
        mAllCallAdapter = new SwipeCallLogGroupAdapter(mMainActivity, mAllCallListView, null);
        mAllCallListView.setAdapter(mAllCallAdapter);
    }

    private void deleteCallData(int id, String from) {
        if (from.equals(CallMessageUtil.DATA_TYPE_ALL) && mAllCallAdapter != null && mAllCallAdapter.getCount() > 0) {
            for (int i = 0; i < mAllCallAdapter.getCount(); i++) {
                if (mAllCallAdapter.getItem(i).getId() == id) {
                    mAllCallAdapter.remove(mAllCallAdapter.getItem(i));
                    break;
                }
            }
        }

        if (mMissedCallAdapter != null && mMissedCallAdapter.getCount() > 0) {
            for (int i = 0; i < mMissedCallAdapter.getCount(); i++) {
                if (mMissedCallAdapter.getItem(i).getId() == id) {
                    mMissedCallAdapter.remove(mMissedCallAdapter.getItem(i));
                    break;
                }
            }
        }
    }

    /**
     * 删除 服务器数据
     *
     * @param list list
     */
    private void deleteWebServer(ArrayList<String> list, String from) {
        JSONObject jsonObject = mMainActivity.getAuthJSONObject();
        try {
            jsonObject.put("call_status", from);
            jsonObject.put("phones", list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callMessageUtil.deleteCallGroup(jsonObject);
    }

    public SwipyRefreshLayout.OnRefreshListener onRefreshListener = new SwipyRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh(final SwipyRefreshLayoutDirection direction) {
            mMainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!(direction == SwipyRefreshLayoutDirection.TOP)) {
                        if (callViewType == ALL_CALL_TYPE) {
                            pageIndex++;
                            if (mAllCallAdapter == null || mAllCallAdapter.getCount() == 0) {
                                pageIndex = 0;
                            }
                            loadLocalCache(ALL_CALL_TYPE, pageIndex, pageSize);
                            pullToRefreshView.setRefreshing(false);
                        } else {
                            missPageIndex++;
                            if (mMissedCallAdapter == null || mMissedCallAdapter.getCount() == 0) {
                                missPageIndex = 0;
                            }
                            loadLocalCache(MISS_CALL_TYPE, missPageIndex, missPageSize);
                            pullToRefreshViewMiss.setRefreshing(false);
                        }
                    }

                }
            });
        }
    };

    private void initMissedCall() {
        mMissedCallListView = (SwipeMenuListView) findViewById(R.id.lv_missedcall);
        mMissedCallListView.setMenuCreator(new SwipeMenuCreator() {
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
        mMissedCallListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                CallLogData data = mMissedCallAdapter.getItem(position);
                ArrayList<String> list = new ArrayList<>();
                if (!StringUtil.isBlank(data.getToNumber())) {
                    list.add(data.getToNumber());
                    callMessageUtil.deleteLocalCallByPhone(data.getToNumber(), data.getFrom());
                    deleteCallData(data.getId(), data.getFrom());
                    loadLocalCache(ALL_CALL_TYPE, 0, pageSize);
                } else {
                    callMessageUtil.deleteLocalCallHistory(String.valueOf(data.getId()));
                    //mAllCallAdapter.remove(data);
                    mMissedCallAdapter.remove(data);
                }
                deleteWebServer(list, data.getFrom());

            }
        });
        mMissedCallAdapter = new SwipeCallLogGroupAdapter(mMainActivity, mMissedCallListView, null);
        mMissedCallListView.setAdapter(mMissedCallAdapter);
    }

    private void initNumberView() {
        mNumberContainer = findViewById(R.id.rlyt_number_container);
        mNumberEditText = (EditText) findViewById(R.id.tv_numberinput);
        mIvAddNumber = (ImageView) findViewById(R.id.ibtn_add);
        mIvAddNumber.setOnClickListener(this);
        mDialNumberListView = (ListView) findViewById(R.id.lv_flitercontacts);
        mLayoutKeyboard = (LinearLayout) findViewById(R.id.ll_keyboard);
        mKeyboardHelper = new KeyboardHelper(getBaseActivity(), mNumberEditText, mLayoutKeyboard);
        mKeyboardHelper.setOnKeybordListener(new OnKeybordListener() {
            @Override
            public void onTextChange(String inputText) {
                if (TextUtils.isEmpty(inputText)) {
                    pullToRefreshView.setVisibility(View.VISIBLE);
                    pullToRefreshViewMiss.setVisibility(View.VISIBLE);
                    mDialNumberListView.setVisibility(View.GONE);
                    mNumberAdapter.refreash(null);
                    refreshKeyboardBlur();
                } else {
                    List<RDContact> fileterList = new ArrayList<>();
                    if (!RDContactHelper.isEmptyList(mAllDialContacts)) {
                        for (RDContact contact : mAllDialContacts) {
                            if (contact.matchDialPhone(inputText)) {
                                fileterList.add(contact);
                            }
                        }
                        mDialNumberListView.setVisibility(View.VISIBLE);
                        pullToRefreshView.setVisibility(View.GONE);
                        pullToRefreshViewMiss.setVisibility(View.GONE);
                        mNumberAdapter.setSearchText(inputText);
                        mNumberAdapter.refreash(fileterList);
                        refreshKeyboardBlur();
                    }
                }
            }

            @Override
            public void onCall(final String inputText) {
                if (inputText.length() > 0) {
                    LinphoneActivity.instance().setAddressGoToDialerAndCall(inputText, null, null);//.newOutgoingCall(inputText,inputText);
                } else {
                    if (getBaseActivity().getResources().getBoolean(R.bool.call_last_log_if_adress_is_empty)) {
                        LinphoneCallLog[] logs = LinphoneManager.getLc().getCallLogs();
                        LinphoneCallLog log = null;
                        for (LinphoneCallLog l : logs) {
                            if (l.getDirection() == CallDirection.Outgoing) {
                                log = l;
                                break;
                            }
                        }
                        if (log == null) {
                            return;
                        }
                        mNumberEditText.setText(CallUtil.getRealToNumber(log.getTo().asStringUriOnly()));
                        mNumberEditText.setSelection(mNumberEditText.getText().toString().length());
                    }
                }
                //getBaseActivity().toActivity(CallingActivity.class, null);
            }
        });
        mNumberAdapter = new PhoneNumberAdapter(mMainActivity, null);
        mDialNumberListView.setAdapter(mNumberAdapter);
    }

    /**
     * called in MainNewActivity
     *
     * @param eventkeyboardTab
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setKeyboardState(EventkeyboardTab eventkeyboardTab) {
        // TODO Auto-generated method stub
        if (mLayoutKeyboard.getVisibility() != View.VISIBLE) {
            //第一次没有展示设置为展示状态
            mLayoutKeyboard.setVisibility(View.VISIBLE);
        }
        translationAnimRun(mLayoutKeyboard, eventkeyboardTab.isShow(), 150);

        if (eventkeyboardTab.isShow()) {
            refreshKeyboardBlur();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callHistory(EventCallHistory event) {
        if (event.isInsert() && isAdded()) {
            blacklistDBModels = CallMessageUtil.blackDao.queryAll();
            CallDetailRecordDBModel callDetailRecordDBModel = event.model;
            if (blacklistDBModels != null && blacklistDBModels.size() > 0) {
                for (BlacklistDBModel item : blacklistDBModels) {
                    if (item.phone.equals(callDetailRecordDBModel.showNumber)) {
                        return;
                    }
                }
            }

            org.linphone.mediastream.Log.w("新增来电记录---" + callDetailRecordDBModel.isIncoming() + "||" + callDetailRecordDBModel.getCallee() + "||" + callDetailRecordDBModel.getCaller());
            removeCallLog(callDetailRecordDBModel.showNumber);
            CallLogData callLogData = CallLogDataHelper.getCallLogDataNew(mMainActivity.getApplicationContext(), callDetailRecordDBModel);
            mAllCallAdapter.insert(callLogData, 0);
            if (callDetailRecordDBModel.isIncoming() == 0 && callDetailRecordDBModel.getStatus() == LinphoneCallLog.CallStatus.Missed.toInt()) {
                mMissedCallAdapter.insert(callLogData, 0);
                mMainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callMessageUtil.getGroupAllCallList(mMainActivity.getAuthJSONObject(), false, false);
                    }
                });

            }
        } else if (event.isDelete()) {
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void loadContactSuccess(EventLoadContactEnd eventLoadContactEnd) {
        mAllDialContacts = RDContactHelper.getAllSystemContacts();
        RoamApplication.isLoadCallHistory = false;
        RoamApplication.isLoadMissCallHistory = false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void loadCallHistorySuccess(EventLoadCallHistory eventLoadCallHistory) {
        int unreadNumber = eventLoadCallHistory.getUnreadNumber();
        org.linphone.mediastream.Log.w("通话记录未读数目--》" + unreadNumber);
        setMissCall(unreadNumber);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshGroupCall(EventNetworkConnect networkConnect) {
        if (isAdded()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (WifiAdmin.isNetworkConnected(mMainActivity.getApplicationContext())) {
                        JSONObject jsonObject = mMainActivity.getAuthJSONObject();
                        callMessageUtil.getGroupAllCallList(jsonObject, false, true);
                        //callMessageUtil.getGroupMissAllCallList(jsonObject, false);
                    }
                }
            }).start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshGroupCall(EventBlacklist eventBlacklist) {
        pageIndex = 0;
        missPageIndex = 0;
        loadLocalCache(ALL_CALL_TYPE, pageIndex, pageSize);
        //loadLocalCache(MISS_CALL_TYPE, missPageIndex, missPageSize);
    }

    @Override
    public void setListener() {
        super.setListener();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mDialNumberListView.setOnItemClickListener(this);
        mDialNumberListView.setOnScrollListener(new KeyBoardOnScrollListener());
        mMissedCallListView.setOnItemClickListener(this);
        mMissedCallListView.setOnScrollListener(new KeyBoardOnScrollListener());
        mAllCallListView.setOnScrollListener(new KeyBoardOnScrollListener());
        mAllCallListView.setOnItemClickListener(this);
        //监听展示内容
        mNumberEditText.addTextChangedListener(new TextWatcher() {
            private boolean before;
            private boolean after;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                before = s != null && s.length() != 0;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                after = s != null && s.length() != 0;
                if (!before && after) {
                    mNumberContainer.setVisibility(View.VISIBLE);
                    mCallLogContainer.setVisibility(View.INVISIBLE);
                } else if (before && !after) {
                    mNumberContainer.setVisibility(View.INVISIBLE);
                    mCallLogContainer.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    //监听选中的短信条数的辩护
    public void onSelectSizeChange() {
        int selectCount = getSelectMessageList().size();
        //全部选中
        if (selectCount == getCurrentCallLogAdapter().getCount()) {
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mDialNumberListView) {
            RDContact contact = (RDContact) mDialNumberListView.getItemAtPosition(position);
            Uri photoUri = RDContactHelper.getContactPhotoUri(contact.getId());
            LinphoneActivity.instance().setAddressGoToDialerAndCall(contact.getDialPhone().getNumber().replace(" ", ""), contact.getDisplayName(), photoUri);
            //去除拨打的号码 并隐藏输入框
            mDialNumberListView.setVisibility(View.GONE);
            pullToRefreshView.setVisibility(View.VISIBLE);
            pullToRefreshViewMiss.setVisibility(View.VISIBLE);
            mNumberEditText.setText("");
        } else {
            SwipeCallLogGroupAdapter adapter = getCurrentCallLogAdapter();
            //如果为编辑状态
            if (adapter.isEdit()) {
                CallLogData data = adapter.getItem(position);
                data.setSelect(!data.isSelect());
                ImageView handleView = (ImageView) view.findViewById(R.id.id_handle_item);
                handleView.setImageResource(data.isSelect() ? R.drawable.ic_choosed : R.drawable.ic_unchoose);
                //每次都刷新影响效率
                //mAllCallAdapter.notifyDataSetChanged();
                onSelectSizeChange();
                return;
            }

            if (LinphoneActivity.isInstanciated()) {
                CallLogData log = null;
                if (parent == mMissedCallListView) {
                    log = mMissedCallAdapter.getItem(position);
                }
                if (parent == mAllCallListView) {
                    log = mAllCallAdapter.getItem(position);
                }
                if (log != null) {
                    String toNumber = log.getToNumber();
                    RDContact contact = RDContactHelper.findContactByPhone(toNumber);
                    LinphoneActivity.instance().setAddressGoToDialerAndCall(toNumber, contact != null ? contact.getDisplayName() : null, null);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == mIvAddNumber) {
            RDContactHelper.addContact(mMainActivity, mNumberEditText.getText().toString());
        } else if (v == tvEdit) {
            SwipeCallLogGroupAdapter adapter = getCurrentCallLogAdapter();
            //没有短息状态禁止编辑
            if (adapter.isEmpty()) {
                getBaseActivity().showToast("暂无信息");
                return;
            }
            /** 编辑状态 点击 全部还原不选中状态 ***/
            adapter.setEdit(!adapter.isEdit());
            if (adapter.isEdit()) {
                setAllMessageSelect(false);
                //编辑状态键盘被隐藏
                mMainActivity.setKeyBoardHidden();
            }
            adapter.notifyDataSetChanged();
            setEditText(adapter.isEdit());

        } else if (v == tvDelete) {
            removeCallLogs();
            SwipeCallLogGroupAdapter adapter = getCurrentCallLogAdapter();
            adapter.setEdit(!adapter.isEdit());
            adapter.notifyDataSetChanged();
            setEditText(adapter.isEdit());
            mMessageEditLayout.setVisibility(View.GONE);
            mAllCallAdapter.notifyDataSetChanged();
            mMissedCallAdapter.notifyDataSetChanged();
        } else if (v == tvSelectAll) {
            if (tvSelectAll.getText().equals("全选")) {
                setAllMessageSelect(true);
            } else {
                setAllMessageSelect(false);
            }
            getCurrentCallLogAdapter().notifyDataSetChanged();
        } else if (v.getId() == R.id.id_circle_image_number || v.getId() == R.id.id_circle_image_tab) {
            ((MainNewActivity) getBaseActivity()).getDrawerLayout().openDrawer(
                    GravityCompat.START);
        }
    }

    private void removeCallLogs() {
        ArrayList<String> list = new ArrayList<>();
        String from = "all";
        for (CallLogData data : getSelectMessageList()) {
            callMessageUtil.deleteLocalCallByPhone(data.getToNumber(), data.getFrom());
            removeCallLog(data.getToNumber());
            list.add(data.getToNumber());
            from = data.getFrom();
        }
        deleteWebServer(list, from);
    }

    private void removeCallLog(String phoneNumber) {
        if (mAllCallAdapter != null && mAllCallAdapter.getCount() > 0) {
            for (CallLogData item : mAllCallAdapter.getData()) {
                if (item.getToNumber().equals(phoneNumber)) {
                    mAllCallAdapter.remove(item);
                    break;
                }
            }
        }
        if (mMissedCallAdapter != null && mMissedCallAdapter.getCount() > 0) {
            for (CallLogData item : mMissedCallAdapter.getData()) {
                if (item.getToNumber().equals(phoneNumber)) {
                    mMissedCallAdapter.remove(item);
                    break;
                }
            }
        }
    }

    public void displayTextInAddressBar(String numberOrSipAddress) {
        //shouldEmptyAddressField = false;
        mNumberEditText.setText(numberOrSipAddress);
    }

    public void newOutgoingCall(String numberOrSipAddress) {
        displayTextInAddressBar(numberOrSipAddress);
        LinphoneActivity.instance().setAddressGoToDialerAndCall(numberOrSipAddress, null, null);
    }

    public void newOutgoingCall(Intent intent) {
        if (intent != null && intent.getData() != null) {
            String scheme = intent.getData().getScheme();
            if (scheme.startsWith("imto")) {
                mNumberEditText.setText("sip:" + intent.getData().getLastPathSegment());
            } else if (scheme.startsWith("call") || scheme.startsWith("sip")) {
                mNumberEditText.setText(intent.getData().getSchemeSpecificPart());
            } else {
                Uri contactUri = intent.getData();
                String address = ContactsManager.getInstance().queryAddressOrNumber(LinphoneService.instance().getContentResolver(), contactUri);
                if (address != null) {
                    mNumberEditText.setText(address);
                } else {
                    Log.e("Unknown scheme: ", scheme);
                    mNumberEditText.setText(intent.getData().getSchemeSpecificPart());
                }
            }
            //mAddress.clearDisplayedName();
            intent.setData(null);
            LinphoneActivity.instance().setAddressGoToDialerAndCall(mNumberEditText.getText().toString(), null, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String head_url = SPreferencesTool.getInstance().getStringValue(getActivity().getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_headUrl);
        if (head_url == null || "".equals(head_url)) {
            ivUserIcon.setImageResource(R.drawable.nav_user_default);
            ivNumberHead.setImageResource(R.drawable.nav_user_default);
        } else {
            ivUserIcon.setImageURI(UriHelper.obtainUri(head_url));
            ivNumberHead.setImageURI(UriHelper.obtainUri(head_url));
        }
        long delayTime = 0;
        if (RoamApplication.bSwitchAccount) {
            missPageIndex = 0;
            pageIndex = 0;
            mMissedCallAdapter.refreash(null);
            mAllCallAdapter.refreash(null);
            delayTime = 3000;
            RoamApplication.bSwitchAccount = false;
        }

        fragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mMainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initCallHistory();
                        initMissCallHistory();
                    }
                });
            }
        }, delayTime);
    }

    class KeyBoardOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState != SCROLL_STATE_IDLE) {
                //编辑状态键盘被隐藏
                mMainActivity.setKeyBoardHidden();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    }

    //设置高斯模糊

    /**
     * 1、第一次展示时需要设置
     * 2、每次从隐藏到显示时需要展示
     * 3、搜索内容变化的时候需要显示
     */
    private void refreshKeyboardBlur() {
        final View cacheView = getCurrentCacheView();
        cacheView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                cacheView.getViewTreeObserver().removeOnPreDrawListener(this);
                AsyBlurHelper.startBlur(cacheView, mLayoutKeyboard, "#80FFFFFF", 4, 12, -mBlurOffset, new AsyBlurHelper.OnBlurListener() {
                    @Override
                    public void OnBlurEnd(Bitmap bitmap) {
                        mLayoutKeyboard.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
                    }
                });
                return true;
            }
        });
    }

    private View getCurrentCacheView() {
        if (mNumberContainer.getVisibility() == View.VISIBLE) {
            return mDialNumberListView;
        }
        Log.e("GetCacheView--->", callViewType + "");
        return callViewType == ALL_CALL_TYPE ? mAllCallListView : mMissedCallListView;
    }


    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_GET_GROUP_ALL_CALL_LIST:
                mMainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadLocalCache(ALL_CALL_TYPE, pageIndex, pageSize);
                    }
                });
                break;
            case MsgType.MSG_GET_GROUP_MISS_CALL_LIST:
                mMainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadLocalCache(MISS_CALL_TYPE, missPageIndex, missPageSize);
                    }
                });
                break;
            case MsgType.MSG_GET_LOCAL_ALL_CALL_SUCCESS:
                List<CallLogData> callLogDataList = (List<CallLogData>) msg.obj;
                if (callLogDataList != null) {
                    //Log.e("通话查询本地数据成功", "刷新适配器" + callLogDataList.size());
                    List<BlacklistDBModel> blacklistDBModels = CallMessageUtil.blackDao.queryAll();
                    if (blacklistDBModels != null && blacklistDBModels.size() > 0) {
                        for (BlacklistDBModel item : blacklistDBModels) {
                            for (CallLogData callLog : callLogDataList) {
                                if (callLog.getToNumber().equals(item.phone)) {
                                    callLogDataList.remove(callLog);
                                    break;
                                }
                            }
                        }
                    }
                    if (pageIndex == 0) {
                        mAllCallAdapter.refreash(callLogDataList);
                    } else {
                        mAllCallAdapter.loadMore(callLogDataList);
                    }
                }
                refreshKeyboardBlur();
                break;
            case MsgType.MSG_GET_LOCAL_MISS_CALL_SUCCESS:
                //Log.e("未接通话查询本地数据成功", "刷新适配器" + msg.obj != null ? msg.obj.toString() : "");
                List<CallLogData> missCallList = (List<CallLogData>) msg.obj;
                if (missCallList != null && missCallList.size() > 0) {
                    List<BlacklistDBModel> blacklistDBModels = CallMessageUtil.blackDao.queryAll();
                    if (blacklistDBModels != null && blacklistDBModels.size() > 0) {
                        for (BlacklistDBModel item : blacklistDBModels) {
                            for (CallLogData callLog : missCallList) {
                                if (callLog.getToNumber().equals(item.phone)) {
                                    missCallList.remove(callLog);
                                    break;
                                }
                            }
                        }
                    }
                    if (missPageIndex == 0) {
                        mMissedCallAdapter.refreash(missCallList);
                    } else {
                        mMissedCallAdapter.loadMore(missCallList);
                    }
                }
                refreshKeyboardBlur();
                break;
        }
    }

    private void initMissCallHistory() {
        if (!RoamApplication.isLoadMissCallHistory || mMissedCallAdapter.getCount() == 0) {
            Log.e("本地数据获取未接来电通话记录--》", DateTimeTool.GetDateTimeNow("yyyy-MM-dd HH:mm:ss"));
            loadLocalCache(MISS_CALL_TYPE, missPageIndex, missPageSize);//读取本地数据;
            RoamApplication.isLoadMissCallHistory = true;
        }
    }

    private void initCallHistory() {
        if (!RoamApplication.isLoadCallHistory || mAllCallAdapter.getCount() == 0) {
            Log.e("本地数据获取全部通话记录--》", DateTimeTool.GetDateTimeNow("yyyy-MM-dd HH:mm:ss"));
            loadLocalCache(ALL_CALL_TYPE, pageIndex, pageSize);//读取本地数据;
            RoamApplication.isLoadCallHistory = true;
        }
    }

    private void loadLocalCache(int loadType, int pageIndex, int pageSize) {
        if (isAdded()) {
            List<CallLogData> callLogDataList;
            if (loadType == ALL_CALL_TYPE) {
                callLogDataList = CallLogDataHelper.getCallLogData(mMainActivity.getApplicationContext(), callMessageUtil.getAllCallList(false, CallMessageUtil.DATA_TYPE_ALL, pageIndex, pageSize));
                fragmentHandler.obtainMessage(MsgType.MSG_GET_LOCAL_ALL_CALL_SUCCESS, callLogDataList).sendToTarget();
            }
            if (loadType == MISS_CALL_TYPE) {
                callLogDataList = CallLogDataHelper.getCallLogData(mMainActivity.getApplicationContext(), callMessageUtil.getAllCallList(false, CallMessageUtil.DATA_TYPE_MISSED, pageIndex, pageSize));
                fragmentHandler.obtainMessage(MsgType.MSG_GET_LOCAL_MISS_CALL_SUCCESS, callLogDataList).sendToTarget();
            }
        }
    }

    /**
     * 广播 接收 未接电话数目
     */
    private MissCallReceiver.iMissCallBack iMissCallBack = new MissCallReceiver.iMissCallBack() {
        @Override
        public void newCall(Intent intent) {
            if (isAdded()) {
                int unreadNumber = intent.getIntExtra("unreadNumber", 1);
                setMissCall(unreadNumber);
            }
        }
    };

    /**
     * 设置未读数目 和小红点
     *
     * @param number 数目
     */
    private void setMissCall(int number) {
        int localNum = mMainActivity.getBadgeNumber(SPreferencesTool.login_badge_miss_call_number, 0);
        localNum = localNum + number;
        mMainActivity.updateBadgeNumber(SPreferencesTool.login_badge_miss_call_number, localNum);
        mMainActivity.setMissCallNumber(localNum);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (missCallReceiver != null) {
            missCallReceiver.unRegister(missCallReceiver);
        }
        EventBus.getDefault().unregister(this);
    }
}