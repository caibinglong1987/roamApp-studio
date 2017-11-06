package com.roamtech.telephony.roamapp.activity;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneAuthInfo;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.TelephonyUtils;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.bean.Phone;
import com.roamtech.telephony.roamapp.bean.WiFi;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 络漫宝 设置界面
 * 输入wifi 账号 密码
 * 设置 手机
 */
public class LMBaoSettingActivity extends BaseActivity {
    private Handler mHandler = new Handler();
    private TextView tvSelectwifi;
    private TextView tvSelectWifishow;
    private TextView tvWifiPassword;
    private EditText etWifiPassword;
    private TextView tvBindPhone;
    private TextView tvBindPhoneShow;
    private TextView tvSettingTip;
    private Button settingTouch;
    private String touchAPIP;
    private String devid;
    private WifiInfo mWifiInfo;

    private boolean passwordOk = true;
    private boolean touchOk = false;
    private int touchconnectStatus = 0;
    private int retry_cnt = 0;
    private Timer mTimer;
    private TimerTask connectTouchTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmbao_setting);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        tvSettingTip = (TextView) findViewById(R.id.tv_setttingtip);
        tvSelectwifi = (TextView) findViewById(R.id.tv_selectwifi);
        tvSelectWifishow = (TextView) findViewById(R.id.tv_selectwifiShow);
        tvWifiPassword = (TextView) findViewById(R.id.tv_wifipassword);
        etWifiPassword = (EditText) findViewById(R.id.et_wifipassword);

        tvBindPhone = (TextView) findViewById(R.id.tv_bindPhone);
        tvBindPhoneShow = (TextView) findViewById(R.id.tv_bindPhoneshow);
        settingTouch = (Button) findViewById(R.id.id_submit_ok);
        addPasswordHandler(etWifiPassword);
        mWifiInfo = TelephonyUtils.getInstance().getCurrentWifiConnection();
        mTimer = LinphoneActivity.instance().getTimer();
        String defaultSsid = mWifiInfo.getSSID();
        if (defaultSsid.startsWith("\"") && defaultSsid.endsWith("\"")) {
            defaultSsid = defaultSsid.substring(1, defaultSsid.length() - 1);
        }
        tvSelectWifishow.setText(defaultSsid);
        List<Phone> phones = LinphoneActivity.instance().getMyPhones();
        if (!phones.isEmpty()) {
            tvBindPhoneShow.setText(phones.get(phones.size() - 1).getPhoneNumber());
        }
        settingTouch.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                configureTouch();
            }
        });
        connectTouch();
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        tvSelectwifi.setOnClickListener(this);
        tvBindPhone.setOnClickListener(this);
    }

    private static final int REQUEST_CODE_PHONE = 1;
    private static final int REQUEST_CODE_WIFI = 2;

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == tvSelectwifi) {
            Bundle bundle = new Bundle();
            bundle.putString("wifi", tvSelectWifishow.getText().toString());
            toActivityForResult(SelectWiFiActivity.class, REQUEST_CODE_WIFI, bundle);
        } else if (v == tvBindPhone) {
            Bundle bundle = new Bundle();
            bundle.putString("phone", tvBindPhoneShow.getText().toString());
            toActivityForResult(MinePhoneActivity.class, REQUEST_CODE_PHONE, bundle);
        }/* else if(v==btnSettingLmbao){
            Dialog dialog=new Dialog(this,R.style.dialog_settinglmbao);
			dialog.setOwnerActivity(this);
			dialog.setContentView(R.layout.dialog_setting_lmbao);
			dialog.show();
		}*/
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // TODO Auto-generated method stub
        super.onActivityResult(arg0, arg1, arg2);
        if (arg1 == RESULT_OK) {
            if (arg0 == REQUEST_CODE_PHONE) {
                Phone phone = (Phone) arg2.getSerializableExtra("phone");
                tvBindPhoneShow.setText(phone.getPhoneNumber());
            } else if (arg0 == REQUEST_CODE_WIFI) {
                WiFi Wifi = (WiFi) arg2.getSerializableExtra("wifi");
                tvSelectWifishow.setText(Wifi.getSsid());
            }
        }
    }

    private void setTouchIp(int ipAddress) {
        String ipString = "";
        if (ipAddress != 0) {
            ipString = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "."
                    + (ipAddress >> 16 & 0xff) + "." + "1");//(ipAddress >> 24 & 0xff));
            touchAPIP = ipString;
        } else {
            touchAPIP = null;
        }
    }

    private void checkTouchConnected() {
        WifiInfo wifiInfo = TelephonyUtils.getInstance().getCurrentWifiConnection();
        if (wifiInfo.getSSID().equals("roamtouchAP") || (wifiInfo.getSSID().equals("\"" + "roamtouchAP" + "\""))) {
            touchconnectStatus = 2;
            setTouchIp(wifiInfo.getIpAddress());
            if (touchAPIP != null) {
                tvSettingTip.setText(getString(R.string.connect_lmb_success));//+touchAPIP);
                touchOk = true;
                settingTouch.setEnabled(passwordOk && touchOk);
            } else {
                tvSettingTip.setText(getString(R.string.connect_lmb_now));//+touchAPIP);
                touchOk = false;
                settingTouch.setEnabled(passwordOk && touchOk);
            }
        }
    }

    public void connectTouch() {
        final Runnable runNotReachable = new Runnable() {
            public void run() {
                tvSettingTip.setTextColor(getResources().getColor(R.color.red_dark));
                tvSettingTip.setText(getString(R.string.no_find_lmb));
                touchOk = false;
                //icon.setImageResource(R.drawable.wizard_notok);
                settingTouch.setEnabled(passwordOk && touchOk);
            }
        };
        final Runnable runNotOk = new Runnable() {
            public void run() {
                tvSettingTip.setTextColor(getResources().getColor(R.color.red_dark));
                tvSettingTip.setText(getString(R.string.connect_lmb_disconnet));
                touchOk = false;
                settingTouch.setEnabled(passwordOk && touchOk);
            }
        };
        final Runnable runOk = new Runnable() {
            public void run() {
                tvSettingTip.setTextColor(getResources().getColor(R.color.green));
                tvSettingTip.setText(getString(R.string.find_lbm_connect));
                touchconnectStatus = 1;
                checkTouchConnected();
            }
        };
        final Runnable runUpdateOk = new Runnable() {
            public void run() {
                checkTouchConnected();
            }
        };
        connectTouchTask = new TimerTask() {
            @Override
            public void run() {
                if (touchconnectStatus == 2) {
                    WifiInfo wifiInfo = TelephonyUtils.getInstance().getCurrentWifiConnection();
                    if (!wifiInfo.getSSID().equals("roamtouchAP") && (!wifiInfo.getSSID().equals("\"" + "roamtouchAP" + "\""))) {
                        touchconnectStatus = 0;
                        mHandler.post(runNotOk);
                    } else {
                        mHandler.post(runUpdateOk);
                    }
                }
                if (touchconnectStatus != 2) {
                    if (retry_cnt == 0 || touchconnectStatus == 0 || retry_cnt % 5 == 0) {
                        TelephonyUtils.getInstance().disconnectTouch();
                        if (!TelephonyUtils.getInstance().connectTouch()) {
                            mHandler.post(runNotReachable);
                            touchconnectStatus = 0;
                        } else {
                            mHandler.post(runOk);
                        }
                    }
                }
                retry_cnt++;
            }
        };
        mTimer.schedule(connectTouchTask, 0, 1000);
    }

    private void configureTouch() {
        LinphoneAuthInfo[] authInfos = LinphoneManager.getLc().getAuthInfosList();
        LinphoneAuthInfo authInfo = authInfos[authInfos.length - 1];
        final String username = authInfo.getUsername();
        final String password = authInfo.getPassword();
        final String wifi_ssid = tvSelectWifishow.getText().toString();
        final String wifi_password = etWifiPassword.getText().toString();
        final String sphone = tvBindPhoneShow.getText().toString();
        final String domain = authInfo.getDomain();
        String touchConfigUrl = "http://" + touchAPIP + ":8080/config?" + "username=" + username + "&password=" + password + "&ssid=" + wifi_ssid + "&wifi_password=" + wifi_password + "&phone=" + sphone + "&domain=" + domain;
        OkHttpUtil.getRequest(touchConfigUrl, hashCode(), new OKCallback<String>(new TypeToken<UCResponse<String>>() {
        }) {
            @Override
            public void onResponse(int statuscode, @Nullable UCResponse<String> ucResponse) {
                if (isSucccess()) {
                    devid = ucResponse.getDevId();
                    LinphoneActivity.instance().bindTouch(sphone, devid, wifi_ssid, wifi_password);
                    saveCreatedAccount(username, password, sphone, domain);
                    success();
                } else {
                    Toast.makeText(LMBaoSettingActivity.this, ucResponse.getErrorInfo(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(IOException e) {
                showToast(e.getMessage());
                touchOk = false;
                //icon.setImageResource(R.drawable.wizard_notok);
                settingTouch.setEnabled(passwordOk && touchOk);
                touchconnectStatus = 0;
            }
        });
    }

    private boolean isPasswordCorrect(String password) {
        return (password.length() >= 8 || password.length() == 0);
    }

    private void addPasswordHandler(final EditText field1) {
        TextWatcher passwordListener = new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int count, int after) {
                passwordOk = false;
                if (isPasswordCorrect(field1.getText().toString())) {
                    passwordOk = true;

                }
                settingTouch.setEnabled(passwordOk && touchOk);
            }
        };

        field1.addTextChangedListener(passwordListener);
    }

    public void success() {
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    protected void onDestroy() {
        connectTouchTask.cancel();
        TelephonyUtils.getInstance().disconnectTouch();
        if (!mWifiInfo.getSSID().equals("roamtouchAP") && (!mWifiInfo.getSSID().equals("\"" + "roamtouchAP" + "\""))) {
            TelephonyUtils.getInstance().connectUplink(mWifiInfo.getSSID());
        }
        super.onDestroy();
    }
}
