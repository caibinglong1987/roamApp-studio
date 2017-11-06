package com.roamtech.telephony.roamapp.HandlerMessag;

/**
 * Created by long
 * on 2016/10/8 10:34
 * handler msg
 */

public class MsgType {
    private static final int BASE = 0X35;
    public static final int ZFB_PAY_SUCCESS = BASE + 5; //支付宝付款成功
    public static final int WEI_XIN_PAY_SUCCESS = BASE + 6; //微信付款成功
    public static final int ZFB_PAY_ERROR = BASE + 7; //支付宝付款失败
    public static final int CRED_TRIP_PAY_SUCCESS = BASE + 1; //信程支付成功
    public static final int CRED_TRIP_PAY_CANCEL = BASE + 2; //信程支付取消
    public static final int CRED_TRIP_PAY_ERROR = BASE + 3; //信程支付失败
    public static final int UP_PAY_SUCCESS = BASE + 4;//银联支付
    public static final int UP_PAY_CANCEL = BASE + 15;//银联支付取消
    public static final int UP_PAY_ERROR = BASE + 16;//银联支付失败

    public static final int WEI_XIN_PAY_ERROR = BASE + 8; //微信支付失败
    public static final int ZFB_PAY_CANCEL = BASE + 9; //支付宝支付过程中取消
    public static final int WEI_XIN_PAY_CANCEL = BASE + 10; //微信支付过程中取消
    public static final int PAY_CANCEL = BASE + 11; //取消支付
    public static final int HANDLER_RECEIVED_ORDER = BASE + 12; //确认收货
    public static final int ORDER_CANCEL_SUCCESS = BASE + 13; //订单取消 成功
    public static final int HANDLER_VIEWPAGER_CHANGE = BASE + 14;// 络漫宝切换

    public static final int MSG_SET_WAN_PHONE_SUCCESS = BASE + 1243;//配置络漫宝手机
    public static final int MSG_SET_WAN_PHONE_ERROR = BASE + 1244;
    public static final int MSG_SET_WAN_PHONE_TIMEOUT = BASE + 1245;

    public static final int MSG_SET_LAN_WIFI_SUCCESS = BASE + 1249; //配置wifi
    public static final int MSG_SET_LAN_WIFI_ERROR = BASE + 1250;
    public static final int MSG_SET_LAN_WIFI_TIMEOUT = BASE + 1251;

    public static final int MSG_GET_ROAM_BOX_SUCCESS = BASE + 1252; //获取络漫宝列表
    public static final int MSG_GET_ROAM_BOX_NO_DATA = BASE + 1352; //获取络漫宝列表
    public static final int MSG_GET_ROAM_BOX_TIMEOUT = BASE + 1353; //获取络漫宝列表超时
    public static final int MSG_ROAM_BOX_TOKEN_SUCCESS = BASE + 1253; //获取络漫宝配置 token
    public static final int MSG_ROAM_BOX_TOKEN_ERROR = BASE + 1254;
    public static final int MSG_ROAM_BOX_TOKEN_TIMEOUT = BASE + 1255;
    public static final int MSG_ROAM_BOX_CONFIG_ERROR_GET_ROAM_BOX_SUCCESS = BASE + 1100;
    public static final int MSG_ROAM_BOX_CONFIG_ERROR_GET_ROAM_BOX_ERROR = BASE + 1101;

    public static final int MSG_DETECT_NETWORK_SUCCESS = BASE + 1256; //获取络漫宝配置数据
    public static final int MSG_DETECT_NETWORK_ERROR = BASE + 1257;
    public static final int MSG_DETECT_NETWORK_TIMEOUT = BASE + 1258;

    public static final int MSG_RESTART_ROAM_BOX_SUCCESS = BASE + 1259; //重启络漫宝
    public static final int MSG_RESTART_ROAM_BOX_ERROR = BASE + 1260; //重启络漫宝
    public static final int MSG_ROAM_BOX_RESTART_SUCCESS = BASE + 1266;//络漫宝重启 完成

    public static final int MSG_CHANGE_ROAM_BOX = BASE + 1261; //络漫宝切换

    public static final int MSG_SET_WAN_STATIC_IP_SUCCESS = BASE + 1262; //静态上网
    public static final int MSG_SET_WAN_STATIC_IP_ERROR = BASE + 1263; //静态上网
    public static final int MSG_SET_WAN_STATIC_IP_TIMEOUT = BASE + 1264; //静态上网

    public static final int MSG_SET_WAN_WIRELESS_SUCCESS = BASE + 1265; //无线中继
    public static final int MSG_SET_WAN_WIRELESS_ERROR = BASE + 1266;
    public static final int MSG_SET_WAN_WIRELESS_TIMEOUT = BASE + 1267;

    public static final int MSG_ROAM_BOX_NETWORK_SUCCESS = BASE + 1271; //络漫宝网络正常
    public static final int MSG_ROAM_BOX_NETWORK_ERROR = BASE + 1278; //络漫宝网络不正常

    public static final int MSG_NETWORK_TIMEOUT = BASE + 1272; //网络不正常

    public static final int MSG_SET_BROADBAND_SUCCESS = BASE + 1273; //设置宽带拨号成功
    public static final int MSG_SET_BROADBAND_ERROR = BASE + 1274; //设置宽带拨号失败

    public static final int MSG_SET_WAN_DHCP_SUCCESS = BASE + 1275; //设置DHCP
    public static final int MSG_SET_WAN_DHCP_ERROR = BASE + 1276;

    public static final int MSG_GET_HOME_PAGE_SUCCESS = BASE + 1278; //获取首页数据
    public static final int MSG_GET_HOME_PAGE_TIMEOUT = BASE + 1280;

    public static final int MSG_GET_HOME_HEAD_LINE_SUCCESS = BASE + 1281; //获取headLine数据
    public static final int MSG_GET_HOME_HEAD_LINE_TIMEOUT = BASE + 1283;

    public static final int MSG_LOGOUT_SUCCESS = BASE + 1284;
    public static final int MSG_LOGOUT_ERROR = BASE + 1286;

    public static final int MSG_CHANGE_NETWORK_SUCCESS = BASE + 1300;//切换网络成功
    public static final int MSG_CHANGE_NETWORK_ERROR = BASE + 1301;//切换网络失败
    public static final int MSG_CHANGE_NETWORK_UPDATE = BASE + 1302;//切换网络失败

    public static final int MSG_SEARCH_CONTACT_TEXT = BASE + 1303;//搜索联系人

    public static final int MSG_GET_ROAM_BOX_WIFI_SUCCESS = BASE + 1304;//络漫宝wifi 成功
    public static final int MSG_GET_ROAM_BOX_WIFI_ERROR = BASE + 1305;//络漫宝wifi 失败

    public static final int MSG_NO_ROAM_BOX_WIFI = BASE + 1306;//不是络漫宝wifi

    public static final int MSG_GET_GROUP_ALL_CALL_LIST = BASE + 1307;//获取通话记录
    public static final int MSG_GET_GROUP_MISS_CALL_LIST = BASE + 1309;//获取未接通话记录
    public static final int MSG_GET_ALL_CALL_BY_PHONE = BASE + 1323;//获取通话记录


    public static final int MSG_GET_GROUP_MESSAGE_LIST = BASE + 13010;//获取消息列表记录
    public static final int MSG_GET_MESSAGE_LIST = BASE + 13011;//获取消息列表记录个人

    public static final int MSG_NO_DATA = BASE + 1308;//没数据


    public static final int MSG_GET_LOCAL_ALL_CALL_SUCCESS = BASE + 1314;//
    public static final int MSG_GET_LOCAL_MISS_CALL_SUCCESS = BASE + 1315;//

    public static final int MSG_MESSAGE_SEARCH_SUCCESS = BASE + 1316;// 消息内容搜索

    public static final int MSG_SEARCH_CHANGE = BASE + 1318;// 消息内容搜索监听

    public static final int MSG_GET_EVOUVHER_SUCCESS = BASE + 1319; //订单优惠券
    public static final int MSG_DELETE_ERROR = BASE + 1320; //删除失败
    public static final int MSG_DELETE_SUCCESS = BASE + 1321; //删除成功

    public static final int MSG_TEXT_CHANGE = BASE + 1322;
}