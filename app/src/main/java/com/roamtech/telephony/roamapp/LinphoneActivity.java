package com.roamtech.telephony.roamapp;

/*
 LinphoneActivity.java
 Copyright (C) 2012  Belledonne Communications, Grenoble, France

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.Receiver.ActionValue;
import com.roamtech.telephony.roamapp.activity.AddNewPhoneActivity;
import com.roamtech.telephony.roamapp.activity.CallAnswerActivity;
import com.roamtech.telephony.roamapp.activity.CallingActivity;
import com.roamtech.telephony.roamapp.activity.ChattingActivity;
import com.roamtech.telephony.roamapp.activity.LMBAOIndexActivity;
import com.roamtech.telephony.roamapp.activity.LoginActivity;
import com.roamtech.telephony.roamapp.activity.MainNewActivity;
import com.roamtech.telephony.roamapp.activity.Parameter.KeyValue;
import com.roamtech.telephony.roamapp.activity.function.Domain;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.base.ServicePackageCallback;
import com.roamtech.telephony.roamapp.base.VoiceAvailableCallback;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.bean.DomainRDO;
import com.roamtech.telephony.roamapp.bean.LoginInfo;
import com.roamtech.telephony.roamapp.bean.NetworkConfigBean;
import com.roamtech.telephony.roamapp.bean.Phone;
import com.roamtech.telephony.roamapp.bean.PhoneRDO;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.bean.ServicePackage;
import com.roamtech.telephony.roamapp.bean.ServiceRDO;
import com.roamtech.telephony.roamapp.bean.TouchRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.bean.UserProfile;
import com.roamtech.telephony.roamapp.bean.VoiceNumber;
import com.roamtech.telephony.roamapp.bean.VoiceTalk;
import com.roamtech.telephony.roamapp.db.dao.CommonDao;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.enums.MainTab;
import com.roamtech.telephony.roamapp.event.EventRefresh;
import com.roamtech.telephony.roamapp.event.EventWebLoad;
import com.roamtech.telephony.roamapp.fragment.KeyboardGroupFragment;
import com.roamtech.telephony.roamapp.fragment.RDMallFragment;
import com.roamtech.telephony.roamapp.helper.ActivityCollector;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.BadgeUtil;
import com.roamtech.telephony.roamapp.util.CallUtil;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.will.common.tool.wifi.MacAddressUtils;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.ChatFragment;
import org.linphone.ChatMessage;
import org.linphone.ChatStorage;
import org.linphone.Contact;
import org.linphone.ContactFragment;
import org.linphone.ContactsManager;
import org.linphone.FragmentsAvailable;
import org.linphone.HistoryDetailFragment;
import org.linphone.InCallActivity;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneService;
import org.linphone.LinphoneUtils;
import org.linphone.StatusFragment;
import org.linphone.compatibility.Compatibility;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCallLog.CallStatus;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.Reason;
import org.linphone.mediastream.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.ACTION_MAIN;
import static com.roamtech.telephony.roamapp.enums.LoadingState.SESSION_TIME_OUT;


/**
 * @author Sylvain Berfini
 */
public class LinphoneActivity extends MainNewActivity implements OnClickListener {
    private static final int SETTINGS_ACTIVITY = 123;
    public static final int FIRST_LOGIN_ACTIVITY = 101;
    private static final int FIRST_PHONE_SETTING_ACTIVITY = 104;
    private static final int CALL_ACTIVITY = 19;
    private static final int CHAT_ACTIVITY = 21;

    private static LinphoneActivity instance;

    private StatusFragment statusFragment;
    private FragmentsAvailable currentFragment;
    private List<FragmentsAvailable> fragmentsHistory;
    private Fragment dialerFragment;
    private ChatFragment chatFragment;
    private boolean newProxyConfig;
    private boolean isAnimationDisabled = false;
    private OrientationEventListener mOrientationHelper;
    private LinphoneCoreListenerBase mListener;
    private Timer mTimer;
    private List<TouchDBModel> mTouchDBModels;
    private List<Phone> mPhones;
    private UserProfile mUser;
    private RoamDialog roamDialog;
    private List<ServicePackage> mServicePackages;
    private List<ServicePackage> mAllServicePackages;
    private VoiceTalk mVoiceTalk;
    private VoiceNumber mVoiceNumber;
    private TipDialog mKickOutDlg;
    private String toNumber;
    private boolean isVoiceNumberTalk = false;//是否专属号 拨打
    public int voiceTalk = 0;  // 1是络漫宝 2 是专属号 3是随机号
    private final int ROAM_BOX_CALL_TYPE = 1; //络漫宝拨打电话
    private final int VOICE_NUMBER_CALL_TYPE = 2;//专属号拨打电话
    private final int RANDOM_NUMBER_CALL_TYPE = 3;//随机号拨打电话
    private String loginPhone;
    private RoamBoxFunction roamBoxFunction;

    public VoiceNumber getVoiceNumber() {
        return mVoiceNumber;
    }

    public static final boolean isInstanciated() {
        return instance != null;
    }

    public static final LinphoneActivity instance() {
        if (instance != null)
            return instance;
        //Log.e("LinphoneActivity not instantiated yet");
        throw new RuntimeException("LinphoneActivity not instantiated yet");
    }

    public void dismissKickOut() {
        if (mKickOutDlg != null && mKickOutDlg.isShowing()) {
            mKickOutDlg.dismiss();
        }
    }

    public Timer getTimer() {
        return mTimer;
    }

    public List<Phone> getMyPhones() {
        return mPhones;
    }

    public List<TouchDBModel> getMyTouchs() {
        return mTouchDBModels;
    }

    public UserProfile getUserProfile() {
        return mUser;
    }

    public List<ServicePackage> getAvailTrafficPackages(List<ServicePackage> sps) {
        if (sps != null && sps.size() > 0) {
            List<ServicePackage> trafficPkgs = new ArrayList<>();
            for (ServicePackage sp : sps) {
                if (sp.getType() != null && sp.getType() == 2) {
                    trafficPkgs.add(sp);
                }
            }
            return trafficPkgs;
        }
        return null;
    }

    public List<ServicePackage> getVoiceNumberPackages(List<ServicePackage> sps) {
        if (sps != null && sps.size() > 0) {
            List<ServicePackage> trafficPkgs = new ArrayList<ServicePackage>();
            for (ServicePackage sp : sps) {
                if (sp.getType() == null || sp.getType() != 2) {
                    trafficPkgs.add(sp);
                }
            }
            return trafficPkgs;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isTablet() && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (!isTablet() && getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        if (!LinphoneManager.isInstanciated()) {
            Log.e("No service running: avoid crash by starting the launcher", this.getClass().getName());
            if (!LinphoneService.isReady()) {
                startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
            }
            LinphoneManager.createAndStart(this);
            finish();
            return;
        }
        LinphoneService.instance().setActivityToLaunchOnIncomingReceived(LinphoneActivity.class);
        mTouchDBModels = new ArrayList<>();
        mPhones = new ArrayList<>();
        mTimer = new Timer("setup timer");
        mUser = new UserProfile();
        mUser.setName(getResources().getString(R.string.defaultname));
        mUser.setGender(getResources().getString(R.string.defaultgender));
        mUser.setAddress(getResources().getString(R.string.defaultaddress));
        roamDialog = new RoamDialog(this, getString(R.string.waiting_for_startup));
        roamDialog.show();
        mKickOutDlg = null;
        roamBoxFunction = new RoamBoxFunction(getApplicationContext());
        if (LinphonePreferences.instance().getAccountCount() > 0) {
            LinphonePreferences.instance().firstLaunchSuccessful();
        }

        if (getResources().getBoolean(R.bool.use_linphone_tag)) {
            ContactsManager.getInstance().initializeSyncAccount(getApplicationContext(), getContentResolver());
        } else {
            ContactsManager.getInstance().initializeContactManager(getApplicationContext(), getContentResolver());
        }

        if (!LinphonePreferences.instance().isContactsMigrationDone()) {
            ContactsManager.getInstance().migrateContacts();
            LinphonePreferences.instance().contactsMigrationDone();
        }
        RDContactHelper.initRoamTechContact(this);
        //setContentView(R.layout.main);
        instance = this;
        fragmentsHistory = new ArrayList<>();

        currentFragment = FragmentsAvailable.DIALER;
        fragmentsHistory.add(currentFragment);
        mListener = new LinphoneCoreListenerBase() {
            @Override
            public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {
                if (message.getFrom().getUserName().equals("ucmsg")) {
                    try {
                        JSONObject ucmsg = new JSONObject(message.getText());
                        String action = ucmsg.getString("action");
                        String description = ucmsg.getString("description");
                        String sessionId = ucmsg.getString("by");
                        if (getLoginInfo().getSessionId() != null && !sessionId.equals(getLoginInfo().getSessionId())) {
                            Log.w("ucmsg", "bySessionId:" + sessionId + "mySessionId" + getLoginInfo().getSessionId());
                            if (action.equals("kickout")) {
                                resetForLogin(true);
                                mKickOutDlg = new TipDialog(ActivityCollector.getLastActivity(), "提示", description);
                                mKickOutDlg.setRightButton(getString(R.string.content_description_setup_ok), new TipDialog.OnClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        mKickOutDlg = null;
                                        ActivityCollector.finishToFirstView();
                                        toActivityClearTopWithState(LoginActivity.class, FIRST_LOGIN_ACTIVITY, null);
                                    }
                                });
                                mKickOutDlg.show();
                            }
                        }
                    } catch (JSONException e) {
                    }
                    cr.deleteMessage(message);
                }
            }

            @Override
            public void registrationState(LinphoneCore lc, LinphoneProxyConfig proxy, LinphoneCore.RegistrationState state, String smessage) {
                if (state.equals(RegistrationState.RegistrationCleared)) {
                    if (lc != null) {
                        LinphoneAuthInfo authInfo = lc.findAuthInfo(proxy.getIdentity(), proxy.getRealm(), proxy.getDomain());
                        if (authInfo != null)
                            lc.removeAuthInfo(authInfo);
                    }
                }

                if (state.equals(RegistrationState.RegistrationFailed) && newProxyConfig) {
                    newProxyConfig = false;
                    if (proxy.getError() == Reason.BadCredentials) {
                        ToastUtils.showToast(getApplicationContext(), getString(R.string.error_bad_credentials), Toast.LENGTH_LONG);
                    }
                    if (proxy.getError() == Reason.Unauthorized) {
                        ToastUtils.showToast(getApplicationContext(), getString(R.string.error_unauthorized), Toast.LENGTH_LONG);
                    }
                    if (proxy.getError() == Reason.IOError) {
                        ToastUtils.showToast(getApplicationContext(), getString(R.string.error_io_error), Toast.LENGTH_LONG);
                    }
                }
            }

            @Override
            public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
                LinphoneCallLog callLog = call.getCallLog();
                Log.w("来电接听执行顺序LinphoneActivity");
                Log.e("long 拨打电话状态-->", "state->" + state.toString() + "||call->" + call.getErrorInfo() + "message-->" + message + "||callData--->" + call.getUserData() + "||callLogState-->" + callLog.getStatus());
                if (state == State.OutgoingInit) {
                    if (call.getCurrentParamsCopy().getVideoEnabled()) {
                        startVideoActivity(call);
                    } else {
                        startInCallActivity(call);
                    }
                } else if (message != null && state == State.CallEnd && callLog.getStatus() == CallStatus.Missed) {
                    BadgeUtil.setBadgeCount(getApplicationContext(), 1, R.drawable.ic_launcher);
                    Intent intent = new Intent();
                    intent.setAction(ActionValue.ACTION_NEW_MISS_CALL);
                    intent.putExtra("unreadNumber", 1);
                    sendBroadcast(intent);
                } else if (message != null && state == State.CallEnd) {
                    isVoiceNumberTalk = false;
                } else if (message != null && state == State.Error) {
                    showCallDialog(call);
                    if (state == State.CallReleased && call.getDirection() == CallDirection.Outgoing) {
                        requestVoiceAvailable(null, null);
                    }
                    resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
                } else if (state == State.Connected) {
                }
            }
        };

        LinphoneCore lc = LinphoneManager.getInstance().getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.addListener(mListener);
        }
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                rotation = 0;
                break;
            case Surface.ROTATION_90:
                rotation = 90;
                break;
            case Surface.ROTATION_180:
                rotation = 180;
                break;
            case Surface.ROTATION_270:
                rotation = 270;
                break;
        }

        LinphoneManager.getInstance().getLcIfManagerNotDestroyedOrNull().setDeviceRotation(rotation);
        mAlwaysChangingPhoneAngle = rotation;
        updateAnimationsState();
        loginPhone = loginUserPhone();
        EventBus.getDefault().register(this);
    }

    private boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    private void updateAnimationsState() {
        isAnimationDisabled = getResources().getBoolean(R.bool.disable_animations) || !LinphonePreferences.instance().areAnimationsEnabled();
    }

    public boolean isAnimationDisabled() {
        return isAnimationDisabled;
    }

    public void displayHistoryDetail(String sipUri, LinphoneCallLog log) {
        RDContact contact = RDContactHelper.findContactByPhone(sipUri);

        String displayName = null;
        String photoUri = null;
        if (contact != null) {
            Uri uri = RDContactHelper.getContactPhotoUri(contact.getId());
            if (uri != null)
                photoUri = uri.toString();
            displayName = contact.getDisplayName();
        }

        String status;
        if (log.getDirection() == CallDirection.Outgoing) {
            status = "Outgoing";
        } else {
            if (log.getStatus() == CallStatus.Missed) {
                status = "Missed";
            } else {
                status = "Incoming";
            }
        }

        String callTime = secondsToDisplayableString(log.getCallDuration());
        String callDate = String.valueOf(log.getTimestamp());

        Fragment fragment2 = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer2);
        if (fragment2 != null && fragment2.isVisible() && currentFragment == FragmentsAvailable.HISTORY_DETAIL) {
            HistoryDetailFragment historyDetailFragment = (HistoryDetailFragment) fragment2;
            historyDetailFragment.changeDisplayedHistory(sipUri, displayName, photoUri, status, callTime, callDate);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String secondsToDisplayableString(int secs) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.time_format_hh_mm_ss));
        Calendar cal = Calendar.getInstance();
        cal.set(0, 0, 0, 0, 0, secs);
        return dateFormat.format(cal.getTime());
    }

    public void displayContact(Contact contact, boolean chatOnly) {
        Fragment fragment2 = getSupportFragmentManager().findFragmentById(android.R.id.tabcontent);//R.id.fragmentContainer2);
        if (fragment2 != null && fragment2.isVisible() && currentFragment == FragmentsAvailable.CONTACT) {
            ContactFragment contactFragment = (ContactFragment) fragment2;
            contactFragment.changeDisplayedContact(contact);
        }
    }

    public void displayChat(String sipUri) {
        if (getResources().getBoolean(R.bool.disable_chat)) {
            return;
        }
        Intent intent = new Intent(this, ChattingActivity.class);
        intent.putExtra("SipUri", sipUri);
        startOrientationSensor();
        startActivityForResult(intent, CHAT_ACTIVITY);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        //resetSelection();
        setSelectedMenu(id);
    }

    @SuppressWarnings("incomplete-switch")
    public void selectMenu(FragmentsAvailable menuToSelect) {
        currentFragment = menuToSelect;
    }

    public void updateChatFragment(ChatFragment fragment) {
        chatFragment = fragment;
    }

    public void updateStatusFragment(StatusFragment fragment) {
        statusFragment = fragment;
    }

    public List<String> getChatList() {
        return getChatStorage().getChatList();
    }

    public List<String> getDraftChatList() {
        return getChatStorage().getDrafts();
    }

    public List<ChatMessage> getChatMessages(String correspondent) {
        return getChatStorage().getMessages(correspondent);
    }

    public void removeFromChatList(String sipUri) {
        getChatStorage().removeDiscussion(sipUri);
    }

    public void removeFromDrafts(String sipUri) {
        getChatStorage().deleteDraft(sipUri);
    }

    public void setAddressGoToDialerAndCall(String number, String name, Uri photo) {
        if (WifiAdmin.isNetworkConnected(getApplicationContext()) && LinphoneManager.isInstanciated() && LinphoneManager.getLc().getDefaultProxyConfig() != null) {
            newOutgoingCall(number, name);
        } else {
            ToastUtils.showToast(this, getString(R.string.error_io_error), Toast.LENGTH_LONG);
        }
    }

    public void startVideoActivity(LinphoneCall currentCall) {
        Intent intent = new Intent(this, InCallActivity.class);
        intent.putExtra("VideoEnabled", true);
        startOrientationSensor();
        startActivityForResult(intent, CALL_ACTIVITY);
    }

    public void startInCallActivity(LinphoneCall currentCall) {
        Intent intent = new Intent(this, CallingActivity.class);
        intent.putExtra("VideoEnabled", false);
        startOrientationSensor();
        startActivityForResult(intent, CALL_ACTIVITY);
    }

    public void sendLogs(Context context, String info) {
        final String appName = context.getString(R.string.app_name);

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.about_bugreport_email)});
        i.putExtra(Intent.EXTRA_SUBJECT, appName + " Logs");
        i.putExtra(Intent.EXTRA_TEXT, info);
        i.setType("application/zip");

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e(ex);
        }
    }

    private void createTouch(final String phone, final String devid, final String ssid, final String wifipassword) {
        JSONObject json = getAuthJSONObject();
        try {
            json.put("devid", devid);
            json.put("phone", phone);
            json.put("wifi_ssid", ssid);
            json.put("wifi_password", wifipassword);

            OkHttpUtil.postJsonRequest(Constant.TOUCHDBMODEL_BIND, json, hashCode(), new OKCallback<String>(new TypeToken<UCResponse<String>>() {
            }) {
                @Override
                public void onResponse(int statuscode, @Nullable UCResponse<String> ucResponse) {
                    if (isSucccess()) {
                        requestMyTouchDBModels();
                    } else if (isSessionTimeout()) {
                        roamDialog.dismiss();
                        showToast(SESSION_TIME_OUT.getText());
                        toActivityClearTopWithState(LoginActivity.class, null);
                    }
                }

                @Override
                public void onFailure(IOException e) {
                    // mEmptyView.setState(LoadingState.getErrorState(e));
                    android.util.Log.e("print", "IOException====" + e);
                    roamDialog.dismiss();
                    bindTouch(phone, devid, ssid, wifipassword);
                }
            });
        } catch (JSONException ex) {

        }
    }

    public void updateTouchDBModel(String devid, String phone, String ssid, String wifipassword) {
        String userId = getUserId();
        boolean bExist = false;
        for (TouchDBModel TouchDBModel : mTouchDBModels) {
            if (TouchDBModel.getDevid().equals(devid)) {
                TouchDBModel.setPhone(phone);
                TouchDBModel.setUserId(Long.getLong(userId));
                TouchDBModel.setWifiSsid(ssid);
                TouchDBModel.setWifiPassword(wifipassword);
                bExist = true;
            }
        }
        if (!bExist) {
            TouchDBModel TouchDBModel = new TouchDBModel();
            TouchDBModel.setDevid(devid);
            TouchDBModel.setPhone(phone);
            TouchDBModel.setUserId(Long.getLong(userId));
            TouchDBModel.setWifiSsid(ssid);
            TouchDBModel.setWifiPassword(wifipassword);
            mTouchDBModels.add(TouchDBModel);
        }
        RoamApplication.loginTouchPhone = phone;
    }

    public void bindTouch(final String phone, final String devid, final String ssid, final String wifipassword) {
        updateTouchDBModel(devid, phone, ssid, wifipassword);
        TimerTask lTask = new TimerTask() {
            @Override
            public void run() {
                createTouch(phone, devid, ssid, wifipassword);
            }
        };
        mTimer.schedule(lTask, 2000);
    }

    /**
     * Register a sensor to track phoneOrientation changes
     */
    private synchronized void startOrientationSensor() {
        if (mOrientationHelper == null) {
            mOrientationHelper = new LocalOrientationEventListener(this);
        }
        mOrientationHelper.enable();
    }

    private int mAlwaysChangingPhoneAngle = -1;

    private class LocalOrientationEventListener extends OrientationEventListener {
        public LocalOrientationEventListener(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(final int o) {
            if (o == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            int degrees = 270;
            if (o < 45 || o > 315)
                degrees = 0;
            else if (o < 135)
                degrees = 90;
            else if (o < 225)
                degrees = 180;

            if (mAlwaysChangingPhoneAngle == degrees) {
                return;
            }
            mAlwaysChangingPhoneAngle = degrees;

            Log.d("Phone orientation changed to ", degrees);
            int rotation = (360 - degrees) % 360;
            LinphoneCore lc = LinphoneManager.getInstance().getLcIfManagerNotDestroyedOrNull();
            if (lc != null) {
                lc.setDeviceRotation(rotation);
                LinphoneCall currentCall = lc.getCurrentCall();
                if (currentCall != null && currentCall.cameraEnabled() && currentCall.getCurrentParamsCopy().getVideoEnabled()) {
                    lc.updateCall(currentCall, null);
                }
            }
        }
    }


    public void resetClassicMenuLayoutAndGoBackToCallIfStillRunning() {
        /*if (dialerFragment != null) {
            ((DialerFragment) dialerFragment).resetLayout(false);
		}*/

        if (LinphoneManager.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0) {
            LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
            if (call.getState() == LinphoneCall.State.IncomingReceived) {
                startActivity(new Intent(LinphoneActivity.this, CallAnswerActivity.class));
            } else if (call.getCurrentParamsCopy().getVideoEnabled()) {
                startVideoActivity(call);
            } else {
                startInCallActivity(call);
            }
        }
    }

    /*public FragmentsAvailable getCurrentFragment() {
        return currentFragment;
    }*/

    public ChatStorage getChatStorage() {
        return ChatStorage.getInstance();
    }

    public void addContact(String displayName, String sipUri) {
        if (getResources().getBoolean(R.bool.use_android_native_contact_edit_interface)) {
            Intent intent = Compatibility.prepareAddContactIntent(displayName, sipUri);
            startActivity(intent);
        }
    }

    public void editContact(Contact contact) {
        if (getResources().getBoolean(R.bool.use_android_native_contact_edit_interface)) {
            Intent intent = Compatibility.prepareEditContactIntent(Integer.parseInt(contact.getID()));
            startActivity(intent);
        }
    }

    public void editContact(Contact contact, String sipAddress) {
        if (getResources().getBoolean(R.bool.use_android_native_contact_edit_interface)) {
            Intent intent = Compatibility.prepareEditContactIntentWithSipAddress(Integer.parseInt(contact.getID()), sipAddress);
            startActivity(intent);
        }
    }

    public void exit() {
        finish();
        stopService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == FIRST_LOGIN_ACTIVITY) {
            mServicePackages = null;
            mAllServicePackages = null;
            requestMyTouchDBModels();
            setDefaultTab();
            EventBus.getDefault().post(new EventWebLoad());
        }
        if (resultCode == Activity.RESULT_FIRST_USER && requestCode == SETTINGS_ACTIVITY) {
            if (data.getExtras().getBoolean("Exit", false)) {
                exit();
            } else {
                FragmentsAvailable newFragment = (FragmentsAvailable) data.getExtras().getSerializable("FragmentToDisplay");
                //changeCurrentFragment(newFragment, null, true);
                selectMenu(newFragment);
            }
        } else if (resultCode == Activity.RESULT_FIRST_USER && requestCode == CALL_ACTIVITY) {
            getIntent().putExtra("PreviousActivity", CALL_ACTIVITY);
            boolean callTransfer = data == null ? false : data.getBooleanExtra("Transfer", false);
            if (LinphoneManager.getLc().getCallsNb() > 0) {
                //initInCallMenuLayout(callTransfer);
            } else {
                resetClassicMenuLayoutAndGoBackToCallIfStillRunning();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPause() {
        getIntent().putExtra("PreviousActivity", 0);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!LinphoneService.isReady()) {
            startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
        }
        final String userid = getLoginInfo().getUserId();
        final String sessionid = getLoginInfo().getSessionId();
        if (userid == null || sessionid == null || userid.equals("0") || sessionid.equals("0")) {
            if (mKickOutDlg == null || !mKickOutDlg.isShowing()) {
                mKickOutDlg = null;
                toActivityClearTopWithState(LoginActivity.class, FIRST_LOGIN_ACTIVITY, null);
            }
        } else if (roamDialog.isShowing()) {
            RoamApplication.bGoConfig = false;
            requestMyTouchDBModels();
            EventBus.getDefault().post(new EventWebLoad());
            getServiceSipDomain();
        }
        ContactsManager.getInstance().prepareContactsInBackground();
        if (LinphoneManager.isInstanciated()) {
            LinphoneManager.getInstance().changeStatusToOnline();
        }

        if (getIntent().getIntExtra("PreviousActivity", 0) != CALL_ACTIVITY) {
            if (LinphoneManager.getLc().getCalls().length > 0) {
                LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
                LinphoneCall.State callState = call.getState();
                if (callState == State.IncomingReceived) {
                    startActivity(new Intent(this, CallAnswerActivity.class));
                } else {
                    if (call.getCurrentParamsCopy().getVideoEnabled()) {
                        startVideoActivity(call);
                    } else {
                        startInCallActivity(call);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        uiHandler.removeCallbacksAndMessages(null);
        if (mOrientationHelper != null) {
            mOrientationHelper.disable();
            mOrientationHelper = null;
        }
        LinphoneCore lc = LinphoneManager.getLinCore(getApplicationContext());
        if (lc != null) {
            lc.removeListener(mListener);
        }
        instance = null;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        unbindDrawables(findViewById(R.id.topLayout));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view != null && view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView)) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras != null && extras.getBoolean("GoToChat", false)) {
            String sipUri = extras.getString("ChatContactSipUri");
            RoamApplication.isLoadGroupMessage = false;
            displayChat(sipUri);
        } else if (extras != null && extras.getBoolean("Notification", false)) {
            if (LinphoneManager.isInstanciated() && LinphoneManager.getLc().getCallsNb() > 0) {
                LinphoneCall call = LinphoneManager.getLc().getCalls()[0];
                if (call.getCurrentParamsCopy().getVideoEnabled()) {
                    startVideoActivity(call);
                } else {
                    startInCallActivity(call);
                }
            }
        } else {
            dialerFragment = getCurrentFragment("keyboard");
            if (dialerFragment != null) {
                if (extras != null && extras.containsKey("SipUriOrNumber")) {
                    if (getResources().getBoolean(R.bool.automatically_start_intercepted_outgoing_gsm_call)) {
                        ((KeyboardGroupFragment) dialerFragment).newOutgoingCall(extras.getString("SipUriOrNumber"));
                    } else {
                        ((KeyboardGroupFragment) dialerFragment).displayTextInAddressBar(extras.getString("SipUriOrNumber"));
                    }
                } else {
                    ((KeyboardGroupFragment) dialerFragment).newOutgoingCall(intent);
                }
            }
            if (LinphoneManager.isInstanciated() && LinphoneManager.getLc().getCalls().length > 0) {
                LinphoneCall calls[] = LinphoneManager.getLc().getCalls();
                if (calls.length > 0) {
                    LinphoneCall call = calls[0];

                    if (call != null && call.getState() != LinphoneCall.State.IncomingReceived) {
                        if (call.getCurrentParamsCopy().getVideoEnabled()) {
                            startVideoActivity(call);
                        } else {
                            startInCallActivity(call);
                        }
                    }
                }

                // If a call is ringing, start incomingcallactivity
                Collection<LinphoneCall.State> incoming = new ArrayList<LinphoneCall.State>();
                incoming.add(LinphoneCall.State.IncomingReceived);
                if (LinphoneUtils.getCallsInState(LinphoneManager.getLc(), incoming).size() > 0) {
                    startActivity(new Intent(this, CallAnswerActivity.class));
                }
            }
        }

        requestMyTouchDBModels();
        requestVoiceAvailable(null, null);
        EventBus.getDefault().post(new EventWebLoad());
        if (RoamApplication.isNewProxyConfig) {
            newProxyConfig = true;
            RoamApplication.isNewProxyConfig = false;
        }
        loginPhone = loginUserPhone();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //关闭抽屉
        // TODO Auto-generated method stub
//        boolean isOpen = getDrawerLayout().isDrawerOpen(GravityCompat.START);
//        if (isOpen && keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//            getDrawerLayout().closeDrawer(GravityCompat.START);
//            return true;
//        }

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (currIndex == 4 && RoamApplication.webCanGoBack) { //商城tab界面
//                sendBroadcast(new Intent(ActionValue.ACTION_KEY_BACK));
                ((RDMallFragment) getCurrentFragment("RD_mall")).goBack();
                return true;
            }
            if (currentFragment == FragmentsAvailable.DIALER
                    || currentFragment == FragmentsAvailable.CONTACTS
                    || currentFragment == FragmentsAvailable.HISTORY
                    || currentFragment == FragmentsAvailable.CHATLIST
                    || currentFragment == FragmentsAvailable.ABOUT_INSTEAD_OF_CHAT
                    || currentFragment == FragmentsAvailable.ABOUT_INSTEAD_OF_SETTINGS) {
                boolean isBackgroundModeActive = LinphonePreferences.instance().isBackgroundModeEnabled();
                if (!isBackgroundModeActive) {
                    stopService(new Intent(Intent.ACTION_MAIN).setClass(this, LinphoneService.class));
                    finish();
                } else if (LinphoneUtils.onKeyBackGoHome(this, keyCode, event)) {
                    return true;
                }
            } else {
                if (isTablet()) {
                    if (currentFragment == FragmentsAvailable.SETTINGS) {
                        updateAnimationsState();
                    }
                }
            }
        } else if (keyCode == KeyEvent.KEYCODE_MENU && statusFragment != null) {
            if (event.getRepeatCount() < 1) {
                statusFragment.openOrCloseStatusBar(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getPhones(final boolean success2TouchDBModelSetting) {
        JSONObject json = getAuthJSONObject();
        OkHttpUtil.postRequest(Constant.GET_PHONES, json, hashCode(), new OKCallback<PhoneRDO>(new TypeToken<UCResponse<PhoneRDO>>() {
        }) {
            @Override
            public void onResponse(int statuscode, @Nullable UCResponse<PhoneRDO> ucResponse) {
                if (isSucccess() && ucResponse != null && ucResponse.getAttributes() != null) {
                    roamDialog.dismiss();
                    mPhones = ucResponse.getAttributes().getPhones();
                    if (mPhones.isEmpty()) {
                        if (getLoginInfo().getType() == LoginInfo.NORMAL_USER) {
                            /*need configure TouchDBModel*/
                            Toast.makeText(LinphoneActivity.this, "需要配置我的号码", Toast.LENGTH_LONG).show();
                            Intent phoneIntent = new Intent();
                            phoneIntent.setClass(LinphoneActivity.this, AddNewPhoneActivity.class);
                            phoneIntent.putExtra("success2TouchDBModelSetting", success2TouchDBModelSetting);
                            startActivityForResult(phoneIntent, FIRST_PHONE_SETTING_ACTIVITY);
                        }
                    } else {
                        for (Phone phone : mPhones) {
                            if (phone.getAreaCode() == null) {
                                phone.setAreaCode("+86");
                            }
                            if (LinphoneManager.isInstanciated()) {
                                linphoneLogIn(LinphoneManager.getInstance().getUserId(), LinphoneManager.getInstance().getSessionId(), phone.getPhoneNumber(), getResources().getBoolean(R.bool.setup_account_validation_mandatory));
                            }
                        }
                    }
                } else if (isSessionTimeout()) {
                    roamDialog.dismiss();
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, FIRST_LOGIN_ACTIVITY, null);
                }
            }

            @Override
            public void onFailure(IOException e) {
                // mEmptyView.setState(LoadingState.getErrorState(e));
                android.util.Log.e("print", "IOException====" + e);
                roamDialog.dismiss();
            }
        });
    }

    /**
     * 获取络漫宝设备列表
     */
    public void requestMyTouchDBModels() {
        JSONObject json = getAuthJSONObject();
        OkHttpUtil.postRequest(Constant.GET_ROAM_BOX_LIST, json, hashCode(), new OKCallback<TouchRDO>(new TypeToken<UCResponse<TouchRDO>>() {
        }) {
            @Override
            public void onResponse(int statuscode, @Nullable UCResponse<TouchRDO> ucResponse) {
                roamDialog.dismiss();
                if (isSucccess()) {
                    mTouchDBModels = ucResponse.getAttributes().getTouchs();
                    RoamApplication.RoamBoxList = mTouchDBModels;
                    if (mTouchDBModels.isEmpty()) {
                            /*need configure TouchDBModel*/
                        getPhones(true);
                        RoamApplication.loginTouchPhone = "-1";
                        uiHandler.sendEmptyMessage(MsgType.MSG_GET_ROAM_BOX_NO_DATA);
                    } else {
                        for (TouchDBModel TouchDBModel : mTouchDBModels) {
                            if (!StringUtil.isBlank(TouchDBModel.getPhone()) && LinphoneManager.isInstanciated()) {
                                linphoneLogIn(TouchDBModel.getUserId().toString(), LinphoneManager.getInstance().getSessionId(), TouchDBModel.getPhone(), getResources().getBoolean(R.bool.setup_account_validation_mandatory));
                            }
                        }
                        RoamApplication.loginTouchPhone = mTouchDBModels.get(0).getPhone();
                        RoamApplication.RoamBoxList = mTouchDBModels;
                        getPhones(false);
                        uiHandler.sendEmptyMessage(MsgType.MSG_GET_ROAM_BOX_SUCCESS);
                    }
                    isGoRoamBoxConfig();
                } else if (isSessionTimeout()) {
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, FIRST_LOGIN_ACTIVITY, null);
                }
            }

            @Override
            public void onFailure(IOException e) {
                roamDialog.dismiss();
                uiHandler.sendEmptyMessage(MsgType.MSG_NETWORK_TIMEOUT);
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_NETWORK_TIMEOUT:
                CommonDao<TouchDBModel> commonDaoSelect = new CommonDao<>(RoamApplication.sDatabaseHelper, TouchDBModel.class);
                mTouchDBModels = commonDaoSelect.queryAll();
                break;
            case MsgType.MSG_GET_ROAM_BOX_SUCCESS:
                if (mTouchDBModels != null && !mTouchDBModels.isEmpty()) {
                    CommonDao<TouchDBModel> commonDao = new CommonDao(RoamApplication.sDatabaseHelper, TouchDBModel.class);
                    commonDao.deleteAll(TouchDBModel.class);
                    for (TouchDBModel item : mTouchDBModels) {
                        commonDao.add(item);
                    }
                }
                break;
            case MsgType.MSG_ROAM_BOX_TOKEN_SUCCESS:
                //获取token 成功
                getRoamBoxConfig();
                break;
            case MsgType.MSG_DETECT_NETWORK_SUCCESS:
                //络漫宝没有任何配置数据
                if (RoamApplication.RoamBoxConfigOld == null || RoamApplication.RoamBoxConfigOld.phone.length() == 0) {
                    if (!RoamApplication.bGoConfig) {
                        RoamApplication.bGoConfig = true;
                        toActivity(LMBAOIndexActivity.class, null);
                    }
                }
                break;
        }

    }

    private void isGoRoamBoxConfig() {
        //未获取到络漫宝数据
        if (!RoamApplication.bGoConfig) {
            final WifiAdmin wifiAdmin = new WifiAdmin(getApplicationContext());
            if (wifiAdmin.getSSID().replace("'", "").contains("RoamBox")) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String IpAddress = MacAddressUtils.intToIpAddress(wifiAdmin.getIpAddress());
                        IpAddress = IpAddress.substring(0, IpAddress.lastIndexOf(".") + 1) + "1";
                        Constant.ROAM_BOX_AUTH = Constant.LMBAO_AUTH_START + IpAddress + Constant.LMBAO_AUTH_END;
                        Constant.ROAM_BOX_CONFIG = Constant.LMBAO_AUTH_START + IpAddress + Constant.LMBAO_CONFIG_END;
                        getAuthTokenNew();
                    }
                });
            }
        }
    }

    /**
     * 获取 token
     */
    private void getAuthTokenNew() {
        roamBoxFunction.getRoamBoxAuthToken(Constant.ROAM_BOX_AUTH, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                if (response != null) {
                    CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                    }.getType());
                    if (result != null && result.attributes != null) {
                        RoamApplication.RoamBoxToken = result.attributes;
                        uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_SUCCESS);
                    } else {
                        uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_ERROR);
                    }
                }
            }

            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_TOKEN_TIMEOUT);
            }
        });
    }

    /**
     * 获取络漫宝配置
     */
    private void getRoamBoxConfig() {
        if (RoamApplication.RoamBoxToken == null || RoamApplication.RoamBoxToken.length() == 0) {
            return;
        }
        roamBoxFunction.getRoamBoxConfigurationInfo(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_DETECT_NETWORK_TIMEOUT);
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    CommonRoamBox<NetworkConfigBean> networkConfigBean = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<NetworkConfigBean>>() {
                    }.getType());
                    if (networkConfigBean != null && networkConfigBean.attributes != null) {
                        RoamApplication.RoamBoxConfigOld = networkConfigBean.attributes;
                        android.util.Log.e("络漫宝配置--->", RoamApplication.RoamBoxConfigOld.toString());
                        uiHandler.obtainMessage(MsgType.MSG_DETECT_NETWORK_SUCCESS, RoamApplication.RoamBoxConfigOld).sendToTarget();
                    } else {
                        uiHandler.sendEmptyMessage(MsgType.MSG_DETECT_NETWORK_ERROR);
                    }
                }
            }
        });

    }


    public void requestAllTrafficvoice(final ServicePackageCallback spCallback, Boolean refresh) {
        if (!refresh && mAllServicePackages != null) {
            if (spCallback != null) {
                spCallback.handle(mAllServicePackages, mVoiceTalk);
            }
        } else {
            requestTrafficVoice(Constant.GET_ALL_TRAFFIC_VOICE, "traffic_voice_all", spCallback);
        }
    }

    private void requestTrafficVoice(final String url, final String service, final ServicePackageCallback spCallback) {
        JSONObject json = getAuthJSONObject();
        OkHttpUtil.postRequest(url, json, hashCode(), new OKCallback<ServiceRDO>(new TypeToken<UCResponse<ServiceRDO>>() {
        }) {
            @Override
            public void onResponse(int statuscode, @Nullable UCResponse<ServiceRDO> ucResponse) {
                if (isSucccess() && ucResponse != null && ucResponse.getAttributes() != null) {
                    if (service.equals("traffic_voice_get")) {
                        mServicePackages = ucResponse.getAttributes().getServicePackages();//ucResponse.getResultData("servicepackages", new TypeToken<List<ServicePackage>>() {});
                    } else {
                        mAllServicePackages = ucResponse.getAttributes().getServicePackages();
                    }
                    mVoiceTalk = ucResponse.getAttributes().getVoiceTalk(); //ucResponse.getResultData("voiceavailable", VoiceTalk.class);
                    if (spCallback != null) {
                        spCallback.handle(ucResponse.getAttributes().getServicePackages(), mVoiceTalk);
                    }
                } else if (isSessionTimeout()) {
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                }
            }

            @Override
            public void onFailure(IOException e) {
                // mEmptyView.setState(LoadingState.getErrorState(e));
                android.util.Log.e("print", "IOException====" + e);
            }
        });
    }

    public void requestVoiceAvailable(final String calleeNumber, final String displayName) {
        requestVoiceAvailable(calleeNumber, displayName, null);
    }

    public void requestVoiceAvailable(final String tonumber, final String displayName, final VoiceAvailableCallback callback) {
        JSONObject json = getAuthJSONObject();
        OkHttpUtil.postRequest(Constant.VOICE_AVAILABLE, json, hashCode(), new OKCallback<ServiceRDO>(new TypeToken<UCResponse<ServiceRDO>>() {
        }) {
            @Override
            public void onResponse(int statuscode, @Nullable UCResponse<ServiceRDO> ucResponse) {
                if (isSucccess() && ucResponse != null && ucResponse.getAttributes() != null) {
                    mVoiceTalk = ucResponse.getAttributes().getVoiceTalk(); //ucResponse.getResultData("voiceavailable", VoiceTalk.class);
                    mVoiceNumber = ucResponse.getAttributes().getVoiceNumber(); //ucResponse.getResultData("voicenumber", VoiceNumber.class);
                    Log.i("remain talk timestamp " + mVoiceTalk.getRemaindertime());
                    Log.i("remain talk timestamp number " + mVoiceNumber.getPhone());
                    if (callback != null) {
                        callback.handle(mVoiceNumber, mVoiceTalk);
                    }
                    if (!StringUtil.isTrimBlank(tonumber)) {
                        newOutgoingCall(tonumber, displayName, false);
                    }
                } else if (isSessionTimeout()) {
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                }
            }

            @Override
            public void onFailure(IOException e) {
                // mEmptyView.setState(LoadingState.getErrorState(e));
                android.util.Log.e("print", "IOException====" + e);
            }
        });
    }

    public String getSipChatTo(String realdest) {
        if (mTouchDBModels != null && !mTouchDBModels.isEmpty()) {
            return CallUtil.getSipTo(mTouchDBModels.get(0).getPhone()) + ";to=" + realdest + ";userid=" + getLoginInfo().getUserId();
        }
        if (mVoiceNumber != null && mVoiceNumber.getStartTime() != null && mVoiceNumber.getEndTime() != null && mVoiceNumber.getPhone() != null) {
            Date now = new Date();
            if (mVoiceNumber.getStartTime().before(now) && mVoiceNumber.getEndTime().after(now)) {
                return CallUtil.getSipTo(mVoiceNumber.getPhone()) + ";to=" + realdest + ";userid=" + getLoginInfo().getUserId();
            }
        }
        return CallUtil.getSipTo(getLoginInfo().getPhone()) + ";to=" + realdest + ";userid=" + getLoginInfo().getUserId();
    }

    boolean newOutgoingCall(String toNumber, String displayName, Boolean asyncCall) {
        loginPhone = loginUserPhone();
        if (toNumber.equals(loginPhone) || toNumber.equals("-1") || toNumber.equals("unknown") || toNumber.equals(getString(R.string.str_unknown_number))) {
            Activity activity = ActivityCollector.getCallActivity() == null ? LinphoneActivity.this : ActivityCollector.getCallActivity();
            String alertText = getString(R.string.str_no_can_call_self);
            if (toNumber.equals("unknown") || toNumber.equals("-1") || toNumber.equals(getString(R.string.str_unknown_number))) {
                alertText = getString(R.string.str_no_can_call_unknown);
            }
            if (toNumber.equals(loginPhone) || toNumber.equals(getRoamPhone())) {
                alertText = getString(R.string.str_no_can_call_self);
            }
            mKickOutDlg = new TipDialog(activity, getString(R.string.prompt), alertText);
            mKickOutDlg.setLeftButton(getString(R.string.button_ok), null);
            mKickOutDlg.setRightButton("", null);
            mKickOutDlg.show();
            return false;
        }
        if (!LinphoneManager.isInstanciated()) {
            LinphoneManager.createAndStart(getApplicationContext());
            return false;
        }
        this.toNumber = toNumber;
        String defaultCallerPhone = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_caller_phone, "0");
        int defaultCallerType = SPreferencesTool.getInstance().getIntValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_caller_type, 0);
        if (!defaultCallerPhone.equals("0")) {
            final Activity dialogContext = ActivityCollector.getCallActivity() == null ? LinphoneActivity.this : ActivityCollector.getCallActivity();
            if (defaultCallerType == 1) { //设置 专属号 拨打
                return newOutgoingCallByVoiceNumber(dialogContext, defaultCallerPhone, toNumber, displayName);
            }
            if (defaultCallerType == 2) {//设置络漫宝 拨打
                return newOutgoingCallByRoamBox(dialogContext, defaultCallerPhone, toNumber, displayName);
            }
        }

        if (mTouchDBModels != null && !mTouchDBModels.isEmpty() && defaultCallerType != 1) {
            voiceTalk = ROAM_BOX_CALL_TYPE;
            Log.e("络漫宝呼出方式---》");
            LinphoneManager.getInstance().newOutgoingCall(CallUtil.getSipTo(mTouchDBModels.get(0).getPhone()) + ";to=" + toNumber + ";userid=" + getLoginInfo().getUserId(), displayName);
            return true;
        }
        if (mVoiceTalk != null && mVoiceTalk.getRemaindertime() > 0) {
            if (mVoiceNumber != null && mVoiceNumber.getStartTime() != null && mVoiceNumber.getEndTime() != null && mVoiceNumber.getPhone() != null) {
                Date now = new Date();
                if (mVoiceNumber.getStartTime().before(now) && mVoiceNumber.getEndTime().after(now)) {
                    voiceTalk = VOICE_NUMBER_CALL_TYPE;
                    Log.e("专属号呼出方式---》");
                    LinphoneManager.getInstance().newOutgoingCall(CallUtil.getSipTo(mVoiceNumber.getPhone()) + ";to=" + toNumber + ";userid=" + getLoginInfo().getUserId(), displayName);
                    return true;
                }
            }
            voiceTalk = RANDOM_NUMBER_CALL_TYPE;
            Log.e("随机号呼出方式---》");
            LinphoneManager.getInstance().newOutgoingCall(CallUtil.getSipTo("autodispatch") + ";to=" + toNumber + ";userid=" + getLoginInfo().getUserId(), displayName);
            return true;
        } else if (!asyncCall) {
            //语音时长不够
            mKickOutDlg = new TipDialog(ActivityCollector.getLastActivity(), getString(R.string.prompt), getString(R.string.str_alert_no_call_time));
            mKickOutDlg.setLeftButton(getString(R.string.button_cancel), null)
                    .setRightButton(getString(R.string.str_btn_now_pay), new TipDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {
                            Bundle bundle = new Bundle();
                            bundle.putString(KeyValue.TAB_TARGET, "RD_mall");
                            toActivityClearTopWithState(LinphoneActivity.class, bundle);
                        }
                    });
            mKickOutDlg.show();
            return false;
        }
        if (asyncCall) {
            requestVoiceAvailable(toNumber, displayName);
        }
        return false;
    }

    /**
     * 选择 洛漫宝 呼出
     *
     * @param context     context
     * @param callerPhone caller
     * @param toNumber    callee
     * @param displayName name
     * @return bool
     */
    boolean newOutgoingCallByRoamBox(final Activity context, String callerPhone, String toNumber, String displayName) {
        if (mTouchDBModels != null && !mTouchDBModels.isEmpty()) {
            String phone = null;
            if (callerPhone != null) {
                for (TouchDBModel item : mTouchDBModels) {
                    if (item.getPhone().equals(callerPhone)) {
                        phone = callerPhone;
                        break;
                    }
                }
            }
            if (phone == null) { //设置的主叫号码 已经失效 默认第一个
                phone = mTouchDBModels.get(0).getPhone();
                if (callerPhone != null) {
                    SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO,
                            SPreferencesTool.login_caller_phone, phone);
                }
            }
            voiceTalk = ROAM_BOX_CALL_TYPE;
            Log.e("络漫宝呼出方式---》");
            LinphoneManager.getInstance().newOutgoingCall(CallUtil.getSipTo(phone) + ";to=" + toNumber + ";userid=" + getLoginInfo().getUserId(), displayName);
            return true;
        } else {
            //没有绑定络漫宝
            mKickOutDlg = new TipDialog(context, getString(R.string.prompt), getString(R.string.str_phone_failure));
            mKickOutDlg.setLeftButton(getString(R.string.button_ok), null);
            mKickOutDlg.setRightButton("", null);
            mKickOutDlg.show();
            return false;
        }
    }

    /**
     * 使用云号码呼出
     *
     * @param toNumber    toNumber
     * @param displayName displayName
     * @return bool
     */
    boolean newOutgoingCallByVoiceNumber(final Activity context, String defaultCallerPhone, String toNumber, String displayName) {
        if (mVoiceTalk != null && mVoiceTalk.getRemaindertime() > 0) {
            if (mVoiceNumber != null && mVoiceNumber.getStartTime() != null
                    && mVoiceNumber.getEndTime() != null && mVoiceNumber.getPhone() != null) {
                Date now = new Date();
                if (mVoiceNumber.getStartTime().before(now) && mVoiceNumber.getEndTime().after(now)) {
                    String callerPhone = null;
                    if (defaultCallerPhone != null) {
                        if (defaultCallerPhone.equals(mVoiceNumber.getPhone())) {
                            callerPhone = defaultCallerPhone;
                        } else {
                            SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO,
                                    SPreferencesTool.login_caller_phone, mVoiceNumber.getPhone());
                        }
                    } else {
                        callerPhone = mVoiceNumber.getPhone();
                    }
                    isVoiceNumberTalk = true;
                    voiceTalk = VOICE_NUMBER_CALL_TYPE;
                    Log.e("专属号呼出方式---》");
                    LinphoneManager.getInstance().newOutgoingCall(CallUtil.getSipTo(callerPhone) + ";to=" + toNumber + ";userid=" + getLoginInfo().getUserId(), displayName);
                    return true;
                }
            }
            isVoiceNumberTalk = false;
            voiceTalk = RANDOM_NUMBER_CALL_TYPE;
            Log.e("随机号呼出方式---》");
            LinphoneManager.getInstance().newOutgoingCall(CallUtil.getSipTo("autodispatch") + ";to=" + toNumber + ";userid=" + getLoginInfo().getUserId(), displayName);
            return true;
        } else {
            //语音时长不够
            mKickOutDlg = new TipDialog(context, getString(R.string.prompt), getString(R.string.str_alert_no_call_time));
            mKickOutDlg.setLeftButton(getString(R.string.button_cancel), null)
                    .setRightButton(getString(R.string.str_btn_now_pay), new TipDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {

                            Bundle bundle = new Bundle();
                            bundle.putString(KeyValue.TAB_TARGET, MainTab.MALL.getTag());
                            toActivityClearTopWithState(context.getClass(), bundle);
                        }
                    });
            mKickOutDlg.show();
            return false;
        }
    }

    void newOutgoingCallByAuto(final Activity context, String tonumber, String displayName) {
        if (mVoiceTalk != null && mVoiceTalk.getRemaindertime() > 0) {
            isVoiceNumberTalk = false;
            voiceTalk = RANDOM_NUMBER_CALL_TYPE;
            LinphoneManager.getInstance().newOutgoingCall(CallUtil.getSipTo("autodispatch") + ";to=" + toNumber + ";userid=" + getLoginInfo().getUserId(), displayName);
        } else {
            //语音时长不够
            mKickOutDlg = new TipDialog(context, getString(R.string.prompt), getString(R.string.str_alert_no_call_time));
            mKickOutDlg.setLeftButton(getString(R.string.button_cancel), null)
                    .setRightButton(getString(R.string.str_btn_now_pay), new TipDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {
                            Bundle bundle = new Bundle();
                            bundle.putString(KeyValue.TAB_TARGET, MainTab.MALL.getTag());
                            toActivityClearTopWithState(context.getClass(), bundle);
                        }
                    });
            mKickOutDlg.show();
        }
    }

    void newOutgoingCall(String tonumber, String displayName) {
        newOutgoingCall(tonumber, displayName, true);
    }

    public LinphoneChatRoom initChatRoom(String sipUri) {
        Log.e("Tring Chat To: " + sipUri);
        LinphoneChatRoom chatRoom = null;
        LinphoneCore lc = LinphoneManager.getInstance(getApplicationContext()).getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            String chatTo = LinphoneActivity.instance().getSipChatTo(CallUtil.getRealToNumber(sipUri));
            if (chatTo != null) {
                chatRoom = lc.getOrCreateChatRoom(chatTo);//sipUri);
                Log.e("Chat To: " + chatRoom.getPeerAddress().asString());
            }
        }
        return chatRoom;
    }

    /**
     * 电话 拨打失败 显示 错误对话框
     */
    private void showCallDialog(LinphoneCall call) {
        final Activity dialogContext = ActivityCollector.getCallActivity() == null ? LinphoneActivity.this : ActivityCollector.getCallActivity();
        Reason reason = call.getErrorInfo().getReason();
        int errorCode = call.getErrorInfo().getProtocolCode();
        String error_message = null;
        if (reason == Reason.Declined) {
            ToastUtils.showToast(dialogContext, getString(R.string.error_call_declined), Toast.LENGTH_LONG);
        } else if (reason == Reason.Media) {
            ToastUtils.showToast(dialogContext, getString(R.string.error_incompatible_media), Toast.LENGTH_LONG);
        } else if (errorCode == 480 || reason == Reason.NotFound || reason == Reason.ServerTimeout || errorCode == 408) {
            error_message = getString(R.string.str_alert_roam_call_error);
        } else if (errorCode == 500 || errorCode == 502 || errorCode == 503) {
            error_message = getString(R.string.str_alert_roam_call_error_no_sim);
        }
        Log.e("long-->拨打电话--", "Reason->" + reason.toString() + "||code-->" + errorCode + "||message-" + error_message);
        if (!mTouchDBModels.isEmpty() && !isVoiceNumberTalk && error_message != null) {
            //有络漫宝用户
            mKickOutDlg = new TipDialog(dialogContext, getString(R.string.str_alert_call_title), error_message);
            mKickOutDlg.setLeftButton(getString(R.string.button_cancel), null)
                    .setRightButton(getString(R.string.button_ok), new TipDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {
                            newOutgoingCallByVoiceNumber(dialogContext, null, toNumber, null);
                        }
                    });
            mKickOutDlg.show();
        } else {
            //没有络漫宝用户
            if (isVoiceNumberTalk) {
                if (mVoiceNumber != null && mVoiceTalk.getRemaindertime() > 0) {
                    if (mVoiceNumber != null && mVoiceNumber.getStartTime() != null && mVoiceNumber.getEndTime() != null && mVoiceNumber.getPhone() != null) {
                        //存在专属号码
                        Date now = new Date();
                        if (mVoiceNumber.getStartTime().before(now) && mVoiceNumber.getEndTime().after(now)) {
                            mKickOutDlg = new TipDialog(dialogContext, getString(R.string.str_alert_call_title), getString(R.string.str_alert_voice_number_call_error));
                            mKickOutDlg.setLeftButton(getString(R.string.button_cancel), null)
                                    .setRightButton(getString(R.string.button_ok), new TipDialog.OnClickListener() {
                                        @Override
                                        public void onClick(int which) {
                                            newOutgoingCallByAuto(dialogContext, toNumber, null);
                                        }
                                    });
                            mKickOutDlg.show();
                        }
                    }
                }
            } else {

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshRoamBox(EventRefresh event) {
        refreshRoamBoxList();
    }

    private void refreshRoamBoxList() {
        roamBoxFunction.getRoamBoxList(Constant.GET_ROAM_BOX_LIST, getAuthJSONObject(), hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    UCResponse<TouchRDO> list = JsonUtil.fromJson(response, new TypeToken<UCResponse<TouchRDO>>() {
                    }.getType());
                    if (list != null && list.getAttributes() != null
                            && list.getAttributes().getTouchs() != null && list.getAttributes().getTouchs().size() > 0) {
                        RoamApplication.RoamBoxList = list.getAttributes().getTouchs();
                    }
                    android.util.Log.e("重新获取络漫宝数据----》", "重新获取络漫宝数据----》");
                }
            }
        });
    }

    /**
     * 获取 sip domain
     */
    private void getServiceSipDomain() {
        new Domain(getApplicationContext()).getDomain(getAuthJSONObject(), hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                if (response != null) {
                    UCResponse<DomainRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<DomainRDO>>() {
                    }.getType());
                    if (result != null && result.getAttributes() != null) {
                        String sipDomain = result.getAttributes().domains.domain;
                        if (!StringUtil.isBlank(sipDomain)) {
                            SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_domain, sipDomain);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }
        });
    }
}


