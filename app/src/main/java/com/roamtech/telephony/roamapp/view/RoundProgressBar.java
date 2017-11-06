package com.roamtech.telephony.roamapp.view;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.util.LocalDisplay;
/**
 * 仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 * 
 * @author csdn
 * 
 */
public class RoundProgressBar extends View {
	/**
	 * 画笔对象的引用
	 */
	private Paint paint;

	/**
	 * 圆环的颜色
	 */
	private int roundColor;
	

	/**
	 * 圆环进度的颜色
	 */
	private int roundProgressColor;

	/**
	 * 中间进度百分比的字符串的颜色
	 */
	private int textColor;

	/**
	 * 中间进度百分比的字符串的字体
	 */
	private float textSize;
	/**
	 * 圆环的宽度
	 */
	private float roundWidth;

	/**
	 * 最大进度
	 */
	private int max;
	/**
	 * 当前进度
	 */
	private float progress;
	/**
	 * 是否显示中间的进度
	 */
	private boolean textIsDisplayable;

	/**
	 * 进度的风格，实心或者空心
	 */
	private int style;

	public static final int STROKE = 0;
	public static final int FILL = 1;
	//默认偏离的角度
	private static final int DEVIATION_ANGLE=90;
	private int deviationAngle;
	//半径所占屏幕宽度的百分比
	private float radiusPercent;
	public RoundProgressBar(Context context) {
		this(context, null);
	}

	public RoundProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setProgress(0);
		paint = new Paint();
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.RoundProgressBar);
		// 获取自定义属性和默认值
		roundColor = mTypedArray.getColor(
				R.styleable.RoundProgressBar_roundColor, Color.RED);
		roundProgressColor = mTypedArray.getColor(
				R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
		textColor = mTypedArray.getColor(
				R.styleable.RoundProgressBar_roundtextColor, Color.GREEN);
		textSize = mTypedArray.getDimension(
				R.styleable.RoundProgressBar_roundtextSize, 15);
		roundWidth = mTypedArray.getDimension(
				R.styleable.RoundProgressBar_roundWidth, 5);
		radiusPercent=mTypedArray.getFloat(R.styleable.RoundProgressBar_radius_percent, 0.3f);
		max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
		deviationAngle=mTypedArray.getInteger(R.styleable.RoundProgressBar_deviation_angle, DEVIATION_ANGLE);
		textIsDisplayable = mTypedArray.getBoolean(
				R.styleable.RoundProgressBar_textIsDisplayable, false);
		style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, STROKE);
		mTypedArray.recycle();
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int rectangleSize=(int) (getSizeByRadiusPercent()*2+roundWidth);
		//设置组件的宽高
		setMeasuredDimension(rectangleSize, rectangleSize);
	}
	public int getSizeByRadiusPercent(){
		int percentH=(int) (LocalDisplay.SCREEN_HEIGHT_PIXELS*radiusPercent);
		int percentW=(int) (LocalDisplay.SCREEN_WIDTH_PIXELS*radiusPercent);
		return percentW<percentH?percentW:percentH;
		
	}
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/**
		 * 画最外层的大圆环
		 */
		int centre = getWidth() / 2; // 获取圆心的x坐标
		int radius = (int) (centre - roundWidth / 2); // 圆环的半径
		paint.setColor(roundColor); // 设置圆环的颜色
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setAntiAlias(true); // 消除锯齿
		canvas.drawCircle(centre, centre, radius, paint); // 画出圆环
		/**
		 * 画进度百分比
		 */
		paint.setStrokeWidth(0);
		int percent = (int) (((float) progress / (float) max) * 100); // 中间的进度百分比，先转换成float在进行除法运算，不然都为0
		// float textWidth = paint.measureText(percent + "%"); //
		// 测量字体宽度，我们需要根据字体的宽度设置在圆环中间
		// if (textIsDisplayable && percent != 0 && style == STROKE) {
		if (textIsDisplayable && style == STROKE) {
			// canvas.drawText(percent + "%", centre - textWidth / 2, centre
			// + textSize / 2, paint); // 画出进度百分比
			paint.setTextSize(textSize);
			paint.setTypeface(Typeface.DEFAULT); // 设置字体
			if (percent != 0) {
				paint.setColor(textColor);
			} else {
				paint.setColor(roundColor);
			}
			canvas.drawText(percent + "%",
					(getWidth() - paint.measureText(percent + "%")) / 2.0f,
					(getWidth() - (paint.descent() + paint.ascent())) / 2.0f,
					paint);
		}
		/**
		 * 画圆弧 ，画圆环的进度
		 */
		// 设置进度是实心还是空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setColor(roundProgressColor); // 设置进度的颜色
		//弧形jianbin
		if(mSweepGradient==null){
			//如果两个颜色不一样 我认为是需要线性变化的
			if(startColor!=endColor){
				float array[]=null;
				if(isUp){
					array=new float[]{0.5f,1};
				}else{
					array=new float[]{0,0.5f};
				}
				mSweepGradient = new SweepGradient(getWidth() / 2, getWidth() / 2, new int[] {startColor,endColor}, array);
				paint.setShader(mSweepGradient);
			}
		}		
		RectF oval = new RectF(centre - radius, centre - radius, centre
				+ radius, centre + radius); // 用于定义的圆弧的形状和大小的界限
		switch (style) {
		/** 从90度开始 **/
		case STROKE: {
			if (progress != 0) {
				paint.setStyle(Paint.Style.STROKE);
				canvas.drawArc(oval, deviationAngle, -360 * progress / max, false, paint); // 根据进度画圆弧
			}
			break;
		}
		case FILL: {
			if (progress != 0) {
				paint.setStyle(Paint.Style.FILL_AND_STROKE);
				canvas.drawArc(oval, deviationAngle, -360 * progress / max, true, paint); // 根据进度画圆弧
			}
			break;
		}
		}
	}

	public synchronized int getMax() {
		return max;
	}

	/**
	 * 设置进度的最大值
	 * 
	 * @param max
	 */
	public synchronized void setMax(int max) {
		if (max < 0) {
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}
	/**
	 * 获取进度.需要同步
	 * 
	 * @return
	 */
	public synchronized float getProgress() {
		return progress;
	}
	/**
	 * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步 刷新界面调用postInvalidate()能在非UI线程刷新
	 * 
	 * @param progress
	 */
	public synchronized void setProgress(float progress) {
		if (progress < 0) {
			throw new IllegalArgumentException("progress not less than 0");
		}
		if (progress > max) {
			progress = max;
		}
		if (progress <= max) {
			this.progress = progress;
			postInvalidate();
		}
	}
	@SuppressLint("HandlerLeak")
	private class AnimHandler extends Handler{
		private static final int ANIM_MESSAGE=1;
		//动画开始的位置
		private float fromProgress;
		//动画结束的位置
		private float endProgress;
		//记录动画当前位置的变量
		private float animProgress;
		//每次递减的进度
		private  float decrease;
		//间隔时间
		private  int dTime;
		private AnimHandler(int fromProgress, int endProgress,float decrease,int dTime){
			this.fromProgress=fromProgress;
			this.endProgress=endProgress;
			this.decrease=decrease;
			this.dTime=dTime;
		}

		private void startAnim(int delay, int endProgress) {
			this.endProgress=endProgress;
			startAnim(delay);
		}

		private void startAnim(int delay){
			animProgress=fromProgress;
			//进度条位置要和动画开始的位置一致
			setProgress(fromProgress);
			removeMessages(ANIM_MESSAGE);
			mAnimHandler.sendEmptyMessageDelayed(AnimHandler.ANIM_MESSAGE, delay);		
		}
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==ANIM_MESSAGE){				
				if(animProgress>endProgress){
					animProgress=animProgress-decrease;
					if(animProgress<endProgress){
						animProgress=endProgress;
					}
					setProgress(animProgress);
					sendEmptyMessageDelayed(ANIM_MESSAGE, dTime);
				}else{
					//in case
					removeMessages(ANIM_MESSAGE);
				}
			}
		}
	}
	private AnimHandler mAnimHandler;
	public void startAnimTimer(int fromProgress,int toProgress,int dTime,float decrease,int delay){
		if(mAnimHandler==null){
			mAnimHandler=new AnimHandler(fromProgress,toProgress, decrease, dTime);
		}
		mAnimHandler.startAnim(delay, toProgress);
	}
	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		if(mAnimHandler!=null){
			//in case
			mAnimHandler.removeMessages(AnimHandler.ANIM_MESSAGE);
		}
	}
	public int getCricleColor() {
		return roundColor;
	}

	public void setCricleColor(int cricleColor) {
		this.roundColor = cricleColor;
	}

	public int getCricleProgressColor() {
		return roundProgressColor;
	}

	public void setCricleProgressColor(int cricleProgressColor) {
		this.roundProgressColor = cricleProgressColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}
	private SweepGradient mSweepGradient;
	private int startColor, endColor;
	private boolean isUp;
	//上下位置之分
	public void setSweepGradientColor(int startColor,int endColor,boolean isUp){
		this.startColor=startColor;
		this.endColor=endColor;
		this.isUp=isUp;
	}
	public SweepGradient getSweepGradient(){
		if(mSweepGradient==null){
			float array[]=new float[]{0,0.5f};
			mSweepGradient = new SweepGradient(getWidth() / 2, getWidth() / 2, new int[] {Color.parseColor("#62B3E1"),Color.parseColor("#CA94E6")}, array);
		}
		return mSweepGradient;
	}
}
