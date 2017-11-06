package com.roamtech.telephony.roamapp.application;

import android.os.Environment;
import java.io.File;

/**
 * Created by long on 2016/9/20.
 * 全局配置
 */

public class AppConfig {
    public static final String FILEPATH_ROOT_NAME = "roamapp";
    public static final String FILEPATH_CAMERA_NAME = "camera";
    public static final String FILEPATH_CACHE_NAME = "cache";
    public static final String FILEPATH_RECORD_NAME = "recording";
    public static final String FILEPATH_UPAPK_NAME = "upapk";

    public static final String LINPHONE_SERVICE_NAME = "org.linphone.LinphoneService";
    public static final String SERVICE_SIP = "com.roamtech.telephony.roamapp.server.SipService";

    public static final String NUMBER_ADDRESS_DB_PATH =
            File.separator + "data" + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + "com.roamtech.telephony.roamapp"
                    + File.separator + "databases" + File.separator + "telocation.db";
    //拍照获取图片
    public static final int SET_ADD_PHOTO_CAMERA = 10001;
    //从相册获取图片
    public static final int SET_ADD_PHOTO_ALBUM = 10002;
    //图像裁剪返回代码
    public static final int REQUEST_CROP_PICTURE = 10003;
}
