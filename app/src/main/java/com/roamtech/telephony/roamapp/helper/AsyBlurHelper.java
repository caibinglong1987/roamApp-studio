package com.roamtech.telephony.roamapp.helper;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewTreeObserver;

import com.roamtech.telephony.roamapp.util.BitmapTools;
import com.roamtech.telephony.roamapp.util.FastBlur;

/**
 * Created by user on 6/20/2016.
 */
public class AsyBlurHelper {
    public static void startBlur(final View cacheView, final View blurView, final String overlayColor, final float scaleFactor, final float radius, final int offset, final OnBlurListener onBlurListener) {
        int width = cacheView.getWidth();
        if (width != 0) {
            Bitmap cache = BitmapTools.getViewBitmap(cacheView, false);
            Bitmap bmp = FastBlur.blur(cache, radius, scaleFactor, blurView,
                    overlayColor, offset);
            cacheView.destroyDrawingCache();
            onBlurListener.OnBlurEnd(bmp);

        } else {
            cacheView.getViewTreeObserver().addOnPreDrawListener(
                    new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            cacheView.getViewTreeObserver().removeOnPreDrawListener(
                                    this);
                            if (cacheView.getWidth() != 0) {
                                //递归
                                startBlur(cacheView, blurView, overlayColor, scaleFactor, radius, offset, onBlurListener);
                            }
                            return true;
                        }
                    });

        }
    }

    public interface OnBlurListener {
        void OnBlurEnd(Bitmap bitmap);
    }
}
