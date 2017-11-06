package com.roamtech.telephony.roamapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;

/**
 * Created by asus on 2016/8/23.
 */
public class TalkTimeDialog extends Dialog implements View.OnClickListener {

    private TextView mTvRemanTalkTime;
    private TextView mTvExclusiveNumber;
    private TextView mTvRightNowSet;
    private TextView mTvTaklTimeStart;
    private TextView mTvTalkTimeEnd;
    private TextView mTvTalkTimeReminDay;
    private Context mContext;
    public OnRightNowSetListener mOnRightNowSetListener;
    private RelativeLayout mRlRemainderTime;

    public TalkTimeDialog(Context context, int theme) {
        super(context, R.style.dialog_card);
        this.mContext = context;
    }

    public interface OnRightNowSetListener {
        public void doSet();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_layout_talk_time);
        initView();
        initListener();
    }

    private void initListener() {
        mTvRightNowSet.setOnClickListener(this);
    }

    private void initView() {
        mRlRemainderTime = (RelativeLayout) findViewById(R.id.rl_remainde_time);
        //剩余时间
        mTvRemanTalkTime = (TextView) findViewById(R.id.tv_remain_talk_time);
        //专属号码
        mTvExclusiveNumber = (TextView) findViewById(R.id.tv_exclusive_number);

        mTvRightNowSet = (TextView) findViewById(R.id.tv_rightnow_set);
        //开始的时间
        mTvTaklTimeStart = (TextView) findViewById(R.id.tv_talk_time_start);
        //结束的时间
        mTvTalkTimeEnd = (TextView) findViewById(R.id.tv_talk_time_end);

        //剩余几天
        mTvTalkTimeReminDay = (TextView) findViewById(R.id.tv_talk_time_remain_day);

    }


    public void setTvRemanTalkTime(String minute) {
        mTvRemanTalkTime.setText(minute);
    }

    //设置专属号
    public void setTvExclusiveNumber(String excuteumber) {
        mTvExclusiveNumber.setText(excuteumber);
    }

    public void setRlRemainderTimeGone() {
        mRlRemainderTime.setVisibility(View.GONE);
    }

    public void setTvTaklTimeStart(String starttime) {
        mTvTaklTimeStart.setText(starttime);
    }

    //结束的时间
    public void setTvTalkTimeEn(String endtime) {
        mTvTalkTimeEnd.setText(endtime);
    }


    //剩余的天数
    public void setTvTalkTimeReminDay(String day) {
        mTvTalkTimeReminDay.setText(day);
    }

    public void setOnRightNowListener(OnRightNowSetListener onRightNowSetListener) {
        mOnRightNowSetListener = onRightNowSetListener;
    }


    @Override
    public void onClick(View v) {
        //
        if (v == mTvRightNowSet) {
            //跳到来电转接界面
            mOnRightNowSetListener.doSet();

        }


    }
}
