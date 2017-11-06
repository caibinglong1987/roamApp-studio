package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;

/**
 * Created by long
 * on 2016/10/10 14:55
 * 络漫宝 上网方式 （拨号、静态IP、自动获取、无线）
 */

public class LMBAOCheckIWayActivity extends HeaderBaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmb_manual_check);
    }
}
