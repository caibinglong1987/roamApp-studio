package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.view.ActionSheetDialog;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by long
 * on 2016/10/12.
 * 自动获取 ip 上网模式
 */

public class LMBAOAutoInternetEditActivity extends HeaderBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_internet_auto);
        initView();
    }

    private void initView() {
        headerLayout.showTitle(getString(R.string.activity_title_auto_obtain_ip));
        headerLayout.showLeftBackButton(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        headerLayout.showRightSubmitButton(R.string.toggle_mode, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ActionSheetDialog(LMBAOAutoInternetEditActivity.this)
                        .builder()
                        .setCancelable(true)
                        .setCanceledOnTouchOutside(true)
                        .addSheetItem(getString(R.string.btn_static_ip), ActionSheetDialog.SheetItemColor.COLOR_888888,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        toActivityWithFinish(LMBAOStaticIpEditActivity.class, null);
                                    }
                                })
//                        .addSheetItem(getString(R.string.btn_wireless_relay), ActionSheetDialog.SheetItemColor.COLOR_888888,
//                                new ActionSheetDialog.OnSheetItemClickListener() {
//                                    @Override
//                                    public void onClick(int which) {
//                                        toActivityWithFinish(LMBAOWirelessRelayEditActivity.class, null);
//                                    }
//                                })
                        .addSheetItem(getString(R.string.activity_title_broadband), ActionSheetDialog.SheetItemColor.COLOR_888888,
                                new ActionSheetDialog.OnSheetItemClickListener() {
                                    @Override
                                    public void onClick(int which) {
                                        toActivityWithFinish(LMBAOBroadbandDialEditActivity.class, null);
                                    }
                                })
                        .show();
            }
        });
        if (!RoamApplication.RoamBoxConfigOld.wan_proto.equals("dhcp")
                || !RoamApplication.RoamBoxConfigOld.wan_type.equals("line")) {
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    setWanAuto();
                }
            }, 2000);
        }
    }

    private void setWanAuto() {
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("line");
        jsonArray.put("dhcp");
        new RoamBoxFunction(this).setRoamBoxWan(Constant.ROAM_BOX_CONFIG + "?auth=" + RoamApplication.RoamBoxToken, jsonArray, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                //uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_DHCP_TIMEOUT);
                Log.e("络漫宝设置自动上网--->", "请求超时");
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    CommonRoamBox<String> result = JsonUtil.fromJson(response, new TypeToken<CommonRoamBox<String>>() {
                    }.getType());
                    if (result != null && result.attributes != null && result.attributes.equals("true")) {
                        Log.e("络漫宝设置自动上网--->", result.attributes);
                        RoamApplication.RoamBoxConfigOld.wan_proto = RoamApplication.WAN_PROTO_DHCP;
                        RoamApplication.RoamBoxConfigOld.wan_type = "line";
                        //uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_DHCP_SUCCESS);
                    } else {
                        Log.e("络漫宝设置自动上网--->", "false");
                        //uiHandler.sendEmptyMessage(MsgType.MSG_SET_WAN_DHCP_ERROR);
                    }
                }
            }
        });
    }
}
