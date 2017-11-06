package com.roamtech.telephony.roamapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.ServicePackageCallback;
import com.roamtech.telephony.roamapp.bean.ServicePackage;
import com.roamtech.telephony.roamapp.bean.VoiceTalk;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MyRoamNumberActivity extends BaseActivity {
    protected SimpleDateFormat formatterToDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private TextView tvSetTransfer;
    private TextView tvPhone;
    private TextView tvRemainTime;
    private TextView effectDate;

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_roamnumber;
    }

    private void getRoamNumberData() {
        LinphoneActivity.instance()
                .requestAllTrafficvoice(new ServicePackageCallback() {

                    @Override
                    public void handle(List<ServicePackage> sps, VoiceTalk vt) {
                        if (LinphoneActivity.isInstanciated()) {
                            List<ServicePackage> voicenumbers = LinphoneActivity.instance().getVoiceNumberPackages(sps);
                            if (voicenumbers != null && !voicenumbers.isEmpty()) {
                                Collections.sort(voicenumbers);
                                receiveRoamNumber(voicenumbers.get(0), vt);
                            } else {
                                receiveRoamNumber(null, vt);
                            }
                        }
                    }
                },false);
        /*
        showDialog("加载中...");
        JSONObject json = getAuthJSONObject();
        OkHttpUtil.postJsonRequest(getUCServiceUrl("voiceavailable_get"), json, hashCode(), new OKCallback<ServiceRDO>(new TypeToken<UCResponse<ServiceRDO>>(){}) {
            public void onResponse(int statuscode, UCResponse<ServiceRDO> ucResponse) {
                if (isSucccess()) {
                    VoiceNumber voiceNumber = ucResponse.getAttributes().getVoiceNumber();
                    VoiceTalk voiceTalk = ucResponse.getAttributes().getVoiceTalk();
                    receiveRoamNumber(voiceNumber, voiceTalk);
                } else {
                    showFailDialog();
                }
                dismissDialog();
            }

            @Override
            public void onFailure(IOException e) {
                dismissDialog();
                showToast(LoadingState.getErrorState(e).getText());
                showFailDialog();
            }
        });*/
    }

    private void showFailDialog() {
        new AlertDialog.Builder(MyRoamNumberActivity.this).setMessage("加载失败").setPositiveButton("重新加载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getRoamNumberData();
            }
        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        }).setCancelable(false).show();
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        tvSetTransfer = (TextView) findViewById(R.id.tv_setTransfer);
        tvPhone = (TextView) findViewById(R.id.tv_roamPhone);
        tvRemainTime = (TextView) findViewById(R.id.tv_remainTime);
        effectDate = (TextView) findViewById(R.id.tv_effectdate);
        getRoamNumberData();
    }

    private void receiveRoamNumber(ServicePackage voiceNumber, VoiceTalk voiceTalk) {
        int remainTime = voiceTalk!=null&&voiceTalk.getRemaindertime()!=null?voiceTalk.getRemaindertime().intValue():0;
        tvRemainTime.setText(String.format(Locale.CHINA, "剩余时长：%d分钟", remainTime));
        if (voiceNumber != null) {
            setRoamPhone(voiceNumber.getPhone());
            if(voiceNumber.getPhone()!=null){
                tvPhone.setText(getString(R.string.roamChinaNumber) + voiceNumber.getPhone());
            }else{
                tvPhone.setText(getString(R.string.roamChinaNumber) + "无");
            }
            Date startTime = voiceNumber.getStartTime();
            Date endTime = voiceNumber.getEndTime();
            if (startTime != null && endTime != null) {
                effectDate.setText(String.format("使用日期：%s至%s", formatDate(startTime), formatDate(endTime)));
            } else if (startTime != null) {
                effectDate.setText(String.format("使用日期：%s开始生效", formatDate(startTime)));
            } else if (endTime != null) {
                effectDate.setText(String.format("使用日期：请于%s前使用", formatDate(endTime)));
            }
        } else {
            tvPhone.setText(getString(R.string.roamChinaNumber) + "无");
        }
    }

    protected String formatDate(Date date) {
        return formatterToDay.format(date);
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        tvSetTransfer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == tvSetTransfer) {
            toActivity(CallTransferActivity.class, null);
        }
    }
}
