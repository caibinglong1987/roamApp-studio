package com.roamtech.telephony.roamapp.application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.multidex.MultiDex;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import com.roam.daemon.DaemonApplication;
import com.roam.daemon.DaemonConfigurations;
import com.roamtech.telephony.roamapp.Receiver.ProcessReceiverOne;
import com.roamtech.telephony.roamapp.Receiver.ProcessReceiverTwo;
import com.roamtech.telephony.roamapp.bean.Bell;
import com.roamtech.telephony.roamapp.bean.NetworkConfigBean;
import com.roamtech.telephony.roamapp.db.DBConfig;
import com.roamtech.telephony.roamapp.db.DatabaseHelper;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;
import com.roamtech.telephony.roamapp.event.EventLoadContactEnd;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.server.ServiceGuard;
import com.roamtech.telephony.roamapp.server.SipService;
import com.roamtech.telephony.roamapp.util.LocalDisplay;

import org.greenrobot.eventbus.EventBus;
import org.linphone.ContactsManager;
import org.linphone.LinphoneService;
import org.linphone.mediastream.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.Intent.ACTION_MAIN;


/**
 * Created by xincheng on 6/6/2016.
 * 完成一些app启动的初始化操作
 */
public class RoamApplication extends DaemonApplication {
    private static final int CONTACT_CHANGE = 1;
    private static String SDCARD_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static final String FILEPATH_ROOT = SDCARD_ROOT + File.separator + AppConfig.FILEPATH_ROOT_NAME;
    public static final String FILEPATH_CAMERA = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_CAMERA_NAME;
    public static final String FILEPATH_RECORD = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_RECORD_NAME;
    public static final String FILEPATH_UPAPK = FILEPATH_ROOT + File.separator + AppConfig.FILEPATH_UPAPK_NAME;
    private AppInterfaceImpl mAppInterface = new AppInterfaceImpl();
    public static final ExecutorService pool = Executors.newFixedThreadPool(5);
    public static DatabaseHelper sDatabaseHelper;
    public static List<TouchDBModel> RoamBoxList = new ArrayList<>();
    public static boolean bGoConfig = false; //是否进行配置
    public static String RoamBoxToken = ""; //配置络漫宝 token
    public static boolean isAutoCancel = false; //是否取消自动检测
    public static int credTripStatus = 0;// 1正在支付 2 取消支付 3支付失败  4支付成功
    public static long credTripOrderId = 0;//订单ID
    public static NetworkConfigBean RoamBoxConfigOld = null; //当前络漫宝配置信息
    public static boolean isLoadCallHistory = false; //是否已经加载 通话记录
    public static boolean isLoadMissCallHistory = false;
    public static boolean isLoadGroupMessage = false;
    public static boolean bSwitchAccount = false; //切换账号
    public static boolean isNewProxyConfig = false;
    public static String loginTouchPhone = null;
    public static String chatNowUserPhone = null; //正在聊天的对象 chatting
    public static int badgeSumNumber = 0;
    public static Bell bell;
    public static boolean isLogined = false;

    //常量区
    public static final boolean isDebug = true; //配置环境 正式 or 测试
    public static boolean webCanGoBack = true;

    public static final String WAN_PROTO_STATIC = "static";
    public static final String WAN_PROTO_PPPOE = "pppoe";
    public static final String WAN_PROTO_DHCP = "dhcp";
    public static final String WAN_PROTO_WIRELESS = "wireless";

    @Override
    public void onCreate() {
        super.onCreate();
        //不用每次获取都传递Context对象
        LocalDisplay.init(this);
        loadingContacts();
        contactChangeListener();
        initImageLoader();
        List<String> dirs = new ArrayList<>();
        {
            dirs.add(FILEPATH_UPAPK);
            dirs.add(FILEPATH_CAMERA);
            dirs.add(FILEPATH_RECORD);
        }
        mAppInterface.initThirdPlugin(getApplicationContext());
        mAppInterface.initDir(dirs);
        mAppInterface.initDB(getApplicationContext(), DBConfig.DB_NAME);
        mAppInterface.initService(getApplicationContext(), LinphoneService.class, ACTION_MAIN);
        mAppInterface.initService(getApplicationContext(), SipService.class, "");
        mAppInterface.initCallHistory(getApplicationContext());
    }

    private void initImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                //.memoryCacheExtraOptions(480, 800) // default = device screen dimensions 内存缓存文件的最大长宽
                //.diskCacheExtraOptions(480, 800, null)  // 本地缓存的详细信息(缓存的最大长宽)，最好不要设置这个
                .threadPoolSize(3) // default  线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)) //可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)  // 内存缓存的最大值
                .memoryCacheSizePercentage(13) // default
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb sd卡(本地)缓存的最大值
                .diskCacheFileCount(100)  // 可以缓存的文件数量
                // default为使用HASHCODE对UIL进行加密命名， 还可以用MD5(new Md5FileNameGenerator())加密
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .imageDownloader(new BaseImageDownloader(this)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs() // 打印debug log
                .build(); //开始构建
        ImageLoader.getInstance().init(config);
    }

    /**
     * 程序启动的时候就加载通讯录
     */
    private void loadingContacts() {
        new AsyContactTask().execute();
    }

    private void contactChangeListener() {
        ContentObserver mObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                // 当系统联系人数据库发生更改时触发此操作
                mContactHandler.removeMessages(CONTACT_CHANGE);
                //延时ELAPSE_TIME(10秒）发送同步信号“0”
                mContactHandler.sendEmptyMessageDelayed(CONTACT_CHANGE, 200);
            }
        };
        //注册监听联系人数据库
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mObserver);
    }

    class AsyContactTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //Looper.prepare();
            //ContactHelper.loadContacts(getContentResolver());
            RDContactHelper.loadContacts(getContentResolver());
            //Looper.loop();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            EventBus.getDefault().postSticky(new EventLoadContactEnd("load Success"));
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mContactHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CONTACT_CHANGE) {
                ContactsManager.getInstance().setAllPhoneContacts();
                loadingContacts();
            }
        }
    };

    /**
     * you can override this method instead of {@link android.app.Application attachBaseContext}
     *
     * @param base
     */
    @Override
    public void attachBaseContextByDaemon(Context base) {
        super.attachBaseContextByDaemon(base);
        MultiDex.install(this);
    }

    /**
     * give the configuration to lib in this callback
     *
     * @return
     */
    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.roamtech.telephony.roamapp:child",
                SipService.class.getCanonicalName(),
                ProcessReceiverOne.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.roamtech.telephony.roamapp:guard",
                ServiceGuard.class.getCanonicalName(),
                ProcessReceiverTwo.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }


    class MyDaemonListener implements DaemonConfigurations.DaemonListener {
        @Override
        public void onPersistentStart(Context context) {
            Log.w("JNI---onPersistentStart");
        }

        @Override
        public void onDaemonAssistantStart(Context context) {
            Log.w("JNI---onDaemonAssistantStart");
        }

        @Override
        public void onWatchDaemonDaed() {
            Log.w("JNI---onWatchDaemonDaed");
        }
    }

}
