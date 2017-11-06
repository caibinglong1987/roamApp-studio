package com.roamtech.telephony.roamapp.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by caibinglong
 * on 2017/3/8.
 */

public class IpAddressUtil {
    public static String getHostAddress(String domain) {
        String ip;
        InetAddress x;
        try {
            x = InetAddress.getByName(domain);
            ip = x.getHostAddress();//得到字符串形式的ip地址
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip = domain;
        }
        return ip;
    }
}
