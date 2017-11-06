package com.roamtech.telephony.roamapp.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roamtech.telephony.roamapp.R;

/**
 * @author long
 *         消息提示toast
 */
public class ToastUtils {
    public static final int TOAST_DURATION = 3000;
    private static int IMG_HEIGHT = 0;

    public static void showToast(Context context, int resId) {
        showToast2(context, context.getString(resId));
    }

    public static void showToast(Context context, String tips) {
        if (null == tips || TextUtils.isEmpty(tips.trim())) {
            return;
        }
        showToast2(context, tips);
    }

    public static void showToast(Context context, int resId, int time) {
        String toastString = context.getString(resId);
        Toast mToast = Toast.makeText(context, "", time);
        mToast.setText(toastString);
        mToast.show();
    }

    public static void showToast(Context context, String str, int time) {
        Toast mToast = Toast.makeText(context, "", time);
        mToast.setText(str);
        mToast.show();
    }

    public static void showToast2(Context context, String toastString) {
        Toast mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mToast.setText(toastString);
        mToast.show();
    }

    public static void showToastCenter(Context context, String toastString) {
        Toast mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        mToast.setText(toastString);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    /**
     * 自定义 toast
     * @param context 上下文
     * @param title 标题
     * @param content 内容
     */
    public static void customizeToast(Context context, String title, String content) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_tip, null);
        layout.findViewById(R.id.btn_left).setVisibility(View.GONE);
        layout.findViewById(R.id.btn_right).setVisibility(View.GONE);
        layout.findViewById(R.id.view_line).setVisibility(View.GONE);
        ((TextView) layout.findViewById(R.id.tv_title)).setText(title);
        ((TextView) layout.findViewById(R.id.tv_message)).setText(content);
        Toast mToast = new Toast(context);
        mToast.setView(layout);
        mToast.show();
    }

    /**
     * 带图片的toast提示
     *
     * @param context
     * @param ImageResourceId
     */
    public static void ImageToast(Context context, String toastString, int ImageResourceId) {
        //创建带图片Toast提示消息
        Toast mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);

        //设置Toast提示消息在屏幕上的位于中间
        mToast.setGravity(Gravity.BOTTOM, 0, 150);
        //获取Toast提示消息里原有的View   
//      View toastView = mToast.getView();  
        //创建图像ImageView
        ImageView img = new ImageView(context);
        img.setImageResource(ImageResourceId);
//        float scale = context.getResources().getDisplayMetrics().density;
//        int width = (int) (25 * scale + 0.5f);
//        int height = (int) (25 * scale + 0.5f);
        img.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        //创建一个对象LineLayout容器
        // LinearLayout toastView = (LinearLayout) mToast.getView();
        //toastView.setOrientation(LinearLayout.HORIZONTAL);

        //向LinearLayout中添加ImageView和Toast原有的View
        //toastView.addView(img, 0);
        //toastView.addView(tv, 0);

        TextView textView = new TextView(context);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setTextColor(0xFFFFFF);
        textView.setText(toastString);

        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 70);
        layoutParams.setMargins(20, 0, 10, 0);
        linearLayout.setLayoutParams(layoutParams);
        linearLayout.setBackgroundColor(0x50333333);
        linearLayout.setPadding(30, 0, 30, 50);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(img);
        linearLayout.addView(textView);
        //mToast.setText(toastString);

        //将LineLayout容器设置为toast的View
        mToast.setView(linearLayout);
        //显示消息
        mToast.show();
    }

    /**
     * 图片 加文字的 toast
     *
     * @param context         上下文
     * @param ImageResourceId 图片
     */
    public static void ImageTextToast(Context context, int ImageResourceId) {
        Toast mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.BOTTOM, 0, 150);

        LinearLayout linearLayout = new LinearLayout(context);
        ImageView img = new ImageView(context);
        img.setImageResource(ImageResourceId);
        linearLayout.addView(img);
        mToast.setView(linearLayout);
        mToast.show();
    }
}

