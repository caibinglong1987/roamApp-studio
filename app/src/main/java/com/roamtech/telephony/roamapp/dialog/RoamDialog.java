package com.roamtech.telephony.roamapp.dialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.roamtech.telephony.roamapp.R;
public class RoamDialog extends Dialog {
	private TextView tvTitle;
	private String mTitle;
	private RoamDialog(Context context, int defStyle) {
		super(context, defStyle);
	}
	private ImageView loadingImage;
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		AnimationDrawable an= (AnimationDrawable) loadingImage.getDrawable();
		an.start();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		AnimationDrawable an= (AnimationDrawable) loadingImage.getDrawable();
		an.stop();
	}

	public RoamDialog(Context context, String title) {
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
		setContentView(R.layout.dialog_roaming);
		tvTitle=(TextView) findViewById(R.id.load_textView);
		tvTitle.setText(mTitle);
		loadingImage= (ImageView) findViewById(R.id.iv_loading);
	}
}
