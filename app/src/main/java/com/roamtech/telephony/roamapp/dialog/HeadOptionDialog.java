package com.roamtech.telephony.roamapp.dialog;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.roamtech.telephony.roamapp.R;


public class HeadOptionDialog extends Dialog implements
		android.view.View.OnClickListener {
	public interface OnQuickOptionformClick {
		void onQuickOptionClick(int id);
	}

	private OnQuickOptionformClick mListener;

	private HeadOptionDialog(Context context, boolean flag,
			OnCancelListener listener) {
		super(context, flag, listener);
	}

	@SuppressLint("InflateParams")
	private HeadOptionDialog(Context context, int defStyle) {
		super(context, defStyle);
		View contentView = getLayoutInflater().inflate(
				R.layout.dialog_head_quick_option, null);
		contentView.findViewById(R.id.tv_takephoto).setOnClickListener(this);
		contentView.findViewById(R.id.tv_takepicturefromAlbum).setOnClickListener(this);
		contentView.findViewById(R.id.tv_cancel).setOnClickListener(this);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		contentView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				HeadOptionDialog.this.dismiss();
				return true;
			}
		});
		super.setContentView(contentView);

	}

	public HeadOptionDialog(Context context) {
		this(context, R.style.quick_option_dialog);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		getWindow().setGravity(Gravity.BOTTOM);
		WindowManager m = getWindow().getWindowManager();
		Display d = m.getDefaultDisplay();
		WindowManager.LayoutParams p = getWindow().getAttributes();
		p.width = d.getWidth();
		getWindow().setAttributes(p);
	}

	public void setOnQuickOptionformClickListener(OnQuickOptionformClick lis) {
		mListener = lis;
	}

	@Override
	public void onClick(View v) {
		if (mListener != null) {
			mListener.onQuickOptionClick(v.getId());
		}
		dismiss();
	}
}
