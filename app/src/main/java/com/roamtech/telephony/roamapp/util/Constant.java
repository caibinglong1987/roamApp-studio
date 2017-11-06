package com.roamtech.telephony.roamapp.util;

/**
 * Created by xincheng on 2016/7/26.
 * modify user long 2016/9/23
 */
public class Constant {
    public static final String ROAM_VERSION_USER_AGENT = "versionName"; //user_agent 需要传给服务端
    public static final String HOST_DOMAIN_HTTPS = "https://www.roam-tech.com/";


    //商城正式
    public static final String HOST_ROAM_MALL_HTTPS = "https://www.roam-tech.com/roammall/"; //web 服务器

    public static final String IMAGE_URL = "http://www.roam-tech.com/roammall/";
    public static final String AUDIO_URL = "http://www.roam-tech.com/";

    //<--HTTPS 请求-->
    public static final String GLOBAL_CARD = HOST_DOMAIN_HTTPS + "uc/services/datacard_gets";
    public static final String ALLTRAFFICVOICE = HOST_DOMAIN_HTTPS + "uc/services/alltrafficvoice_get";
    public static final String VOICE_AVAILABLE = HOST_DOMAIN_HTTPS + "uc/services/voiceavailable_get";
    public static final String CARDTRAFFIC_BIND = HOST_DOMAIN_HTTPS + "uc/services/cardtraffic_bind";
    public static final String UPGRADE_CHECK = HOST_DOMAIN_HTTPS + "uc/services/roamchat_upgrade_check";////漫话应用升级检查
    public static final String EVOUCHER = HOST_DOMAIN_HTTPS + "uc/services/evoucher_gets";//获取电子券
    public static final String ORDER_LIST = HOST_DOMAIN_HTTPS + "uc/services/order_gets"; //订单列表
    public static final String PRODUCT_GET = HOST_DOMAIN_HTTPS + "uc/services/product_gets";//获取商品信息
    public static final String PAYMENT_GET = HOST_DOMAIN_HTTPS + "uc/services/payment_gets";//获取支付方式
    public static final String GET_ROAM_BOX_LIST = HOST_DOMAIN_HTTPS + "uc/services/touch_gets"; //获取绑定的络漫宝
    public static final String ROAM_BOX_BIND = HOST_DOMAIN_HTTPS + "uc/services/touch_bind"; //绑定络漫宝
    public static final String TOUCHDBMODEL_BIND = HOST_DOMAIN_HTTPS + "uc/services/TouchDBModel_bind";
    public static final String GET_HOME_BANNER = HOST_DOMAIN_HTTPS + "uc/services/homepage_gets";//获取首页配置
    public static final String GET_ALL_TRAFFIC_VOICE = HOST_DOMAIN_HTTPS + "uc/services/alltrafficvoice_get"; //所有可用流量语音套餐
    public static final String GET_TRAFFIC_VOICE = HOST_DOMAIN_HTTPS + "uc/services/trafficvoice_get";//当前可用流量语音套餐

    public static final String SHOPPING_LIST = HOST_DOMAIN_HTTPS + "uc/services/shipping_gets"; //获取配送方式
    public static final String PRD_CATEGORY = HOST_DOMAIN_HTTPS + "uc/services/prdcategory_gets"; //获取产品类目表
    public static final String GET_ADDRESS = HOST_DOMAIN_HTTPS + "uc/services/address_gets";//获取我的收货地址
    public static final String LOGOUT = HOST_DOMAIN_HTTPS + "uc/services/logout";
    public static final String REGISTER_PRECHECK = HOST_DOMAIN_HTTPS + "uc/services/register_precheck";
    public static final String IS_MOBILE_EXIST = HOST_DOMAIN_HTTPS + "uc/services/is_mobile_exist";
    public static final String GET_PHONES = HOST_DOMAIN_HTTPS + "uc/services/phone_gets";  //获取绑定手机号列表
    public static final String CREDTRIP_OPEN = HOST_DOMAIN_HTTPS + "uc/services/credtrip_open"; //开通信程分期

    public static final String ORDER_CANCEL = HOST_DOMAIN_HTTPS + "uc/services/order_cancel";//取消订单
    public static final String ORDER_REFUND = HOST_DOMAIN_HTTPS + "uc/services/refund"; //申请退货退款
    public static final String DATA_CARD_BIND = HOST_DOMAIN_HTTPS + "uc/services/datacard_bind";//卡绑定
    public static final String DATA_CARD_UNBIND = HOST_DOMAIN_HTTPS + "uc/services/datacard_unbind";
    public static final String CHANGE_PASSWORD = HOST_DOMAIN_HTTPS + "uc/services/change_password";
    public static final String RESET_PASSWORD = HOST_DOMAIN_HTTPS + "uc/services/reset_password";
    public static final String CHECK_CODE_GET = HOST_DOMAIN_HTTPS + "uc/services/checkcode_get";
    public static final String USER_LOGIN = HOST_DOMAIN_HTTPS + "uc/services/login";
    public static final String USER_REGISTER = HOST_DOMAIN_HTTPS + "uc/services/register";
    public static final String PAY_ORDER = HOST_DOMAIN_HTTPS + "uc/services/order_paying"; //支付 订单
    public static final String ORDER_RECEIVED = HOST_DOMAIN_HTTPS + "uc/services/order_received";//确认收货
    public static final String REDEEM_CODE = HOST_DOMAIN_HTTPS + "uc/services/evouchersn_exchange"; //兑换码兑换
//    public static final String GET_CALL_MESSAGE_LIST = HOST_DOMAIN_HTTPS + "uc/services/call_message_gets"; //通话记录获取 消息获取

    public static final String DELETE_CALL_MESSAGE = HOST_DOMAIN_HTTPS + "uc/services/call_delete"; // 删除通话或消息记录
    public static final String GET_CALL_GROUP = HOST_DOMAIN_HTTPS + "uc/services/call_group_gets";//获取首页群组通话记录
    public static final String GET_CALL = HOST_DOMAIN_HTTPS + "uc/services/call_gets";//获取单组通话记录
    public static final String DELETE_CALL_GROUP = HOST_DOMAIN_HTTPS + "uc/services/call_group_delete";//按组删除通话记录
    public static final String GET_MESSAGE_GROUP = HOST_DOMAIN_HTTPS + "uc/services/message_group_gets";//获取首页群组消息记录
    public static final String GET_ALL_MESSAGE = HOST_DOMAIN_HTTPS + "uc/services/message_gets";
    public static final String DELETE_MESSAGE_GROUP = HOST_DOMAIN_HTTPS + "uc/services/message_group_delete";//按组删除消息记录
    public static final String DELETE_MESSAGE = HOST_DOMAIN_HTTPS + "uc/services/call_message_delete"; //删除消息记录单条或多条记录
    public static final String BELL_GET = HOST_DOMAIN_HTTPS + "uc/services/bell_get";//获取铃声
    public static final String DELI_VERY_VOUCHER_VERIFY = HOST_DOMAIN_HTTPS + "om/services/deliveryvoucher_verify";//验证提货券
    public static final String DELI_VERY_VOUCHER_COMPLETE = HOST_DOMAIN_HTTPS + "om/services/deliveryvoucher_complete";//提货券完成提货
    public static final String GET_DOMAIN = HOST_DOMAIN_HTTPS + "uc/services/sip_domain_gets"; //获取 domain
    public static final String GET_BLACKLIST = HOST_DOMAIN_HTTPS + "uc/services/blacklist_get"; //获取黑名单数据
    public static final String ADD_BLACKLIST = HOST_DOMAIN_HTTPS + "uc/services/blacklist_add"; //添加黑名单号码
    public static final String DELETE_BLACKLIST = HOST_DOMAIN_HTTPS + "uc/services/blacklist_delete"; //删除黑名单号码


    //<--络漫宝 配置-->
    public final static String LMBAO_AUTH_START = "http://";
    public final static String LMBAO_AUTH_END = "/cgi-bin/luci/rpc/auth";//络漫宝访问令牌
    public final static String LMBAO_CONFIG_END = "/cgi-bin/luci/rpc/roambox";//络漫宝配置
    public static String ROAM_BOX_AUTH = ""; //络漫宝访问令牌 完整地址  LMBAO_AUTH_START + Ip + LMBAO_AUTH_END
    public static String ROAM_BOX_CONFIG = "";//络漫宝配置 完整地址  LMBAO_AUTH_START + Ip + LMBAO_CONFIG_END

    //配置 html 页面
    public static final String HTML_XIE_CHENG = "http://m.ctrip.com/html5/?Allianceid=328632&sid=810023&sourceid=2055&popup=close&autoawaken=close";
    public static final String CRED_TRIP_LOGIN = "https://api.credtrip.com/uc/login";
    public static final String CRED_TRIP_PAY_CALLBACK_URL = "http://www.roam-tech.com/uc/services/credtrip/pay_success1.html"; //信程支付回调
    public static final String CRED_TRIP_CALLBACK_URL = "http://www.roam-tech.com/uc/services/credtrip/pay_success.html";//信程开通
}
