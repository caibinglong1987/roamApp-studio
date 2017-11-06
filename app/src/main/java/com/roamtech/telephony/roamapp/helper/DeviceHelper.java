package com.roamtech.telephony.roamapp.helper;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by xincheng on 2016/7/15.
 */
public class DeviceHelper {
    //设置状态栏透明
//       @TargetApi(21)
//    public static void setStatusTransParent(Activity activity) {
//        setStatusTransParent(activity, Color.TRANSPARENT, false);
//    }
//
//    /**Called after setContentView
//     * @param statusBarColor
//     * @param fitSystemWindow ViewGroup是否给状态栏留空间（若为true viewGroup的padding属性会失效）
//     */
//    //设置状态栏透明
//    @TargetApi(21)
//    public static void setStatusTransParent(Activity activity, @ColorInt int statusBarColor, boolean fitSystemWindow) {
//        //5.0以上设置状态栏透明 兼容4.4太麻烦
//        // xml设置了android:windowTranslucentStatus的属性(若为true(即透明) 设置statusBarColor 颜色是无效的)
//        // fitSystemWimdow(如果为true 其内部的padding无效掉)才有效-->End
//        //=========================================
//        //此处代码设置内容可延伸至状态栏下面  fitSystemWimdow（默认为false）有效,还可以改变状态栏颜色
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = activity.getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
//                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            //设置透明 否则为主题颜色
//            window.setStatusBarColor(statusBarColor);
//            //设置fitSystemWindow属性
//            ViewGroup contentFrameLayout = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
//            View parentView = contentFrameLayout.getChildAt(0);
//            if (parentView != null) {
//                parentView.setFitsSystemWindows(fitSystemWindow);
//            }
//        }
//    }

    /**
     * android系统定制商
     * 没住设置状态哪里你颜色必须是toobar的颜色一致 设置不同颜色无效
     *
     * @return
     */
    public static boolean isMeizu() {
        return android.os.Build.BRAND.equalsIgnoreCase("Meizu");
    }

    public static void toApnSetting(Activity activity) {
        //设置apn ATION_APN_SETTINGS
        Intent intent;
        if (android.os.Build.BRAND.equals("SMARTISAN")) { //锤子手机
            intent = new Intent(Settings.ACTION_APN_SETTINGS);
        } else {
            intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
        }
        activity.startActivity(intent);
    }
}
