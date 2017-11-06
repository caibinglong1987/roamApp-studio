package com.roamtech.telephony.roamapp.activity;


import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.helper.KeyboardCallingHelper;
import com.roamtech.telephony.roamapp.helper.KeyboardCallingHelper.OnKeyboardListener;
import com.roamtech.telephony.roamapp.util.CallTime;
import com.roamtech.telephony.roamapp.util.CallUtil;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.view.RippleBackground;
import com.roamtech.telephony.roamapp.view.RoundImageView;

import org.linphone.BluetoothManager;
import org.linphone.Contact;
import org.linphone.ContactsManager;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneUtils;
import org.linphone.core.CallDirection;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallLog;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.core.LinphonePlayer;
import org.linphone.mediastream.Version;


public class CallingActivity extends BaseActivity {
    private RippleBackground mRippleBackground;
    private final String TAG = "InCall";
    private TextView tvSavevoiceTime;
    private RoundImageView headPhoto;
    private TextView tvUsername;
    private TextView tvCallingstate;
    private CallTime recordTime;
    private CallTime callTime;

    private TextView tvCallNumber;
    private TextView tvSaveVoice;
    private TextView tvHandFree;
    private TextView tvMute;
    private TextView tvHideKeybord;
    private LinearLayout mLayoutCallingHand;
    private KeyboardCallingHelper mKeybordCallingHelperHelper;
    private GridView mGvKeybord;
    private ImageView btnHangup;
    private boolean isSpeakerEnabled = false, isMicMuted = false, isRecording = false;
    private LinphoneCoreListenerBase mListener;
    private TableLayout callsList;
    private LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (km.inKeyguardRestrictedInputMode()) {
            // 处于锁屏状态
            final Window win = getWindow();
            win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        setContentView(R.layout.activity_calling);
        isSpeakerEnabled = LinphoneManager.getInstance().getLcIfManagerNotDestroyedOrNull().isSpeakerEnabled();

        if (Version.sdkAboveOrEqual(Version.API11_HONEYCOMB_30)) {
            if (!BluetoothManager.getInstance().isBluetoothHeadsetAvailable()) {
                BluetoothManager.getInstance().initBluetooth();
            } else {
                isSpeakerEnabled = false;
            }
        }
        mListener = new LinphoneCoreListenerBase() {
            @Override
            public void callState(LinphoneCore lc, final LinphoneCall call, LinphoneCall.State state, String message) {
                if (LinphoneManager.getLc().getCallsNb() == 0 && !isFinishing()) {
                    finish();
                    return;
                }

                if (state == State.IncomingReceived) {
                    startIncomingCallActivity();
                    return;
                }

                if (state == State.StreamsRunning) {
//                    LinphoneManager.startProximitySensorForActivity(CallingActivity.this);
                    LinphoneManager.getLc().enableSpeaker(isSpeakerEnabled);
                    isMicMuted = LinphoneManager.getLc().isMicMuted();
                    enableAndRefreshInCallActions();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        refreshInCallActions();
                        refreshCallList(getResources());
                    }
                });

            }

            @Override
            public void callEncryptionChanged(LinphoneCore lc, final LinphoneCall call, boolean encrypted, String authenticationToken) {

            }

        };
        LinphoneManager.getInstance().changeStatusToOnThePhone();
        if (savedInstanceState != null) {
            // Fragment already created, no need to create it again (else it will generate a memory leak with duplicated fragments)
            isSpeakerEnabled = savedInstanceState.getBoolean("Speaker");
            isMicMuted = savedInstanceState.getBoolean("Mic");
            refreshInCallActions();
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        mRippleBackground = (RippleBackground) findViewById(R.id.layoutCalling);
        mRippleBackground.startRippleAnimation();
        tvSavevoiceTime = (TextView) findViewById(R.id.tv_savevoicetime);
        //tvSavevoiceTime.setText("00:03正在通话录音中...");
        recordTime = new CallTime(new CallTime.OnTickListener() {
            @Override
            public void onTickForCallTimeElapsed(long paramLong) {
                tvSavevoiceTime.setText(String.format(getString(R.string.now_call_recording), getTimeString(paramLong)));
            }
        });
        headPhoto = (RoundImageView) findViewById(R.id.id_circle_image);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvCallingstate = (TextView) findViewById(R.id.tv_callingstate);
        callTime = new CallTime(new CallTime.OnTickListener() {
            @Override
            public void onTickForCallTimeElapsed(long paramLong) {
                tvCallingstate.setText(getTimeString(paramLong));
            }
        });
        tvCallNumber = (TextView) findViewById(R.id.tv_callnumber);
        tvSaveVoice = (TextView) findViewById(R.id.tv_savevoice);
        tvHandFree = (TextView) findViewById(R.id.tv_handfree);
        tvMute = (TextView) findViewById(R.id.tv_mute);
        tvHideKeybord = (TextView) findViewById(R.id.tv_hidekeybord);
        mLayoutCallingHand = (LinearLayout) findViewById(R.id.ll_callinghand);
        btnHangup = (ImageView) findViewById(R.id.btnhangup);
        mGvKeybord = (GridView) findViewById(R.id.gv_keyboard);
        mKeybordCallingHelperHelper = new KeyboardCallingHelper(this);
        mKeybordCallingHelperHelper.setOnKeyboardListener(new OnKeyboardListener() {
                    @Override
                    public void onTextChange(String inputText) {
                        //if (!linphoneServiceReady()) return;
                        LinphoneCore lc = LinphoneManager.getLc();
                        if (lc.isIncall()) {
                            lc.sendDtmf(inputText.charAt(0));
                        }
                    }

                    @Override
                    public void onCall(String inputText) {
                        // TODO Auto-generated method stub
                    }
                });
        inflater = LayoutInflater.from(this);
        callsList = (TableLayout) findViewById(R.id.calls);
    }

    private void showCallsList(boolean show) {
        if (show) {
            mRippleBackground.stopRippleAnimation();
            callsList.setVisibility(View.VISIBLE);
            headPhoto.setVisibility(View.GONE);
            tvUsername.setVisibility(View.GONE);
            tvCallingstate.setVisibility(View.GONE);
            callTime.cancelTimer();
            tvSavevoiceTime.setVisibility(View.GONE);
            recordTime.cancelTimer();
        } else {
            callsList.setVisibility(View.GONE);
            headPhoto.setVisibility(View.VISIBLE);
            tvUsername.setVisibility(View.VISIBLE);
            tvCallingstate.setVisibility(View.VISIBLE);
            if (tvSaveVoice.isSelected()) {
                recordTime.setActiveCallMode(System.currentTimeMillis());
                recordTime.reset();
                recordTime.periodicUpdateTimer();
                tvSavevoiceTime.setVisibility(View.VISIBLE);
            } else {
                recordTime.cancelTimer();
                tvSavevoiceTime.setVisibility(View.GONE);
            }
            registerCallDurationTimer(LinphoneManager.getLc().getCalls()[0]);
            mRippleBackground.startRippleAnimation();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("Speaker", LinphoneManager.getLc().isSpeakerEnabled());
        outState.putBoolean("Mic", LinphoneManager.getLc().isMicMuted());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        tvCallNumber.setOnClickListener(this);
        tvSaveVoice.setOnClickListener(this);
        tvHandFree.setOnClickListener(this);
        tvMute.setOnClickListener(this);
        tvHideKeybord.setOnClickListener(this);
        btnHangup.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == tvCallNumber) {
            tvCallNumber.setSelected(true);
            mLayoutCallingHand.setVisibility(View.GONE);
            mGvKeybord.setVisibility(View.VISIBLE);
            tvHideKeybord.setVisibility(View.VISIBLE);
        } else if (v == tvSaveVoice) {
            tvSaveVoice.setSelected(!tvSaveVoice.isSelected());
            if (tvSaveVoice.isSelected()) {
                recordTime.setActiveCallMode(System.currentTimeMillis());
                recordTime.reset();
                recordTime.periodicUpdateTimer();
                tvSavevoiceTime.setVisibility(View.VISIBLE);
            } else {
                recordTime.cancelTimer();
                tvSavevoiceTime.setVisibility(View.GONE);
            }
            isRecording = !isRecording;
            if (isRecording) {
                LinphoneManager.getLc().getCurrentCall().startRecording();
            } else {
                LinphoneManager.getLc().getCurrentCall().stopRecording();
            }
        } else if (v == tvHandFree) {
            toggleSpeaker();
        } else if (v == tvMute) {
            toggleMicro();
        } else if (v == tvHideKeybord) {
            tvCallNumber.setSelected(false);
            mLayoutCallingHand.setVisibility(View.VISIBLE);
            mGvKeybord.setVisibility(View.GONE);
            tvHideKeybord.setVisibility(View.GONE);

        } else if (v == btnHangup) {
            hangUp();
        } else if (v.getId() == R.id.callStatus) {
            LinphoneCall call = (LinphoneCall) v.getTag();
            pauseOrResumeCall(call);
        }
    }

    private void enableAndRefreshInCallActions() {
        //addCall.setEnabled(LinphoneManager.getLc().getCallsNb() < LinphoneManager.getLc().getMaxCalls());
        //transfer.setEnabled(getResources().getBoolean(R.bool.allow_transfers));
        //options.setEnabled(!getResources().getBoolean(R.bool.disable_options_in_call) && (addCall.isEnabled() || transfer.isEnabled()));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvMute.setEnabled(true);
                tvHandFree.setEnabled(true);
                tvCallNumber.setEnabled(true);
                refreshInCallActions();
            }
        });
    }

    private void refreshInCallActions() {
        tvHandFree.setSelected(isSpeakerEnabled);
        tvMute.setSelected(isMicMuted);
    }

    public void refreshCallList(Resources resources) {
        int index = 0;
        callsList.removeAllViews();
        if (LinphoneManager.getLc().getCallsNb() == 0) {
            goBackToDialer();
            return;
        }

        boolean bRinging = false;
        LinphoneCall[] calls = LinphoneManager.getLc().getCalls();
        for (LinphoneCall call : calls) {
            if (call.getState() == State.IncomingReceived) {
                bRinging = true;
                break;
            }
        }
        if (bRinging) {
            startIncomingCallActivity();
            return;
        }
        for (LinphoneCall call : calls) {
            displayCall(resources, call, index);
            index++;
        }

    }

    private void displayCall(Resources resources, LinphoneCall call, int index) {
        final LinphoneCallLog log = call.getCallLog();
        LinphoneAddress lAddress;
        if (log.getDirection() == CallDirection.Incoming) {
            String from = CallUtil.getRealToNumber(log.getFrom().asStringUriOnly(), log.getTo().asStringUriOnly(), "from");
            lAddress = LinphoneCoreFactory.instance().createLinphoneAddress(from, LinphoneManager.getLc().getDefaultProxyConfig().getDomain(), "");
        } else {
            String to = CallUtil.getRealToNumber(log.getTo().asStringUriOnly());
            lAddress = LinphoneCoreFactory.instance().createLinphoneAddress(to, LinphoneManager.getLc().getDefaultProxyConfig().getDomain(), "");
        }
        if (LinphoneManager.getLc().getCallsNb() > 1) {
            showCallsList(true);
            // Control Row
            LinearLayout callView = (LinearLayout) inflater.inflate(R.layout.active_call_control_row, mRippleBackground, false);
            callView.setId(index + 1);
            setContactName(callView, lAddress, lAddress.asStringUriOnly(), resources);
            displayCallStatusIconAndReturnCallPaused(callView, call);
            registerCallDurationTimer(callView, call);
            callsList.addView(callView);

            // Image Row
            RoundImageView photoView = (RoundImageView) callView.findViewById(R.id.id_circle_image);
            Contact contact = ContactsManager.getInstance().findContactWithAddress(getContentResolver(), lAddress);
            if (contact != null) {
                displayOrHideContactPicture(photoView, contact.getPhotoUri(), contact.getThumbnailUri(), false);
            } else {
                displayOrHideContactPicture(photoView, null, null, false);
            }
            callView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getTag() != null) {
                        View imageView = (View) v.getTag();
                        if (imageView.getVisibility() == View.VISIBLE)
                            imageView.setVisibility(View.GONE);
                        else
                            imageView.setVisibility(View.VISIBLE);
                        callsList.invalidate();
                    }
                }
            });
        } else {
            showCallsList(false);
            setContactName(lAddress, lAddress.asStringUriOnly(), resources);
            if (call.getState() == State.Paused || call.getState() == State.PausedByRemote || call.getState() == State.Pausing) {
                LinphoneManager.getLc().resumeCall(call);
            }
            registerCallDurationTimer(call);
        }
    }

    private boolean displayCallStatusIconAndReturnCallPaused(LinearLayout callView, LinphoneCall call) {
        boolean isCallPaused, isInConference;
        ImageView callState = (ImageView) callView.findViewById(R.id.callStatus);
        TextView tvCallState = (TextView) callView.findViewById(R.id.tvCallStatus);
        callState.setTag(call);
        callState.setOnClickListener(this);

        if (call.getState() == State.Paused || call.getState() == State.PausedByRemote || call.getState() == State.Pausing) {
            callState.setImageResource(R.drawable.ic_play);
            tvCallState.setText(R.string.holding);
            isCallPaused = true;
            isInConference = false;
        } else if (call.getState() == State.OutgoingInit || call.getState() == State.OutgoingProgress || call.getState() == State.OutgoingRinging) {
            callState.setImageResource(R.drawable.call_state_ringing_default);
            tvCallState.setText(R.string.ringing);
            isCallPaused = false;
            isInConference = false;
        } else {
            /*if (isConferenceRunning && call.isInConference()) {
                callState.setImageResource(R.drawable.remove);
				isInConference = true;
			} else*/
            {
                tvCallState.setTextColor(getResources().getColor(R.color.green));
                tvCallState.setText(R.string.talking);
                callState.setImageResource(R.drawable.ic_hold);
                isInConference = false;
            }
            isCallPaused = false;
        }

        return isCallPaused || isInConference;
    }

    private void displayOrHideContactPicture(RoundImageView photoView, Uri pictureUri, Uri thumbnailUri, boolean hide) {
        if (pictureUri != null) {
            LinphoneUtils.setImagePictureFromUri(this, photoView, Uri.parse(pictureUri.toString()), thumbnailUri, R.drawable.photo_bg_xl);
        }
    }

    private void setContactName(LinearLayout callView, LinphoneAddress lAddress, String sipUri, Resources resources) {
        TextView contact = (TextView) callView.findViewById(R.id.contactNameOrNumber);
        Contact lContact = ContactsManager.getInstance().findContactWithAddress(callView.getContext().getContentResolver(), lAddress);
        if (lContact == null) {
            if (resources.getBoolean(R.bool.only_display_username_if_unknown) && LinphoneUtils.isSipAddress(sipUri)) {
                contact.setText(lAddress.getUserName());
            } else {
                contact.setText(sipUri);
            }
        } else {
            contact.setText(lContact.getName());
        }
    }

    private void registerCallDurationTimer(View v, LinphoneCall call) {
        int callDuration = call.getDuration();
        if (callDuration == 0 && call.getState() != State.StreamsRunning) {
            return;
        }

        Chronometer timer = (Chronometer) v.findViewById(R.id.callTimer);
        if (timer == null) {
            throw new IllegalArgumentException("no callee_duration view found");
        }

        timer.setBase(SystemClock.elapsedRealtime() - 1000 * callDuration);
        timer.start();
    }

    private void setContactName(LinphoneAddress lAddress, String sipUri, Resources resources) {
        TextView contact = tvUsername;
        Contact lContact = ContactsManager.getInstance().findContactWithAddress(getContentResolver(), lAddress);
        if (lContact == null) {
            if (resources.getBoolean(R.bool.only_display_username_if_unknown) && LinphoneUtils.isSipAddress(sipUri)) {
                if (lAddress.getUserName().equals(getString(R.string.roamPhone))) {
                    contact.setText(getString(R.string.roamservice));
                } else {
                    if (lAddress.getUserName().equals("unknown")) {
                        contact.setText(getString(R.string.str_unknown_number));
                    } else {
                        contact.setText(lAddress.getUserName());
                    }
                }
            } else {
                contact.setText(sipUri);
            }
            if (lAddress.getUserName().equals(getString(R.string.roamPhone))) {
                LinphoneUtils.setImagePictureFromUri(this, headPhoto, null, null, R.drawable.ic_service);
            } else {
                displayOrHideContactPicture(headPhoto, null, null, false);
            }
        } else {
            contact.setText(lContact.getName());
            displayOrHideContactPicture(headPhoto, lContact.getPhotoUri(), lContact.getThumbnailUri(), false);
        }
    }

    public void goBackToDialer() {
        Intent intent = new Intent();
        intent.putExtra("Transfer", false);
        setResult(Activity.RESULT_FIRST_USER, intent);
        finish();
    }

    private void registerCallDurationTimer(LinphoneCall call) {
        int callDuration = call.getDuration();
        if (callDuration == 0 && call.getState() != State.StreamsRunning) {
            return;
        }
        callTime.setActiveCallMode(System.currentTimeMillis() - 1000 * callDuration);
        callTime.reset();
        callTime.periodicUpdateTimer();
    }

    private void toggleMicro() {
        LinphoneCore lc = LinphoneManager.getLc();
        isMicMuted = !isMicMuted;
        lc.muteMic(isMicMuted);
        tvMute.setSelected(isMicMuted);
    }

    private void toggleSpeaker() {
        isSpeakerEnabled = !isSpeakerEnabled;
        if (isSpeakerEnabled) {
            LinphoneManager.getInstance().routeAudioToSpeaker();
            LinphoneManager.getLc().enableSpeaker(isSpeakerEnabled);
        } else {
            //Log.d("Toggle speaker off, routing back to earpiece");
            LinphoneManager.getInstance().routeAudioToReceiver();
        }
        tvHandFree.setSelected(isSpeakerEnabled);
    }

    /**
     * 挂断电话事件
     */
    private void hangUp() {
        LinphoneCore lc = LinphoneManager.getLc();
        LinphoneCall currentCall = lc.getCurrentCall();
        boolean isNeedFinish = LinphoneManager.getLc().getCallsNb() == 1;
        if (currentCall != null) {
            lc.terminateCall(currentCall);
        } else if (lc.isInConference()) {
            lc.terminateConference();
        } else {
            lc.terminateAllCalls();
        }
        callTime.cancelTimer();
        if (isNeedFinish && !isFinishing()) {
            finish();
        }
    }

    public void pauseOrResumeCall(LinphoneCall call) {
        LinphoneCore lc = LinphoneManager.getLc();
        if (call != null && LinphoneUtils.isCallRunning(call)) {
            if (call.isInConference()) {
                lc.removeFromConference(call);
                if (lc.getConferenceSize() <= 1) {
                    lc.leaveConference();
                }
            } else {
                lc.pauseCall(call);
                //isMicMuted = true;
                //tvMute.setSelected(isMicMuted);
            }
        } else if (call != null) {
            if (call.getState() == State.Paused) {
                lc.resumeCall(call);
                //isMicMuted = false;
                //tvMute.setSelected(isMicMuted);
            }
        }
    }

    private boolean isVideoEnabled(LinphoneCall call) {
        if (call != null) {
            return call.getCurrentParamsCopy().getVideoEnabled();
        }
        return false;
    }

    @Override
    protected void onResume() {
        //instance = this;
        super.onResume();
        LinphoneCore lc = LinphoneManager.getInstance().getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.addListener(mListener);
        }
        refreshCallList(getResources());

        handleViewIntent();
        LinphoneManager.getInstance().startProximitySensorForActivity(this);
    }

    public void startIncomingCallActivity() {
        startActivity(new Intent(this, CallAnswerActivity.class));
    }

    private void handleViewIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.getAction() == "android.intent.action.VIEW") {
            LinphoneCall call = LinphoneManager.getLc().getCurrentCall();
            if (call != null && isVideoEnabled(call)) {
                LinphonePlayer player = call.getPlayer();
                String path = intent.getData().getPath();
                Log.i(TAG, "Openning " + path);
                int openRes = player.open(path, new LinphonePlayer.Listener() {

                    @Override
                    public void endOfFile(LinphonePlayer player) {
                        player.close();
                    }
                });
                if (openRes == -1) {
                    String message = "Could not open " + path;
                    Log.e(TAG, message);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i(TAG, "Start playing");
                if (player.start() == -1) {
                    player.close();
                    String message = "Could not start playing " + path;
                    Log.e(TAG, message);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        LinphoneCore lc = LinphoneManager.getInstance().getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.removeListener(mListener);
        }
        super.onPause();

        if (!isVideoEnabled(LinphoneManager.getLc().getCurrentCall())) {
            LinphoneManager.getInstance().stopProximitySensorForActivity(this);
        }
    }

    @Override
    protected void onDestroy() {
        LinphoneManager.getInstance().changeStatusToOnline();
        mRippleBackground.stopRippleAnimation();
        callTime.cancelTimer();
        super.onDestroy();
        System.gc();
    }

    private String getTimeString(long paramLong) {
        String str = DateUtils.formatElapsedTime(paramLong);
        if (str.length() == 4) {
            str = "0" + str;
        }
        return str;
    }
}
