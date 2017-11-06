package com.roamtech.telephony.roamapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.bean.Evoucher;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.LocalDisplay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author 实名认证窗口
 */
public class CdfDialog extends Dialog implements
        View.OnClickListener {
    private SimpleDateFormat formatterToSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat formatterToDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private Activity mActivity;
    private TextView tvTip;
    private String tipMessage;
    private Evoucher mEvoucher;
    private ImageView ivQrCode;
    private CdfDialog(Context context, int defStyle) {
        super(context, defStyle);
    }

    @Override
    public void show() {
        super.show();
    }

    public CdfDialog(Activity context, Evoucher evoucher, String tipMessage) {
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
        setContentView(R.layout.dialog_cdf);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = LocalDisplay.SCREEN_WIDTH_PIXELS;
        // lp.height = LocalDisplay.SCREEN_HEIGHT_PIXELS;
        getWindow().setAttributes(lp);
        initView();
    }

    private void initView() {
        ivQrCode = (ImageView) findViewById(R.id.iv_qrcode);
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .build();
        ImageLoader.getInstance().displayImage(Constant.IMAGE_URL + mEvoucher.getImage(), new ImageViewAware(ivQrCode), options);
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
