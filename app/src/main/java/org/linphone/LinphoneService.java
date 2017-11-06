/*
LinphoneService.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.linphone;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.MediaStore;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.application.AppConfig;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.bean.TouchRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.bean.UserRDO;
import com.roamtech.telephony.roamapp.db.model.BlacklistDBModel;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;
import com.roamtech.telephony.roamapp.event.EventNetworkConnect;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.server.SipService;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.util.CallUtil;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.IpAddressUtil;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.compatibility.Compatibility;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.GlobalState;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.mediastream.Log;
import org.linphone.mediastream.Version;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Notification.PRIORITY_DEFAULT;

/**
 * Linphone service, reacting to Incoming calls, ...<br />
 * <p>
 * Roles include:<ul>
 * <li>Initializing LinphoneManager</li>
 * <li>Starting C libLinphone through LinphoneManager</li>
 * <li>Reacting to LinphoneManager state changes</li>
 * <li>Delegating GUI state change actions to GUI listener</li>
 *
 * @author Guillaume Beraudo
 */
public final class LinphoneService extends Service {
    /* Listener needs to be implemented in the Service as it calls
     * setLatestEventInfo and startActivity() which needs a context.
     */
    public static final String START_LINPHONE_LOGS = " ==== Phone information dump ====";
    public static final int IC_LEVEL_ORANGE = 0;
    /*private static final int IC_LEVEL_GREEN=1;
    private static final int IC_LEVEL_RED=2;*/
    public static final int IC_LEVEL_OFFLINE = 3;

    private static LinphoneService instance;

    private final static int NOTICE_ID = 1;
    private final static int IN_CALL_NOTICE_ID = 2;
    private final static int MESSAGE_NOTICE_ID = 3;
    private final static int CUSTOM_NOTICE_ID = 4;
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;

    public static boolean isReady() {
        return instance != null;
    }

    /**
     * @throws RuntimeException service not instantiated
     */
    public static LinphoneService instance() {
        if (isReady()) return instance;
        Log.e("LinphoneService not instantiated yet");
        return instance = new LinphoneService();
        //throw new RuntimeException("LinphoneService not instantiated yet");
    }

    private NotificationManager mNM;

    private Notification mNotice;
    private Notification mInCallNotice;
    private Notification mMsgNotice;
    private int mMsgNoticeCount;
    private PendingIntent mNoticeContentIntent;
    private PendingIntent mKeepAlivePendingIntent;
    private String mNotificationTitle;
    private boolean mDisableRegistrationStatus;
    private LinphoneCoreListenerBase mListener;
    public static int notificationsPriority = (Version.sdkAboveOrEqual(Version.API16_JELLY_BEAN_41) ? PRIORITY_DEFAULT : 0);
    private CallMessageUtil callMessageUtil;

    @Override
    public void onCreate() {
        super.onCreate();
        // In case restart after a crash. Main in LinphoneActivity
        mNotificationTitle = getString(R.string.service_name);
        // Needed in order for the two next calls to succeed, libraries must have been loaded first
        LinphoneCoreFactory.instance().setLogCollectionPath(getFilesDir().getAbsolutePath());
        LinphoneCoreFactory.instance().enableLogCollection(!(getResources().getBoolean(R.bool.disable_every_log)));

        // Dump some debugging information to the logs
        Log.i(START_LINPHONE_LOGS);
        dumpDeviceInformation();
        dumpInstalledLinphoneInformation();

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNM.cancel(IN_CALL_NOTICE_ID); // in case of crash the icon is not removed
        instance = this; // instance is ready once linphone manager has been created
        LinphoneManager.createAndStart(this);
        disableNotificationsAutomaticRegistrationStatusContent();
        LinphoneManager.getInstance().addLinCoreListener(mListener = new LinphoneCoreListenerBase() {

            @Override
            public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
                if (instance == null) {
                    Log.i("Service not ready, discarding call state change to ", state.toString());
                    return;
                }

                if (state == LinphoneCall.State.IncomingReceived) {
                    String caller = getCallerNumber(call);
                    String userId = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                            SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, "0");
                    BlacklistDBModel queryModel = CallMessageUtil.blackDao.queryBlacklistByPhone(caller, userId);
                    if (queryModel != null) {
                        LinphoneManager.getLinCore(getApplicationContext()).terminateCall(call);
                        return;
                    }
                    onIncomingReceived();
                }

                if (state == State.CallUpdatedByRemote) {
                }

                if (state == State.StreamsRunning) {
                    // Workaround bug current call seems to be updated after state changed to streams running
                    if (getResources().getBoolean(R.bool.enable_call_notification))
                        refreshInCallIcon(call);
                } else {
                    if (getResources().getBoolean(R.bool.enable_call_notification))
                        refreshInCallIcon(LinphoneManager.getLc().getCurrentCall());
                }
            }

            @Override
            public void globalState(LinphoneCore lc, LinphoneCore.GlobalState state, String message) {
                if (state == GlobalState.GlobalOn) {
                    Log.i(mNotificationTitle + getString(R.string.notification_started));//sendNotification(IC_LEVEL_OFFLINE, R.string.notification_started);
                }
            }

            @Override
            public void registrationState(LinphoneCore lc, LinphoneProxyConfig cfg, LinphoneCore.RegistrationState state, String smessage) {
                if (!mDisableRegistrationStatus) {
                    if (state == RegistrationState.RegistrationOk && LinphoneManager.getLc().getDefaultProxyConfig() != null && LinphoneManager.getLc().getDefaultProxyConfig().isRegistered()) {
                        sendNotification(IC_LEVEL_ORANGE, R.string.notification_registered);
                    }

                    if ((state == RegistrationState.RegistrationFailed || state == RegistrationState.RegistrationCleared) && (LinphoneManager.getLc().getDefaultProxyConfig() == null || !LinphoneManager.getLc().getDefaultProxyConfig().isRegistered())) {
                        sendNotification(IC_LEVEL_OFFLINE, R.string.notification_register_failure);
                    }

                    if (state == RegistrationState.RegistrationNone) {
                        sendNotification(IC_LEVEL_OFFLINE, R.string.notification_started);
                    }
                }
            }

            @Override
            public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {
                if (message.getFrom().getUserName() != null && message.getFrom().getUserName().equals("ucmsg")) {
                    Log.w("异地登录通知LinphoneManager");
                    return;
                }
                String textMessage = message.getText();
                if (textMessage != null && !textMessage.contains("result")) {
                    String caller = getCallerNumber(message);
                    String userId = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                            SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, "0");
                    BlacklistDBModel queryModel = CallMessageUtil.blackDao.queryBlacklistByPhone(caller, userId);
                    boolean sendBoard = true;
                    if (queryModel != null) {
                        sendBoard = false;
                    } else {
                        try {
                            String number = CallUtil.getRealToNumber(message.getFrom().asStringUriOnly());
                            RDContact contact = RDContactHelper.findContactByPhone(number);
                            if (contact != null) {
                                displayMessageNotification(number, contact.getDisplayName(), textMessage);
                            } else {
                                displayMessageNotification(number, number, textMessage);
                            }
                        } catch (Exception e) {
                            Log.e(e);
                        }
                    }
                    callMessageUtil.addMessageHistory(message, sendBoard);
                }
                super.messageReceived(lc, cr, message);
            }
        });

        // Retrieve methods to publish notification and keep Android
        // from killing us and keep the audio quality high.
        if (Version.sdkStrictlyBelow(Version.API05_ECLAIR_20)) {
            try {
                mSetForeground = getClass().getMethod("setForeground", mSetFgSign);
            } catch (NoSuchMethodException e) {
                Log.e(e, "Couldn't find foreground method");
            }
        } else {
            try {
                mStopForeground = getClass().getMethod("stopForeground", mStopFgSign);
            } catch (NoSuchMethodException e) {
                Log.e(e, "Couldn't find startForeground or stopForeground");
            }
        }
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mObserver);
        keepAlive();
        callMessageUtil = new CallMessageUtil(getApplicationContext());
    }

    private ContentObserver mObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }
    };

    private enum InCallIconState {IN_CALL, PAUSE, VIDEO, IDLE}

    private InCallIconState mCurrentInCallIconState = InCallIconState.IDLE;

    private synchronized void setInCallIcon(InCallIconState state) {
        if (state == mCurrentInCallIconState) return;
        mCurrentInCallIconState = state;

        int notificationTextId = 0;
        int inIconId = 0;

        switch (state) {
            case IDLE:
                mNM.cancel(IN_CALL_NOTICE_ID);
                return;
            case IN_CALL:
                inIconId = R.drawable.conf_unhook;
                notificationTextId = R.string.incall_notif_active;
                break;
            case PAUSE:
                inIconId = R.drawable.conf_status_paused;
                notificationTextId = R.string.incall_notif_paused;
                break;
            case VIDEO:
                inIconId = R.drawable.conf_video;
                notificationTextId = R.string.incall_notif_video;
                break;
            default:
                throw new IllegalArgumentException("Unknown state " + state);
        }

        if (LinphoneManager.getLc().getCallsNb() == 0) {
            return;
        }

        LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
        String userName = call.getRemoteAddress().getUserName();
        String domain = call.getRemoteAddress().getDomain();
        String displayName = call.getRemoteAddress().getDisplayName();
        LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress(userName, domain, null);
        address.setDisplayName(displayName);

        Contact contact = ContactsManager.getInstance().findContactWithAddress(getContentResolver(), address);
        Uri pictureUri = contact != null ? contact.getPhotoUri() : null;
        Bitmap bm;
        try {
            bm = MediaStore.Images.Media.getBitmap(getContentResolver(), pictureUri);
        } catch (Exception e) {
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        }
        String name = address.getDisplayName() == null ? address.getUserName() : address.getDisplayName();
        mInCallNotice = Compatibility.createInCallNotification(getApplicationContext(), mNotificationTitle, getString(notificationTextId), inIconId, bm, name, mNoticeContentIntent);

        notifyWrapper(IN_CALL_NOTICE_ID, mInCallNotice);
    }

    public void refreshInCallIcon(LinphoneCall currentCall) {
        LinphoneCore lc = LinphoneManager.getLc();
        if (currentCall != null) {
            if (currentCall.getCurrentParamsCopy().getVideoEnabled() && currentCall.cameraEnabled()) {
                // checking first current params is mandatory
                setInCallIcon(InCallIconState.VIDEO);
            } else {
                setInCallIcon(InCallIconState.IN_CALL);
            }
        } else if (lc.getCallsNb() == 0) {
            setInCallIcon(InCallIconState.IDLE);
        } else if (lc.isInConference()) {
            setInCallIcon(InCallIconState.IN_CALL);
        } else {
            setInCallIcon(InCallIconState.PAUSE);
        }
    }

    public void displayMessageNotification(String fromSipUri, String fromName, String message) {
        if (RoamApplication.chatNowUserPhone != null && RoamApplication.chatNowUserPhone.equals(fromSipUri)) {
            return;
        }
        Intent noticeIntent = new Intent(this, LinphoneActivity.class);
        noticeIntent.putExtra("GoToChat", true);
        noticeIntent.putExtra("ChatContactSipUri", fromSipUri);
        PendingIntent noticeContentIntent = PendingIntent.getActivity(this, 0, noticeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (fromName == null) {
            fromName = fromSipUri;
        }

        if (mMsgNotice == null) {
            mMsgNoticeCount = 1;
        } else {
            mMsgNoticeCount++;
        }

        Uri pictureUri = null;
        try {
            Contact contact = ContactsManager.getInstance().findContactWithAddress(getContentResolver(), LinphoneCoreFactory.instance().createLinphoneAddress(fromSipUri));
            if (contact != null)
                pictureUri = contact.getPhotoUri();
        } catch (LinphoneCoreException e1) {
            Log.e("Cannot parse from address ", e1);
        }

        Bitmap bm;
        if (pictureUri != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getContentResolver(), pictureUri);
            } catch (Exception e) {
                bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
            }
        } else {
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        }
        mMsgNotice = Compatibility.createMessageNotification(getApplicationContext(), mMsgNoticeCount, fromName, message, bm, noticeContentIntent);
        notifyWrapper(MESSAGE_NOTICE_ID, mMsgNotice);
    }

    public void removeMessageNotification() {
        mNM.cancel(MESSAGE_NOTICE_ID);
        resetIntentLaunchedOnNotificationClick();
    }

    private static final Class<?>[] mSetFgSign = new Class[]{boolean.class};
    private static final Class<?>[] mStartFgSign = new Class[]{int.class, Notification.class};
    private static final Class<?>[] mStopFgSign = new Class[]{boolean.class};

    private Method mSetForeground;
    private Method mStopForeground;
    private Object[] mSetForegroundArgs = new Object[1];
    private Object[] mStopForegroundArgs = new Object[1];
    private Class<? extends Activity> incomingReceivedActivity = LinphoneActivity.class;

    void invokeMethod(Method method, Object[] args) {
        try {
            method.invoke(this, args);
        } catch (InvocationTargetException e) {
            // Should not happen.
            Log.w(e, "Unable to invoke method");
        } catch (IllegalAccessException e) {
            // Should not happen.
            Log.w(e, "Unable to invoke method");
        }
    }

    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
        // Fall back on the old API.
        if (mSetForeground != null) {
            mSetForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
            // continue
        }
        notifyWrapper(id, notification);
    }

    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
        // If we have the new stopForeground API, then use it.
        if (mStopForeground != null) {
            mStopForegroundArgs[0] = Boolean.TRUE;
            invokeMethod(mStopForeground, mStopForegroundArgs);
            return;
        }

        // Fall back on the old API.  Note to cancel BEFORE changing the
        // foreground state, since we could be killed at that point.
        mNM.cancel(id);
        if (mSetForeground != null) {
            mSetForegroundArgs[0] = Boolean.FALSE;
            invokeMethod(mSetForeground, mSetForegroundArgs);
        }
    }

    @SuppressWarnings("deprecation")
    private void dumpDeviceInformation() {
        StringBuilder sb = new StringBuilder();
        sb.append("DEVICE=").append(Build.DEVICE).append("\n");
        sb.append("MODEL=").append(Build.MODEL).append("\n");
        //MANUFACTURER doesn't exist in android 1.5.
        //sb.append("MANUFACTURER=").append(Build.MANUFACTURER).append("\n");
        sb.append("SDK=").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("EABI=").append(Version.getCpuAbis().get(0)).append("\n");
        Log.i(sb.toString());
    }

    private void dumpInstalledLinphoneInformation() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException nnfe) {
        }

        if (info != null) {
            Log.i("Linphone version is ", info.versionName + " (" + info.versionCode + ")");
        } else {
            Log.i("Linphone version is unknown");
        }
    }

    public void disableNotificationsAutomaticRegistrationStatusContent() {
        mDisableRegistrationStatus = true;
    }

    public synchronized void sendNotification(int level, int textId) {
        String text = getString(textId);
        if (text.contains("%s") && LinphoneManager.getLc() != null) {
            // Test for null lc is to avoid a NPE when Android mess up badly with the String resources.
            LinphoneProxyConfig lpc = LinphoneManager.getLc().getDefaultProxyConfig();
            String id = lpc != null ? lpc.getIdentity() : "";
            text = String.format(text, id);
        }

        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        } catch (Exception e) {
        }
        mNotice = Compatibility.createNotification(this, mNotificationTitle, text, R.drawable.status_level, level, bm, mNoticeContentIntent, true, notificationsPriority);
        notifyWrapper(NOTICE_ID, mNotice);
    }

    public void resetMessageNotificationCount() {
        mMsgNoticeCount = 0;
        removeMessageNotification();
    }

    /**
     * Wrap notifier to avoid setting the linphone icons while the service
     * is stopping. When the (rare) bug is triggered, the linphone icon is
     * present despite the service is not running. To trigger it one could
     * stop linphone as soon as it is started. Transport configured with TLS.
     */
    private synchronized void notifyWrapper(int id, Notification notification) {
        if (instance != null && notification != null) {
            mNM.notify(id, notification);
        } else {
            Log.i("Service not ready, discarding notification");
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (getResources().getBoolean(R.bool.kill_service_with_task_manager)) {
            Log.d("Task removed, stop service");
            if (LinphoneManager.isInstanciated()) {
                LinphoneManager.getLc().setNetworkReachable(false);
            }
            stopSelf();
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public synchronized void onDestroy() {
        instance = null;
        LinphoneManager.getInstance().removeLinCoreListener(mListener);
        mNM.cancel(IN_CALL_NOTICE_ID);
        mNM.cancel(MESSAGE_NOTICE_ID);
        ((AlarmManager) this.getSystemService(Context.ALARM_SERVICE)).cancel(mKeepAlivePendingIntent);
        getContentResolver().unregisterContentObserver(mObserver);
        super.onDestroy();
    }

    public void setActivityToLaunchOnIncomingReceived(Class<? extends Activity> activity) {
        incomingReceivedActivity = activity;
        //resetIntentLaunchedOnNotificationClick();
    }

    private void resetIntentLaunchedOnNotificationClick() {
        Intent noticeIntent = new Intent(this, incomingReceivedActivity);
        mNoticeContentIntent = PendingIntent.getActivity(this, 0, noticeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (mNotice != null) {
            mNotice.contentIntent = mNoticeContentIntent;
        }
        notifyWrapper(NOTICE_ID, mNotice);
    }

    protected void onIncomingReceived() {
        //wakeup linphone
        startActivity(new Intent()
                .setClass(this, incomingReceivedActivity)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        threadTask();
        if (WifiAdmin.isNetworkConnected(getApplicationContext())) {
            autoLogin();
        }
        LinphoneManager.getLinCore(this).setNetworkReachable(true);
        return START_STICKY;
    }

    private void restartForeground() {
        Intent noticeIntent = new Intent(this, incomingReceivedActivity);
        noticeIntent.putExtra("Notification", true);
        mNoticeContentIntent = PendingIntent.getActivity(this, 0, noticeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        } catch (Exception e) {
        }
        mNotice = Compatibility.createNotification(this, mNotificationTitle, "", R.drawable.status_level, IC_LEVEL_OFFLINE, bm, mNoticeContentIntent, true, notificationsPriority);
        startForegroundCompat(NOTICE_ID, mNotice);
    }

    private void autoLogin() {
        String userId = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, "0");
        String sessionId = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_sessionId, "0");
        String phone = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_phone, "0");
        final String domain = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_domain, getString(R.string.default_domain));
        if (!userId.equals("0") && !sessionId.equals("0")) {
            linphoneLogin(userId, sessionId, phone, domain);
            requestMyTouchDBModels(userId, sessionId, domain);
            RoamApplication.isLogined = true;
        } else {
            String username = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                    SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userName, "0");
            String password = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                    SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_password, "0");
            if (!username.equals("0") && !password.equals("0")) {
                JSONObject json = new JSONObject();
                try {
                    json.put("username", username);
                    json.put("password", password);
                    new HttpFunction(this).postJsonRequest(Constant.USER_LOGIN, json, hashCode(), new HttpBusinessCallback() {
                        @Override
                        public void onSuccess(String response) {
                            super.onSuccess(response);
                            UCResponse<UserRDO> ucResponse = JsonUtil.fromJson(response, new TypeToken<UCResponse<UserRDO>>() {
                            });
                            if (ucResponse != null && ucResponse.getErrorNo() == 0) {
                                String userId = ucResponse.getUserId().toString();
                                String sessionId = ucResponse.getSessionId();
                                SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, userId);
                                SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_sessionId, sessionId);
                                linphoneLogin(userId, sessionId, ucResponse.getAttributes().getPhone(), domain);
                                requestMyTouchDBModels(userId, sessionId, domain);
                                RoamApplication.isLogined = true;
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void requestMyTouchDBModels(final String userId, final String sessionId, final String domain) {
        JSONObject json = new JSONObject();
        try {
            json.put("userid", userId);
            json.put("sessionid", sessionId);
            new HttpFunction(this).postJsonRequest(Constant.GET_ROAM_BOX_LIST, json, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onSuccess(String response) {
                    super.onSuccess(response);
                    UCResponse<TouchRDO> ucResponse = JsonUtil.fromJson(response, new TypeToken<UCResponse<TouchRDO>>() {
                    });
                    if (ucResponse != null && ucResponse.getErrorNo() == 0) {
                        List<TouchDBModel> touchs = ucResponse.getAttributes().getTouchs();
                        for (TouchDBModel touch : touchs) {
                            if (touch.getPhone() != null) {
                                linphoneLogin(userId, sessionId, touch.getPhone(), domain);
                            }
                        }
                        RoamApplication.RoamBoxList = touchs;
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void linphoneLogin(String username, String password, String phone, String domain) {
        saveCreatedAccount(username, password, phone, domain);
    }

    private String getCallerNumber(LinphoneCall call) {
        LinphoneCallLog callLog = call.getCallLog();
        String callee = callLog.getTo().asString();
        String caller = callLog.getFrom().asString();
        Log.w("通话记录callee--->" + callee + "||caller--->" + caller);
        caller = CallUtil.getRealToNumber(caller, caller, "from");
        callee = CallUtil.getRealToNumber(callee);
        Log.w("通话记录2callee--->" + callee + "||caller--->" + caller);
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
        Log.w("通话记录3callee--->" + callee + "||caller--->" + caller);

        return caller;
    }

    private String getCallerNumber(LinphoneChatMessage message) {
        String callee = message.getTo().asString();
        String caller = message.getFrom().asString();
        Log.w("消息记录callee--->" + callee + "||caller--->" + caller);
        caller = CallUtil.getRealToNumber(caller, callee, "from");
        callee = CallUtil.getRealToNumber(callee);
        Log.w("消息记录callee--->" + callee + "||caller--->" + caller);
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
        Log.w("消息记录callee--->" + callee + "||caller--->" + caller);
        return caller;
    }

    private void saveCreatedAccount(final String username, final String password, final String phone, final String domain) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String newUserName = username;
                String newPassword = password;
                String ipAddress = IpAddressUtil.getHostAddress(domain);
                String identity = "sip:" + phone + "@" + ipAddress;
                LinphoneAddress address;
                try {
                    address = LinphoneCoreFactory.instance().createLinphoneAddress(identity);
                } catch (LinphoneCoreException e) {
                    return;
                }

                if (!LinphoneManager.isInstanciated()) return;
                LinphoneCore core = LinphoneManager.getLinCore(LinphoneService.instance());
                LinphoneProxyConfig[] proxyCfgList = core.getProxyConfigList();
                LinphoneAuthInfo[] authInfos = core.getAuthInfosList();
                if (proxyCfgList != null) {
                    for (int i = 0; i < proxyCfgList.length; i++) {
                        if (proxyCfgList[i].getIdentity().equals(address.asString())) {
                            core.removeProxyConfig(proxyCfgList[i]);
                            if (i < authInfos.length && (newUserName == null || authInfos[i].getUsername().equals(newUserName))) {
                                newUserName = authInfos[i].getUsername();
                                newPassword = authInfos[i].getPassword();
                                core.removeAuthInfo(authInfos[i]);
                            }
                        }
                    }
                }

                LinphonePreferences.AccountBuilder builder = new LinphonePreferences.AccountBuilder(LinphoneManager.getLc())
                        .setUsername(newUserName)
                        .setDomain(ipAddress)
                        .setPassword(newPassword)
                        .setExtention(phone);

                builder.setProxy(ipAddress + ":5060")
                        .setTransport(LinphoneAddress.TransportType.LinphoneTransportUdp);

                builder.setExpires("3600")
                        .setOutboundProxyEnabled(true)
                        .setAvpfEnabled(true)
                        .setAvpfRRInterval(3)
        /*.setQualityReportingCollector("sip:voip-metrics@sip.roam-tech.com")
        .setQualityReportingEnabled(true)
		.setQualityReportingInterval(180)*/
                        .setRealm("sip.roam-tech.com");

                LinphonePreferences mPrefs = LinphonePreferences.instance();
                mPrefs.setStunServer("208.109.222.137"/*"stun.linphone.org"*/);
                mPrefs.setIceEnabled(true);

                try {
                    builder.saveNewAccount();
//			accountCreated = true;
                } catch (LinphoneCoreException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 刷新 注册 sip
     */
    private void keepAlive() {
        Intent intent = new Intent(this, KeepAliveHandler.class);
        mKeepAlivePendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = ((AlarmManager) this.getSystemService(Context.ALARM_SERVICE));
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP
                , SystemClock.elapsedRealtime() + 60 * 1000
                , 600 * 1000
                , mKeepAlivePendingIntent);
    }


    private void threadTask() {
        stopTimer();
        startTimer();
    }

    private synchronized void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if (!LinphoneManager.isServiceWorked(LinphoneService.this.getApplicationContext(), AppConfig.SERVICE_SIP)) {
                        startService(new Intent(LinphoneService.this, SipService.class));
                    }
                }
            };
        }

        if (mTimer != null) {
            mTimer.schedule(mTimerTask, 0, 60 * 1000);
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }
}

