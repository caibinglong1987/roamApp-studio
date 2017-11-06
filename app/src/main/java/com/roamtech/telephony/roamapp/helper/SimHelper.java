package com.roamtech.telephony.roamapp.helper;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by xincheng on 2016/7/11.
 */
public class SimHelper {

    public static final int MOBILE = 0;
    public static final int UNICOM = 1;
    public static final int TELECOM = 2;


    /**
     * -1位未获取到运营商
     *
     * @return
     */
    public static int getProvider(Context context) {
        // 返回唯一的用户ID;就是这张卡的编号神马的
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = telephonyManager.getSubscriberId();
        // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
            return MOBILE;
        } else if (IMSI.startsWith("46001")) {
            return UNICOM;
        } else if (IMSI.startsWith("46003")) {
            return TELECOM;
        }
        return -1;
    }
    public static int getSimState(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimState();
    }
}
