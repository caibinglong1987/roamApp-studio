package com.roamtech.telephony.roamapp.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;


/**
 * 自定义title
 */
public class HeaderLayout extends LinearLayout {
    private LayoutInflater mInflater;
    private RelativeLayout header;
    private TextView titleView;
    private LinearLayout leftContainer, rightContainer;
    private Button backBtn;
    private TextView submit;
    private View imageViewLayout;
    private TextView rightButton;

    public HeaderLayout(Context context) {
        super(context);
        init();
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mInflater = LayoutInflater.from(getContext());
        header = (RelativeLayout) mInflater.inflate(R.layout.base_common_header, null, false);
        titleView = (TextView) header.findViewById(R.id.titleView);
        leftContainer = (LinearLayout) header.findViewById(R.id.leftContainer);
        rightContainer = (LinearLayout) header.findViewById(R.id.rightContainer);
        backBtn = (Button) header.findViewById(R.id.id_toback);
        submit = (TextView) header.findViewById(R.id.submit);
        addView(header);
    }

    public void showTitle(int titleId) {
        titleView.setText(titleId);
    }

    public void showTitle(String s) {
        titleView.setText(s);
    }

    public void showTitle(String s,int textSize,int textColor){
        titleView.setText(s);
        titleView.setTextColor(textColor);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,textSize);
    }

    public void showLeftBackButton(OnClickListener listener) {
        showLeftBackButton(R.string.empty_header_str, listener);
    }

    public void showLeftBackButton() {
        showLeftBackButton(null);
    }

    public void showLeftBackButton(int backTextId, OnClickListener listener) {
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.nav_back_arrow, 0, 0, 0);
        backBtn.setText(backTextId);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        backBtn.setOnClickListener(listener);
    }

    public void showLeftBackButton(int backTextId, int resId,OnClickListener listener) {
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        backBtn.setOnClickListener(listener);
    }

    public void showLeftBackButton(int backTextId, OnClickListener listener, boolean isback) {
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setText(backTextId);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        backBtn.setOnClickListener(listener);
    }

    public void showRightSubmitButton(int backTextId, OnClickListener listener) {
        submit.setVisibility(View.VISIBLE);
        submit.setText(backTextId);
        if (listener == null) {
            listener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) getContext()).finish();
                }
            };
        }
        submit.setOnClickListener(listener);
    }


    public void showLeftImageButton(int rightResId, OnClickListener listener) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton leftButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
        leftButton.setImageResource(rightResId);
        leftButton.setOnClickListener(listener);
        leftContainer.addView(imageViewLayout);
    }

    public void showLeftImageButton(int rightResId, OnClickListener listener, boolean isShow) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton leftButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
        leftButton.setImageResource(rightResId);
        leftButton.setOnClickListener(listener);
        leftContainer.addView(imageViewLayout);
    }

    public void showRightImageButton(int rightResId, OnClickListener listener) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton rightButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
        rightButton.setImageResource(rightResId);
        rightButton.setOnClickListener(listener);
        rightContainer.addView(imageViewLayout);
    }

    public void showRightImageButton(int rightResId, OnClickListener listener, boolean isShow) {
        imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_image_btn, null, false);
        ImageButton rightButton = (ImageButton) imageViewLayout.findViewById(R.id.imageBtn);
        rightButton.setImageResource(rightResId);
        rightButton.setOnClickListener(listener);
        rightContainer.addView(imageViewLayout);
    }

    public void removeView() {
        rightContainer.removeView(imageViewLayout);
    }

    public void showRightTextButton(int color, int rightResId, OnClickListener listener) {
        View imageViewLayout = mInflater.inflate(R.layout.base_common_header_right_btn, null, false);
        rightButton = (TextView) imageViewLayout.findViewById(R.id.textBtn);
        rightButton.setText(rightResId);
        rightButton.setTextColor(color);
        rightButton.setOnClickListener(listener);
        rightContainer.addView(imageViewLayout);
    }


    public void setRightTextButton(int color){
        rightButton.setTextColor(color);
    }

    public void setLeftTextButton(int color, int rightResId){
        backBtn.setText(rightResId);
        backBtn.setTextColor(color);
    }

    public void setHeaderBackground(int color){
        header.setBackgroundColor(color);
    }

}
