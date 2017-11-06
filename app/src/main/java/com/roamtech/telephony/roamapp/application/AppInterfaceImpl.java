package com.roamtech.telephony.roamapp.application;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.roamtech.telephony.roamapp.activity.function.Blacklist;
import com.roamtech.telephony.roamapp.activity.function.GetBell;
import com.roamtech.telephony.roamapp.db.DatabaseHelper;
import com.roamtech.telephony.roamapp.db.dao.CommonDao;
import com.roamtech.telephony.roamapp.db.model.AddressDBModel;
import com.roamtech.telephony.roamapp.db.model.HomePageDBModel;
import com.roamtech.telephony.roamapp.db.model.PaymentDBModel;
import com.roamtech.telephony.roamapp.db.model.Prd_categoryDBModel;
import com.roamtech.telephony.roamapp.db.model.ProductDBModel;
import com.roamtech.telephony.roamapp.db.model.ShippingDBModel;
import com.roamtech.telephony.roamapp.helper.numberAttr.NumberResource;
import com.roamtech.telephony.roamapp.util.CallMessageUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.UserInfoUtil;
import com.umeng.analytics.MobclickAgent;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * app 初始化 接口实现
 * Created by long on 16-2-29.
 */
public class AppInterfaceImpl implements AppInterface {

    private ExecutorService pool = Executors.newFixedThreadPool(1);
    public static CommonDao<AddressDBModel> addressCommon;
    public static CommonDao<HomePageDBModel> homeCommon;
    public static CommonDao<PaymentDBModel> paymentCommon;
    public static CommonDao<ShippingDBModel> shipCommon;
    public static CommonDao<ProductDBModel> productCommon;
    public static CommonDao<Prd_categoryDBModel> categoryCommon;

    @Override
    public void initThirdPlugin(final Context context) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                //Fresco.initialize(context);
                //MobclickAgent.setDebugMode(true);
                MobclickAgent.enableEncrypt(true);
                MobclickAgent.setCatchUncaughtExceptions(true);
            }
        });
    }

    @Override
    public void initDir(List<String> dirs) {
        for (String dir : dirs) {
            File dirFile = new File(dir);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
        }
    }

    public void initService(Context context, Class<? extends Service> service, String action) {
        Intent serviceIntent = new Intent(context, service);
        serviceIntent.setAction(action);
        context.startService(serviceIntent);
    }

    @Override
    public void initDB(Context context, String DBName) {
        RoamApplication.sDatabaseHelper = DatabaseHelper.open(context);
        addressCommon = new CommonDao<>(RoamApplication.sDatabaseHelper, AddressDBModel.class);
        homeCommon = new CommonDao<>(RoamApplication.sDatabaseHelper, HomePageDBModel.class);
        paymentCommon = new CommonDao<>(RoamApplication.sDatabaseHelper, PaymentDBModel.class);
        shipCommon = new CommonDao<>(RoamApplication.sDatabaseHelper, ShippingDBModel.class);
        productCommon = new CommonDao<>(RoamApplication.sDatabaseHelper, ProductDBModel.class);
        categoryCommon = new CommonDao<>(RoamApplication.sDatabaseHelper, Prd_categoryDBModel.class);

        try {
            NumberResource.copyDataBase(context, AppConfig.NUMBER_ADDRESS_DB_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initCallHistory(Context context) {
        JSONObject jsonObject = UserInfoUtil.getInstance().getJsonUser(context);
        CallMessageUtil callMessageUtil = new CallMessageUtil(context, null);
        UserInfoUtil.getInstance().clearMissCallNumber(context);
        if (jsonObject != null) {
            callMessageUtil.getGroupAllCallList(jsonObject, false, true);
            jsonObject = UserInfoUtil.getInstance().getJsonUser(context);
            callMessageUtil.getGroupMissAllCallList(jsonObject, false);
            jsonObject = UserInfoUtil.getInstance().getJsonUser(context);
            callMessageUtil.getBlacklist(jsonObject);
        }
        RoamApplication.bell = SPreferencesTool.getInstance().getBell(context);
        new GetBell(context).getBell();
    }

    @Override
    public void destroy() {
        RoamApplication.pool.shutdownNow();//关闭线程池里面所有任务
        RoamApplication.sDatabaseHelper.close();
    }

}
