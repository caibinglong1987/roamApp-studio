package com.roamtech.telephony.roamapp.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.bean.AppNewVersionRDO;
import com.roamtech.telephony.roamapp.bean.LoginInfo;
import com.roamtech.telephony.roamapp.bean.UserRDO;
import com.roamtech.telephony.roamapp.dialog.HeadOptionDialog;
import com.roamtech.telephony.roamapp.dialog.LoadingDialog;
import com.roamtech.telephony.roamapp.dialog.MessageOptionDialog;
import com.roamtech.telephony.roamapp.handler.CommonDoHandler;
import com.roamtech.telephony.roamapp.handler.CommonHandler;
import com.roamtech.telephony.roamapp.helper.ActivityCollector;
import com.roamtech.telephony.roamapp.interf.IBaseViewInterface;
import com.roamtech.telephony.roamapp.util.IpAddressUtil;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.umeng.analytics.MobclickAgent;
import com.will.common.tool.PackageTool;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneProxyConfig;

import java.math.BigDecimal;

/**
 * 所有Activity的基类
 *
 * @author xincheng
 */
public abstract class BaseActivity extends AppCompatActivity implements IBaseViewInterface,
        OnClickListener, OnItemClickListener, CommonDoHandler {
    private Dialog dialog;
    public CommonHandler<BaseActivity> uiHandler;

    @Override
    public void doHandler(Message msg) {
        uiHandler.handleMessage(msg);
    }

    // private DisplayImageOptions mOptions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        if (getLayoutId() > 0) {
            //兼容重写onCreate
            setContentView(getLayoutId(), savedInstanceState);
        }
        uiHandler = new CommonHandler<>(this);
    }

    public void setContentView(int layoutResID, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.setContentView(layoutResID);
        initData();
        initView(savedInstanceState);
        setListener();
    }

    public void setContentView(View view, Bundle savedInstanceState) {
        super.setContentView(view);
        initData();
        initView(savedInstanceState);
        setListener();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        setContentView(layoutResID, null);
    }

    public void setContentView(View view) {
        setContentView(view, (Bundle) null);
    }

    @Override
    public void initData() {
    }

    /**
     * 初始化View控件 setContentView 之后被调用
     */
    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        View backBtn = findViewById(R.id.id_toback);
        if (backBtn != null) {
            backBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });
        }
    }


    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void setListener() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        OkHttpUtil.cancel(hashCode());
    }

    /**
     * @param onQuickOptionformClick
     */
    protected void showMessageQuicSharekOption(
            MessageOptionDialog.OnQuickOptionformClick onQuickOptionformClick) {
        MessageOptionDialog dialog = new MessageOptionDialog(this);
        dialog.setOnQuickOptionformClickListener(onQuickOptionformClick);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    /**
     * 是否短信分享
     *
     * @param onQuickOptionformClick
     */
    protected void showHeadQuicSharekOption(
            HeadOptionDialog.OnQuickOptionformClick onQuickOptionformClick) {
        HeadOptionDialog dialog = new HeadOptionDialog(this);
        dialog.setOnQuickOptionformClickListener(onQuickOptionformClick);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub

    }

    public void showDialog(String title) {
        dialog = new LoadingDialog(this, title);
        dialog.show();
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public String getEditTextValue(EditText editText) {
        return editText.getText().toString();
    }

    public String getEditTextValue(int editTextId) {
        EditText et = (EditText) findViewById(editTextId);
        return getEditTextValue(et);
    }

    /**
     * 设置TextView的数据
     *
     * @param textViewId
     * @param data
     */
    public void setText(int textViewId, String data) {
        TextView textView = (TextView) findViewById(textViewId);
        textView.setText(data);
    }

    /**
     * 解决设置系统字体大小引起app字体大小的改变
     *
     * @return
     */
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    public BigDecimal getEditTextDecimal(EditText editText) {
        return new BigDecimal(editText.getText().toString());
    }

    /**
     * 判断是不是空白数据
     *
     * @param editText
     * @return
     */
    public boolean isTrimBlank(EditText editText) {
        return StringUtil.isTrimBlank(getEditTextValue(editText));
    }


    /**
     * 从传递的Intent中获取 根据
     * bundle获取值 获取从上一个界面传递的信息
     */
    protected Bundle getBundle() {
        return getIntent().getExtras();
    }

    public void toActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    public void toActivityForResult(Class<?> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    public void toActivityWithFinish(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        this.finish();
    }

    /**
     * http://www.360doc.com/content/12/1225/15/6541311_256191828.shtml
     **/
    public void toActivityClearTop(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 注意本行的FLAG设置
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);//设置NO_ANIMATION在set之后才有效
        startActivity(intent);
    }

    /**
     * Activity 不会无限制重启 会调用 onNewIntent
     *
     * @param cls
     * @param bundle
     */
    public void toActivityClearTopWithState(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 注意本行的FLAG设置
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    /**
     * Activity 不会无限制重启 会调用 onNewIntent
     *
     * @param cls
     * @param bundle
     */
    public void toActivityClearTopWithState(Class<?> cls, int requestCode, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 注意本行的FLAG设置
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent, requestCode);
    }

    public void toWebViewActivity(Class<?> cls, String url, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void toWebViewActivity(Class<?> cls, String url) {
        toWebViewActivity(cls, url, null);
    }

    public void showToast(String text) {
        showToast(text, false);
    }

    public void showToast(String text, boolean isLong) {
        if (text != null) {
            Toast.makeText(this, text, isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }

    public void showToast(int stringResId) {
        showToast(stringResId, false);
    }

    public void showToast(int stringResId, boolean isLong) {
        if (stringResId != 0) {
            Toast.makeText(this, stringResId,
                    isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }

    public void clearLoginInfo() {
        SPreferencesTool.getInstance().clearPreferences(getApplicationContext(), SPreferencesTool.LOGIN_INFO);
    }

    @SuppressLint("CommitPrefEdits")
    public void setLoginInfo(String userId, String sessionId, String password, UserRDO result) {
        if (result.getPhone() == null || result.getPhone().equals("0")) {
            result.setPhone(result.getUsername());
        }
        String oldLoginUserId = SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId);
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, userId);
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_sessionId, sessionId);
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userName, result.getUsername());
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_password, password);
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_phone, result.getPhone());
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_type, result.getType());
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_status, result.getStatus());
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_badge_miss_call_number, 0);
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_badge_message_number, 0);
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.every_day_update, 0l);
        //SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_domain, getString(R.string.default_domain));
        String defaultPhone = "0";
        int defaultType = 0;
        if (oldLoginUserId != null && oldLoginUserId.equals(userId)) {
            defaultPhone = SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_caller_phone, "0");
            defaultType = SPreferencesTool.getInstance().getIntValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_caller_type, 0);
        }
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_caller_phone, defaultPhone);
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_caller_type, defaultType);
    }

    public LoginInfo getLoginInfo() {
        LoginInfo info = new LoginInfo();
        info.setUserId(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId, "0"));
        info.setSessionId(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_sessionId, "0"));
        info.setUsername(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userName, "0"));
        info.setPhone(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_phone, "0"));
        info.setType(SPreferencesTool.getInstance().getIntValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_type, 0));
        info.setStatus(SPreferencesTool.getInstance().getIntValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_status, 0));
        info.setUser_photo(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_headUrl));
        info.setUser_gender(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_gender));
        info.setUseraddress(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_address));
        return info;
    }

    @SuppressLint("CommitPrefEdits")
    public void setRoamPhone(String roamPhone) {
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.roamPhone, roamPhone);
    }

    public String getRoamPhone() {
        return SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.roamPhone, "0");
    }

    /**
     * 获取 角标 数目
     *
     * @param key    xml key
     * @param number number
     * @return
     */
    public int getBadgeNumber(String key, int number) {
        return SPreferencesTool.getInstance().getIntValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, key, number);
    }

    public String loginUserPhone() {
        String phone = SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_phone, "0");
        if (phone.equals("0")) {
            phone = getUserName();
        }
        return phone;
    }

    public String getUserId() {
        return SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId);
    }

    public String getSessionId() {
        return SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_sessionId, "0");

    }

    public String getSipDomain() {
        return SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_domain, "0");

    }

    public String getUserName() {
        return SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userName, "0");
    }

    public JSONObject getAuthJSONObject() {
        LoginInfo loginInfo = getLoginInfo();
        JSONObject authJson = new JSONObject();
        try {
            authJson.put("userid", loginInfo.getUserId());
            authJson.put("sessionid", loginInfo.getSessionId());
            authJson.put("versionName", getHttpUserAgent());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return authJson;
    }


    private void logIn(String username, String password, String phone, String domain, boolean sendEcCalibrationResult) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        saveCreatedAccount(username, password, phone, domain);
        /*if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
            launchEchoCancellerCalibration(sendEcCalibrationResult);
		}*/
    }

    public void checkAccount(String username, String password, String phone, String domain) {
        saveCreatedAccount(username, password, phone, domain);
    }

    public void linphoneLogIn(String username, String password, String phone, boolean validate) {
        String domain = SPreferencesTool.getInstance().getStringValue(getApplicationContext(),
                SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_domain, getString(R.string.default_domain));
        if (validate) {
            checkAccount(username, password, phone, domain);
        } else {
            logIn(username, password, phone, domain, true);
        }
    }

    public void genericLogIn(String username, String password, String domain) {
        logIn(username, password, username, domain, false);
    }

    public void saveCreatedAccount(final String username, final String password, final String phone, final String domain) {
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
                LinphoneProxyConfig[] proxyCfgs = LinphoneManager.getLc().getProxyConfigList();
                LinphoneAuthInfo[] authInfos = LinphoneManager.getLc().getAuthInfosList();
                if (proxyCfgs != null) {
                    for (int i = 0; i < proxyCfgs.length; i++) {
                        if (proxyCfgs[i].getIdentity().equals(address.asString())) {
                            LinphoneManager.getLc().removeProxyConfig(proxyCfgs[i]);
                            if (i < authInfos.length && (newUserName == null || authInfos[i].getUsername().equals(newUserName))) {
                                newUserName = authInfos[i].getUsername();
                                newPassword = authInfos[i].getPassword();
                                LinphoneManager.getLc().removeAuthInfo(authInfos[i]);
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

    @SuppressLint("CommitPrefEdits")
    public void setAppNewVersion(AppNewVersionRDO info) {
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME,
                SPreferencesTool.AppReleasedConfig.VERSION_NAME, info.getVersionName());
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME,
                SPreferencesTool.AppReleasedConfig.VERSION, info.getVersion());
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME,
                SPreferencesTool.AppReleasedConfig.DESCRIPTION, info.getDescription());
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME,
                SPreferencesTool.AppReleasedConfig.URL, info.getUrl());
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME,
                SPreferencesTool.AppReleasedConfig.UPGRADE_TIME, info.getUpgradeTime());
        SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME,
                SPreferencesTool.AppReleasedConfig.RELEASE_TIME, info.getReleaseTime());
    }

    public AppNewVersionRDO getAppNewVersion() {
        AppNewVersionRDO info = new AppNewVersionRDO();
        info.setVersionName(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME, SPreferencesTool.AppReleasedConfig.VERSION_NAME));
        info.setVersion(SPreferencesTool.getInstance().getIntValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME, SPreferencesTool.AppReleasedConfig.VERSION, 0));
        info.setDescription(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME, SPreferencesTool.AppReleasedConfig.DESCRIPTION));
        info.setUrl(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME, SPreferencesTool.AppReleasedConfig.URL));
        info.setUpgradeTime(SPreferencesTool.getInstance().getLongValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME, SPreferencesTool.AppReleasedConfig.UPGRADE_TIME, 0));
        info.setReleaseTime(SPreferencesTool.getInstance().getLongValue(getApplicationContext(), SPreferencesTool.AppReleasedConfig.PROFILE_NAME, SPreferencesTool.AppReleasedConfig.RELEASE_TIME, 0));
        return info;
    }

    public String getVersionName() {
        return PackageTool.getVersionName(this);
    }

    public String getHttpUserAgent() {
        return "RoamPhone/" + PackageTool.getVersionName(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //Intent intent = new Intent(this, StartActivity.class);
        //startActivity(intent);
    }

    /**
     * 是否清除 登录信息
     *
     * @param isClearUserInfo true 清除
     */
    public void resetForLogin(boolean isClearUserInfo) {
        //清除专属号
        setRoamPhone("0");
        if (LinphoneManager.isInstanciated()) {
            LinphoneManager.getInstance().setUserSession(null, null);
            LinphoneManager.getLc().clearAuthInfos();
            LinphoneManager.getLc().clearProxyConfigs();
        }
        if (isClearUserInfo) {
            clearLoginInfo();
        }
        MobclickAgent.onProfileSignOff();
    }
}
