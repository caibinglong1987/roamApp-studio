package com.roamtech.telephony.roamapp.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.will.common.log.DebugLogs;
import com.will.common.tool.network.NetWorkUtil;

/***
 * 网络广播
 *
 */
public class NetworkReceiver extends BroadcastReceiver {
    public static int HISTORY_TYPE = NetWorkUtil.TYPE_NOT_CONNECTED;
    private NetWorkHandler mNetWorkHandler;

    public NetworkReceiver(NetWorkHandler netWorkHandler){
        this.mNetWorkHandler = netWorkHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            String action = intent.getAction();
            if(NetWorkUtil.ACTION_NETWORK.equals(action)) {
                NetworkInfo info = NetWorkUtil.getNetworkInfo(context);
                if(info != null) {
                    if(HISTORY_TYPE == info.getType()){
                        return;
                    }
                    if(mNetWorkHandler != null){
                        mNetWorkHandler.onActive(info);
                    }
                }
                else {
                    HISTORY_TYPE = NetWorkUtil.TYPE_NOT_CONNECTED;
                    DebugLogs.e("没有可用网络");
                    if(mNetWorkHandler != null){
                        mNetWorkHandler.onInactive();
                    }
                }
            }
        }
    }

    public interface NetWorkHandler{
        void onActive(NetworkInfo info);//网络可用
        void onInactive();//网络不可用
    }
}
