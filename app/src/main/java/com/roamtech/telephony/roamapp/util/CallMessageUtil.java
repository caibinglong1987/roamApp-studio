package com.roamtech.telephony.roamapp.util;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.Receiver.ActionValue;
import com.roamtech.telephony.roamapp.activity.function.Blacklist;
import com.roamtech.telephony.roamapp.activity.function.CallMessage;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.bean.BlacklistRDO;
import com.roamtech.telephony.roamapp.bean.CallDetailRecordDBModel;
import com.roamtech.telephony.roamapp.bean.CallMessageBean;
import com.roamtech.telephony.roamapp.bean.CallMessageRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.db.dao.CommonDao;
import com.roamtech.telephony.roamapp.db.model.BlacklistDBModel;
import com.roamtech.telephony.roamapp.db.model.ChatDBModel;
import com.roamtech.telephony.roamapp.event.EventCallHistory;
import com.roamtech.telephony.roamapp.event.EventLoadCallHistory;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.common.tool.PackageTool;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneChatMessage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by caibinglong
 * on 2016/12/2.
 * 通话记录和 消息记录
 */

public class CallMessageUtil extends HttpFunction {
    final static CommonDao<CallDetailRecordDBModel> allCallDao = new CommonDao<>(RoamApplication.sDatabaseHelper, CallDetailRecordDBModel.class);
    final static CommonDao<ChatDBModel> chatDao = new CommonDao<>(RoamApplication.sDatabaseHelper, ChatDBModel.class);
    public final static CommonDao<BlacklistDBModel> blackDao = new CommonDao<>(RoamApplication.sDatabaseHelper, BlacklistDBModel.class);
    private Handler handler = null;
    private Context context;
    private CallMessage callMessage;
    private String loginPhone;
    private String touchPhone = null;
    private String userId = null;
    public static final String DATA_TYPE_ALL = "all";
    public static final String DATA_TYPE_MISSED = "missed";
    private final String DATA_TYPE_ALL_MISSED = "all_missed";

    public CallMessageUtil(Context context, Handler handler) {
        super(context);
        this.context = context;
        this.handler = handler;
        callMessage = new CallMessage(context);
        verificationPhone();
    }

    public CallMessageUtil(Context context) {
        super(context);
        this.context = context;
        this.handler = null;
    }

    public void setPhone(String phone) {
        this.loginPhone = phone;
    }

    /**
     * 获取 通话记录
     *
     * @param json json
     */
    public void getGroupAllCallList(JSONObject json, boolean ascending, final boolean initialization) {
        if (json.optString("userid").equals("0")) {
            return;
        }
        long logId = getMaxLogIdByParent(ascending, 1, DATA_TYPE_ALL);
        try {
            String versionName = "RoamPhone/" + PackageTool.getVersionName(context);
            json.put("id", logId);
            json.put("versionName", versionName);
            json.put("pageSize", 60);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callMessage.getGroupAllCall(json, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
                Log.e("网络获取全部通话记录失败--》", errorMap.get("key_call").toString() + "||" + errorMap.get("key_volleyerror"));
            }

            @Override
            public void onSuccess(String response) {
                Log.e("网络获取全部通话记录--》", response);
                UCResponse<CallMessageRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<CallMessageRDO>>() {
                }.getType());
                if (result != null && result.getAttributes() != null && result.getAttributes().call_records != null) {
                    addCallDetail(result.getAttributes().call_records, 1, DATA_TYPE_ALL, null);
                    sendCallUnreadNumber(result.getAttributes().unread_number, initialization);
                    if (handler != null) {
                        handler.obtainMessage(MsgType.MSG_GET_GROUP_ALL_CALL_LIST, result.getAttributes().unread_number).sendToTarget();
                    }
                } else {
                    if (handler != null) {
                        handler.sendEmptyMessage(MsgType.MSG_NO_DATA);
                    }
                }
            }
        });
    }

    /**
     * 发送 未读数目
     *
     * @param unreadNumber int
     */
    private void sendCallUnreadNumber(int unreadNumber, boolean initialization) {
        if (initialization) {
            EventLoadCallHistory callHistory = new EventLoadCallHistory();
            callHistory.setUnreadNumber(unreadNumber);
            EventBus.getDefault().postSticky(callHistory);
        }
    }

    /**
     * 获取 通话记录
     *
     * @param json json
     */
    public void getGroupMissAllCallList(JSONObject json, boolean ascending) {
        if (json.optString("userid").equals("0")) {
            return;
        }
        long logId = getMaxLogIdByParent(ascending, 1, DATA_TYPE_MISSED);
        try {
            String versionName = "RoamPhone/" + PackageTool.getVersionName(context);
            json.put("id", logId);
            json.put("versionName", versionName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callMessage.getGroupMissCallHistory(json, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
                Log.e("网络获取未接通话记录失败--》", errorMap.get("key_call").toString() + "||" + errorMap.get("key_volleyerror"));
            }

            @Override
            public void onSuccess(String response) {
                Log.e("网络获取未接通话记录--》", response);
                UCResponse<CallMessageRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<CallMessageRDO>>() {
                }.getType());
                if (result != null && result.getAttributes() != null && result.getAttributes().call_records != null) {
                    addCallDetail(result.getAttributes().call_records, 1, DATA_TYPE_MISSED, null);
                    if (handler != null) {
                        handler.sendEmptyMessage(MsgType.MSG_GET_GROUP_MISS_CALL_LIST);
                    }
                } else {
                    if (handler != null) {
                        handler.sendEmptyMessage(MsgType.MSG_NO_DATA);
                    }
                }
            }
        });
    }

    /**
     * 删除 通话记录 分组
     *
     * @param jsonObject json
     */
    public void deleteCallGroup(JSONObject jsonObject) {
        callMessage.deleteGroupCall(jsonObject, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                org.linphone.mediastream.Log.w("删除分组通话记录失败-" + errorMap.get("key_call").toString() + "||" + errorMap.get("key_volleyerror"));
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                org.linphone.mediastream.Log.w("删除分组通话记录成功" + response);
                if (handler != null) {
                    UCResponse<String> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<String>>() {
                    }.getType());
                    if (result != null && HttpFunction.isSuccess(result.getErrorNo())) {

                    } else {

                    }
                }
            }
        });
    }

    public void getCallListByPhone(JSONObject json, boolean ascending, final String from) {
        final String phoneNumber = json.optString("phone");
        long logId = getAllCallLogIdByPhone(ascending, phoneNumber, 0);
        try {
            json.put("call_status", from);
            if (logId > 0) {
                json.put("is_fetch_new", true);
            }
            json.put("id", logId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callMessage.getCallListByPhone(json, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
                Log.e("网络获取全部通话记录失败--》", errorMap.get("key_call").toString() + "||" + errorMap.get("key_volleyerror"));
            }

            @Override
            public void onSuccess(String response) {
                Log.e("网络获取全部通话记录--》", response);
                UCResponse<CallMessageRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<CallMessageRDO>>() {
                }.getType());
                if (result != null && result.getAttributes() != null && result.getAttributes().call_records != null) {
                    addCallDetail(result.getAttributes().call_records, 0, from, phoneNumber);
                    if (handler != null) {
                        handler.sendEmptyMessage(MsgType.MSG_GET_ALL_CALL_BY_PHONE);
                    }
                }
            }
        });
    }

    /**
     * 获取消息 列表 group userId
     *
     * @param json json
     */
    public void getServiceMessageGroupList(JSONObject json) {
        if (json.optString("userid").equals("0")) {
            return;
        }
        callMessage.getGroupMessage(json, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {

            }

            @Override
            public void onSuccess(String response) {
                Log.e("消息列表获取--》", response);
                UCResponse<CallMessageRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<CallMessageRDO>>() {
                }.getType());
                if (result != null && result.getAttributes() != null) {
                    List<CallMessageBean> list = result.getAttributes().message_records;
                    addMessageDetail(list);
                    if (handler != null && list != null && list.size() > 0) {
                        handler.sendEmptyMessage(MsgType.MSG_GET_GROUP_MESSAGE_LIST);
                    }
                } else {
                    if (handler != null) {
                        handler.sendEmptyMessage(MsgType.MSG_NO_DATA);
                    }
                }
            }
        });
    }

    /**
     * 获取消息 单组
     *
     * @param jsonObject jsonObject
     */
    public void getMessageListByPhone(JSONObject jsonObject, boolean ascending, String phone) {
        verificationPhone();
        try {
            long logId = getMessageLogId(ascending, phone);
            jsonObject.put("id", logId);
            if (logId > 0) {
                jsonObject.put("is_fetch_new", true);
            }
            jsonObject.put("phone", phone);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callMessage.getMessageListByPhone(jsonObject, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {

            }

            @Override
            public void onSuccess(String response) {
                Log.e("消息列表获取--》", response);
                UCResponse<CallMessageRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<CallMessageRDO>>() {
                }.getType());
                if (result != null && result.getAttributes() != null) {
                    addMessageByUserId(result.getAttributes().message_records);
                    if (handler != null) {
                        handler.sendEmptyMessage(MsgType.MSG_GET_MESSAGE_LIST);
                    }
                } else {
                    if (handler != null) {
                        handler.sendEmptyMessage(MsgType.MSG_NO_DATA);
                    }
                }
            }
        });
    }

    /**
     * 模糊搜索内容
     *
     * @param searchKey searchKey
     * @return list
     */
    public List<ChatDBModel> searchMessage(String searchKey, String loginPhone) {
        List<ChatDBModel> result = chatDao.searchMessage("timestamp", false, searchKey, loginPhone, userId);
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

    /**
     * 添加 数据库 操作
     *
     * @param historyList historyList
     */
    private void addCallDetail(List<CallMessageBean> historyList, int parent, String from, String phoneNumber) {
        verificationPhone();
        if (historyList != null && !historyList.isEmpty()) {
            CallDetailRecordDBModel model;
            int state = 0;
            int status;
            int direction;
            int sipCode;
            String showNumber;
            for (CallMessageBean item : historyList) {
                if (StringUtil.isBlank(item.sipCode)) {
                    sipCode = 0;
                } else {
                    sipCode = Integer.parseInt(item.sipCode);
                }
                if (from.equals(DATA_TYPE_MISSED)) {
                    status = 2;
                    direction = 0;
                    showNumber = item.caller;
                } else {
                    if (parent == 1) {
                        direction = item.direction ? 1 : 0;
                        showNumber = item.direction ? item.callee : item.caller;
                    } else {
                        //二级内容
                        if (phoneNumber != null) {
                            if (item.caller.equals(phoneNumber)) {
                                direction = 0;
                            } else {
                                direction = 1;
                            }
                            showNumber = phoneNumber;
                        } else {
                            direction = 1; //
                            showNumber = item.direction ? item.callee : item.caller;
                        }
                    }
                    if ((sipCode >= 200 && sipCode <= 300)) {
                        status = 0;
                    } else if (sipCode == 603) {
                        status = 3;
                    } else {
                        status = 2;
                    }
                }

                model = new CallDetailRecordDBModel(item.logId, item.caller, item.callee,
                        item.time, item.duration, direction, state, 1,
                        String.valueOf(item.userId), item.callId, userId);
                model.parent = parent;
                model.showNumber = showNumber;
                model.setStatus(status);
                model.from = from;
                model.setCallStatus(status);
                if (model.getCaller() == null || model.getCaller().equals("unknown") || model.getCaller().isEmpty()) {
                    model.setCaller("-1");
                }
                addCallDBData(model);
            }
        }
    }

    /**
     * 添加 分组消息 数据库 操作
     *
     * @param historyList historyList
     */
    private void addMessageDetail(List<CallMessageBean> historyList) {
        verificationPhone();
        if (historyList != null && !historyList.isEmpty()) {
            String fromPhone;
            String toPhone;
            ChatDBModel chatModel;
            for (CallMessageBean item : historyList) {
                if (StringUtil.isBlank(item.caller) || StringUtil.isBlank(item.callee)
                        || item.callee.contains("RoamBox") || item.caller.contains("RoamBox")
                        || item.caller.equals("ucmsg")) {
                    continue;
                }
                chatModel = new ChatDBModel();
                if (item.direction) {
                    fromPhone = item.callee;
                    toPhone = item.caller;
                    chatModel.direction = 1;
                } else {
                    fromPhone = item.caller;
                    toPhone = item.callee;
                    chatModel.direction = 0;
                }
                if (StringUtil.isBlank(item.sipCode)) {
                    item.sipCode = "-1";
                }
                int intValue = Integer.parseInt(item.sipCode);
                if (intValue >= 200 && intValue <= 300) {
                    chatModel.messageStatus = 1;
                } else {
                    chatModel.messageStatus = 0;
                }
                chatModel.fromContact = fromPhone;
                chatModel.toContact = toPhone;
                chatModel.timestamp = item.time;
                chatModel.logId = item.logId;
                chatModel.deleteStatus = 0;
                chatModel.callId = item.callId;
                chatModel.message = item.message;
                chatModel.parent = 1;
                chatModel.unreadNumber = item.unread_number;
                chatModel.loginUserId = userId;
                ChatDBModel query = chatDao.queryItem(chatModel.fromContact);
                if (query == null) {
                    addMessageChatData(chatModel);
                } else {
                    chatModel.Id = query.Id;
                    chatModel.unreadNumber = chatModel.unreadNumber + query.unreadNumber;
                    chatDao.update(chatModel);
                }
            }
        }
    }

    /**
     * 更新消息 未读数目
     *
     * @param phone     对方号码
     * @param number    数目
     * @param clearData true 未读 数目清零
     */
    public void updateChatUnreadNumber(String phone, int number, boolean clearData) {
        ChatDBModel queryModel = chatDao.queryItem(phone);
        if (queryModel != null) {
            if (clearData) {
                queryModel.unreadNumber = 0;
            } else {
                queryModel.unreadNumber += number;
            }
            chatDao.update(queryModel);
        }
    }

    /**
     * 消息 添加   所有人的记录
     *
     * @param historyList historyList
     */
    private void addMessageByUserId(List<CallMessageBean> historyList) {
        if (historyList != null && !historyList.isEmpty()) {
            ChatDBModel model;
            for (CallMessageBean item : historyList) {
                model = new ChatDBModel();
                if (item.caller.equals("ucmsg")) {
                    continue;
                }
                model.fromContact = item.caller;
                model.toContact = item.callee;
                model.direction = loginPhone.equals(item.caller) || touchPhone.equals(item.caller) ? 1 : 0;
                model.timestamp = item.time;
                if (StringUtil.isBlank(item.sipCode)) {
                    item.sipCode = "-1";
                }
                int intValue = Integer.parseInt(item.sipCode);
                if (intValue >= 200 && intValue <= 300) {
                    model.messageStatus = 1;
                } else {
                    model.messageStatus = 0;
                }
                model.status = 2;
                model.image = "";
                model.logId = item.logId;
                model.callId = item.callId;
                model.userId = String.valueOf(item.userId);
                model.loginUserId = userId;
                model.messageType = 0;
                model.message = item.message;
                addMessageChatData(model);
            }
        }
    }

    /**
     * app 新增电话记录
     *
     * @param call
     */
    public void addCallHistory(LinphoneCall call) {
        verificationPhone();
        LinphoneCallLog callLog = call.getCallLog();
        String callee = callLog.getTo().asString();
        String caller = callLog.getFrom().asString();
        caller = CallUtil.getRealToNumber(caller, caller, "from");
        callee = CallUtil.getRealToNumber(callee);
        // callee = "<sip:13003663467@120.55.192.228;from=15958112371;userid=1624>";
        if (StringUtil.isBlank(caller) || caller.equals("unknown")) {
            caller = "-1";
        }
        if (callee.contains("<") || callee.contains(">") || callee.contains("sip")) {
            String[] str = CallUtil.getRealNumber(callee);
            if (str != null && str.length > 3) {
                caller = str[3];
                callee = str[0];
            }
        }
        CallDetailRecordDBModel model = new CallDetailRecordDBModel();
        model.setCallee(callee);
        model.setCaller(caller);
        model.deleteStatus = 0;
        model.setCallId(callLog.getCallId());
        model.setDuration(callLog.getCallDuration());
        model.setIncoming(loginPhone.equals(caller) || touchPhone.equals(caller) ? 1 : 0);
        model.setTimestamp(callLog.getTimestamp());
        model.setId(0);

        int callStatus;
        if (model.isIncoming() == 1) {
            model.showNumber = callee;
            callStatus = callLog.getStatus().toInt();
        } else {
            model.showNumber = caller;
            if (callLog.getStatus().toInt() == 1 || callLog.getStatus().toInt() == 2) {
                callStatus = 2;
            } else {
                callStatus = callLog.getStatus().toInt();
            }
        }

        model.setStatus(callStatus);
        model.setCallStatus(callStatus);
        model.setQuality(1);
        model.setUserId(userId);
        model.loginUserId = userId;
        addOrUpdateCallToDB(model);
        //通知 界面 增加记录
        EventCallHistory eventCallHistory = EventCallHistory.getInsertEvent(0);
        eventCallHistory.setModel(model);
        EventBus.getDefault().post(eventCallHistory);

    }

    /**
     * app 新增收到消息记录
     *
     * @param message   ChatMessage
     * @param sendBoard true 发送通知广播
     */
    public void addMessageHistory(LinphoneChatMessage message, boolean sendBoard) {
        verificationPhone();
        String callee = message.getTo().asString();
        String caller = message.getFrom().asString();
        caller = CallUtil.getRealToNumber(caller, callee, "from");
        callee = CallUtil.getRealToNumber(callee);
        if (caller.contains("<") || caller.contains(">") || caller.contains("sip")) {
            String[] str = CallUtil.getRealNumber(caller);
            if (str != null && str.length > 3) {
                if (str[0].equals(callee)) {
                    caller = str[3];
                } else {
                    caller = CallUtil.getRealToNumber(caller);
                }
            }
        }
        if (sendBoard) {
            Intent intent = new Intent();
            String callId = message.getCustomHeader("Call-ID");
            intent.putExtra("message", message.getText());
            intent.putExtra("fromSipUri", caller);
            intent.putExtra("callId", callId);
            intent.setAction(ActionValue.ACTION_NEW_MESSAGE);
            context.sendBroadcast(intent);
        }
    }

    /**
     * 获取数据库 最大 最小记录 （服务端获取的记录）
     *
     * @param ascending true 最小id false 最大id
     * @return max id
     */
    private long getMaxLogIdByParent(boolean ascending, int parent, String from) {
        CallDetailRecordDBModel model = allCallDao.queryItemByLogIdAndState(ascending, from, userId, parent);
        if (model != null) {
            return model.getId();
        }
        return 0;
    }

    private long getAllCallLogIdByPhone(boolean ascending, String phone, int parent) {
        CallDetailRecordDBModel model = allCallDao.queryItemByLogIdAndState(ascending, DATA_TYPE_ALL_MISSED, userId, parent, phone);
        if (model != null) {
            return model.getId();
        }
        return 0;
    }


    /**
     * 获取数据库 最大 最小记录 （服务端获取的记录）
     *
     * @param ascending true 最小id false 最大id
     * @return
     */
    private long getMessageLogId(boolean ascending, String phone) {
        try {
            ChatDBModel model = chatDao.queryByConditionSingle("timestamp", ascending, true, phone, userId);
            if (model != null) {
                return model.logId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取 通话记录 分页
     * 所有联系人的分组 记录
     *
     * @param ascending 升序 降序
     * @param pageIndex pageIndex
     * @param pageSize  pageSize
     * @return
     */
    public List<CallDetailRecordDBModel> getAllCallList(boolean ascending, String from, int pageIndex, int pageSize) {
        verificationPhone();
        Map<String, Object> map = new HashMap<>();
        List<CallDetailRecordDBModel> result = null;
        long startRow = pageIndex * pageSize;
        long endRow = pageSize;
        map.put("del_status", 0);
        map.put("parent", 1);
        map.put("login_user_id", userId);
        map.put("from", from);
        try {
            result = allCallDao.queryByConditionLimit("timestamp", ascending, "show_number", map, startRow, endRow);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result == null ? new ArrayList<CallDetailRecordDBModel>() : result;
    }

    /**
     * 获取 通话记录 分页
     * 某个联系人的 通话记录
     *
     * @param otherPhone 对方手机
     * @param pageIndex  pageIndex
     * @param pageSize   pageSize
     * @return list
     */
    public List<CallDetailRecordDBModel> getAllCallListByPhone(String otherPhone, int pageIndex, int pageSize) {
        verificationPhone();
        List<CallDetailRecordDBModel> result = null;
        long startRow = pageIndex * pageSize;
        long endRow = pageSize;
        try {
            result = allCallDao.getAllCallByPhone(otherPhone, userId, startRow, endRow);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result == null ? new ArrayList<CallDetailRecordDBModel>() : result;
    }

    /**
     * 获取 未接来电
     *
     * @param otherPhone phone
     * @param pageIndex  pageIndex
     * @param pageSize   pageSize
     * @return
     */
    public List<CallDetailRecordDBModel> getMissCallListByPhone(String otherPhone, int pageIndex, int pageSize) {
        verificationPhone();
        List<CallDetailRecordDBModel> missCallList = new ArrayList<>();
        long startRow = pageIndex * pageSize;
        long endRow = pageSize;
        try {
            missCallList = allCallDao.getAllMissCallByPhone(otherPhone, userId, startRow, endRow);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return missCallList;
    }


    /**
     * 获取 group message 会话列表
     *
     * @param ascending 升序 降序
     * @param pageIndex start
     * @param pageSize  end
     * @return List<ChatDBModel>
     */
    public List<ChatDBModel> getLocalMessageGroupList(boolean ascending, int pageIndex, int pageSize) {
        verificationPhone();
        Map<String, Object> map = new HashMap<>();
        List<ChatDBModel> messageGroupList = new ArrayList<>();
        long startRow = pageIndex * pageSize;
        long endRow = pageSize;
        map.put("parent", 1);
        map.put("login_user_id", userId);
        try {
            messageGroupList = chatDao.queryByConditionLimit("timestamp", ascending, map, startRow, endRow);
            if (messageGroupList != null) {
                return messageGroupList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageGroupList == null ? new ArrayList<ChatDBModel>() : messageGroupList;
    }

    /**
     * 获取 message 会话列表
     *
     * @param ascending 升序 降序
     * @param pageIndex start
     * @param pageSize  end
     * @return List<ChatDBModel>
     */
    public List<ChatDBModel> getMessageByPhoneList(boolean ascending, String toPhone, int pageIndex, int pageSize) {
        verificationPhone();
        Map<String, Object> map = new HashMap<>();
        List<ChatDBModel> messageList = new ArrayList<>();
        long startRow = pageIndex * pageSize;
        long endRow = pageSize;
        map.put("del_status", 0);
        map.put("parent", 0);
        map.put("login_user_id", userId);
        try {
            messageList = chatDao.queryAllLimit("timestamp", ascending, toPhone, userId, startRow, endRow);
            chatDao.updateUnreadNumber(toPhone, 0, userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageList == null ? new ArrayList<ChatDBModel>() : messageList;
    }

    /**
     * 添加 全部电话
     *
     * @param historyList historyList
     */
    private void addCallDBData(CallDetailRecordDBModel historyList) {
        Map<String, Object> map = new HashMap<>();
        if (historyList != null) {
            CallDetailRecordDBModel model;
            if (historyList.parent == 0) {
                historyList.from = DATA_TYPE_ALL_MISSED;
            }
            if (historyList.parent == 1) {
                model = allCallDao.queryItemByLogIdAndState
                        (false, historyList.from, userId, historyList.parent, historyList.isIncoming() == 1 ? historyList.getCallee() : historyList.getCaller());
            } else {
                map.put("callid", historyList.callId);
                map.put("login_user_id", historyList.loginUserId);
                map.put("parent", historyList.parent);
                map.put("from", historyList.from);
                model = allCallDao.queryByCallIdAndParentAndFrom(map);
            }

            if (model == null) {
                allCallDao.add(historyList);
            } else {
                historyList.deleteStatus = model.state;
                historyList.Id = model.Id;
                allCallDao.update(historyList);
            }
        }
    }

    /**
     * 添加消息列表记录
     * 所有消息 数据库
     *
     * @param history history
     */
    private void addMessageChatData(ChatDBModel history) {
        if (history != null) {
            ChatDBModel model = chatDao.queryByCallIdAndParentAndFrom(history.callId, history.parent, history.loginUserId);
            if (model == null) {
                chatDao.add(history);
            } else {
                history.Id = model.Id;
                chatDao.update(history);
            }
        }
    }

    /**
     * 更新 message　列表
     *
     * @param history 　history
     */
    public void updateGroupMessage(ChatDBModel history) {
        if (history != null) {
            ChatDBModel model = chatDao.queryByCallIdAndParentAndFrom(history.callId, history.parent, history.loginUserId);
            history.parent = 1;
            if (model == null) {
                ChatDBModel query = chatDao.queryItem(history.fromContact);
                if (query != null) {
                    query.message = history.message;
                    query.timestamp = history.timestamp;
                    query.callId = history.callId;
                    query.status = history.status;
                    query.messageStatus = history.messageStatus;
                    query.logId = history.logId;
                    query.userId = history.userId;
                    query.toContact = history.toContact;
                    query.direction = history.direction;
                    query.unreadNumber = history.unreadNumber + query.unreadNumber;
                    chatDao.update(query);
                } else {
                    chatDao.add(history);
                }
            } else {
                history.Id = model.Id;
                chatDao.update(history);
            }
        }
    }

    /**
     * 单条记录 添加通话记录记录
     *
     * @param model model
     */
    public void addOrUpdateCallToDB(CallDetailRecordDBModel model) {
        if (model == null) {
            return;
        }
        model.parent = 0;
        model.from = DATA_TYPE_ALL_MISSED;
        allCallDao.add(model); //添加二级内容

        model.parent = 1;
        model.from = DATA_TYPE_ALL;
        CallDetailRecordDBModel queryModel = allCallDao.queryItemByLogIdAndState
                (false, model.from, userId, model.parent, model.isIncoming() == 1 ? model.getCallee() : model.getCaller());

        if (queryModel != null) {
            model.Id = queryModel.Id;
            allCallDao.update(model);
        } else {
            allCallDao.add(model);
        }
        if (model.isIncoming() == 0 && model.getCallStatus() == 2) {
            model.from = DATA_TYPE_MISSED;
            model.parent = 1;
            queryModel = allCallDao.queryItemByLogIdAndState(false, model.from, userId, model.parent, model.getCallee());
            if (queryModel != null) {
                model.Id = queryModel.Id;
                allCallDao.update(model);
            } else {
                allCallDao.add(model);
            }
        }
    }

    /**
     * 单条记录 添加消息记录
     *
     * @param model model
     */
    public void addOrUpdateMessageToDB(ChatDBModel model) {
        if (model != null) {
            verificationPhone();
            ChatDBModel qModel = chatDao.queryByCallIdAndParentAndFrom(model.callId, model.parent, model.loginUserId);
            model.parent = 0;
            if (model.fromContact.equals(loginPhone) || model.fromContact.equals(touchPhone)) {
                model.direction = 1;
            } else {
                model.direction = 0;
            }
            if (qModel == null) {
                chatDao.add(model); //添加会话内容
            } else {
                chatDao.update(model); //添加会话内容
            }
        }
    }

    /**
     * @return int
     */
    public int getDbId(String callId) {
        CallDetailRecordDBModel model = allCallDao.getMaxId();
        if (model == null) {
            return 1;
        }
        if (callId != null && callId.equals(model.callId)) {
            return model.Id;
        }
        return model.Id + 1;
    }

    /**
     * 删除 全部通话记录 字段
     *
     * @param callId id
     */
    public void deleteLocalCallHistory(String callId) {
        allCallDao.deleteByCallId(callId, userId);
    }

    /**
     * 删除 消息 字段
     *
     * @param id id
     */
    public void deleteLocalMessageById(int id) {
        chatDao.deleteById(id);
    }

    /**
     * 删除 消息 字段 callId 数组
     *
     * @param list callId list
     */
    public void deleteMessageMultiple(JSONObject jsonObject, ArrayList<String> list) {
        try {
            jsonObject.put("callids", new JSONArray(list));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callMessage.updateMessageByListCallId(jsonObject, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
                if (handler != null) {
                    handler.sendEmptyMessage(MsgType.MSG_DELETE_ERROR);
                }
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                if (handler != null) {
                    UCResponse<String> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<String>>() {
                    }.getType());
                    if (result != null && HttpFunction.isSuccess(result.getErrorNo())) {
                        handler.sendEmptyMessage(MsgType.MSG_DELETE_SUCCESS);
                    } else {
                        handler.sendEmptyMessage(MsgType.MSG_DELETE_ERROR);
                    }
                }
            }
        });
    }


    /**
     * 删除 通话记录
     *
     * @param phone phone
     */
    public void deleteLocalCallByPhone(String phone, String from) {
        if (from.equals(DATA_TYPE_ALL)) {
            allCallDao.deleteCallByPhone(phone, userId);
        } else {
            allCallDao.deleteCallByPhone(phone, userId, 2);
        }
    }

    public void deleteLocalMessageByPhone(String phone) {
        verificationPhone();
        chatDao.deleteMessageByPhone(phone, userId);
    }

    private void verificationPhone() {
        loginPhone = SPreferencesTool.getInstance().getStringValue(context, SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_phone, "0");
        touchPhone = SPreferencesTool.getInstance().getStringValue(context, SPreferencesTool.LOGIN_INFO, SPreferencesTool.roamPhone, "-1");
        userId = SPreferencesTool.getInstance().getStringValue(context, SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, "0");
    }

    /**
     * 获取黑名单操作
     *
     * @param jsonObject json
     */
    public void getBlacklist(JSONObject jsonObject) {
        //黑名单数据
        new Blacklist(context).getBlacklist(jsonObject, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                //黑名单数据写入本地数据库
                if (response != null) {
                    UCResponse<BlacklistRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<BlacklistRDO>>() {
                    }.getType());
                    if (result != null && result.getAttributes() != null) {
                        List<BlacklistDBModel> list = result.getAttributes().blackList;
                        if (list != null) {
                            blackDao.deleteAll(BlacklistDBModel.class);
                            for (BlacklistDBModel model : list) {
                                blackDao.add(model);
                            }
                        }
                    }
                }
            }
        });
    }

//    public static ChatDBModel getParentChatByPhone(String phone) {
//        return chatDao.queryItem(phone);
//    }

    /**
     * 删除 数据
     */
    public static void deleteALL() {
        allCallDao.deleteAll(CallDetailRecordDBModel.class);
        chatDao.deleteAll(ChatDBModel.class);
    }
}
