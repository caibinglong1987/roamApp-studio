package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.dialog.MessageOptionDialog.OnQuickOptionformClick;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.util.CallUtil;
import com.roamtech.telephony.roamapp.view.RippleBackground;
import com.roamtech.telephony.roamapp.view.RoundImageView;

import org.linphone.Contact;
import org.linphone.ContactsManager;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneUtils;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCall.State;
import org.linphone.core.LinphoneCallParams;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneCoreListenerBase;
import org.linphone.mediastream.Log;

import java.util.List;


public class CallAnswerActivity extends BaseActivity {
    private RippleBackground mRippleBackground;
    private RoundImageView headPhoto;
    private TextView tvUsername;
    private TextView tvCallingstate;
    private ImageView ivHandupmessage;
    private ImageView ivHangup;
    private ImageView ivCallAnswer;
    // private static CallAnswerActivity instance;
    private LinphoneCall mCall;
    private LinphoneCoreListenerBase mListener;
    private CallMessageUtil callMessageUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming);
        // set this flag so this activity will stay in front of the keyguard
        int flags = WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().addFlags(flags);
        mListener = new LinphoneCoreListenerBase() {
            @Override
            public void callState(LinphoneCore lc, LinphoneCall call, LinphoneCall.State state, String message) {
                if (call == mCall && State.CallEnd == state) {
                    finish();
                }
                if (state == State.StreamsRunning) {
                    // The following should not be needed except some devices need it (e.g. Galaxy S).
                    LinphoneManager.getLinCore(getApplicationContext()).enableSpeaker(LinphoneManager.getLinCore(getApplicationContext()).isSpeakerEnabled());
                }
            }
        };
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        mRippleBackground = (RippleBackground) findViewById(R.id.layoutCalling);
        mRippleBackground.startRippleAnimation();
        headPhoto = (RoundImageView) findViewById(R.id.id_circle_image);
        tvUsername = (TextView) findViewById(R.id.tv_username);
        tvCallingstate = (TextView) findViewById(R.id.tv_callingstate);
        ivHangup = (ImageView) findViewById(R.id.ivhangup);
        ivCallAnswer = (ImageView) findViewById(R.id.ivaccept);
        ivHandupmessage = (ImageView) findViewById(R.id.iv_hangupsms);
        callMessageUtil = new CallMessageUtil(getApplicationContext());
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        ivHangup.setOnClickListener(this);
        ivCallAnswer.setOnClickListener(this);
        ivHandupmessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == ivHangup) {
            decline();
            finish();
        } else if (v == ivCallAnswer) {
            answer();
            finish();
        } else if (v == ivHandupmessage) {
            showMessageQuicSharekOption(new OnQuickOptionformClick() {

                @Override
                public void onQuickOptionClick(int id) {
                    // TODO Auto-generated method stub
                    if (id == R.id.tv_calllater) {

                    } else if (id == R.id.tv_whatthing) {


                    } else if (id == R.id.tv_define) {
                    } else if (id == R.id.tv_cancel) {

                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //instance = this;
        LinphoneCore lc = LinphoneManager.getInstance(getApplicationContext()).getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.addListener(mListener);
        }

        // Only one call ringing at a timestamp is allowed
        if (LinphoneManager.getInstance(getApplicationContext()).getLcIfManagerNotDestroyedOrNull() != null) {
            List<LinphoneCall> calls = LinphoneUtils.getLinphoneCalls(LinphoneManager.getLinCore(getApplicationContext()));
            for (LinphoneCall call : calls) {
                if (State.IncomingReceived == call.getState()) {
                    mCall = call;
                    break;
                }
            }
        }
        if (mCall == null) {
            Log.e("Couldn't find incoming call");
            finish();
            return;
        }

        String from = CallUtil.getRealToNumber(mCall.getCallLog().getFrom().asStringUriOnly(), mCall.getCallLog().getTo().asStringUriOnly(), "from");
        LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress(from, LinphoneManager.getLinCore(getApplicationContext()).getDefaultProxyConfig().getDomain(), "");
        // May be greatly sped up using a drawable cache
        Contact contact = ContactsManager.getInstance().findContactWithAddress(getContentResolver(), address);
        if (from.equals(getString(R.string.roamPhone))) {
            LinphoneUtils.setImagePictureFromUri(this, headPhoto, null, null, R.drawable.ic_service);
            tvUsername.setText(getString(R.string.roamservice));
        } else {
            LinphoneUtils.setImagePictureFromUri(this, headPhoto, contact != null ? contact.getPhotoUri() : null,
                    contact != null ? contact.getThumbnailUri() : null, R.drawable.photo_bg_xl);

            // To be done after findUriPictureOfContactAndSetDisplayName called
            String userName = contact != null ? contact.getName() : address.getUserName();
            if (userName.equals("unknown")) {
                userName = getString(R.string.str_unknown_number);
            }
            tvUsername.setText(userName);
        }
    }

    @Override
    protected void onPause() {
        LinphoneCore lc = LinphoneManager.getInstance(getApplicationContext()).getLcIfManagerNotDestroyedOrNull();
        if (lc != null) {
            lc.removeListener(mListener);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mRippleBackground.stopRippleAnimation();
        super.onDestroy();
        //instance = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (LinphoneManager.isInstanciated() && (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME)) {
            LinphoneManager.getLinCore(getApplicationContext()).terminateCall(mCall);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void decline() {
        LinphoneManager.getLinCore(getApplicationContext()).terminateCall(mCall);
    }

    private void answer() {
        LinphoneCallParams params = LinphoneManager.getLinCore(getApplicationContext()).createDefaultCallParameters();
        boolean isLowBandwidthConnection = !LinphoneUtils.isHighBandwidthConnection(this);
        if (isLowBandwidthConnection) {
            params.enableLowBandwidth(true);
            Log.d("Low bandwidth enabled in call params");
        }

        int dbId = callMessageUtil.getDbId(mCall.getCallLog().getCallId());
        params.setRecordFile(LinphoneManager.getInstance(getApplicationContext()).getRecordingFile(String.valueOf(dbId)));
        if (!LinphoneManager.getInstance(getApplicationContext()).acceptCallWithParams(mCall, params)) {
            // the above method takes care of Samsung Galaxy S
            Toast.makeText(this, R.string.couldnt_accept_call, Toast.LENGTH_LONG).show();
        } else {
            if (!LinphoneActivity.isInstanciated()) {
                return;
            }
            final LinphoneCallParams remoteParams = mCall.getRemoteParams();
            if (remoteParams != null && remoteParams.getVideoEnabled() && LinphonePreferences.instance().shouldAutomaticallyAcceptVideoRequests()) {
                LinphoneActivity.instance().startVideoActivity(mCall);
            } else {
                LinphoneActivity.instance().startInCallActivity(mCall);
            }
        }
    }
}
