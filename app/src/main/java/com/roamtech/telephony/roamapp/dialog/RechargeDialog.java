package com.roamtech.telephony.roamapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.bean.Evoucher;
import com.roamtech.telephony.roamapp.util.AreaMap;
import com.roamtech.telephony.roamapp.util.LocalDisplay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author 实名认证窗口
 */
public class RechargeDialog extends Dialog implements
        View.OnClickListener {
    private SimpleDateFormat formatterToSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat formatterToDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Activity mActivity;
    private Button btnOk;
    private TextView tvTip;
    private String tipMessage;
    private Evoucher mEvoucher;
    private ImageView ivArea;
    private TextView tvName;
    private TextView tvEffectDate;
    private TextView tvOrderId;

    private RechargeDialog(Context context, int defStyle) {
        super(context, defStyle);
    }

    @Override
    public void show() {
        super.show();
    }

    public RechargeDialog(Activity context, Evoucher evoucher, String tipMessage) {
        this(context, R.style.dialog_fullscreen);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        mEvoucher = evoucher;
        mActivity = context;
        this.tipMessage = tipMessage;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_recharge);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = LocalDisplay.SCREEN_WIDTH_PIXELS;
        // lp.height = LocalDisplay.SCREEN_HEIGHT_PIXELS;
        getWindow().setAttributes(lp);
        initView();
    }

    private void initView() {
        ivArea = (ImageView) findViewById(R.id.iv_area);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvEffectDate = (TextView) findViewById(R.id.tv_effectdate);
        tvOrderId = (TextView) findViewById(R.id.tv_orderId);
        if (AreaMap.getResId(mEvoucher.getAreaName()) != null) {
            ivArea.setImageResource(AreaMap.getResId(mEvoucher.getAreaName()));
        } else {
            ivArea.setVisibility(View.GONE);
        }
        Date effectDate = mEvoucher.getStartTime();
        Date failureDate = mEvoucher.getEndTime();
        int days = (int) ((failureDate.getTime() - effectDate.getTime()) / (24 * 60 * 60 * 1000)) + 1;
        tvName.setText(String.format("%s [ %d天 ] 流量套餐", mEvoucher.getAreaName(), days));
        tvOrderId.setText(String.format(Locale.US, "所属订单：%d", mEvoucher.getOrderid()));
        if (effectDate != null && failureDate != null) {
            tvEffectDate.setText(String.format("%s 至 %s", formatDate(effectDate), formatDate(failureDate)));
        } else if (effectDate != null) {
            tvEffectDate.setText(String.format("%s开始生效", formatDate(effectDate)));
        } else if (failureDate != null) {
            tvEffectDate.setText(String.format("请于%s前使用", formatDate(failureDate)));
        }
    }

    private String format(String strDate) {
        Date date = parseStrDate(strDate);
        if (date != null) {
            return formatDate(date);
        }
        return null;
    }

    private Date parseStrDate(String strDate) {
        try {
            return formatterToSecond.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String formatDate(Date date) {
        return formatterToDay.format(date);
    }

    @Override
    public void onClick(View v) {

    }
}
