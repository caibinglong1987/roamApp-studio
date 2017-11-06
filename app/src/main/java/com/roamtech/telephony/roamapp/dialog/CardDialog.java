package com.roamtech.telephony.roamapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.bean.ServicePackage;

import java.util.ArrayList;

/**
 * Created by asus on 2016/8/19.
 */
public class CardDialog extends Dialog implements View.OnClickListener {

    private ArrayList<ServicePackage> dataList;

    private String mText;
    private TextView mTvSimisNum;
    private TextView mMTvStartTime;
    private TextView mMTvEndView;
    private TextView mTvReaminTime;
    private TextView mTvCountryName;
    private Button mBTnShowHome;

    OnShowHomepageListener mOnShowHomepageListener;

    public interface OnShowHomepageListener {
        void showHome();
    }


    public CardDialog(Context context, boolean cancelable, OnCancelListener cancelListener, ArrayList<ServicePackage> dataList) {
        super(context, cancelable, cancelListener);

    }

    public CardDialog(Context context, int theme) {

        super(context, R.style.dialog_card);


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_flow_details);
        initView();

    }

    private void initView() {
        //上网卡号
        mTvSimisNum = (TextView) findViewById(R.id.simis_number_tv);
        //开始时间
        mMTvStartTime = (TextView) findViewById(R.id.starttime_tv);
        //结束时间
        mMTvEndView = (TextView) findViewById(R.id.endtime_tv);
        //剩余时间
        mTvReaminTime = (TextView) findViewById(R.id.remaintime_tv);
        //使用套餐的国家
        mTvCountryName = (TextView) findViewById(R.id.use_state_tv);
        //在首页展示
        mBTnShowHome = (Button) findViewById(R.id.show_homepage_btn);
        mBTnShowHome.setOnClickListener(this);
    }


    public void setTvSimsNum(String num) {
        mTvSimisNum.setText(num);
    }

    public void setTvStartTime(String starttime) {
        mMTvStartTime.setText(starttime);

    }

    public void setTvEndTime(String endtime) {
        mMTvEndView.setText(endtime);
    }

    public void setReamintime(int remaintime) {
        mTvReaminTime.setText(remaintime + "");
    }

    public void setCountryName(String countryname) {
        mTvCountryName.setText(countryname);
    }

    public void setOnShowHomeListener(OnShowHomepageListener onShowHomeListener) {
        this.mOnShowHomepageListener = onShowHomeListener;

    }

        public void setTvShowHomeGone(){
            mBTnShowHome.setVisibility(View.GONE);
        }



    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.show_homepage_btn) {
            mOnShowHomepageListener.showHome();
        }


    }
}








