package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.utils.L;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;

public class CommunicationSettingActivity extends BaseActivity {

    private LinearLayout setApnLayout;
    private LinearLayout setTransferLayout;
    private LinearLayout layout_set_roam_box;
    @Override
    public int getLayoutId() {
        return R.layout.activity_communication_setting;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setApnLayout = (LinearLayout) findViewById(R.id.layout_setApn);
        setTransferLayout = (LinearLayout) findViewById(R.id.layout_setTransfer);
        layout_set_roam_box = (LinearLayout) findViewById(R.id.layout_set_roam_box);
    }

    @Override
    public void setListener() {
        super.setListener();
        setApnLayout.setOnClickListener(this);
        setTransferLayout.setOnClickListener(this);
        layout_set_roam_box.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.layout_setApn:
                toActivity(ApnSettingActivity.class, null);
                break;
            case R.id.layout_setTransfer:
                toActivity(CallTransferActivity.class, null);
                break;
            case R.id.layout_set_roam_box:
                toActivity(LMBAOIndexActivity.class, null);
                break;
        }
    }
}
