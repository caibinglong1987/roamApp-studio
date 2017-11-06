package com.roamtech.telephony.roamapp.db;

import com.roamtech.telephony.roamapp.bean.CallDetailRecordDBModel;
import com.roamtech.telephony.roamapp.db.model.AddressDBModel;
import com.roamtech.telephony.roamapp.db.model.BlacklistDBModel;
import com.roamtech.telephony.roamapp.db.model.ChatDBModel;
import com.roamtech.telephony.roamapp.db.model.HomePageDBModel;
import com.roamtech.telephony.roamapp.db.model.PaymentDBModel;
import com.roamtech.telephony.roamapp.db.model.Prd_categoryDBModel;
import com.roamtech.telephony.roamapp.db.model.ProductDBModel;
import com.roamtech.telephony.roamapp.db.model.ShippingDBModel;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long
 * on 16-9-27.
 * 数据库配置
 */
public class DBConfig {
    //db_name
    public static final String DB_NAME = "roam.db";
    //db_version
    public static final int DB_VERSION = 32;

    //table_name
    public static final String TABLE_CHAT = "chat"; //lin_phone
    public static final String TABLE_CALL_DETAIL = "all_call_history";//全部通话记录
    public static final String TABLE_PRODUCT = "Product"; //商品表
    public static final String TABLE_PAYMENT = "Payment"; //支付方式
    public static final String TABLE_SHIPPING = "Shipping";//配送方式
    public static final String TABLE_PRD_CATEGORY = "Prd_category"; //产品类目表
    public static final String TABLE_USER_ADDRESS = "Address";//收货地址表
    public static final String TABLE_ROAM_BOX_TOUCH = "RoamBoxTouch";//我的络漫宝表
    public static final String TABLE_HOME_PAGE = "HomePage";//首页
    public static final String TABLE_NUMBER_ADDRESS = "areas";
    public static final String TABLE_BLACKLIST = "Blacklist"; //黑名单
    //table class list
    public static final List<Class<?>> DB_CLASSES = new ArrayList<>();

    static {
        DB_CLASSES.add(ProductDBModel.class);
        DB_CLASSES.add(PaymentDBModel.class);
        DB_CLASSES.add(ShippingDBModel.class);
        DB_CLASSES.add(ChatDBModel.class);
        DB_CLASSES.add(Prd_categoryDBModel.class);
        DB_CLASSES.add(AddressDBModel.class);
        DB_CLASSES.add(TouchDBModel.class);
        DB_CLASSES.add(HomePageDBModel.class);
        DB_CLASSES.add(CallDetailRecordDBModel.class);
        DB_CLASSES.add(BlacklistDBModel.class);
    }
}
