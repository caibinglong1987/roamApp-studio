package com.roamtech.telephony.roamapp.helper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.CallLogStorage;
import com.roamtech.telephony.roamapp.bean.CallDetailRecordDBModel;
import com.roamtech.telephony.roamapp.bean.CallLogData;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.helper.numberAttr.NumberHelper;
import com.roamtech.telephony.roamapp.util.RunTime;
import com.will.common.tool.time.DateTimeTool;

import org.linphone.LinphoneManager;
import org.linphone.LinphoneUtils;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.mediastream.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 通话记录的所需要绑定的数据列表
 **/
public class CallLogDataHelper {
    private static Bitmap missedCall, outgoingCall, incomingCall;
    private static Bitmap defaultHeadPhoto;

    private static void ensureBitmap(Resources res) {
        if (missedCall == null || outgoingCall == null || incomingCall == null || defaultHeadPhoto == null) {
            missedCall = BitmapFactory.decodeResource(res, R.drawable.ic_call_no_recieve);
            outgoingCall = BitmapFactory.decodeResource(res, R.drawable.ic_call_out);
            incomingCall = BitmapFactory.decodeResource(res, R.drawable.ic_call_in);
            defaultHeadPhoto = BitmapFactory.decodeResource(res, R.drawable.logo_default_userphoto);
        }
    }

    //获取所有的通话记录
    public static List<CallDetailRecordDBModel> getLinphoneCallLogs(String limit) {
        if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
            try {
                LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress(LinphoneManager.getLc().getDefaultProxyConfig().getIdentity());
                return CallLogStorage.getInstance().getCallLogs(address.getUserName(), limit);
            } catch (LinphoneCoreException e) {

            }
        }
        return new ArrayList<>();//Arrays.asList(LinphoneManager.getLc().getCallLogs()));
    }

    //获取所有的未接通话
    public static List<CallDetailRecordDBModel> getMissedCallsFromLogs(List<CallDetailRecordDBModel> linphoneCallLogs, String limit) {
        List<CallDetailRecordDBModel> missedCalls = new ArrayList<>();
        if (linphoneCallLogs == null && LinphoneManager.getLc().getDefaultProxyConfig() != null) {
            try {
                LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress(LinphoneManager.getLc().getDefaultProxyConfig().getIdentity());
                missedCalls = CallLogStorage.getInstance().getMissCallLogs(address.getUserName(), limit);
            } catch (LinphoneCoreException e) {

            }
        }
        if (!missedCalls.isEmpty()) {
            return missedCalls; //直接查数据 未接电话
        }
        if (linphoneCallLogs == null) {
            return null;
        }
        //根据所有电话 筛选
        for (CallDetailRecordDBModel log : linphoneCallLogs) {
            if (log.getStatus() == LinphoneCallLog.CallStatus.Missed.toInt()) {
                missedCalls.add(log);
            }
        }
        return missedCalls;
    }

    public static List<CallLogData> getCallLogData(Context context, List<CallDetailRecordDBModel> calllogs) {
        if (calllogs == null) {
            return null;
        }
        List<CallLogData> logDataList = new ArrayList<>();
        for (CallDetailRecordDBModel linLog : calllogs) {
            CallLogData data = getCallLogDataNew(context, linLog);

            logDataList.add(data);
        }
        return logDataList;
    }

    //耗时操作
    public static CallLogData getCallLogDataNew(Context context, CallDetailRecordDBModel log) {
        Resources res = context.getResources();
        ensureBitmap(res);
        CallLogData callLogData = new CallLogData();
        RunTime.start();
        LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress(log.showNumber, context.getString(R.string.default_domain), "");
        if (log.isIncoming() == 1) {
            //to 呼出
            callLogData.setCallStatusBitmap(outgoingCall);
        } else {
            if (log.getCallStatus() == LinphoneCallLog.CallStatus.Missed.toInt()) {
                callLogData.setCallStatusBitmap(missedCall);
            } else {
                callLogData.setCallStatusBitmap(incomingCall);
            }
        }
        callLogData.setDirection(log.isIncoming());
        callLogData.setToNumber(log.showNumber);
        RDContact contact = RDContactHelper.findContactByPhone(log.showNumber);
        final String sipUri = address.asStringUriOnly();
        String displayName = null;
        if (log.showNumber.equals(context.getString(R.string.str_unknown)) || log.showNumber.equals("-1")) {
            callLogData.setHasUser(false);
        } else {
            callLogData.setHasUser(true);
        }
        if (contact != null) {
            displayName = contact.getDisplayName();
            if (contact.getHeadPhoto() != null) {
                callLogData.setHeadPhotoBitmap(contact.getHeadPhoto());
                callLogData.setHasHeadPhoto(true);
            }
        }
        if (contact == null || contact.getHeadPhoto() == null) {
            callLogData.setHeadPhotoBitmap(defaultHeadPhoto);
            callLogData.setHasHeadPhoto(false);
        }
        if (displayName == null) {
            if (res.getBoolean(R.bool.only_display_username_if_unknown) && LinphoneUtils.isSipAddress(sipUri)) {
                callLogData.setUser(address.getUserName());
            } else {
                callLogData.setUser(sipUri);
            }
        } else {
            if (res.getBoolean(R.bool.only_display_username_if_unknown) /*&& LinphoneUtils.isSipAddress(address.getDisplayName())*/) {
                callLogData.setUser(displayName);
            } else {
                callLogData.setUser(sipUri);
            }
        }

        callLogData.setCallTime(timestampToHumanDate(res, log.getTimestamp()));
        callLogData.setAttribution("");
        callLogData.setId(log.Id);
        callLogData.setCallId(log.getCallId());
        callLogData.setCallStatus(log.getCallStatus());
        callLogData.setFrom(log.from);
        //Log.w("通话记录数据手机号码---》" + log.showNumber);
        callLogData.setArea(NumberHelper.getAddressByNumber(context, log.showNumber));
        return callLogData;
    }

    public static String timestampToHumanDate(Resources res, long time) {
        Calendar logTime = Calendar.getInstance();
        logTime.setTimeInMillis(time);
        SimpleDateFormat dateFormat;
        if (DateTimeTool.isToday(logTime)) {
            dateFormat = new SimpleDateFormat(res.getString(R.string.today_date_format));
        } else if (DateTimeTool.isYesterday(logTime)) {
            return res.getString(R.string.yesterday);
        } else {
            dateFormat = new SimpleDateFormat(res.getString(R.string.history_date_format));
        }

        return dateFormat.format(logTime.getTime());
    }

    public static String timestampToHumanDate(Context context, long time) {
        SimpleDateFormat dateFormat;
        Calendar logTime = Calendar.getInstance();
        logTime.setTimeInMillis(time);
        if (DateTimeTool.isToday(logTime)) {
            dateFormat = new SimpleDateFormat(context.getString(R.string.time_today_date_format));
        } else if (DateTimeTool.isYesterday(logTime)) {
            dateFormat = new SimpleDateFormat(context.getString(R.string.messages_yesterday_format));
        } else {
            dateFormat = new SimpleDateFormat(context.getString(R.string.messages_date_format));
        }

        return dateFormat.format(logTime.getTime());
    }

}