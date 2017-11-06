package com.roamtech.telephony.roamapp.util;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.view.KeyEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class TelephonyUtils {
    private Method telephonyCall;
    private Method telephonyEndCall;
    private Method telephonyAnswerCall;
    private Object telephonyObject;
    private static TelephonyUtils sMe;
    private Context _context;
    private WifiManager mWifiManager;
    private WifiManagerUtil mWifiUtil;

    public TelephonyUtils(Context context) {
        try {
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";

            Class telephonyClass;
            Class telephonyStubClass;
            Class serviceManagerClass;
            Class serviceManagerStubClass;
            Class serviceManagerNativeClass;
            Class serviceManagerNativeStubClass;
            Method getDefault;
            Method[] temps;
            Constructor[] serviceManagerConstructor;
            // Method getService;
            Object serviceManagerObject;

            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);

            Method getService = // getDefaults[29];
                    serviceManagerClass.getMethod("getService", String.class);

            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);

            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");

            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);

            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyAnswerCall = telephonyClass.getMethod("answerRingingCall");
            mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            mWifiUtil = WifiManagerUtil.createInstance(mWifiManager);
            _context = context;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized static final TelephonyUtils createAndStart(Context context) {
        if (sMe != null)
            throw new RuntimeException("TelephonyUtils is already initialized");
        sMe = new TelephonyUtils(context);
        return sMe;
    }

    public static TelephonyUtils getInstance() {
        if (sMe == null) {
            throw new IllegalStateException("No TelephonyUtils here!");
        }
        return sMe;
    }

    public void destroy() {
        if (sMe == null) return;
        sMe = null;
    }

    public void call(String number) {
        try {
            telephonyCall.invoke(telephonyObject, "com.roamtech.telephony.Dialer", number);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void answerRingingCall() {
        Intent meidaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_HEADSETHOOK);
        meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        _context.sendOrderedBroadcast(meidaButtonIntent, null);
        /*
		try {
			telephonyAnswerCall.invoke(telephonyObject);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }

    public void endCall() {
        try {
            telephonyEndCall.invoke(telephonyObject);
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public WifiInfo getCurrentWifiConnection() {
        return mWifiManager.getConnectionInfo();
    }

    public void disconnectTouch() {
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo.getSSID().equals("roamtouchAP") || (mWifiInfo.getSSID().equals("\"" + "roamtouchAP" + "\""))) {
            mWifiManager.removeNetwork(mWifiInfo.getNetworkId());//.disableNetwork(mWifiInfo.getNetworkId());
        }
    }

    public void connectUplink(String ssid) {
        List<WifiConfiguration> configuredWifis = mWifiManager.getConfiguredNetworks();
        if (configuredWifis != null) {
            for (WifiConfiguration config : configuredWifis) {
                if (config.SSID != null && (config.SSID.equals("\"" + ssid + "\"") ||
                        config.SSID.equals(ssid))) {
                    mWifiManager.enableNetwork(config.networkId, true);
                }
            }
        }
    }

    public List<WifiConfiguration> getConfiguredNetworks() {
        return mWifiManager.getConfiguredNetworks();
    }

    public void disconnectUplink(int network) {
        mWifiManager.disableNetwork(network);
    }

    public boolean connectTouch() {
        if (!mWifiUtil.checkWifiConnected("roamtouchAP")) {
            boolean ret = false;
			/*int retry_cnt = 0;
			 do{*/
            ret = mWifiUtil.enableNetwork("roamtouchAP", null, true);
				/* try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}while(!ret&&retry_cnt++<3);*/
            return ret;
        }
        return true;
    }
}
