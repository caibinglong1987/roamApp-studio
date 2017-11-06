package com.roamtech.telephony.roamapp.server;

/**
 *
 *
 *
 *
 */
public class IServiceValues {


    private static final String CMD_PREFIX = "com.roamtech.telephony.roamapp.ISERVICE_CMD_";
    private static final int CMD_VALUE_PREFIX = 0xff;

    //向service发送命令类型 action 定义（ACTION_CMD_name1_name2...)
    public static final String ACTION_CMD_WAY = CMD_PREFIX + "WAY";
    public static final String ACTION_CMD_TEST = CMD_PREFIX + "TEST";

    //命令码
    public static final int CMD_LOGIN = CMD_VALUE_PREFIX + 1;//登录
    public static final int CMD_EXIT_LOGIN = CMD_VALUE_PREFIX + 2;//退出

    //连接网络
    public static final int NETWORK_SUCCESS = CMD_VALUE_PREFIX + 5;

    /*************************
     * key 部分
     *********************/
    //获取命令码的key
    public static final String KEY_BROADCAST_CMD_VALUE = "cmd";
    //获取签名的key
    public static final String KEY_BROADCAST_SIGN = "sign";

}
