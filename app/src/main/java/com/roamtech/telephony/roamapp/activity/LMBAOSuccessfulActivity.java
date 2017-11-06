package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.bean.TouchRDO;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;
import com.roamtech.telephony.roamapp.dialog.RoamBoxSettingDialog;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneManager;
import org.linphone.LinphoneService;

import java.util.List;
import java.util.Map;

/**
 * Created by caibinglong
 * on 2017/1/2.
 */

public class LMBAOSuccessfulActivity extends HeaderBaseActivity {
    private TextView tv_success, tv_config_state, tv_config_desc, tv_go_wifi;
    private RelativeLayout layout_status;
    private String phone_number = null;
    private ImageView iv_config_state;
    private LinearLayout layout_roam_box_status;
    private RoamBoxSettingDialog roamBoxSettingDialog;
    private RelativeLayout noDataLayout;
    private int connectNumber = 0;
    private RoamBoxFunction roamBoxFunction;

    private JSONObject loginUser = new JSONObject();
    private boolean RoamBoxIsSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_config_successful);
        //phone_number = getIntent().getStringExtra("phone_number");
        phone_number = RoamApplication.RoamBoxConfigOld.phone;
        if (phone_number == null || phone_number.length() == 0) {
            phone_number = SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_phone, "-1");
        }
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_config_lmb));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goOtherActivity();
            }
        });
        layout_roam_box_status = (LinearLayout) findViewById(R.id.layout_roam_box_status);
        noDataLayout = (RelativeLayout) findViewById(R.id.no_data_layout);
        layout_status = (RelativeLayout) findViewById(R.id.layout_status);
        iv_config_state = (ImageView) findViewById(R.id.iv_config_state);
        tv_success = (TextView) findViewById(R.id.tv_success);
        tv_config_state = (TextView) findViewById(R.id.tv_config_state);
        tv_config_desc = (TextView) findViewById(R.id.tv_config_desc);
        tv_go_wifi = (TextView) findViewById(R.id.tv_go_wifi);
        tv_success.setOnClickListener(this);
        tv_go_wifi.setOnClickListener(this);
        roamBoxFunction = new RoamBoxFunction(this);
        roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.lmb_config_enable_now_please));

        if (StringUtil.isBlank(RoamApplication.RoamBoxToken)) {
            return;
        }
        loginUser = getAuthJSONObject();
        roamBoxSettingDialog.show();
        roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
        setRoamBoxPhone();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_success:
                //配置生效 新手机号码
                if (RoamBoxIsSuccess) {
                    goOtherActivity();
                } else {
                    tv_success.setVisibility(View.INVISIBLE);
                    layout_status.setVisibility(View.INVISIBLE);
                    tv_config_state.setVisibility(View.INVISIBLE);
                    roamBoxSettingDialog.show();
                    roamBoxSettingDialog.setAnimationType(roamBoxSettingDialog.TYPE_ROAM_SET);
                    roamBoxSettingDialog.setTitle(getString(R.string.lmb_config_enable_now_please));
                    setRoamBoxPhone();
                }
                break;
            case R.id.tv_go_wifi:
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
                break;
        }
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_SET_LAN_WIFI_TIMEOUT:
                if (connectNumber >= 1) {
                    roamBoxSettingDialog.dismiss();
                    showNoDataLayout();
                    return;
                }
                connectNumber++;
                setRoamBoxPhone();
                break;
            case MsgType.MSG_SET_LAN_WIFI_SUCCESS:
                Log.e("设置络漫宝手机成功--->", "准备发送重起指令");
                connectNumber = 0;
                restartRoamBox();
                break;
            case MsgType.MSG_RESTART_ROAM_BOX_ERROR:
                Log.e("重启指令->", "发送失败");
                if (connectNumber >= 1) {
                    Log.e("重启络漫宝 超时--->", "发送指令 超时");
                    ToastUtils.showToast(this, getString(R.string.please_check_has_been_connected_to_roam_box));
                    roamBoxSettingDialog.dismiss();
                    showNoDataLayout();
                    return;
                }
                connectNumber++;
                restartRoamBox();
                break;
            case MsgType.MSG_ROAM_BOX_CONFIG_ERROR_GET_ROAM_BOX_SUCCESS:
            case MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS:
                //获取最新络漫宝成功
                //RoamApplication.RoamBoxConfigNew = RoamApplication.RoamBoxConfigOld;
                //RoamApplication.RoamBoxConfigOld = null;//之前老的配置 清空
                RoamApplication.bGoConfig = true;
                roamBoxSettingDialog.dismiss();
                RoamBoxIsSuccess = true;
                noDataLayout.setVisibility(View.GONE);
                layout_roam_box_status.setVisibility(View.VISIBLE);
                layout_status.setVisibility(View.VISIBLE);
                tv_success.setVisibility(View.VISIBLE);
                tv_success.setText(getString(R.string.btn_already_connected));
                tv_config_desc.setVisibility(View.VISIBLE);
                tv_go_wifi.setVisibility(View.VISIBLE);
                tv_config_state.setVisibility(View.VISIBLE);
                tv_config_state.setText(getString(R.string.lmb_config_state_success));
                iv_config_state.setImageResource(R.drawable.check_ok);
                //配置生效 新手机号码
                break;
            case MsgType.MSG_NO_ROAM_BOX_WIFI:
                roamBoxSettingDialog.dismiss();
                Log.e("连接络漫宝设置LAN 超时--->", "设置LAN 超时");
                ToastUtils.showToast(this, getString(R.string.please_check_has_been_connected_to_roam_box));
                showNoDataLayout();
                break;
            case MsgType.MSG_RESTART_ROAM_BOX_SUCCESS:
                Log.e("设置络漫宝wifi成功--->", "开启定时器准备连接新wifi");
                connectNumber = 0;
                roamBoxSettingDialog.setTitle(getString(R.string.str_lmb_restart));
                //络漫宝重启中, 请稍候....
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS);
                    }
                }, 7 * 1000);
                break;
            case MsgType.MSG_ROAM_BOX_CONFIG_ERROR_GET_ROAM_BOX_ERROR:
                //切换网络失败
                Log.e("连接络漫宝wifi失败--->", "连接络漫宝次数---》" + connectNumber);
                RoamBoxIsSuccess = false;
                roamBoxSettingDialog.dismiss();
                noDataLayout.setVisibility(View.GONE);//隐藏 网络错误
                layout_roam_box_status.setVisibility(View.VISIBLE);
                layout_status.setVisibility(View.VISIBLE);
                tv_success.setVisibility(View.VISIBLE);
                tv_success.setText(getString(R.string.str_restart_submit_config));
                tv_config_state.setVisibility(View.VISIBLE);
                tv_config_state.setText(getString(R.string.lmb_config_state_error));
                iv_config_state.setImageResource(R.drawable.check);
                tv_config_desc.setVisibility(View.INVISIBLE);
                tv_go_wifi.setVisibility(View.INVISIBLE);
                connectNumber = 0;
                break;
        }
    }

    private void showNoDataLayout() {
        connectNumber = 0;
        layout_roam_box_status.setVisibility(View.GONE); //隐藏配置结果
        noDataLayout.setVisibility(View.VISIBLE);
        ((ImageView) noDataLayout.findViewById(R.id.no_data_icon)).setImageResource(R.drawable.check);
        noDataLayout.findViewById(R.id.hint_textview1).setVisibility(View.VISIBLE);
        ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(getString(R.string.error_network_unreachable));
        (noDataLayout.findViewById(R.id.hint_textview_desc2)).setVisibility(View.GONE);
        ((TextView) noDataLayout.findViewById(R.id.hint_textview2)).setText(getString(R.string.str_network_ok_restatrt_submit));
        noDataLayout.findViewById(R.id.hint_textview2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noDataLayout.setVisibility(View.GONE);
                roamBoxSettingDialog.show();
                setRoamBoxPhone();
            }
        });
    }

    /**
     * 配置 设置无线配置 信息
     */
    private void setRoamBoxPhone() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(phone_number);
        jsonArray.put(SPreferencesTool.getInstance().getStringValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_userId));
        roamBoxFunction.setRoamBoxPhone(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, jsonArray, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_SET_LAN_WIFI_SUCCESS);
            }

            @Override
            public void onSuccess(String response) {
                CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                }.getType());
                if (result != null && result.attributes != null && result.attributes.equals("true")) {
                    uiHandler.sendEmptyMessage(MsgType.MSG_SET_LAN_WIFI_SUCCESS);
                } else {
                    uiHandler.sendEmptyMessage(MsgType.MSG_SET_LAN_WIFI_TIMEOUT);
                }
            }
        });
    }

    /**
     * 重启络漫宝
     */
    private void restartRoamBox() {
        roamBoxFunction.restartRoamBox(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_RESTART_ROAM_BOX_ERROR);
            }

            @Override
            public void onSuccess(String response) {
                uiHandler.sendEmptyMessage(MsgType.MSG_RESTART_ROAM_BOX_SUCCESS);
            }
        });
    }

    /**
     * 跳转其他界面 处理
     * 判断是否已经配置成功
     * 判断是否连接网络
     * 判断是否已经获取到最新络漫宝数据
     * 判断是否注册最新络漫宝
     */
    private void goOtherActivity() {
        if (RoamBoxIsSuccess) {
            if (WifiAdmin.isNetworkConnected(this)) {
                if (LinphoneService.isReady()) {
                    Log.e("号码配置生效--->", "成功");
                    LinphoneService.instance().requestMyTouchDBModels(getUserId(), getSessionId(), getSipDomain());
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("touch", getNowConfigTouch());
                toActivityClearTop(LMBaoLinkagePagerActivity.class, bundle);
            } else {
                ToastUtils.showToast(this, getString(R.string.wizard_server_unavailable));
            }
        } else {
            finish();
        }
    }

    private TouchDBModel getNowConfigTouch() {
        TouchDBModel touch = new TouchDBModel();
        touch.setDevid(RoamApplication.RoamBoxConfigOld.devid);
        touch.setPhone(RoamApplication.RoamBoxConfigOld.phone);
        touch.setWanProto(RoamApplication.RoamBoxConfigOld.wan_proto);
        touch.setWanType(RoamApplication.RoamBoxConfigOld.wan_type);
        touch.setLanSsid(RoamApplication.RoamBoxConfigOld.lan_ssid);
        return touch;
    }
}
