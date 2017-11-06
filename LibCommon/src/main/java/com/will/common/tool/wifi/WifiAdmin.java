package com.will.common.tool.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by long
 * on 2016/10/8 09:04
 * wifi 操作
 */

public class WifiAdmin {

    public static final int TYPE_NO_PASSWORD = 0x11;
    public static final int TYPE_WEP = 0x12;
    public static final int TYPE_WPA_PSK = 0x13;

    //定义一个WifiManager对象
    private WifiManager mWifiManager;
    //定义一个WifiInfo对象
    private WifiInfo mWifiInfo;
    //扫描出的网络连接列表
    private List<ScanResult> mWifiList;
    private Context mContext;
    //网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;
    private WifiManager.WifiLock mWifiLock;

    public WifiAdmin(Context context) {
        mContext = context;
        //取得WifiManager对象
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        //取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    //打开wifi
    public boolean openWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }

    //关闭wifi
    public void closeWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    // 检查当前wifi状态
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    //锁定wifiLock
    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    //解锁wifiLock
    public void releaseWifiLock() {
        //判断是否锁定
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    //创建一个wifiLock
    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    //得到配置好的网络
    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfigurations;
    }

    //指定配置好的网络进行连接
    public void connetionConfiguration(int index) {
        if (index >= mWifiConfigurations.size()) {
            return;
        }
        //连接配置好指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
    }

    public void startScan() {
        mWifiManager.startScan();
        //得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        //得到配置好的网络连接
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
    }

    //得到网络列表
    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    //查看扫描结果
    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_");
            sb.append(Integer.toString(i + 1));
            sb.append(":");
            // 将ScanResult信息转换成一个字符串包
            // 其中把包括：BSSID、SSID、capabilities、frequency、level
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }

    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    public String getSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID().replace("\"", "");
    }

    public int getIpAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }

    //得到连接的ID
    public int getNetWordId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    //得到wifiInfo的所有信息
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    public WifiInfo getWInfo() {
        return mWifiInfo;
    }

    //添加一个网络并连接
    public boolean addNetWork(WifiConfiguration configuration) {
        int wcgId = mWifiManager.addNetwork(configuration);
        return mWifiManager.enableNetwork(wcgId, false);
    }

    // 指定配置好的网络进行连接
    public void connectConfiguration(int index) {
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfigurations.size()) {
            return;
        }
        // 连接配置好的指定ID的网络
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId, true);
    }

    /**
     * 判断 是否连接网络
     *
     * @param configId
     * @return
     */
    public int getWifiStatus(int configId) {
        return mWifiManager.getConfiguredNetworks().get(configId).status;
    }

    /**
     * 判断wifi是否连接
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                if(mNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获取wifi状态
     *
     * @param networkId
     * @return
     */
    public int getWifiStatusByNetworkId(int networkId) {
        startScan();
        if (mWifiConfigurations != null && networkId != -1) {
            for (int i = 0; i < mWifiConfigurations.size(); i++) {
                if (mWifiConfigurations.get(i).networkId == networkId) {
                    return mWifiConfigurations.get(i).status;
                }
            }
        }
        return 1;
    }

    /**
     * 获取 wifi state
     *
     * @return state
     */
    public int getWifiState() {
        return mWifiManager.getWifiState();  //获取wifi AP状态
    }

    //提供一个外部接口，传入要连接的无线网
    public boolean Connect(String ssId, String Password) {
        if (!openWifi()) {
            return false;
        }
        //开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        //状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                //为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }
        WifiConfiguration tempConfig = isExists(ssId);
        if (tempConfig != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                return mWifiManager.enableNetwork(tempConfig.networkId, false);
            }
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        int security_type = TYPE_WPA_PSK;
        if (Password == null || Password.length() == 0) {
            security_type = TYPE_NO_PASSWORD;
        }
        WifiConfiguration wifiConfig = createWifiInfo(ssId, Password, security_type); //getSecurity(tempConfig)
        if (wifiConfig == null) {
            return false;
        }
        int netId = mWifiManager.addNetwork(wifiConfig);
        return mWifiManager.enableNetwork(netId, false);
    }

    public boolean Connect(String ssId) {
        if (!openWifi()) {
            return false;
        }
        //开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        //状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                //为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }
        WifiConfiguration tempConfig = isExists(ssId);
        if (tempConfig != null) {
            return mWifiManager.enableNetwork(tempConfig.networkId, false);
        }
        return false;
    }

    /**
     * 创建 wifi 连接 获取配置信息
     *
     * @param ssId     ssId
     * @param Password 密码
     * @param Type     类型
     * @return 返回配置信息
     */
    private WifiConfiguration createWifiInfo(String ssId, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssId + "\"";
        switch (Type) {
            case TYPE_WPA_PSK:
                config.preSharedKey = "\"" + Password + "\"";
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.status = WifiConfiguration.Status.ENABLED;
                break;
            case TYPE_NO_PASSWORD:// WIFICIPHER_NOPASS
                config.wepKeys[0] = "\"" + "\"";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            case TYPE_WEP:// WIFICIPHER_WEP
                config.hiddenSSID = true;
                config.wepKeys[0] = "\"" + Password + "\"";
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            default:
                break;
        }
        return config;
    }

    private int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_PSK)) {
            return TYPE_WPA_PSK;
        }
        if (config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.IEEE8021X)) {
            return TYPE_WEP;
        }
        return (config.wepKeys[0] != null) ? TYPE_WEP : TYPE_NO_PASSWORD;
    }

    /**
     * 查看是否 配置过该网络
     *
     * @param ssId ssId
     * @return 配置信息
     */
    public WifiConfiguration isExists(String ssId) {
        for (WifiConfiguration config : mWifiConfigurations) {
            if (config.SSID != null && (config.SSID.equals("\"" + ssId + "\"") ||
                    config.SSID.equals(ssId))) {
                return config;
            }
        }
        return null;
    }

    /**
     * 断开指定ID的网络 * * @param netId
     */
    public void disConnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    /**
     * 连接指定Id的WIFI * * @param wifiId * @return
     */
    public boolean ConnectWifi(int wifiId) {
        for (int i = 0; i < mWifiConfigurations.size(); i++) {
            WifiConfiguration wifi = mWifiConfigurations.get(i);
            if (wifi.networkId == wifiId) {
                return mWifiManager.enableNetwork(wifiId, true);
            }
        }
        return false;
    }

    /**
     * 判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId * * @param SSID * @return
     */
    public int IsConfiguration(String SSID) {
        Log.i("IsConfiguration", String.valueOf(mWifiConfigurations.size()));
        int networkId = -1;
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
        for (int i = 0; i < mWifiConfigurations.size(); i++) {
            if (mWifiConfigurations.get(i).SSID.replace("'","").equals(SSID)) {
                networkId = mWifiConfigurations.get(i).networkId;
                break;
            }
        }
        return networkId;
    }

    /**
     * 网络强度排序
     *
     * @param list 网络列表
     */
    public List<ScanResult> sortByLevel(List<ScanResult> list) {
        for (int i = 0; i < list.size(); i++)
            for (int j = 1; j < list.size(); j++) {
                if (list.get(i).level < list.get(j).level)    //level属性即为强度
                {
                    ScanResult temp = null;
                    temp = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, temp);
                }
            }
        return list;
    }

    /**
     * 添加指定WIFI的配置信息,原列表不存在此SSID
     * * * @param wifiList
     * * @param ssId
     * * @param pwd
     * * @return
     */
    public int AddWifiConfig(List<ScanResult> wifiList, String ssId, String pwd) {
        int wifiId = -1;
        for (int i = 0; i < wifiList.size(); i++) {
            ScanResult wifi = wifiList.get(i);
            if (wifi.SSID.equals(ssId)) {
                Log.i("AddWifiConfig", "equals");
                WifiConfiguration wifiCong = new WifiConfiguration();
                wifiCong.SSID = "\"" + wifi.SSID + "\"";// \"转义字符，代表"
                wifiCong.preSharedKey = "\"" + pwd + "\"";// WPA-PSK密码
                wifiCong.hiddenSSID = false;
                wifiCong.status = WifiConfiguration.Status.ENABLED;
                wifiId = mWifiManager.addNetwork(wifiCong);// 将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1
                if (wifiId != -1) {
                    return wifiId;
                }
            }
        }
        return wifiId;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 1 www.baidu.com");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// 除非百度挂了，否则用这个应该没问题~
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);//ping3次
            // 读取ping的内容，可不加。
//            Log.i("TTT", "result content : " + stringBuffer.toString());
            // PING的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "successful~";
                return true;
            } else {
                result = "failed~ cannot reach the IP address";
            }
        } catch (IOException e) {
            result = "failed~ IOException";
        } catch (InterruptedException e) {
            result = "failed~ InterruptedException";
        } finally {
            Log.i("TTT", "result = " + result);
        }
        return false;
    }

}
