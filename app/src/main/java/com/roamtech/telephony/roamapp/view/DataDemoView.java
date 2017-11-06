package com.roamtech.telephony.roamapp.view;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;
import com.roamtech.telephony.roamapp.util.StringUtil;


/**
 * Created by long
 * on 16/5/3.
 */
public class DataDemoView extends LinearLayout {
    private TouchDBModel touch;
    private iViewCall viewCall;

    public DataDemoView(Context context, TouchDBModel touch, iViewCall callback) {
        super(context);
        this.touch = touch;
        this.viewCall = callback;
        initView(context);
    }

    public DataDemoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DataDemoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View view = inflate(context, R.layout.fragment_lbmao_status, this);
        String mode_desc = "";
        if (touch.getWanType().equals("line")) { //有线
            switch (touch.getWanProto()) {
                case "dhcp":
                    mode_desc = context.getString(R.string.activity_title_auto_obtain_ip);
                    break;
                case "pppoe":
                    mode_desc = context.getString(R.string.activity_title_broadband_internet);
                    break;
                case "static":
                    mode_desc = context.getString(R.string.btn_static_ip);
                    break;
            }
        } else { //无线模式
            mode_desc = context.getString(R.string.activity_title_wireless_internet);
        }
        ((TextView) view.findViewById(R.id.tv_internet)).setText(mode_desc);
        ((TextView) view.findViewById(R.id.tv_phone_number)).setText(touch.getPhone());
        TextView tv_internet_sim_status = (TextView) view.findViewById(R.id.tv_internet_sim_status);
        ImageView iv_status = (ImageView) view.findViewById(R.id.iv_status);
        LinearLayout layout_internet_set = (LinearLayout) view.findViewById(R.id.layout_internet_set);
        LinearLayout layout_wifi_set = (LinearLayout) view.findViewById(R.id.layout_wifi_set);
        LinearLayout layout_lmb_phone_set = (LinearLayout) view.findViewById(R.id.layout_lmb_phone_set);
        LinearLayout layout_advanced_set = (LinearLayout) view.findViewById(R.id.layout_advanced_set);
        LinearLayout layout_restart = (LinearLayout) view.findViewById(R.id.layout_restart);
        if (touch.isBoolNetwork()) {
            if (!StringUtil.isBlank(touch.getOperator())) {
                if (touch.getOperator().equals("-1")) {
                    iv_status.setImageResource(R.drawable.network_2);
                    tv_internet_sim_status.setText(context.getString(R.string.roam_box_network_error));
                } else {
                    iv_status.setImageResource(R.drawable.network);
                    tv_internet_sim_status.setText(context.getString(R.string.roam_box_network) + "," + context.getString(R.string.roam_box_sim));
                }
            } else {
                iv_status.setImageResource(R.drawable.network_2);
                tv_internet_sim_status.setText(context.getString(R.string.roam_box_network) + "," + context.getString(R.string.roam_box_no_sim));
            }
        } else {
            iv_status.setImageResource(R.drawable.network_2);
            tv_internet_sim_status.setText(context.getString(R.string.roam_box_network_error));
        }

        layout_internet_set.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewCall != null) {
                    viewCall.callInternetSet(touch);
                }
            }
        });

        layout_wifi_set.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewCall != null) {
                    viewCall.callWifiSet(touch);
                }
            }
        });

        layout_lmb_phone_set.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewCall != null) {
                    viewCall.callPhoneSet(touch);
                }
            }
        });

        layout_advanced_set.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewCall != null) {
                    viewCall.callAdvancedSet(touch);
                }
            }
        });

        layout_restart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewCall != null) {
                    viewCall.callRestart(touch);
                }
            }
        });

        ViewCompat.setNestedScrollingEnabled(view, true);
    }

    public interface iViewCall {
        void callInternetSet(TouchDBModel touch);

        void callWifiSet(TouchDBModel touch);

        void callPhoneSet(TouchDBModel touch);

        void callAdvancedSet(TouchDBModel touch);

        void callRestart(TouchDBModel touch);
    }

}
