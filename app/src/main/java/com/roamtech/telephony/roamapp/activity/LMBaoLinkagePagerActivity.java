package com.roamtech.telephony.roamapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.LinkagePager;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.Receiver.RoamBoxMessageReceiver;
import com.roamtech.telephony.roamapp.activity.Parameter.KeyValue;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.bean.NetworkConfigBean;
import com.roamtech.telephony.roamapp.bean.TouchRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;
import com.roamtech.telephony.roamapp.dialog.RoamBoxSettingDialog;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.event.EventRefresh;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.ToastUtils;
import com.roamtech.telephony.roamapp.view.DataDemoView;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.will.common.tool.wifi.MacAddressUtils;
import com.will.common.tool.wifi.WifiAdmin;
import com.will.coverflow.CoverFlow;
import com.will.coverflow.core.LinkagePagerContainer;
import com.will.coverflow.core.PageItemClickListener;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.core.LinphoneBuffer;
import org.linphone.core.LinphoneChatMessage;
import org.linphone.core.LinphoneChatRoom;
import org.linphone.core.LinphoneContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * Created by long
 * on 16/5/3.
 * 络漫宝 连接展示 列表
 */
public class LMBaoLinkagePagerActivity extends HeaderBaseActivity {
    private LinkagePagerContainer customPagerContainer;
    private LinkagePager pager;
    private AppBarLayout appBarLayout;
    private int parallaxHeight;
    private View tab;
    private RoamBoxFunction roamBoxFunction;
    private List<TouchDBModel> mTouchList = new ArrayList<>();
    private WifiAdmin wifiAdmin;

    private RoamBoxSettingDialog roamBoxSettingDialog;
    private RoamDialog roamDialog;
    private static final int CALL_TYPE_INTERNET = 1; //上网模式修改
    private static final int CALL_TYPE_WIFI = 2; //wifi 设置
    private static final int CALL_TYPE_ROAM_PHONE = 3; //络漫宝号码设置
    private static final int CALL_TYPE_ADVANCED = 4;//高级设置
    private static final int CALL_TYPE_RESTART = 5;//重启络漫宝
    private int callType = 0;
    private TouchDBModel touchDBModel;
    private int connectNumber = 0;
    private RoamBoxMessageReceiver boxMessageReceiver;
    private MyListPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_sync_collapsing);
        initView();
        boxMessageReceiver = new RoamBoxMessageReceiver(getApplicationContext(), iCallback);
        boxMessageReceiver.register(boxMessageReceiver);
    }

    private void initView() {
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(KeyValue.TAB_TARGET, KeyValue.TAB_HOME);
                bundle.putString(KeyValue.SHOW_LEFT_MENU, "true");//显示左侧侧滑
                toActivityClearTopWithState(LinphoneActivity.class, bundle);
                EventBus.getDefault().post(new EventRefresh());
            }
        });
        headerLayout.showRightImageButton(R.drawable.add, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //RoamApplication.RoamBoxIsSuccess = false;
                toActivity(LMBAOIndexActivity.class, null);
            }
        });
        parallaxHeight = getResources().getDimensionPixelSize(R.dimen.cover_pager_height) - getResources().getDimensionPixelSize(R.dimen.tab_height);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        customPagerContainer = (LinkagePagerContainer) findViewById(R.id.pager_container);
        tab = findViewById(R.id.tab);
        pager = (LinkagePager) findViewById(R.id.pager);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) >= parallaxHeight) {
                    tab.setVisibility(View.VISIBLE);
                } else {
                    tab.setVisibility(View.GONE);
                }
            }
        });

        customPagerContainer.setPageItemClickListener(new PageItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                pager.setCurrentItem(position);
            }
        });
        roamBoxFunction = new RoamBoxFunction(this);
        roamBoxSettingDialog = new RoamBoxSettingDialog(this, getString(R.string.str_detection_internet));

        //roamDialog = new RoamDialog(this, getString(R.string.loadinginfo));
        //roamDialog.show();
        requestMyTouchDBModels();
    }

    /**
     * 检测络漫宝 状态
     */
    private void detectionRoamBox() {
        if (mTouchList != null && mTouchList.size() > 0) {
            LinphoneChatRoom chatRoom = LinphoneActivity.instance().initChatRoom("RoamBox-" + mTouchList.get(0).getDevid());
            LinphoneChatMessage message = chatRoom.createLinphoneChatMessage("{\"method\":\"get_all\",\"params\":[]}");
            message.setListener(listener);
            String callId = UUID.randomUUID().toString();
            message.addCustomHeader("Call-ID", callId);
            chatRoom.sendChatMessage(message);
        }
    }

    private RoamBoxMessageReceiver.iCallback iCallback = new RoamBoxMessageReceiver.iCallback() {
        @Override
        public void newMessageBack(Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle.getString("errorInfo") != null) {
                if (mTouchList != null && mTouchList.size() > 0) {
                    mTouchList.get(0).setOperator("-1");
                    mTouchList.get(0).setBoolNetwork(true);
                }
            } else {
                NetworkConfigBean model = (NetworkConfigBean) bundle.getSerializable(NetworkConfigBean.class.getName());
                String operator;

                if (model != null && model.operator != null) {
                    operator = model.operator;
                } else {
                    operator = "";
                }
                if (model != null) {
                    for (TouchDBModel item : mTouchList) {
                        if (item.getDevid().equals(model.devid)) {
                            item.setOperator(operator);
                            item.setBoolNetwork(true);
                        }
                    }
                }
            }
            setData();
        }
    };

    private LinphoneChatMessage.LinphoneChatMessageListener listener = new LinphoneChatMessage.LinphoneChatMessageListener() {
        @Override
        public void onLinphoneChatMessageStateChanged(LinphoneChatMessage msg, LinphoneChatMessage.State state) {
            org.linphone.mediastream.Log.e("消息发送回调---》", msg.getAppData() + "||" + state.toString() + "||" + msg.getCustomHeader("Call-ID"));
            if (mTouchList != null && mTouchList.size() > 0) {
                mTouchList.get(0).setOperator("-1");
                mTouchList.get(0).setBoolNetwork(false);
                setData();
            }
        }

        @Override
        public void onLinphoneChatMessageFileTransferReceived(LinphoneChatMessage msg, LinphoneContent content, LinphoneBuffer buffer) {

        }

        @Override
        public void onLinphoneChatMessageFileTransferSent(LinphoneChatMessage msg, LinphoneContent content, int offset, int size, LinphoneBuffer bufferToFill) {

        }

        @Override
        public void onLinphoneChatMessageFileTransferProgressChanged(LinphoneChatMessage msg, LinphoneContent content, int offset, int total) {

        }
    };

    class MyListPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mTouchList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            DataDemoView view = new DataDemoView(getApplicationContext(), mTouchList.get(position), callBack);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(getApplicationContext());
            view.setImageResource(R.drawable.roam_box_max);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return mTouchList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }
    }

    public DataDemoView.iViewCall callBack = new DataDemoView.iViewCall() {
        @Override
        public void callInternetSet(TouchDBModel touch) {
            //检测网络环境 获取最新的络漫宝配置
            callType = CALL_TYPE_INTERNET;
            touchDBModel = touch;
            isRoamBoxWifi(touch);
        }

        @Override
        public void callAdvancedSet(TouchDBModel touch) {
            callType = CALL_TYPE_ADVANCED;
            touchDBModel = touch;
            isRoamBoxWifi(touch);
        }

        @Override
        public void callRestart(final TouchDBModel touch) {
            final TipDialog dialog = new TipDialog(LMBaoLinkagePagerActivity.this, getString(R.string.dialong_title_restart_device), getString(R.string.please_restart_connection));
            dialog.setRightButton(getString(R.string.button_ok), new TipDialog.OnClickListener() {
                @Override
                public void onClick(int which) {
                    callType = CALL_TYPE_RESTART;
                    touchDBModel = touch;
                    isRoamBoxWifi(touch);
                }
            }).setLeftButton(getString(R.string.cancel), new TipDialog.OnClickListener() {
                @Override
                public void onClick(int which) {
                    dialog.dismiss();
                }
            }).show();

        }

        @Override
        public void callWifiSet(TouchDBModel touch) {
            callType = CALL_TYPE_WIFI;
            touchDBModel = touch;
            isRoamBoxWifi(touch);
        }

        @Override
        public void callPhoneSet(TouchDBModel touch) {
            callType = CALL_TYPE_ROAM_PHONE;
            touchDBModel = touch;
            isRoamBoxWifi(touch);
        }
    };

    private boolean isRoamBoxWifi(TouchDBModel touch) {
        wifiAdmin = new WifiAdmin(getApplicationContext());
        String ssId = wifiAdmin.getSSID().replace("\"", "");
        if (ssId.equals(touch.getLanSsid())) {
            uiHandler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS);
            return true;
        }

        ToastUtils.showToast(this, getString(R.string.str_error_no_lmb));
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_ROAM_BOX_TOKEN_SUCCESS:
                getLMBAOConfig(); //未做处理
                break;
            case MsgType.MSG_DETECT_NETWORK_SUCCESS:
                //获取最新配置成功
                roamBoxSettingDialog.dismiss();
                buttonCallback();
                break;
            case MsgType.MSG_DETECT_NETWORK_ERROR:
                //获取最新配置失败
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.str_get_lmb_config_error));
                break;
            case MsgType.MSG_ROAM_BOX_TOKEN_ERROR:
            case MsgType.MSG_ROAM_BOX_TOKEN_TIMEOUT:
                roamBoxSettingDialog.dismiss();
                ToastUtils.showToast(this, getString(R.string.please_check_has_been_connected_to_roam_box));
                break;
            case MsgType.MSG_RESTART_ROAM_BOX_SUCCESS:
                ToastUtils.showToast(this, getString(R.string.str_raom_box_restart_connect));
                //重启指令 发送成功
                break;
            case MsgType.MSG_DETECT_NETWORK_TIMEOUT:
                if (connectNumber >= 2) {
                    roamBoxSettingDialog.dismiss();
                    ToastUtils.showToast(this, getString(R.string.str_get_lmb_config_error));
                    return;
                }
                connectNumber++;
                restartRoamBox();
                break;
            case MsgType.MSG_CHANGE_ROAM_BOX:
                int position = (int) msg.obj;
                if (mTouchList.get(position) != null && mTouchList.get(position).getLanSsid() != null) {
                    headerLayout.showTitle(mTouchList.get(position).getLanSsid().replace("\"", ""));
                }
                break;
            case MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS:
                roamBoxSettingDialog.show();
                String IpAddress = MacAddressUtils.intToIpAddress(wifiAdmin.getIpAddress());
                IpAddress = IpAddress.substring(0, IpAddress.lastIndexOf(".") + 1) + "1";
                Constant.ROAM_BOX_AUTH = Constant.LMBAO_AUTH_START + IpAddress + Constant.LMBAO_AUTH_END;
                Constant.ROAM_BOX_CONFIG = Constant.LMBAO_AUTH_START + IpAddress + Constant.LMBAO_CONFIG_END;
                getAuthTokenNew(); //获取token
                break;
            case MsgType.MSG_GET_ROAM_BOX_SUCCESS:
                initTouchData();
                break;
            case MsgType.MSG_GET_ROAM_BOX_NO_DATA:
                TouchDBModel touchDBModel = (TouchDBModel) getBundle().getSerializable("touch");
                if (touchDBModel != null) {
                    mTouchList.clear();
                    mTouchList.add(touchDBModel);
                    initTouchData();
                    RoamApplication.RoamBoxList.add(touchDBModel);
                }
                break;
        }
    }

    private void initTouchData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                detectionRoamBox();
            }
        }).start();
        setData();
    }

    /**
     * 获取 token
     */
    private void getAuthTokenNew() {
        roamBoxFunction.getRoamBoxAuthToken(Constant.ROAM_BOX_AUTH, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                    }.getType());
                    if (result != null && result.attributes != null) {
                        RoamApplication.RoamBoxToken = result.attributes;
                        Log.e("络漫宝Token--->", RoamApplication.RoamBoxToken);
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
    private void getLMBAOConfig() {
        //获取络漫宝 配置
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
                    if (networkConfigBean != null) {
                        RoamApplication.RoamBoxConfigOld = networkConfigBean.attributes;
                        Log.e("络漫宝配置--->", RoamApplication.RoamBoxConfigOld.toString());
                        uiHandler.obtainMessage(MsgType.MSG_DETECT_NETWORK_SUCCESS, RoamApplication.RoamBoxConfigOld).sendToTarget();
                    } else {
                        uiHandler.sendEmptyMessage(MsgType.MSG_DETECT_NETWORK_ERROR);
                    }
                }
            }
        });

    }

    private void setData() {
        if (mTouchList != null && !mTouchList.isEmpty() && mTouchList.size() > 0) {
            if (RoamApplication.RoamBoxConfigOld != null) {
                for (int i = 0; i < mTouchList.size(); i++) { //替换内容
                    if (RoamApplication.RoamBoxConfigOld.devid.equals(mTouchList.get(i).getDevid())) {
                        mTouchList.get(i).setWanType(RoamApplication.RoamBoxConfigOld.wan_type);
                        mTouchList.get(i).setWanProto(RoamApplication.RoamBoxConfigOld.wan_proto);
                        mTouchList.get(i).setLanSsid(RoamApplication.RoamBoxConfigOld.lan_ssid);
                        mTouchList.get(i).setPhone(RoamApplication.RoamBoxConfigOld.phone);
                        break;
                    }
                }
            }
            headerLayout.showTitle(mTouchList.get(0).getLanSsid());
            final LinkagePager cover = customPagerContainer.getViewPager();
            PagerAdapter coverAdapter = new MyPagerAdapter();
            cover.setAdapter(coverAdapter);
            cover.setOffscreenPageLimit(mTouchList.size());
            new CoverFlow.Builder()
                    .withLinkage(cover)
                    .pagerMargin(0f)
                    .scale(0.3f)
                    .spaceSize(0f)
                    .build();

            adapter = new MyListPagerAdapter();
            pager.setOffscreenPageLimit(mTouchList.size());
            pager.setAdapter(adapter);
            cover.setLinkagePager(pager);
            pager.setLinkagePager(cover);
            pager.addOnPageChangeListener(new LinkagePager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    uiHandler.obtainMessage(MsgType.MSG_CHANGE_ROAM_BOX, position).sendToTarget();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

    }

    /**
     * 重启络漫宝
     */
    private void restartRoamBox() {
        roamBoxFunction.restartRoamBox(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MsgType.MSG_RESTART_ROAM_BOX_SUCCESS);
            }

            @Override
            public void onSuccess(String response) {
                uiHandler.sendEmptyMessage(MsgType.MSG_RESTART_ROAM_BOX_SUCCESS);
            }
        });
    }

    private void buttonCallback() {
        switch (callType) {
            case CALL_TYPE_INTERNET:
                if (touchDBModel.getWanType().equals("line")) {
                    if (touchDBModel.getWanProto().equals("dhcp")) {
                        toActivity(LMBAOAutoInternetEditActivity.class, null);
                    }
                    if (touchDBModel.getWanProto().equals("pppoe")) {
                        toActivity(LMBAOBroadbandDialEditActivity.class, null);
                    }
                    if (touchDBModel.getWanProto().equals("static")) {
                        toActivity(LMBAOStaticIpEditActivity.class, null);
                    }
                } else {
                    toActivity(LMBAOWirelessRelayEditActivity.class, null);
                }

                break;
            case CALL_TYPE_WIFI:
                Bundle bundle = new Bundle();
                bundle.putString("wifi_name", touchDBModel.getWifiSsid());
                bundle.putString("wifi_password", touchDBModel.getWifiPassword());
                toActivity(LMBAOWifiEditActivity.class, bundle);
                break;
            case CALL_TYPE_ROAM_PHONE:
                toActivity(LMBAOPhoneEditActivity.class, null);
                break;
            case CALL_TYPE_ADVANCED:
                toActivity(LMBAOSeniorSettingActivity.class, null);
                break;
            case CALL_TYPE_RESTART:
                restartRoamBox();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (boxMessageReceiver != null) {
            boxMessageReceiver.unRegister(boxMessageReceiver);
        }
    }

    public void requestMyTouchDBModels() {
        JSONObject json = getAuthJSONObject();
        new HttpFunction(this).postJsonRequest(Constant.GET_ROAM_BOX_LIST, json, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                UCResponse<TouchRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<TouchRDO>>() {
                });
                if (result != null && result.getAttributes() != null
                        && result.getAttributes().getTouchs() != null && result.getAttributes().getTouchs().size() > 0) {
                    mTouchList = result.getAttributes().getTouchs();
                    uiHandler.sendEmptyMessage(MsgType.MSG_GET_ROAM_BOX_SUCCESS);
                } else {
                    uiHandler.sendEmptyMessage(MsgType.MSG_GET_ROAM_BOX_NO_DATA);
                }
            }

            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
                uiHandler.sendEmptyMessage(MsgType.MSG_GET_ROAM_BOX_NO_DATA);
            }
        });
    }
}
