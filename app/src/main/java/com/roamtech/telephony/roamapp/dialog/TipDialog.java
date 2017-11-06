package com.roamtech.telephony.roamapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.util.StringUtil;

/**
 * @author 实名认证窗口
 */
public class TipDialog extends Dialog implements View.OnClickListener {
    public static final int BUTTON_LEFT = -1;
    public static final int BUTTON_RIGHT = -2;
    private Context mContext;
    private TextView tvTitle;
    private TextView tvMessage;
    private TextView tvBtnLeft;
    private TextView tvBtnRight;
    private String title;
    private String message;
    private String textLeft;
    private String textRight;
    private OnClickListener leftClickListener;
    private OnClickListener rightClickListener;

    private TipDialog(Context context, int defStyle) {
        super(context, defStyle);
    }

    @Override
    public void show() {
        super.show();
    }

    public TipDialog(Context context, String title, String message) {
        this(context, R.style.dialog_tipStyle);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        mContext = context;
        this.title = title;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.dialog_tip);
        initView();
        setListener();
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvMessage = (TextView) findViewById(R.id.tv_message);
        tvBtnLeft = (TextView) findViewById(R.id.btn_left);
        tvBtnRight = (TextView) findViewById(R.id.btn_right);
        if (StringUtil.isTrimBlank(title)) {
            tvTitle.setVisibility(View.GONE);
        } else {
            tvTitle.setText(title);
        }
        if (StringUtil.isTrimBlank(message)) {
            tvMessage.setVisibility(View.GONE);
        } else {
            tvMessage.setText(message);
        }
        if (StringUtil.isTrimBlank(textLeft)) {
            tvBtnLeft.setVisibility(View.GONE);
            findViewById(R.id.dividing_line).setVisibility(View.GONE);
        } else {
            tvBtnLeft.setText(textLeft);
        }
        if (StringUtil.isTrimBlank(textRight)) {
            tvBtnRight.setVisibility(View.GONE);
        } else {
            tvBtnRight.setText(textRight);
        }
    }

    private void setListener() {
        tvBtnLeft.setOnClickListener(this);
        tvBtnRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == tvBtnLeft) {
            dismiss();
            if (leftClickListener != null) {
                leftClickListener.onClick(BUTTON_LEFT);
            }
        } else if (v == tvBtnRight) {
            dismiss();
            if (rightClickListener != null) {
                rightClickListener.onClick(BUTTON_RIGHT);
            }
        }
    }

    public TipDialog setLeftButton(String text, OnClickListener onClickListener) {
        textLeft = text;
        leftClickListener = onClickListener;
        return this;
    }

    public TipDialog setRightButton(String text, OnClickListener onClickListener) {
        textRight = text;
        rightClickListener = onClickListener;
        return this;
    }

    public interface OnClickListener {
        public void onClick(int which);
    }
}
