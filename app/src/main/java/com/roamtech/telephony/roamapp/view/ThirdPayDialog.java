package com.roamtech.telephony.roamapp.view;

/**
 * Created by long
 * on 2016/9/28 16:53
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.bean.OrderBean;
import com.roamtech.telephony.roamapp.db.dao.CommonDao;
import com.roamtech.telephony.roamapp.db.model.PaymentDBModel;
import com.roamtech.telephony.roamapp.util.PayUtil;

import org.json.JSONObject;

import java.util.List;

/**
 * 支付 dialog
 */
public class ThirdPayDialog extends Dialog {
    public static final int PAY_RESULT_SUCCESS = 0x12; //支付成功
    public static final int PAY_RESULT_ERROR = 0x13; //支付 失败
    private static final int ZFB_PAY_ID = 4;
    private static final int WX_PAY_ID = 5;
    private static final int CRED_TRIP_ID = 6;
    private static final int UP_PAY_ID = 7; //银联支付

    public ThirdPayDialog(Context context) {
        super(context);
    }

    public ThirdPayDialog(Context context, int theme) {
        super(context, theme);
    }
    protected ThirdPayDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public static class Builder implements View.OnClickListener {
        private Activity context;
        private Handler handler;
        private ThirdPayDialog dialog;
        private LinearLayout layout_zfb, layout_wx_pay, layout_xc_pay, layout_up_pay;
        private RelativeLayout layout_body;
        private ImageView iv_close;
        private OrderBean orderBean;
        private TextView tv_total_price;
        private JSONObject loginInfo;
        private TextView tv_cred_trip, tv_zfb, tv_wx_pay, tv_up_pay;

        public Builder(Activity context, Handler handler) {
            this.context = context;
            this.handler = handler;
        }

        public Builder setOrder(OrderBean order, JSONObject loginInfo) {
            this.orderBean = order;
            this.loginInfo = loginInfo;
            return this;
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.layout_zfb:
                    new PayUtil(context, handler).getPayParams(loginInfo, orderBean.id, 4);
                    dialog.dismiss();
                    break;
                case R.id.layout_wx_pay:
                    new PayUtil(context, handler).getPayParams(loginInfo, orderBean.id, 5);
                    dialog.dismiss();
                    break;
                case R.id.iv_close:
                    dialog.dismiss();
                    break;
                case R.id.layout_xc_pay:
                    new PayUtil(context, handler).getPayParams(loginInfo, orderBean.id, 6);
                    dialog.dismiss();
                    break;
                case R.id.layout_up_pay:
                    // “00” – 银联正式环境
                    // “01” – 银联测试环境，该环境中不发生真实交易
                    new PayUtil(context, handler).getPayParams(loginInfo, orderBean.id, 7);
                    dialog.dismiss();
                    break;
            }

        }

        public ThirdPayDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            dialog = new ThirdPayDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_third_pay, null);
            layout_body = (RelativeLayout) layout.findViewById(R.id.layout_body);
            layout_zfb = (LinearLayout) layout.findViewById(R.id.layout_zfb);
            layout_wx_pay = (LinearLayout) layout.findViewById(R.id.layout_wx_pay);
            layout_xc_pay = (LinearLayout) layout.findViewById(R.id.layout_xc_pay);
            layout_up_pay = (LinearLayout) layout.findViewById(R.id.layout_up_pay);
            tv_total_price = (TextView) layout.findViewById(R.id.tv_total_price);
            iv_close = (ImageView) layout.findViewById(R.id.iv_close);
            tv_cred_trip = (TextView) layout.findViewById(R.id.tv_cred_trip);
            tv_zfb = (TextView) layout.findViewById(R.id.tv_zfb);
            tv_wx_pay = (TextView) layout.findViewById(R.id.tv_wx_pay);
            tv_up_pay = (TextView) layout.findViewById(R.id.tv_up_pay);
            tv_total_price.setText(String.format(context.getString(R.string.price), orderBean.payable_amount));

            CommonDao<PaymentDBModel> commonDao = new CommonDao<>(RoamApplication.sDatabaseHelper, PaymentDBModel.class);
            List<PaymentDBModel> listPayWay = commonDao.queryAll();
            if (listPayWay != null) {
                for (int i = 0; i < listPayWay.size(); i++) {
                    if (listPayWay.get(i).id == ZFB_PAY_ID) {
                        if (listPayWay.get(i).name != null && listPayWay.get(i).name.length() > 0) {
                            tv_zfb.setText(listPayWay.get(i).name);
                        }
                        layout_zfb.setVisibility(View.VISIBLE);
                    }
                    if (listPayWay.get(i).id == WX_PAY_ID) {
                        if (listPayWay.get(i).name != null && listPayWay.get(i).name.length() > 0) {
                            tv_wx_pay.setText(listPayWay.get(i).name);
                        }
                        layout_wx_pay.setVisibility(View.VISIBLE);
                    }
                    if (listPayWay.get(i).id == CRED_TRIP_ID) {
                        if (listPayWay.get(i).name != null && listPayWay.get(i).name.length() > 0) {
                            tv_cred_trip.setText(listPayWay.get(i).name);
                        }
                        layout_xc_pay.setVisibility(View.VISIBLE);
                    }

                    if (listPayWay.get(i).id == UP_PAY_ID) {
                        if (listPayWay.get(i).name != null && listPayWay.get(i).name.length() > 0) {
                            tv_up_pay.setText(listPayWay.get(i).name);
                        }
                        layout_up_pay.setVisibility(View.VISIBLE);
                    }
                }
            }

            layout_body.setOnClickListener(this);
            layout_zfb.setOnClickListener(this);
            layout_wx_pay.setOnClickListener(this);
            layout_xc_pay.setOnClickListener(this);
            layout_up_pay.setOnClickListener(this);
            iv_close.setOnClickListener(this);
            Window win = dialog.getWindow();
            win.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = win.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            win.setAttributes(lp);
            dialog.setContentView(layout);
            return dialog;
        }
    }

}
