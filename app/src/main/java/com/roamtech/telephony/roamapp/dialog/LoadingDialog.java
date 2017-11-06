package com.roamtech.telephony.roamapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;

public class LoadingDialog extends Dialog {
	private TextView tvTitle;
	private String mTitle;
	private LoadingDialog(Context context, int defStyle) {
		super(context, defStyle);
	}
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}

	public LoadingDialog(Context context,String title) {
		this(context, R.style.dialog_loadingStyle);
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		mTitle=title;
	}
	public void setTitle(String title){
		tvTitle.setText(title);
	}
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.dialog_loading);
		tvTitle=(TextView) findViewById(R.id.load_textView);
		tvTitle.setText(mTitle);
	}
}
