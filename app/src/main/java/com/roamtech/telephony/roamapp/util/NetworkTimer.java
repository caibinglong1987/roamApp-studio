package com.roamtech.telephony.roamapp.util;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.util.Log;

import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.will.common.tool.wifi.MacAddressUtils;
import com.will.common.tool.wifi.WifiAdmin;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by caibinglong
 * on 2016/11/4.
 * 网络操作定时器
 */

public class NetworkTimer {
    private Timer mTimer = null; //定时器
    private TimerTask mTimerTask = null;
    private Handler handler;
    private Context context;
    private String ssId;
    private int TIMEOUT_NUMBER = 6; //超时次数

    public NetworkTimer(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public void setTimeoutNumber(int number) {
        TIMEOUT_NUMBER = number;
    }

    /**
     * 定时器
     */
    public void startTimer(final int networkId) {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    WifiAdmin wifiAdmin = new WifiAdmin(context);
                    wifiAdmin.openWifi();
                    wifiAdmin.startScan();
                    wifiAdmin.ConnectWifi(networkId);
                    while (wifiAdmin.getWifiStatusByNetworkId(networkId) != 0) {
                        wifiAdmin = new WifiAdmin(context);
                        wifiAdmin.startScan();
                        String connectSsId = wifiAdmin.getSSID().replace("'", "");
                        int state = wifiAdmin.getWifiStatusByNetworkId(networkId);
                        if (state == 0 || (connectSsId.equals(ssId) && wifiAdmin.getIpAddress() != 0)) {
                            Log.e("切换络漫宝网络---》", "次数-》" + TIMEOUT_NUMBER + "||state->" + "0" +
                                    "ssID-->" + ssId + "--->ip>" + MacAddressUtils.intToIpAddress(wifiAdmin.getIpAddress()));
                            handler.sendEmptyMessage(MsgType.MSG_CHANGE_NETWORK_SUCCESS);
                            return;
                        }
                        if (TIMEOUT_NUMBER <= 0) {
                            handler.sendEmptyMessage(MsgType.MSG_CHANGE_NETWORK_ERROR);
                            return;
                        }
                        Log.e("切换络漫宝网络---》", "次数-》" + TIMEOUT_NUMBER + "||state->" + state +
                                "ssID-->" + wifiAdmin.getSSID() + "--->ip>" + MacAddressUtils.intToIpAddress(wifiAdmin.getIpAddress()));
                        TIMEOUT_NUMBER--;
                        handler.sendEmptyMessage(MsgType.MSG_CHANGE_NETWORK_UPDATE);
                        try {
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        }
        if (mTimer != null) {
            mTimer.schedule(mTimerTask, 1000, 1000);
        }
    }

    /**
     * 切换网络
     *
     * @param networkId 网络id
     * @return
     */
    public boolean connectRoamBoxWifi(int networkId) {
        WifiAdmin wifiAdmin = new WifiAdmin(context);
        int status = wifiAdmin.getWifiStatusByNetworkId(networkId);
        if (status == 0) {
            Log.e("连接wifi-Status-->", status + "");
            return true;
        }
        Log.e("连接wifi-Status-->", status + "");
        int number = 0;
        while (wifiAdmin.getWifiStatusByNetworkId(networkId) != 0) {
            if (number == 0) {
                wifiAdmin.ConnectWifi(networkId);
            }
            int state = wifiAdmin.getWifiStatusByNetworkId(networkId);
            if (number >= 10) {
                return false;
            }
            if (state == 0) {
                return true;
            }
            Log.e("切换络漫宝网络---》", "次数-》" + number + "||state->" + state);
            number++;

        }
        wifiAdmin.ConnectWifi(networkId);
        return false;
    }

    /**
     * 连接选中的wifi
     *
     * @param ssId
     */
    public void connectCheckSSid(String ssId, String password) {
        this.ssId = ssId;
        WifiAdmin wifiAdmin = new WifiAdmin(context);
        wifiAdmin.startScan();
        if (wifiAdmin.getSSID().replace("\"", "").equals(ssId)) { //当前选中的wifi 和 当前连接的wifi一致
            handler.sendEmptyMessage(MsgType.MSG_CHANGE_NETWORK_SUCCESS);
            return;
        }
        int networkId = wifiAdmin.IsConfiguration(ssId);
        if (networkId == -1) {
            wifiAdmin.Connect(ssId, password);
            networkId = wifiAdmin.IsConfiguration(ssId);
        }
        if (networkId != -1) {
            Log.e("连接络漫宝正常--->", "networkId-->" + networkId + "||wifiName-->" + ssId);
            startTimer(networkId);
        } else {
            Log.e("连接络漫宝失败--->", "networkId-->" + networkId + "||wifiName-->" + ssId);
            handler.sendEmptyMessage(MsgType.MSG_CHANGE_NETWORK_ERROR);
        }
    }

    /**
     * 检测网络是否可以访问外网
     */
    public void checkNetworkIsSuccess() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (WifiAdmin.isNetworkConnected(context) && WifiAdmin.ping()) {
                    handler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_SUCCESS);
                } else {
                    handler.sendEmptyMessage(MsgType.MSG_ROAM_BOX_NETWORK_ERROR);
                }
            }
        }).start();
    }

    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    /**
     * 验证是否连接络漫宝wifi
     *
     * @param ssId
     * @return
     */
    public boolean checkRoamBoxWifi(String ssId) {
        WifiAdmin wifiAdmin = new WifiAdmin(context);
        if (wifiAdmin.getSSID().equals("\"" + ssId + "\"")) {
            return true;
        }
        return false;
    }
}
