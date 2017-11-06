package com.roamtech.telephony.roamapp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.bean.DeliverVoucherVerifyRDO;
import com.roamtech.telephony.roamapp.bean.LoginInfo;
import com.roamtech.telephony.roamapp.bean.SingleDataCardRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.enums.LoadingState;
import com.roamtech.telephony.roamapp.event.EventAddCard;
import com.roamtech.telephony.roamapp.event.EventScanResult;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.zxing.camera.CameraManager;
import com.roamtech.telephony.roamapp.zxing.decoding.CaptureActivityHandler;
import com.roamtech.telephony.roamapp.zxing.decoding.InactivityTimer;
import com.roamtech.telephony.roamapp.zxing.view.ViewfinderView;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import static com.roamtech.telephony.roamapp.enums.LoadingState.SESSION_TIME_OUT;

/**
 * Initial the camera
 *
 * @author Ryan.Tang
 */
public class CaptureActivity extends BaseActivity implements Callback, View.OnClickListener {

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private EditText etQrcode;
    private LinearLayout layoutQrcode;
    private TextView tvQuery;
    private TextView tvHandinput;
    private String origin;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        findViewById(R.id.id_toback).setOnClickListener(this);
        etQrcode = (EditText) findViewById(R.id.et_qrcode);
        etQrcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtil.isTrimBlank(s.toString())) {
                    tvQuery.setText(R.string.cancel);
                } else {
                    tvQuery.setText(R.string.ok);
                }
            }
        });
        tvQuery = (TextView) findViewById(R.id.tv_query);
        tvQuery.setOnClickListener(this);
        layoutQrcode = (LinearLayout) findViewById(R.id.layout_qrcode);
        tvHandinput = (TextView) findViewById(R.id.tv_handinput);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            origin = bundle.getString("origin", "native");
            if (bundle.getBoolean("manualInput", true)) {
                tvHandinput.setOnClickListener(this);
            } else {
                tvHandinput.setVisibility(View.GONE);
            }
        } else {
            tvHandinput.setOnClickListener(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     *
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
            failedDialog(getString(R.string.scan_failed));
        } else if (resultString.length() == 11) {
            JSONObject jsonObject = getAuthJSONObject();
            try {
                jsonObject.put("devid", resultString);
                jsonObject.put("phone", loginUserPhone());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            new RoamBoxFunction(this).bindRoamBox(jsonObject, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(getString(R.string.bind_roam_box_error));
                        }
                    });
                }

                @Override
                public void onSuccess(String response) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            showToast(getString(R.string.bind_roam_box_success));
                        }
                    });
                }
            });
        } else {
            if (getLoginInfo().getType() == LoginInfo.CLERK_USER) {
                verifyDeliveryVoucher(resultString);
            } else {
                if ("RDMall".equals(origin)) {
                    EventBus.getDefault().post(new EventScanResult(resultString));
                    finish();
                } else {
                    bindCard(resultString, true);
                }
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.id_toback) {
            finish();
        } else if (v == tvQuery) {
            if (StringUtil.isTrimBlank(etQrcode.getText().toString())) {
                tvHandinput.setVisibility(View.VISIBLE);
                layoutQrcode.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(etQrcode.getWindowToken(), 0);
            } else {
                //dorequest
                String iccid = etQrcode.getText().toString();
                if (StringUtil.isTrimBlank(iccid)) {
                    if (getLoginInfo().getType() == LoginInfo.CLERK_USER) {
                        showToast(getString(R.string.not_null_receive_code));
                    } else {
                        showToast(getString(R.string.not_null_card_number));
                    }
                    return;
                }
                if (getLoginInfo().getType() == LoginInfo.CLERK_USER) {
                    verifyDeliveryVoucher(iccid);
                } else {
                    bindCard(iccid, false);
                }
            }
        } else if (v == tvHandinput) {
            tvHandinput.setVisibility(View.GONE);
            layoutQrcode.setVisibility(View.VISIBLE);
        }
    }

    private String mEvoucherSN;

    private void verifyDeliveryVoucher(String evoucherSN) {
        showDialog(getString(R.string.valid_card_ticket));
        JSONObject json = getAuthJSONObject();
        try {
            json.put("evoucher_sn", evoucherSN);
            mEvoucherSN = evoucherSN;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtil.postJsonRequest(Constant.DELI_VERY_VOUCHER_VERIFY, json, hashCode(), new OKCallback<DeliverVoucherVerifyRDO>(new TypeToken<UCResponse<DeliverVoucherVerifyRDO>>() {
        }) {
            @Override
            public void onResponse(int statuscode, UCResponse<DeliverVoucherVerifyRDO> ucResponse) {
                dismissDialog();
                if (isSucccess()) {
                    deliveryCardDialog(String.format(getString(R.string.grant_user_card), String.valueOf(ucResponse.getAttributes().getQuantity())));
                    return;
                } else if (isSessionTimeout()) {
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                } else {
                    if (ucResponse != null) {
                        //showToast(ucResponse.getErrorInfo());
                        evoucherFailedDialog(ucResponse.getErrorInfo());
                    } /*else {
                        showToast("领取码");
                    }*/
                }
            }

            @Override
            public void onFailure(IOException e) {
                dismissDialog();
                showToast(LoadingState.getErrorState(e).getText());
            }
        });
    }

    private void completeDeliveryVoucher(String evoucherSN) {
        showDialog(getString(R.string.start_issuer));
        JSONObject json = getAuthJSONObject();
        try {
            json.put("evoucher_sn", evoucherSN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtil.postJsonRequest(Constant.DELI_VERY_VOUCHER_COMPLETE, json, hashCode(), new OKCallback<DeliverVoucherVerifyRDO>(new TypeToken<UCResponse<DeliverVoucherVerifyRDO>>() {
        }) {
            @Override
            public void onResponse(int statuscode, UCResponse<DeliverVoucherVerifyRDO> ucResponse) {
                dismissDialog();
                if (isSucccess()) {
                    finish();
                    return;
                } else if (isSessionTimeout()) {
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                } else {
                    if (ucResponse != null) {
                        showToast(ucResponse.getErrorInfo());
                    } /*else {
                        showToast("领取码");
                    }*/
                }
            }

            @Override
            public void onFailure(IOException e) {
                dismissDialog();
                showToast(LoadingState.getErrorState(e).getText());
            }
        });
    }

    private void bindCard(final String iccid, final boolean isScan) {
        showDialog(getString(R.string.now_bind_card));
        JSONObject json = getAuthJSONObject();
        try {
            json.put("datacardid", iccid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtil.postJsonRequest(Constant.DATA_CARD_BIND, json, hashCode(), new OKCallback<SingleDataCardRDO>(new TypeToken<UCResponse<SingleDataCardRDO>>() {
        }) {
            @Override
            public void onResponse(int statuscode, UCResponse<SingleDataCardRDO> ucResponse) {
                dismissDialog();
                if (isSucccess()) {
                    EventBus.getDefault().post(new EventAddCard("add success"));
                    new TipDialog(CaptureActivity.this, getString(R.string.prompt), getString(R.string.bind_card_success))
                            .setRightButton(getString(R.string.content_description_setup_ok), new TipDialog.OnClickListener() {
                                @Override
                                public void onClick(int which) {
                                    setResult(RESULT_OK, new Intent().putExtra("cardId", iccid));
                                    finish();
                                }
                            })
                            .show();
                    return;
                } else if (isSessionTimeout()) {
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                } else {
                    if (ucResponse != null) {
                        showToast(ucResponse.getErrorInfo());
                    } else {
                        showToast(getString(R.string.bind_card_error));
                    }
                }
                if (isScan) {
                    failedDialog(getString(R.string.bind_card_error));
                }
            }

            @Override
            public void onFailure(IOException e) {
                if (isScan) {
                    failedDialog(getString(R.string.bind_card_error));
                }
                dismissDialog();
                showToast(LoadingState.getErrorState(e).getText());
            }
        });
    }

    /**
     * 添加重新启动扫描的代码
     */
    private void restartCamera() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        initCamera(surfaceHolder);
        if (handler != null) {
            handler.restartPreviewAndDecode();
        }
    }

    private void deliveryCardDialog(String message) {
//        new AlertDialog.Builder(this).setCancelable(false).setTitle("有效领取码").setMessage(message).setPositiveButton("请发卡", deliveryCardListener).show();
        new TipDialog(this, getString(R.string.eff_receive_code), message)
                .setRightButton(getString(R.string.please_issuer), new TipDialog.OnClickListener() {
                    @Override
                    public void onClick(int which) {
                        completeDeliveryVoucher(mEvoucherSN);
                    }
                })
                .show();
    }

    private void evoucherFailedDialog(String message) {
//        new AlertDialog.Builder(this).setCancelable(false).setTitle("无效领取码").setMessage(message).setNegativeButton("取消", deliveryCardListener).show();
        new TipDialog(this, getString(R.string.invalid_receive_code), message)
                .setRightButton(getString(R.string.button_cancel), new TipDialog.OnClickListener() {
                    @Override
                    public void onClick(int which) {
                        finish();
                    }
                })
                .show();
    }

    //    private DialogInterface.OnClickListener deliveryCardListener = new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            if (which == DialogInterface.BUTTON_POSITIVE) {
//                completeDeliveryVoucher(mEvoucherSN);
//            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
//                finish();
//            }
//        }
//    };
    private void failedDialog(String message) {
//        new AlertDialog.Builder(this).setCancelable(false).setTitle("扫码").setMessage(message).setPositiveButton("重新扫描", listener).setNegativeButton("取消", listener).show();
        new TipDialog(this, getString(R.string.qrcode), message)
                .setLeftButton(getString(R.string.button_cancel), listener)
                .setRightButton(getString(R.string.rescan), listener)
                .show();
    }

//    private DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            if (which == DialogInterface.BUTTON_POSITIVE) {
//                restartCamera();
//            } else if (which == DialogInterface.BUTTON_NEGATIVE) {
//                finish();
//            }
//        }
//    };

    private TipDialog.OnClickListener listener = new TipDialog.OnClickListener() {
        @Override
        public void onClick(int which) {
            if (which == TipDialog.BUTTON_LEFT) {
                finish();
            } else if (which == TipDialog.BUTTON_RIGHT) {
                restartCamera();
            }
        }
    };
}