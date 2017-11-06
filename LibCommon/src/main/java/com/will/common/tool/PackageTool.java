package com.will.common.tool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * import android.content.Context;
 * import android.content.pm.PackageInfo;
 * import android.content.pm.PackageManager;
 * <p>
 * /**
 * Author: long
 * Date: 2016-06-24 16:11
 * FIXME:
 * DESC:
 */
public class PackageTool {

    public static PackageInfo getPackageInfo(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
            return packageInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static String getVersionCode(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            String versionCode = String.valueOf(packageInfo.versionCode);
            return versionCode;
        }
        return "0";
    }

    public static String getVersionName(Context context) {
        PackageInfo packageInfo = getPackageInfo(context);
        if (packageInfo != null) {
            String versionName = String.valueOf(packageInfo.versionName);
            return versionName;
        }
        return null;
    }


}
