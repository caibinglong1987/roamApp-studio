package com.roamtech.telephony.roamapp.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

/**
 *  获取图片的截图，根据bitmap获取圆角图片
 * 
 * @author xincheng
 * 
 */
public class BitmapTools {
	public static Bitmap makeRoundCorner(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int left = 0, top = 0, right = width, bottom = height;
		float roundPx = height / 2;
		if (width > height) {
			left = (width - height) / 2;
			top = 0;
			right = left + height;
			bottom = height;
		} else if (height > width) {
			left = 0;
			top = (height - width) / 2;
			right = width;
			bottom = top + width;
			roundPx = width / 2;
		}
		// Log.i(TAG, "ps:"+ left +", "+ top +", "+ right +", "+ bottom);
		Bitmap output = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		int color = 0xff424242;
		Paint paint = new Paint();
		Rect rect = new Rect(left, top, right, bottom);
		RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	public static Bitmap makeRoundCorner(Bitmap bitmap, int px) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		int color = 0xff424242;
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, width, height);
		RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, px, px, paint);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/**
	 * @param v
	 * @param isClone cacheBitmap
	 * @return
	 */
	public static Bitmap getViewBitmap(View v,boolean isClone) {
		v.clearFocus(); //
		v.setPressed(false); //
		
		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);
		// 如果获得的背景不是黑色的则释放以前的绘图缓存
		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			return null;
		}
		Bitmap bitmap=null;
		if(isClone){
			bitmap = Bitmap.createBitmap(cacheBitmap);
			v.destroyDrawingCache();
		}else{
			bitmap=cacheBitmap;
		}
		// Restore the view
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);
		return bitmap;
	}
	public static Bitmap  getCircleBitmap(int color,int dpRadius) {
		int radius= LocalDisplay.dp2px(dpRadius);
		Bitmap bitmap = Bitmap.createBitmap(radius*2,radius*2, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint p=new Paint();
		p.setAntiAlias(true);
		p.setColor(color);
		canvas.drawCircle(radius, radius, radius, p);
		return bitmap;
	}
}
