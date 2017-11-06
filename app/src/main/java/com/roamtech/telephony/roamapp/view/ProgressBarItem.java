package com.roamtech.telephony.roamapp.view;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.util.UIUtils;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProgressBarItem extends LinearLayout
{
	private int paddingTop;
	private int paddingLeft;
	private int size;
	private ImageView mImageView;
	private TextView mLoadingInfo;
    public ProgressBarItem(Context context)
    {
        super(context);
        init( context);
    }
    public ProgressBarItem(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setCustomAttributes(context, attrs);
        init( context);
        
    }
    public ProgressBarItem(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setCustomAttributes(context, attrs);
        init( context);       
    }
	private void setCustomAttributes(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.progressbaritem);
		paddingLeft = a.getDimensionPixelSize(
				R.styleable.progressbaritem_xpos, UIUtils.dip2px(getContext(), 8));
		paddingTop = a.getDimensionPixelSize(
				R.styleable.progressbaritem_ypos, UIUtils.dip2px(getContext(), 8));
		size = a.getDimensionPixelSize(
				R.styleable.progressbaritem_size, UIUtils.dip2px(getContext(), 30));
	}    
    private void init(Context context)
    {
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View  progressBarItemView=(View)inflater.inflate(R.layout.progressbar_item, null);
        addView(progressBarItemView);
        mImageView=(ImageView)this.findViewById(R.id.myloading_image_id);
        //RelativeLayout.LayoutParams ivlayout = (RelativeLayout.LayoutParams) progressImageView.getLayoutParams();
        RelativeLayout.LayoutParams imgvwDimens = 
                new RelativeLayout.LayoutParams(size-2*paddingLeft, size-2* paddingTop);
        imgvwDimens.leftMargin = paddingLeft;
        imgvwDimens.topMargin = paddingTop;
        mImageView.setLayoutParams(imgvwDimens);
        AnimationDrawable animationDrawable = (AnimationDrawable) mImageView.getDrawable();
        animationDrawable.start();
    }
    public void setImageDrawable(Drawable drawable) {
    	mImageView.setImageDrawable(drawable);
    }
    public void setImageResource(int resId) {
    	mImageView.setImageResource(resId);
    	AnimationDrawable animationDrawable = (AnimationDrawable) mImageView.getDrawable();
        animationDrawable.start();
    }
    /**
	 * 得到自定义的progressDialog
	 * @param context
	 * @param msg
	 * @return
	 */
    public static Dialog createLoadingDialog(Context context, String msg) {

		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progressbar_dialog, null);// 得到加载view
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.progressdialog_view);
		/*ProgressBarItem progressbar = (ProgressBarItem) v.findViewById(R.id.progressbar_view);*/// 加载布局
		// main.xml中的ImageView
//		ImageView spaceshipImage = (ImageView) v.findViewById(R.id.myloading_image_id);
		TextView tipTextView = (TextView) v.findViewById(R.id.mylaodingtext_id);// 提示文字
//		AnimationDrawable animationDrawable = (AnimationDrawable) spaceshipImage.getDrawable();
//        animationDrawable.start();
		tipTextView.setText(msg);// 设置加载信息
		Dialog loadingDialog = new Dialog(context, R.style.dialog_settinglmbao);// 创建自定义样式dialog

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            loadingDialog.getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
		loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(  
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));// 设置布局
		return loadingDialog;
	}    
}

