package com.roamtech.telephony.roamapp.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.bean.Evoucher;
import com.roamtech.telephony.roamapp.util.LocalDisplay;
import com.roamtech.telephony.roamapp.view.PinchImageView;

public class ImageViewDialog extends Dialog {
    private Context mContext;
    private Evoucher mEvoucher;
    private PinchImageView imageView;

    private ImageViewDialog(Context context, int defStyle) {
        super(context, defStyle);
    }

    @Override
    public void show() {
        super.show();
    }

    public ImageViewDialog(Context context, Evoucher evoucher) {
        this(context, R.style.dialog_fullscreen);
        mContext = context;
        mEvoucher = evoucher;
        setCancelable(true);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        imageView = new PinchImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);
        setContentView(imageView);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = LocalDisplay.SCREEN_WIDTH_PIXELS;
        lp.height = LocalDisplay.SCREEN_HEIGHT_PIXELS - getStatusBarHeight();
        getWindow().setAttributes(lp);
        initView();
    }

    private void initView() {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)                        // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(false)                         // 设置下载的图片是否缓存在SD卡中
                .build();                                   // 创建配置过得DisplayImageOption对象
        ImageLoader.getInstance().displayImage("http://www.roam-tech.com/roammall/" + mEvoucher.getImage(), new ImageViewAware(imageView), options, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                if (failReason.getType() == FailReason.FailType.NETWORK_DENIED) {
                    Toast.makeText(mContext, "网络异常", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "服务异常", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matrix outerMatrix = new Matrix();
                imageView.getOuterMatrix(outerMatrix);
                float[] value = new float[9];
                outerMatrix.getValues(value);
//                if (outerMatrix.isIdentity()) 缩放后不为单位阵{[1.0, 0.0, 0.0][0.0, 1.0, -1.8310547E-4][0.0, 0.0, 1.0]}
                if (value[0] == 1 && value[4] == 1) {
                    dismiss();
                }
            }
        });
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
