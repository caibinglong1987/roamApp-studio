package com.roamtech.telephony.roamapp.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.enums.LoadingState;

public class EmptyView extends RelativeLayout {
	private LoadingState mState;
	private TextView mStateTextView;
	/** 点击重新加载 **/
	private TextView mTipTextView;
	private String mTipText = "点击此处重试";
	private ImageView ivLoading;
	private View mBindView;
	/**如果之前已经加载成功则跳过 默认为true **/
	private boolean isExcludeSuccessBefore=true;
	public EmptyView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView();
	}

	public EmptyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView();
	}

	private void initView() {
		setExcludeSuccessBefore(true);
		mStateTextView = new TextView(getContext());
		mStateTextView.setTextColor(getResources().getColor(R.color.text_grey));
		mStateTextView.setTextSize(16);
		LayoutParams textViewParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		textViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		mStateTextView.setId(R.id.id_empty_text);
		addView(mStateTextView, textViewParams);
		mTipTextView = new TextView(getContext());
		mTipTextView.setTextColor(getResources().getColor(R.color.roam_color));
		mTipTextView.setTextSize(16);
		mTipTextView.setText(mTipText);
		mTipTextView.setPadding(5,5,5,5);
		LayoutParams tipTextParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		tipTextParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		tipTextParams.addRule(RelativeLayout.BELOW, R.id.id_empty_text);
		addView(mTipTextView, tipTextParams);
		/** ivLoading 布局定义外部 方便修改 **/
		ivLoading = (ImageView) LayoutInflater.from(getContext()).inflate(
				R.layout.progress_loading, this, false);
		AnimationDrawable an= (AnimationDrawable) ivLoading.getDrawable();
		an.start();
		addView(this.ivLoading);
		/** 默认不展示 **/
		setVisibility(View.GONE);

	}

	/**
	 * 在第一次请求数据前调用 绑定刷新的View 和 按钮点击重新加载的监听
	 * 
	 * @param view
	 * @param l
	 * @param state
	 *            当前的状态
	 */
	public void bindViewAndLoadListener(View view, LoadingState state,
			final OnClickListener l) {
		mBindView = view;
		setState(state);
		mTipTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setState(LoadingState.LOADING);
				l.onClick(v);
			}
		});
	}

	/**
	 * 仅限刷新调用
	 * @param state
	 * @return true 表示当前的状态即为设置的状态，设置成功 ,
	 * false 表示设置失败
	 */
	public boolean setState(LoadingState state) {
		if (isExcludeSuccessBefore && mState == LoadingState.SUCCESS)
			return false;
		if (state == mState)
			return true;
		this.mState = state;
		controlEmptyView(state);
		return true;
	}
	public void controlEmptyView(LoadingState state) {
		boolean isBindViewShow = false;
		if (mBindView == null)
			throw new NullPointerException("EmptyView must bind a bindView");
		if (mBindView instanceof ListView) {
			if (state == LoadingState.SUCCESS) {
				isBindViewShow = true;
			}
//			View parentView=(View) mBindView.getParent();
//			if(parentView instanceof RefreshLayout){
//				parentView.setEnabled(isBindViewShow);
//			}
		} else {
			if (state == LoadingState.SUCCESS) {
				isBindViewShow = true;
			}
		}
		mBindView.setEnabled(isBindViewShow);
		setVisibility(!isBindViewShow ? View.VISIBLE : View.GONE);
		/** isBindViewShow 为false时 展示才有意义 **/
		if (!isBindViewShow) {
			if (state == LoadingState.LOADING) {
				ivLoading.setVisibility(View.VISIBLE);
				mTipTextView.setVisibility(View.INVISIBLE);
			} else {
				mTipTextView.setVisibility(View.VISIBLE);
				ivLoading.setVisibility(View.INVISIBLE);
			}
			mStateTextView.setText(state.getText());
		}
	}

	public void setExcludeSuccessBefore(boolean isExcludeSuccessBefore) {
		this.isExcludeSuccessBefore = isExcludeSuccessBefore;
	}
}
