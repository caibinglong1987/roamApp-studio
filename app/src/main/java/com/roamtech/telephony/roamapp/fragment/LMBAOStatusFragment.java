package com.roamtech.telephony.roamapp.fragment;

import android.os.Bundle;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseFragment;

/**
 * Created by long on 2016/10/11.
 * 络漫宝 连接状态 设置 fragment
 */

public class LMBAOStatusFragment extends BaseFragment {
    private static final String ARG_POSITION = "position";
    private int itemPos = 0;

    public static LMBAOStatusFragment newInstance(int position) {
        LMBAOStatusFragment f = new LMBAOStatusFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_lbmao_status;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        itemPos = getArguments().getInt(ARG_POSITION, 0);
    }
}
