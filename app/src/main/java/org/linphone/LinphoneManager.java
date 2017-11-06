/*
LinphoneManager.java
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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.Receiver.ActionValue;
import com.roamtech.telephony.roamapp.activity.function.GetBell;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.bean.NetworkConfigBean;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.util.CallUtil;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.TelephonyUtils;
import com.roamtech.telephony.roamapp.util.ToastUtils;

import org.linphone.compatibility.Compatibility;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneBuffer;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCallStats;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.EcCalibratorStatus;
import org.linphone.core.LinphoneCore.GlobalState;
import org.linphone.core.LinphoneCore.LogCollectionUploadState;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCore.RemoteProvisioningState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListener;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphoneEvent;
import org.linphone.core.LinphoneFriend;
import org.linphone.core.LinphoneInfoMessage;
import org.linphone.core.LinphoneProxyConfig;
import org.linphone.core.PayloadType;
import org.linphone.core.PresenceActivityType;
import org.linphone.core.PresenceModel;
import org.linphone.core.PublishState;
import org.linphone.core.SubscriptionState;
import org.linphone.core.TunnelConfig;
import org.linphone.mediastream.Log;
import org.linphone.mediastream.Version;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration;
import org.linphone.mediastream.video.capture.hwconf.AndroidCameraConfiguration.AndroidCamera;
import org.linphone.mediastream.video.capture.hwconf.Hacks;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Manager of the low level LibLinphone stuff.<br />
 * Including:<ul>
 * <li>Starting C liblinphone</li>
 * <li>Reacting to C liblinphone state changes</li>
 * <li>Calling Linphone android service listener methods</li>
 * <li>Interacting from Android GUI/service with low level SIP stuff/</li>
 * </ul>
 * <p>
 * Add Service Listener to react to Linphone state changes.
 *
 * @author Guillaume Beraudo
 */
public class LinphoneManager implements LinphoneCoreListener, LinphoneChatMessage.LinphoneChatMessageListener {

    private static LinphoneManager instance;
    private static WakeLock wakeLock;
    private Context mServiceContext;
    private AudioManager mAudioManager;
    private PowerManager mPowerManager;
    private Resources mR;
    private LinphonePreferences mPrefs;
    private LinphoneCore mLc;
    private String basePath;
    private String recordingPath;
    private static boolean sExited;
    private boolean mAudioFocused;
    private int mLastNetworkType = -1;
    private ConnectivityManager mConnectivityManager;
    private Handler mHandler = new Handler();
    private WakeLock mIncallWakeLock;
    private List<LinphoneChatMessage> mPendingChatFileMessage;
    private LinphoneChatMessage mUploadPendingFileMessage;

    public String wizardLoginViewDomain = null;

    private static final int LINPHONE_VOLUME_STREAM = AudioManager.STREAM_VOICE_CALL;
    private static final int dbStep = 4;
    /**
     * Called when the activity is first created.
     */
    private final String mLPConfigXsd;
    private final String mLinphoneFactoryConfigFile;
    private final String mLinphoneRootCaFile;
    public final String mLinphoneConfigFile;
    private final String mRingSoundFile;
    private final String mRingbackSoundFile;
    private final String mPauseSoundFile;
    private final String mChatDatabaseFile;
    private final String mErrorToneFile;
    private ByteArrayInputStream mUploadingImageStream;

    private Timer mTimer;
    private String userId;
    private String sessionId;
    private static CallMessageUtil callMessageUtil;

    private List<LinphoneChatMessage.LinphoneChatMessageListener> simpleListeners = new ArrayList<>();

    public void addListener(LinphoneChatMessage.LinphoneChatMessageListener listener) {
        if (!simpleListeners.contains(listener)) {
            simpleListeners.add(listener);
        }
    }

    public void removeListener(LinphoneChatMessage.LinphoneChatMessageListener listener) {
        simpleListeners.remove(listener);
    }

    protected LinphoneManager(final Context c) {
        sExited = false;
        mServiceContext = c;
        basePath = c.getFilesDir().getAbsolutePath();
        mLPConfigXsd = basePath + "/lpconfig.xsd";
        mLinphoneFactoryConfigFile = basePath + "/linphonerc";
        mLinphoneConfigFile = basePath + "/.linphonerc";
        mLinphoneRootCaFile = basePath + "/rootca.pem";
        mRingSoundFile = basePath + "/oldphone_mono.wav";
        mRingbackSoundFile = basePath + "/ringback.wav";
        mPauseSoundFile = basePath + "/toy_mono.wav";
        mChatDatabaseFile = basePath + "/linphone-history.db";
        mErrorToneFile = basePath + "/error.wav";

        mPrefs = LinphonePreferences.instance();
        // LinphoneService.ServiceBinder binder = (LinphoneService.ServiceBinder) service;
        // linphoneService = binder.getIService();
        mAudioManager = ((AudioManager) c.getSystemService(Context.AUDIO_SERVICE));
        mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
        mPowerManager = (PowerManager) c.getSystemService(Context.POWER_SERVICE);
        mConnectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        mR = c.getResources();
        mPendingChatFileMessage = new ArrayList<>();
        buildRecordingPath();
    }

    private void buildRecordingPath() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 获得存储卡的路径
            recordingPath = RoamApplication.FILEPATH_RECORD;
        } else {
            recordingPath = basePath + "/recording";
        }
        File file = new File(recordingPath);
        // 判断文件目录是否存在
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public String getRecordingFile(String AnotherNumber) {
        String recordingFile = AnotherNumber + ".wav";
        return recordingPath + "/" + recordingFile;
    }

    private void routeAudioToSpeakerHelper(boolean speakerOn) {
        Log.w("Routing audio to " + (speakerOn ? "speaker" : "earpiece") + ", disabling bluetooth audio route");
        BluetoothManager.getInstance().disableBluetoothSCO();
        mLc.enableSpeaker(speakerOn);
    }

    public void routeAudioToSpeaker() {
        routeAudioToSpeakerHelper(true);
    }

    public void routeAudioToReceiver() {
        routeAudioToSpeakerHelper(false);
    }

    public synchronized static final LinphoneManager createAndStart(Context ctx) {
        if (instance == null) {
            Context context = ctx.getApplicationContext();
            instance = new LinphoneManager(context);
            instance.startLibLinphone(context);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            boolean gsmIdle = tm.getCallState() == TelephonyManager.CALL_STATE_IDLE;
            instance.setGsmIdle(gsmIdle);
            callMessageUtil = new CallMessageUtil(context);
        }
        return instance;
    }

    public void addDownloadMessagePending(LinphoneChatMessage message) {
        synchronized (mPendingChatFileMessage) {
            mPendingChatFileMessage.add(message);
        }
    }

    public boolean isMessagePending(LinphoneChatMessage message) {
        boolean messagePending = false;
        synchronized (mPendingChatFileMessage) {
            for (LinphoneChatMessage chat : mPendingChatFileMessage) {
                if (chat.getStorageId() == message.getStorageId()) {
                    messagePending = true;
                    break;
                }
            }
        }
        return messagePending;
    }

    private void removePendingMessage(LinphoneChatMessage message) {
        synchronized (mPendingChatFileMessage) {
            for (LinphoneChatMessage chat : mPendingChatFileMessage) {
                if (chat.getStorageId() == message.getStorageId()) {
                    mPendingChatFileMessage.remove(chat);
                }
                break;
            }
        }
    }

    public void setUploadPendingFileMessage(LinphoneChatMessage message) {
        mUploadPendingFileMessage = message;
    }

    public LinphoneChatMessage getMessageUploadPending() {
        return mUploadPendingFileMessage;
    }

    public void setUploadingImageStream(ByteArrayInputStream array) {
        this.mUploadingImageStream = array;
    }


    @Override
    public void onLinphoneChatMessageStateChanged(LinphoneChatMessage msg, LinphoneChatMessage.State state) {
        if (state == LinphoneChatMessage.State.FileTransferDone || state == LinphoneChatMessage.State.FileTransferError) {
            if (msg.isOutgoing() && mUploadingImageStream != null) {
                mUploadPendingFileMessage = null;
                mUploadingImageStream = null;
            } else {
                File file = new File(Environment.getExternalStorageDirectory(), msg.getFileTransferInformation().getName());
                try {
                    String url = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), file.getPath(), msg.getFileTransferInformation().getName(), null);
                    msg.setAppData(url);
                    file.delete();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                removePendingMessage(msg);
            }
        }

        for (LinphoneChatMessage.LinphoneChatMessageListener l : simpleListeners) {
            l.onLinphoneChatMessageStateChanged(msg, state);
        }
    }

    @Override
    public void onLinphoneChatMessageFileTransferReceived(LinphoneChatMessage msg, LinphoneContent content, LinphoneBuffer buffer) {
    }

    @Override
    public void onLinphoneChatMessageFileTransferSent(LinphoneChatMessage msg, LinphoneContent content, int offset, int size, LinphoneBuffer bufferToFill) {
        if (mUploadingImageStream != null && size > 0) {
            byte[] data = new byte[size];
            int read = mUploadingImageStream.read(data, 0, size);
            if (read > 0) {
                bufferToFill.setContent(data);
                bufferToFill.setSize(read);
            } else {
                Log.e("Error, upload task asking for more bytes(" + size + ") than available (" + mUploadingImageStream.available() + ")");
            }
        }
    }

    @Override
    public void onLinphoneChatMessageFileTransferProgressChanged(LinphoneChatMessage msg, LinphoneContent content, int offset, int total) {
        for (LinphoneChatMessage.LinphoneChatMessageListener l : simpleListeners) {
            l.onLinphoneChatMessageFileTransferProgressChanged(msg, content, offset, total);
        }
    }

    private boolean isPresenceModelActivitySet() {
        LinphoneCore lc = getLcIfManagerNotDestroyedOrNull();
        if (isInstanciated() && lc != null) {
            return lc.getPresenceModel() != null && lc.getPresenceModel().getActivity() != null;
        }
        return false;
    }

    public void changeStatusToOnline() {
        LinphoneCore lc = getLcIfManagerNotDestroyedOrNull();
        if (isInstanciated() && lc != null && isPresenceModelActivitySet() && lc.getPresenceModel().getActivity().getType() != PresenceActivityType.Online) {
            lc.getPresenceModel().getActivity().setType(PresenceActivityType.Online);
        } else if (isInstanciated() && lc != null && !isPresenceModelActivitySet()) {
            PresenceModel model = LinphoneCoreFactory.instance().createPresenceModel(PresenceActivityType.Online, null);
            lc.setPresenceModel(model);
        }
    }

    public void changeStatusToOnThePhone() {
        LinphoneCore lc = getLcIfManagerNotDestroyedOrNull();
        if (isInstanciated() && isPresenceModelActivitySet() && lc.getPresenceModel().getActivity().getType() != PresenceActivityType.OnThePhone) {
            lc.getPresenceModel().getActivity().setType(PresenceActivityType.OnThePhone);
        } else if (isInstanciated() && !isPresenceModelActivitySet()) {
            PresenceModel model = LinphoneCoreFactory.instance().createPresenceModel(PresenceActivityType.OnThePhone, null);
            lc.setPresenceModel(model);
        }
    }

    public void changeStatusToOffline() {
        LinphoneCore lc = getLcIfManagerNotDestroyedOrNull();
        if (isInstanciated() && lc != null && isPresenceModelActivitySet() && lc.getPresenceModel().getActivity().getType() != PresenceActivityType.Offline) {
            lc.getPresenceModel().getActivity().setType(PresenceActivityType.Offline);
        } else if (isInstanciated() && lc != null && !isPresenceModelActivitySet()) {
            PresenceModel model = LinphoneCoreFactory.instance().createPresenceModel(PresenceActivityType.Offline, null);
            lc.setPresenceModel(model);
        }
    }

    public static synchronized final LinphoneManager getInstance() {
        if (instance != null) return instance;
        if (sExited) {
            throw new RuntimeException("Linphone Manager was already destroyed. "
                    + "Better use getLcIfManagerNotDestroyed and check returned value");
        }
        throw new RuntimeException("Linphone Manager should be created before accessed");
    }

    public static synchronized final LinphoneManager getInstance(Context context) {
        if (instance != null) return instance;
        return instance = createAndStart(context);
    }

    public static synchronized final LinphoneCore getLc() {
        return getInstance().mLc;
    }

    public static synchronized final LinphoneCore getLinCore(Context context) {
        return getInstance(context).mLc;
    }

    public void newOutgoingCall(String to, String displayName) {
        LinphoneAddress lAddress;
        try {
            lAddress = mLc.interpretUrl(to);
            if (mServiceContext.getResources().getBoolean(R.bool.override_domain_using_default_one)) {
                lAddress.setDomain(mServiceContext.getString(R.string.default_domain));
            }
            LinphoneProxyConfig lpc = mLc.getDefaultProxyConfig();
            if (mR.getBoolean(R.bool.forbid_self_call) && lpc != null && lAddress.asStringUriOnly().equals(lpc.getIdentity())) {
                return;
            }
        } catch (LinphoneCoreException e) {
            return;
        }
        lAddress.setDisplayName(displayName);
        boolean isLowBandwidthConnection = !LinphoneUtils.isHighBandwidthConnection(LinphoneService.instance().getApplicationContext());
        if (mLc.isNetworkReachable()) {
            try {
                //String caller = SPreferencesTool.getInstance().getStringValue(getContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_phone, "0");
                int dbId = callMessageUtil.getDbId(null);
                if (Version.isVideoCapable()) {
                    boolean prefVideoEnable = mPrefs.isVideoEnabled();
                    boolean prefInitiateWithVideo = mPrefs.shouldInitiateVideoCall();
                    CallManager.getInstance().inviteAddress(lAddress, prefVideoEnable && prefInitiateWithVideo, isLowBandwidthConnection, getRecordingFile(String.valueOf(dbId)));
                } else {
                    CallManager.getInstance().inviteAddress(lAddress, false, isLowBandwidthConnection, getRecordingFile(String.valueOf(dbId)));
                }
            } catch (LinphoneCoreException e) {
                return;
            }
        } else if (LinphoneActivity.isInstanciated()) {
            ToastUtils.showToast(LinphoneActivity.instance(), getString(R.string.error_network_unreachable), Toast.LENGTH_LONG);
        } else {
            Log.e("Error: " + getString(R.string.error_network_unreachable));
        }
    }

    private void resetCameraFromPreferences() {
        boolean useFrontCam = mPrefs.useFrontCam();

        int camId = 0;
        AndroidCamera[] cameras = AndroidCameraConfiguration.retrieveCameras();
        for (AndroidCamera androidCamera : cameras) {
            if (androidCamera.frontFacing == useFrontCam)
                camId = androidCamera.id;
        }
        LinphoneManager.getLc().setVideoDevice(camId);
    }

    public interface AddressType {
        void setText(CharSequence s);

        CharSequence getText();

        void setDisplayedName(String s);

        String getDisplayedName();
    }

    public void enableCamera(LinphoneCall call, boolean enable) {
        if (call != null) {
            call.enableCamera(enable);
            if (mServiceContext.getResources().getBoolean(R.bool.enable_call_notification))
                LinphoneService.instance().refreshInCallIcon(mLc.getCurrentCall());
        }
    }

    public void playDtmf(ContentResolver r, char dtmf) {
        try {
            if (Settings.System.getInt(r, Settings.System.DTMF_TONE_WHEN_DIALING) == 0) {
                // audible touch disabled: don't play on speaker, only send in outgoing stream
                return;
            }
        } catch (SettingNotFoundException e) {
        }

        getLc().playDtmf(dtmf, 500);
    }

    public void terminateCall() {
        if (mLc.isIncall()) {
            mLc.terminateCall(mLc.getCurrentCall());
        }
    }

    public void initTunnelFromConf() {
        if (!mLc.isTunnelAvailable())
            return;

        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        mLc.tunnelCleanServers();
        TunnelConfig config = mPrefs.getTunnelConfig();
        if (config.getHost() != null) {
            mLc.tunnelAddServer(config);
            manageTunnelServer(info);
        }
    }

    private boolean isTunnelNeeded(NetworkInfo info) {
        if (info == null) {
            Log.i("No connectivity: tunnel should be disabled");
            return false;
        }

        String pref = mPrefs.getTunnelMode();

        if (getString(R.string.tunnel_mode_entry_value_always).equals(pref)) {
            return true;
        }

        if (info.getType() != ConnectivityManager.TYPE_WIFI
                && getString(R.string.tunnel_mode_entry_value_3G_only).equals(pref)) {
            Log.i("need tunnel: 'no wifi' connection");
            return true;
        }

        return false;
    }

    private void manageTunnelServer(NetworkInfo info) {
        if (mLc == null) return;
        if (!mLc.isTunnelAvailable()) return;

        Log.i("Managing tunnel");
        if (isTunnelNeeded(info)) {
            Log.i("Tunnel need to be activated");
            mLc.tunnelSetMode(LinphoneCore.TunnelMode.enable);
        } else {
            Log.i("Tunnel should not be used");
            String pref = mPrefs.getTunnelMode();
            mLc.tunnelSetMode(LinphoneCore.TunnelMode.disable);
            if (getString(R.string.tunnel_mode_entry_value_auto).equals(pref)) {
                mLc.tunnelSetMode(LinphoneCore.TunnelMode.auto);
            }
        }
    }

    private synchronized void startLibLinphone(Context c) {
        try {
            copyAssetsFromPackage();
            //traces alway start with traces enable to not missed first initialization
            boolean isDebugLogEnabled = !(mR.getBoolean(R.bool.disable_every_log));
            LinphoneCoreFactory.instance().setDebugMode(isDebugLogEnabled, getString(R.string.app_name));
            LinphoneCoreFactory.instance().enableLogCollection(isDebugLogEnabled);

            mLc = LinphoneCoreFactory.instance().createLinphoneCore(this, mLinphoneConfigFile, mLinphoneFactoryConfigFile, null, c);
            try {
                initLiblinphone();
            } catch (LinphoneCoreException e) {
                Log.e(e);
            }

            TimerTask lTask = new TimerTask() {
                @Override
                public void run() {
                    UIThreadDispatcher.dispatch(new Runnable() {
                        @Override
                        public void run() {
                            if (mLc != null) {
                                mLc.iterate();
                            }
                        }
                    });
                }
            };
            /*use schedule instead of scheduleAtFixedRate to avoid iterate from being call in burst after cpu wake up*/
            mTimer = new Timer("Linphone scheduler");
            mTimer.schedule(lTask, 0, 20);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(e, "Cannot start linphone");
        }
    }

    private synchronized void initLiblinphone() throws LinphoneCoreException {
        boolean isDebugLogEnabled = !(mR.getBoolean(R.bool.disable_every_log)) && mPrefs.isDebugEnabled();
        LinphoneCoreFactory.instance().setDebugMode(isDebugLogEnabled, getString(R.string.app_name));
        LinphoneCoreFactory.instance().enableLogCollection(isDebugLogEnabled);

        PreferencesMigrator prefMigrator = new PreferencesMigrator(mServiceContext);
        prefMigrator.migrateRemoteProvisioningUriIfNeeded();
        prefMigrator.migrateSharingServerUrlIfNeeded();

        if (prefMigrator.isMigrationNeeded()) {
            prefMigrator.doMigration();
        }

        // Some devices could be using software AEC before
        // This will disable it in favor of hardware AEC if available
        if (prefMigrator.isEchoMigratioNeeded()) {
            Log.d("Echo canceller configuration need to be updated");
            prefMigrator.doEchoMigration();
            mPrefs.echoConfigurationUpdated();
        }

        mLc.setContext(mServiceContext);
        mLc.setZrtpSecretsCache(basePath + "/zrtp_secrets");

        try {
            String versionName = mServiceContext.getPackageManager().getPackageInfo(mServiceContext.getPackageName(), 0).versionName;
            if (versionName == null) {
                versionName = String.valueOf(mServiceContext.getPackageManager().getPackageInfo(mServiceContext.getPackageName(), 0).versionCode);
            }
            mLc.setUserAgent("RoamphoneAndroid", versionName);
        } catch (NameNotFoundException e) {
            Log.e(e, "cannot get version name");
        }

        if (mR.getBoolean(R.bool.use_linphonecore_ringing)) {
            disableRinging();
        } else {
            mLc.setRing(null);
        }
        mLc.setRootCA(mLinphoneRootCaFile);
        mLc.setPlayFile(mPauseSoundFile);
        mLc.setChatDatabasePath(mChatDatabaseFile);
        //mLc.setCallErrorTone(Reason.NotFound, mErrorToneFile);

        int availableCores = Runtime.getRuntime().availableProcessors();
        Log.w("MediaStreamer : " + availableCores + " cores detected and configured");
        mLc.setCpuCount(availableCores);

        int migrationResult = getLc().migrateToMultiTransport();
        Log.d("Migration to multi transport result = " + migrationResult);

        if (mServiceContext.getResources().getBoolean(R.bool.enable_push_id)) {
            Compatibility.initPushNotificationService(mServiceContext);
        }

		/*IntentFilter lFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        lFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mServiceContext.registerReceiver(mKeepAliveReceiver, lFilter);*/
        mLc.clearAuthInfos();
        mLc.clearProxyConfigs();
        mLc.enableKeepAlive(true);
        mLc.setIncomingTimeout(120);
        for (final PayloadType pt : mLc.getAudioCodecs()) {
            /*if(pt.getMime().equals("iLBC")) {
                mLc.enablePayloadType(pt, true);
			}
			else*/
            if (pt.getMime().equals("SILK") && pt.getRate() == 8000) {
                mLc.enablePayloadType(pt, true);
            } else {
                mLc.enablePayloadType(pt, false);
            }
        }
        updateNetworkReachAbility();
        TelephonyUtils.createAndStart(mServiceContext);
        TelephonyUtils.getInstance().disconnectTouch();
        if (Version.sdkAboveOrEqual(Version.API11_HONEYCOMB_30)) {
            BluetoothManager.getInstance().initBluetooth();
        }
        //resetCameraFromPreferences();
        mLc.setFileTransferServer(LinphonePreferences.instance().getSharingPictureServerUrl());
    }

    private void copyAssetsFromPackage() throws IOException {
        copyIfNotExist(R.raw.oldphone_mono, mRingSoundFile);
        copyIfNotExist(R.raw.ringback, mRingbackSoundFile);
        copyIfNotExist(R.raw.toy_mono, mPauseSoundFile);
        copyIfNotExist(R.raw.incoming_chat, mErrorToneFile);
        copyIfNotExist(R.raw.linphonerc_default, mLinphoneConfigFile);
        copyFromPackage(R.raw.linphonerc_factory, new File(mLinphoneFactoryConfigFile).getName());
        copyIfNotExist(R.raw.lpconfig, mLPConfigXsd);
        //copyIfNotExist(R.raw.rootca, mLinphoneRootCaFile);
    }

    private void copyIfNotExist(int ressourceId, String target) throws IOException {
        File lFileToCopy = new File(target);
        if (!lFileToCopy.exists()) {
            copyFromPackage(ressourceId, lFileToCopy.getName());
        }
    }

    private void copyFromPackage(int ressourceId, String target) throws IOException {
        FileOutputStream lOutputStream = mServiceContext.openFileOutput(target, 0);
        InputStream lInputStream = mR.openRawResource(ressourceId);
        int readByte;
        byte[] buff = new byte[8048];
        while ((readByte = lInputStream.read(buff)) != -1) {
            lOutputStream.write(buff, 0, readByte);
        }
        lOutputStream.flush();
        lOutputStream.close();
        lInputStream.close();
    }

    public void updateNetworkReachAbility() {
        ConnectivityManager cm = (ConnectivityManager) mServiceContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo eventInfo = cm.getActiveNetworkInfo();
        if (eventInfo == null || eventInfo.getState() == NetworkInfo.State.DISCONNECTED) {
            Log.i("No connectivity: setting network unreachable");
            mLc.setNetworkReachable(false);
        } else if (eventInfo.getState() == NetworkInfo.State.CONNECTED) {
            if (!RoamApplication.isLogined) {
                mServiceContext.startService(new Intent(mServiceContext, LinphoneService.class));
            }
            manageTunnelServer(eventInfo);
            boolean wifiOnly = LinphonePreferences.instance().isWifiOnlyEnabled();
            if (wifiOnly) {
                if (eventInfo.getType() == ConnectivityManager.TYPE_WIFI)
                    mLc.setNetworkReachable(true);
                else {
                    Log.i("Wifi-only mode, setting network not reachable");
                    mLc.setNetworkReachable(false);
                }
            } else {
                int curType = eventInfo.getType();
                if (curType != mLastNetworkType || curType == ConnectivityManager.TYPE_WIFI) {
                    //if kind of network has changed, we need to notify network_reachable(false) to make sure all current connections are destroyed.
                    //they will be re-created during setNetworkReachable(true).
                    Log.i("Connectivity has changed.");
                    mLc.setNetworkReachable(false);
                }
                mLc.setNetworkReachable(true);
                mLastNetworkType = curType;
                if (mLc.getCallsNb() > 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            CallManager.getInstance().reinvite();
                        }
                    }).start();
                }

            }
        }
    }

    public void doDestroy() {
        if (LinphoneService.isReady()) {// indeed, no need to crash
            ChatStorage.getInstance().close();
        }
        changeStatusToOffline();
        BluetoothManager.getInstance().destroy();
        try {
            mTimer.cancel();

            mLc.destroy();
            TelephonyUtils.getInstance().destroy();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            sExited = true;
            mLc = null;
            instance = null;
        }
    }

    private String getString(int key) {
        return mR.getString(key);
    }

    private LinphoneCall ringingCall;
    private LinphoneCall outgoingCall;
    private MediaPlayer mRingerPlayer;
    private Vibrator mVibrator;

    public void displayWarning(LinphoneCore lc, String message) {
    }

    public void authInfoRequested(LinphoneCore lc, String realm, String username, String domain) {
    }

    public void displayMessage(LinphoneCore lc, String message) {
    }

    public void show(LinphoneCore lc) {
    }

    public void newSubscriptionRequest(LinphoneCore lc, LinphoneFriend lf, String url) {
    }

    public void notifyPresenceReceived(LinphoneCore lc, LinphoneFriend lf) {
    }

    @Override
    public void dtmfReceived(LinphoneCore lc, LinphoneCall call, int dtmf) {
        Log.d("DTMF received: " + dtmf);
    }

    @Override
    public void messageReceived(LinphoneCore lc, LinphoneChatRoom cr, LinphoneChatMessage message) {
        if (message.getFrom().getUserName().equals("ucmsg")) {
            Log.w("异地登录通知LinphoneManager");
            return;
        }
        String textMessage = message.getText();
        if (textMessage != null && textMessage.contains("result")) {
            CommonRoamBox<NetworkConfigBean> networkConfig = JsonUtil.fromJson(textMessage, new TypeToken<CommonRoamBox<NetworkConfigBean>>() {
            }.getType());
            if (networkConfig != null && networkConfig.attributes != null) {
                NetworkConfigBean bean = networkConfig.attributes;
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable(NetworkConfigBean.class.getName(), bean);
                intent.putExtras(bundle);
                intent.setAction(ActionValue.ACTION_ROAM_BOX_MESSAGE);
                getContext().sendBroadcast(intent);
            }
            if (networkConfig != null && networkConfig.error_info.equals("devid not match")) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("errorInfo", networkConfig.error_info);
                intent.putExtras(bundle);
                intent.setAction(ActionValue.ACTION_ROAM_BOX_MESSAGE);
                getContext().sendBroadcast(intent);
            }
        }
    }

    public void displayStatus(final LinphoneCore lc, final String message) {

    }

    public void globalState(final LinphoneCore lc, final GlobalState state, final String message) {
        Log.i("New global state [", state, "]");
    }

    public void registrationState(final LinphoneCore lc, final LinphoneProxyConfig proxy, final RegistrationState state, final String message) {
        Log.i("New registration state [" + state + "]" + "[" + message + "]");
        if (state == RegistrationState.RegistrationOk) {
//            CallManager.getInstance().reinvite();
        }
        if (state == RegistrationState.RegistrationFailed) {

        }
    }

    private int savedMaxCallWhileGsmIncall;

    private synchronized void preventSIPCalls() {
        if (savedMaxCallWhileGsmIncall != 0) {
            Log.w("SIP calls are already blocked due to GSM call running");
            return;
        }
        savedMaxCallWhileGsmIncall = mLc.getMaxCalls();
        mLc.setMaxCalls(0);
    }

    private synchronized void allowSIPCalls() {
        if (savedMaxCallWhileGsmIncall == 0) {
            Log.w("SIP calls are already allowed as no GSM call known to be running");
            return;
        }
        mLc.setMaxCalls(savedMaxCallWhileGsmIncall);
        savedMaxCallWhileGsmIncall = 0;
    }

    public void setGsmIdle(boolean gsmIdle) {
        if (instance == null) return;
        if (gsmIdle) {
            allowSIPCalls();
        } else {
            preventSIPCalls();
        }
    }

    public Context getContext() {
        try {
            if (LinphoneActivity.isInstanciated())
                return LinphoneActivity.instance();
            else if (mServiceContext != null)
                return mServiceContext;
            else if (LinphoneService.isReady())
                return LinphoneService.instance().getApplicationContext();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressLint("Wakelock")
    public void callState(final LinphoneCore lc, final LinphoneCall call, final State state, final String message) {
        Log.i("New call state [", state, "]");
        if (state == State.IncomingReceived && !call.equals(lc.getCurrentCall())) {
            if (call.getReplacedCall() != null) {
                return;
            }
        }
        Log.w("来电接听执行顺序LinphoneManager");
        if (state == State.IncomingReceived && mR.getBoolean(R.bool.auto_answer_calls)) {
            try {
                mLc.acceptCall(call);
            } catch (LinphoneCoreException e) {
                e.printStackTrace();
            }
        } else if (state == State.IncomingReceived || (state == State.CallIncomingEarlyMedia && mR.getBoolean(R.bool.allow_ringing_while_early_media))) {
            if (mLc.getCallsNb() == 1) {
                BluetoothManager.getInstance().disableBluetoothSCO(); // Just in case
                ringingCall = call;
                startRinging();
            }
        } else if (call == ringingCall && isRinging) {
            //previous state was ringing, so stop ringing
            stopRinging();
        }

        if (state == state.OutgoingInit) {
            outgoingCall = call;
            startRingback();
        } else if (state == State.Connected) {
            if (call == outgoingCall) {
                stopRingback();
            }
            if (mLc.getCallsNb() == 1) {
                requestAudioFocus();
                Compatibility.setAudioManagerInCallMode(mAudioManager);
            }

            if (Hacks.needSoftvolume()) {
                Log.w("Using soft volume audio hack");
                adjustVolume(0); // Synchronize
            }
        } else if (state == State.OutgoingEarlyMedia) {
//            Compatibility.setAudioManagerInCallMode(mAudioManager);
        }

//		if (state == State.CallReleased || state == State.Error) {
        else if (state == State.CallEnd || state == State.Error) {  // 在拨出电话立即结束时只有CallEnd， 不会有CallReleased，其他情况都会有CallReleased，拨打出错时会依次有Error、CallReleased，没有CallEnd
            if (call == outgoingCall) {
                stopRingback();
            }
            if (mLc.getCallsNb() == 0) {
                if (mAudioFocused) {
                    int res = mAudioManager.abandonAudioFocus(null);
                    Log.d("Audio focus released a bit later: " + (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED ? "Granted" : "Denied"));
                    mAudioFocused = false;
                }

                Context activity = getContext();
                if (activity != null) {
                    TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
                    if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
                        Log.d("---AudioManager: back to MODE_NORMAL");
                        mAudioManager.setMode(AudioManager.MODE_NORMAL);
                        Log.d("All call terminated, routing back to earpiece");
                        routeAudioToReceiver();
                    }
                }
            }
            callMessageUtil.addCallHistory(call);
            new GetBell(mServiceContext).getBell();
        }

        if (state == State.CallEnd) {
            if (mLc.getCallsNb() == 0) {
                if (mIncallWakeLock != null && mIncallWakeLock.isHeld()) {
                    mIncallWakeLock.release();
                    Log.i("Last call ended: releasing incall (CPU only) wake lock");
                } else {
                    Log.i("Last call ended: no incall (CPU only) wake lock were held");
                }
            }
        }

        if (state == State.StreamsRunning) {
            if (BluetoothManager.getInstance().isBluetoothHeadsetAvailable()) {
                BluetoothManager.getInstance().routeAudioToBluetooth();
                // Hack to ensure the bluetooth route is really used
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BluetoothManager.getInstance().routeAudioToBluetooth();
                    }
                }, 500);
            }

            if (mIncallWakeLock == null) {
                mIncallWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "incall");
            }
            if (!mIncallWakeLock.isHeld()) {
                Log.i("New call active : acquiring incall (CPU only) wake lock");
                mIncallWakeLock.acquire();
            } else {
                Log.i("New call active while incall (CPU only) wake lock already active");
            }
        }

    }

    public void callStatsUpdated(final LinphoneCore lc, final LinphoneCall call, final LinphoneCallStats stats) {
    }

    public void callEncryptionChanged(LinphoneCore lc, LinphoneCall call,
                                      boolean encrypted, String authenticationToken) {
    }

    private boolean isRinging;
    private boolean disableRinging = false;

    private void disableRinging() {
        disableRinging = true;
    }

    private void requestAudioFocus() {
        if (!mAudioFocused) {
            int res = mAudioManager.requestAudioFocus(null, AudioManager.STREAM_VOICE_CALL, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            Log.d("Audio focus requested: " + (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED ? "Granted" : "Denied"));
            if (res == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) mAudioFocused = true;
        }
    }

    private synchronized void startRinging() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ToastUtils.showToast(getContext(), getString(R.string.str_recording_rights));
            return;
        }
        if (disableRinging) {
            routeAudioToSpeaker();
            return;
        }
        if (mR.getBoolean(R.bool.allow_ringing_while_early_media)) {
            routeAudioToSpeaker(); // Need to be able to ear the ringtone during the early media
        }

        if (Hacks.needGalaxySAudioHack()) {
            mAudioManager.setMode(AudioManager.MODE_RINGTONE);
        }

        try {
            if ((mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE || mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) && mVibrator != null) {
                long[] patern = {0, 1000, 1000};
                mVibrator.vibrate(patern, 1);
            }
            if (mRingerPlayer == null) {
                requestAudioFocus();
                mRingerPlayer = new MediaPlayer();
                mRingerPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                String ringtone = LinphonePreferences.instance().getRingtone(android.provider.Settings.System.DEFAULT_RINGTONE_URI.toString());
                try {
                    if (ringtone.startsWith("content://")) {
                        mRingerPlayer.setDataSource(mServiceContext, Uri.parse(ringtone));
                    } else {
                        FileInputStream fis = new FileInputStream(ringtone);
                        mRingerPlayer.setDataSource(fis.getFD());
                        fis.close();
                    }

                } catch (IOException e) {
                    Log.e(e, "Cannot set ringtone");
                }
                try {
                    mRingerPlayer.prepare();
                    mRingerPlayer.setLooping(true);
                    mRingerPlayer.start();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }

            } else {
                Log.w("already ringing");
            }
        } catch (Exception e) {
            Log.e(e, "cannot handle incoming call");
        }
        isRinging = true;
    }

    private synchronized void stopRinging() {
        if (mRingerPlayer != null) {
            mRingerPlayer.stop();
            mRingerPlayer.release();
            mRingerPlayer = null;
        }
        if (mVibrator != null) {
            mVibrator.cancel();
        }

        if (Hacks.needGalaxySAudioHack())
            mAudioManager.setMode(AudioManager.MODE_NORMAL);

        isRinging = false;
        // You may need to call galaxys audio hack after this method
        if (!BluetoothManager.getInstance().isBluetoothHeadsetAvailable()) {
            if (mServiceContext.getResources().getBoolean(R.bool.isTablet)) {
                Log.d("Stopped ringing, routing back to speaker");
                routeAudioToSpeaker();
            } else {
                Log.d("Stopped ringing, routing back to earpiece");
                routeAudioToReceiver();
            }
        }
    }

    private synchronized void startRingback() {
        if (mRingerPlayer == null) {
            try {
                Compatibility.setAudioManagerInCallMode(mAudioManager);
                mRingerPlayer = new MediaPlayer();
                mRingerPlayer.reset();
                mRingerPlayer.setDataSource(mRingbackSoundFile);
                mRingerPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
                mRingerPlayer.prepare();
                mRingerPlayer.setLooping(false);
                mRingerPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized void stopRingback() {
        if (mRingerPlayer != null) {
            mRingerPlayer.stop();
            mRingerPlayer.release();
            mRingerPlayer = null;
        }
    }

    private boolean reInviteWithVideo() {
        return CallManager.getInstance().reinviteWithVideo();
    }

    /**
     * @return false if already in video call.
     */
    public boolean addVideo() {
        LinphoneCall call = mLc.getCurrentCall();
        enableCamera(call, true);
        return reInviteWithVideo();
    }

    public boolean acceptCallIfIncomingPending() throws LinphoneCoreException {
        if (mLc.isInComingInvitePending()) {
            mLc.acceptCall(mLc.getCurrentCall());
            return true;
        }
        return false;
    }

    public boolean acceptCallWithParams(LinphoneCall call, LinphoneCallParams params) {
        try {
            mLc.acceptCallWithParams(call, params);
            return true;
        } catch (LinphoneCoreException e) {
            Log.i(e, "Accept call failed");
        }
        return false;
    }

    public void adjustVolume(int i) {
        if (Build.VERSION.SDK_INT < 15) {
            int oldVolume = mAudioManager.getStreamVolume(LINPHONE_VOLUME_STREAM);
            int maxVolume = mAudioManager.getStreamMaxVolume(LINPHONE_VOLUME_STREAM);

            int nextVolume = oldVolume + i;
            if (nextVolume > maxVolume) nextVolume = maxVolume;
            if (nextVolume < 0) nextVolume = 0;

            mLc.setPlaybackGain((nextVolume - maxVolume) * dbStep);
        } else
            // starting from ICS, volume must be adjusted by the application, at least for STREAM_VOICE_CALL volume stream
            mAudioManager.adjustStreamVolume(LINPHONE_VOLUME_STREAM, i < 0 ? AudioManager.ADJUST_LOWER : AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }

    private Boolean isProximitySensorNearby(final SensorEvent event) {
        float threshold = 4.001f; // <= 4 cm is near

        final float distanceInCm = event.values[0];
        final float maxDistance = event.sensor.getMaximumRange();
        Log.d("Proximity sensor report [", distanceInCm, "] , for max range [", maxDistance, "]");

        if (maxDistance <= threshold) {
            // Case binary 0/1 and short sensors
            threshold = maxDistance;
        }

        return distanceInCm < threshold;
    }

    private static boolean sLastProximitySensorValueNearby;
    private static Set<Activity> sProximityDependentActivities = new HashSet<>();
    private SensorEventListener sProximitySensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.timestamp == 0) return; //just ignoring for nexus 1
            sLastProximitySensorValueNearby = isProximitySensorNearby(event);
            proximityNearbyChanged();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };


    private static void simulateProximitySensorNearby(Activity activity, boolean nearby) {
        if (wakeLock == null) {
            final Window window = activity.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            View view = new View(activity);
            view.setBackgroundColor(activity.getResources().getColor(R.color.black));
            if (nearby) {
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                ((ViewGroup) window.getDecorView()).addView(view, 1);
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF;
                Compatibility.hideNavigationBar(activity);
            } else {
                if (((ViewGroup) window.getDecorView()).getChildCount() == 2) {
                    ((ViewGroup) window.getDecorView()).removeViewAt(1);
                    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
                params.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
                Compatibility.showNavigationBar(activity);
            }
            window.setAttributes(params);
        }
    }

    private void proximityNearbyChanged() {
        boolean nearby = sLastProximitySensorValueNearby;
        for (Activity activity : sProximityDependentActivities) {
            simulateProximitySensorNearby(activity, nearby);
        }
    }

    public synchronized void startProximitySensorForActivity(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
            if (pm.isWakeLockLevelSupported(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK)) {
                wakeLock = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "PROXIMITY_SCREEN_OFF_WAKE_LOCK");
                wakeLock.acquire();
            }
        }
        if (sProximityDependentActivities.contains(activity)) {
            Log.i("proximity sensor already active for " + activity.getLocalClassName());
            return;
        }

        if (sProximityDependentActivities.isEmpty()) {
            SensorManager sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
            Sensor s = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            if (s != null) {
                sm.registerListener(sProximitySensorListener, s, SensorManager.SENSOR_DELAY_UI);
                Log.i("Proximity sensor detected, registering");
            }
        } else if (sLastProximitySensorValueNearby) {
            simulateProximitySensorNearby(activity, true);
        }

        sProximityDependentActivities.add(activity);
    }

    public synchronized void stopProximitySensorForActivity(Activity activity) {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
        sProximityDependentActivities.remove(activity);
        simulateProximitySensorNearby(activity, false);
        if (sProximityDependentActivities.isEmpty()) {
            SensorManager sm = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
            sm.unregisterListener(sProximitySensorListener);
            sLastProximitySensorValueNearby = false;
        }
    }

    public synchronized LinphoneCore getLcIfManagerNotDestroyedOrNull() {
        if (sExited || instance == null) {
            // Can occur if the UI thread play a posted event but in the meantime the LinphoneManager was destroyed
            // Ex: stop call and quickly terminate application.
            Log.w("Trying to get linphone core while LinphoneManager already destroyed or not created");
            return null;
        }
        return mLc;
    }

    public static final boolean isInstanciated() {
        return instance != null;
    }

    @Override
    public void notifyReceived(LinphoneCore lc, LinphoneCall call,
                               LinphoneAddress from, byte[] event) {
    }

    @Override
    public void transferState(LinphoneCore lc, LinphoneCall call,
                              State new_call_state) {

    }

    @Override
    public void infoReceived(LinphoneCore lc, LinphoneCall call, LinphoneInfoMessage info) {
        Log.d("Info message received from " + call.getRemoteAddress().asString());
        LinphoneContent ct = info.getContent();
        if (ct != null) {
            Log.d("Info received with body with mime type " + ct.getType() + "/" + ct.getSubtype() + " and data [" + ct.getDataAsString() + "]");
        }
    }

    @Override
    public void subscriptionStateChanged(LinphoneCore lc, LinphoneEvent ev,
                                         SubscriptionState state) {
        Log.d("Subscription state changed to " + state + " event name is " + ev.getEventName());
    }

    @Override
    public void notifyReceived(LinphoneCore lc, LinphoneEvent ev,
                               String eventName, LinphoneContent content) {
        Log.d("Notify received for event " + eventName);
        if (content != null)
            Log.d("with content " + content.getType() + "/" + content.getSubtype() + " data:" + content.getDataAsString());
    }

    @Override
    public void publishStateChanged(LinphoneCore lc, LinphoneEvent ev,
                                    PublishState state) {
        Log.d("Publish state changed to " + state + " for event name " + ev.getEventName());
    }

    @Override
    public void isComposingReceived(LinphoneCore lc, LinphoneChatRoom cr) {
        Log.d("Composing received for chatroom " + cr.getPeerAddress().asStringUriOnly());
    }

    @Override
    public void configuringStatus(LinphoneCore lc,
                                  RemoteProvisioningState state, String message) {
        Log.d("Remote provisioning status = " + state.toString() + " (" + message + ")");

        if (state == RemoteProvisioningState.ConfiguringSuccessful) {
            if (LinphonePreferences.instance().isProvisioningLoginViewEnabled()) {
                LinphoneProxyConfig proxyConfig = lc.createProxyConfig();
                try {
                    LinphoneAddress addr = LinphoneCoreFactory.instance().createLinphoneAddress(proxyConfig.getIdentity());
                    wizardLoginViewDomain = addr.getDomain();
                } catch (LinphoneCoreException e) {
                    wizardLoginViewDomain = null;
                }
            }
        }
    }

    @Override
    public void fileTransferProgressIndication(LinphoneCore lc,
                                               LinphoneChatMessage message, LinphoneContent content, int progress) {

    }

    @Override
    public void fileTransferRecv(LinphoneCore lc, LinphoneChatMessage message,
                                 LinphoneContent content, byte[] buffer, int size) {

    }

    @Override
    public int fileTransferSend(LinphoneCore lc, LinphoneChatMessage message,
                                LinphoneContent content, ByteBuffer buffer, int size) {
        return 0;
    }

    @Override
    public void uploadProgressIndication(LinphoneCore linphoneCore, int offset, int total) {
        if (total > 0)
            Log.d("Log upload progress: currently uploaded = " + offset + " , total = " + total + ", % = " + String.valueOf((offset * 100) / total));
    }

    @Override
    public void uploadStateChanged(LinphoneCore linphoneCore, LogCollectionUploadState state, String info) {
        Log.d("Log upload state: " + state.toString() + ", info = " + info);

        if (state == LogCollectionUploadState.LogCollectionUploadStateDelivered) {
            LinphoneActivity.instance().sendLogs(LinphoneService.instance().getApplicationContext(), info);
        }
    }

    @Override
    public void ecCalibrationStatus(LinphoneCore lc, EcCalibratorStatus status,
                                    int delay_ms, Object data) {
        // TODO Auto-generated method stub

    }

    public String getUserId() {
        if (userId == null) {
            userId = mLc.getConfig().getString("misc", "userid", null);
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
        mLc.getConfig().setString("misc", "userid", userId);
    }

    public String getSessionId() {
        if (sessionId == null) {
            sessionId = mLc.getConfig().getString("misc", "sessionid", null);
        }
        return sessionId;
    }

    private void setSessionId(String sessionId) {
        this.sessionId = sessionId;
        mLc.getConfig().setString("misc", "sessionid", sessionId);
    }

    public void setUserSession(String userId, String sessionId) {
        setUserId(userId);
        setSessionId(sessionId);
    }

    public void addLinCoreListener(LinphoneCoreListenerBase listener) {
        mLc.addListener(listener);
    }

    public void removeLinCoreListener(LinphoneCoreListenerBase listener) {
        mLc.removeListener(listener);
    }

    public static boolean isServiceWorked(Context context, String serviceName) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager.getRunningServices(Integer.MAX_VALUE);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().equals(serviceName)) {
                return true;
            }
        }
        return false;
    }
}
