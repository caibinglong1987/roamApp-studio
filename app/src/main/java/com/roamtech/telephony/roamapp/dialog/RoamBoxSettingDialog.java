package com.roamtech.telephony.roamapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.application.RoamApplication;

public class RoamBoxSettingDialog extends Dialog {
    private TextView tvTitle, tv_cancel;
    private String mTitle;
    private iCallback callback;
    private Context context;
    public final int TYPE_ROAM_SET = 11;
    public final int TYPE_AUTO_INTERNET = 12;

    private RoamBoxSettingDialog(Context context, int defStyle) {
        super(context, defStyle);
        this.context = context;
    }

    private ImageView iv_roam_set;
    private ImageView iv_auto_wheel;
    private ImageView iv_internet;

    @Override
    public void show() {
        // TODO Auto-generated method stub
        super.show();
        RotateAnimation rota = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rota.setDuration(1000 * 10);      //旋转的一圈的周期
        rota.setRepeatCount(-1);   //设置动画无限循环
        // rota.setRepeatCount(5);    //设置动画循环五次
        LinearInterpolator lir = new LinearInterpolator();
        rota.setInterpolator(lir);
        iv_auto_wheel.startAnimation(rota);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        iv_auto_wheel.clearAnimation();
    }

    public RoamBoxSettingDialog(Context context, String title) {
        this(context, R.style.dialog_loadingStyle);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mTitle = title;
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void registerCallback(iCallback callback) {
        this.callback = callback;
    }

    /**
     * 是否隐藏 取消按钮
     *
     * @param isShow
     */
    public void showCancel(boolean isShow) {
        if (isShow) {
            tv_cancel.setVisibility(View.VISIBLE);
        } else {
            tv_cancel.setVisibility(View.GONE);
        }
    }

    /**
     * 设置显示哪种动画
     *
     * @param type
     */
    public void setAnimationType(int type) {
        switch (type) {
            case TYPE_ROAM_SET:
                iv_internet.setVisibility(View.GONE);
                iv_roam_set.setVisibility(View.VISIBLE);
                break;
            case TYPE_AUTO_INTERNET:
                iv_internet.setVisibility(View.VISIBLE);
                iv_roam_set.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_roam_box_setting);
        tvTitle = (TextView) findViewById(R.id.load_textView);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tvTitle.setText(mTitle);
        iv_roam_set = (ImageView) findViewById(R.id.iv_roam_set);
        iv_auto_wheel = (ImageView) findViewById(R.id.iv_auto_wheel);
        iv_internet = (ImageView) findViewById(R.id.iv_internet);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                if (callback != null) {
                    callback.cancel();
                }
            }
        });
    }

    /**
     * 取消回调
     */
    public interface iCallback {
        void cancel();
    }
}
