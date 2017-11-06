package com.roamtech.telephony.roamapp.util;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

public class WifiManagerUtil {
    /** These values are matched in string arrays -- changes must be kept in sync */
    static final int SECURITY_NONE = 0;
    static final int SECURITY_WEP = 1;
    static final int SECURITY_PSK = 2;
    /// M: security type @{
    static final int SECURITY_WPA_PSK = 3;
    static final int SECURITY_WPA2_PSK = 4;
    static final int SECURITY_EAP = 5;
    static final int SECURITY_WAPI_PSK = 6;
    static final int SECURITY_WAPI_CERT = 7;
    /** 
     * WAPI with pre-shared key.
     * @hide
     * @internal
     */
    public static final int WAPI_PSK = 5;
    /** 
     * WAPI with certificate authentication.
     * @hide
     * @internal
     */
    public static final int WAPI_CERT = 6;
    public static final String WIFI_AP_STATE_CHANGED_ACTION = "android.net.wifi.WIFI_AP_STATE_CHANGED";
    public static final String EXTRA_WIFI_AP_STATE = "wifi_state";
	private WifiManager mWifiManager;
	private static WifiManagerUtil instance;
	private static final String TAG = "roamapp-wifi";
	private WifiInfo mWifiInfo;
	private List<ScanResult> mWifiList; 
	private List<WifiConfiguration> mWifiConfigurations;  
    public enum AuthenticationType
    {
        TYPE_NONE, TYPE_WPA, TYPE_WPA2
    }
    private WifiManagerUtil(WifiManager wifiManager)
    {
        mWifiManager = wifiManager;
    }

    public synchronized static WifiManagerUtil createInstance(WifiManager wifiManager)
    {
        if (instance == null)
        {
        	instance = new WifiManagerUtil(wifiManager);
        }

        return instance;
    }
    public static WifiManagerUtil getInstance()
    {
        if (instance == null)
        {
        	throw new IllegalStateException("No WifiManagerUtil here!");
        }

        return instance;
    }

    public WifiConfiguration getWifiApConfiguration()
    {
        WifiConfiguration config = null;
        try
        {
            Method method = mWifiManager.getClass().getMethod("getWifiApConfiguration");
            config = (WifiConfiguration) method.invoke(mWifiManager, (Object[]) null);
        }
        catch (Exception e)
        {
            Log.e(TAG, "", e);
        }

        return config;
    }

    public boolean setWifiApConfiguration(WifiConfiguration config)
    {
        boolean ret = false;
        try
        {
            Method method = mWifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            ret = (Boolean) method.invoke(mWifiManager, config);
        }
        catch (Exception e)
        {
            Log.e(TAG, "", e);
        }

        return ret;
    }

    public boolean isWifiApEnabled()
    {
        boolean ret = false;
        try
        {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            ret = (Boolean) method.invoke(mWifiManager, (Object[]) null);
        }
        catch (Exception e)
        {
            Log.e(TAG, "", e);
        }

        return ret;
    }

    public boolean setWifiApEnabled(WifiConfiguration config, boolean enabled)
    {
        boolean ret = false;
        try
        {
            Method method = mWifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            ret = (Boolean) method.invoke(mWifiManager, config, enabled);
        }
        catch (Exception e)
        {
            Log.e(TAG, "", e);
        }

        return ret;
    }

    public WifiConfiguration generateWifiConfiguration(AuthenticationType type, String ssid, String MAC, String password)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = ssid;
        config.BSSID = MAC;
        //Log.d(TAG, "MAC = " + config.BSSID);
        switch (type)
        {
            case TYPE_NONE:
            {
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                break;
            }
            case TYPE_WPA:
            {
                config.preSharedKey = password;

                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.NONE);
                config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

                break;
            }
            case TYPE_WPA2:
            {
                config.preSharedKey = password;

                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.NONE);
                config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);

                break;
            }
            default:
            {
                break;
            }
        }

        return config;
    }
    static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) ||
                config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        /// M: support wapi psk/cert @{
        if (config.allowedKeyManagement.get(WAPI_PSK)) {
            return SECURITY_WAPI_PSK;
        }

        if (config.allowedKeyManagement.get(WAPI_CERT)) {
            return SECURITY_WAPI_CERT;
        }
        
        if (config.wepTxKeyIndex >= 0 && config.wepTxKeyIndex < config.wepKeys.length 
                && config.wepKeys[config.wepTxKeyIndex] != null) {
            return SECURITY_WEP;
        }
        ///@}
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }
	static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WAPI-PSK")) {
            /// M:  WAPI_PSK
            return SECURITY_WAPI_PSK;
        } else if (result.capabilities.contains("WAPI-CERT")) {
            /// M: WAPI_CERT
            return SECURITY_WAPI_CERT;
        } else if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

	public boolean checkWifiConnected(String ssid) {
		mWifiInfo=mWifiManager.getConnectionInfo(); 
		if(mWifiInfo == null || (!mWifiInfo.getSSID().equals(ssid)&&(!mWifiInfo.getSSID().equals("\""+ssid+"\"")))) {
			return false;
		}
		return true;
	}
    WifiConfiguration getConfig(int security,String ssid, String bssid, String password) {

        WifiConfiguration config = new WifiConfiguration();

        config.allowedAuthAlgorithms.clear();  
        config.allowedGroupCiphers.clear();  
        config.allowedKeyManagement.clear();  
        config.allowedPairwiseCiphers.clear();  
        config.allowedProtocols.clear();  
        config.SSID = "\"" + ssid + "\""; ;
        config.hiddenSSID = true;
        /// M: save BSSID to configuration
        config.BSSID = bssid;

        /// M: get priority of configuration
        
        config.priority = 2;

        switch (security) {
        case SECURITY_NONE:
        	  config.wepKeys[0] = null;  
              config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
              config.wepTxKeyIndex = 0;   
              break;

        case SECURITY_WEP:
              config.allowedKeyManagement.set(KeyMgmt.NONE);
              config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
              config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
              if (password != null && password.length() != 0) {
                    int length = password.length();
                    /// M: get selected WEP key index @{
                    int keyIndex = 0;// selected password index, 0~3
                    /*if (mWEPKeyIndex != null
                                && mWEPKeyIndex.getSelectedItemPosition() != AdapterView.INVALID_POSITION) {
                          keyIndex = mWEPKeyIndex.getSelectedItemPosition();
                    }*/
                    /// @}
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 32)
                                && password.matches("[0-9A-Fa-f]*")) {
                          /// M: hex password
                          config.wepKeys[keyIndex] = password;
                    } else {
                          /// M: ASCII password
                          config.wepKeys[keyIndex] = '"' + password + '"';
                    }
                    /// M: set wep index to configuration
                    config.wepTxKeyIndex = keyIndex;
              }
              break;
        case SECURITY_WPA_PSK:
        case SECURITY_WPA2_PSK:
        case SECURITY_PSK:
              config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
              if (password != null && password.length() != 0) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                          config.preSharedKey = password;
                    } else {
                          config.preSharedKey = '"' + password + '"';
                    }
              }
              break;

        default:
              return null;
        }
        return config;
  }
    private WifiConfiguration isExists(String ssid) {
    	mWifiConfigurations=mWifiManager.getConfiguredNetworks();
    	if(mWifiConfigurations == null) return null;
		for(WifiConfiguration config:mWifiConfigurations) {
			if(config.SSID != null && (config.SSID.equals("\""+ssid+"\"")  ||
	        		config.SSID.equals(ssid))) {
				return config;
			}
		}
		return null;
    }
    private WifiConfiguration createWifiInfo(String SSID, String password, int type) {  
        
        Log.e("Roamtouch","SSID = " + SSID + "## Password = " + password + "## Type = " + type);  
          
        WifiConfiguration config = new WifiConfiguration();  
        config.allowedAuthAlgorithms.clear();  
        config.allowedGroupCiphers.clear();  
        config.allowedKeyManagement.clear();  
        config.allowedPairwiseCiphers.clear();  
        config.allowedProtocols.clear();  
        config.SSID = "\"" + SSID + "\"";  
  
      //增加热点时候 如果已经存在SSID 则将SSID先删除以防止重复SSID出现
        WifiConfiguration tempConfig = isExists(SSID);  
        if (tempConfig != null) {  
        	mWifiManager.removeNetwork(tempConfig.networkId);   
        }  
           
        if (type == SECURITY_NONE) {   // WIFICIPHER_NOPASS  
            config.wepKeys[0] = null;  
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
            config.wepTxKeyIndex = 0;   
              
        } else if (type == SECURITY_WEP) {  //  WIFICIPHER_WEP   
            config.hiddenSSID = true;  
            config.wepKeys[0] = "\"" + password + "\"";  
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);  
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);  
            config.wepTxKeyIndex = 0;  
        } else if (type == SECURITY_PSK) {   // WIFICIPHER_WPA  
            config.preSharedKey = "\"" + password + "\"";  
            config.hiddenSSID = true;  
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);  
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);  
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);  
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);   
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);  
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);  
            config.status = WifiConfiguration.Status.ENABLED;  
        }            
        return config;  
    }      
	public void addNetwork(String ssid, String password) {
		/*mWifiManager.startScan();  
		mWifiList=mWifiManager.getScanResults();
		mWifiConfigurations=mWifiManager.getConfiguredNetworks();
		for(WifiConfiguration config:mWifiConfigurations) {
			if(config.SSID != null && (config.SSID.equals("\""+ssid+"\"")  ||
            		config.SSID.equals(ssid))) {
				config.preSharedKey = password;
				return mWifiManager.enableNetwork(config.networkId, true);
			}
		}*/
		/*for(ScanResult scan:mWifiList) {
			if(scan.SSID != null && (scan.SSID.equals("\""+ssid+"\"")  ||
            		scan.SSID.equals(ssid))) {
				int security = getSecurity(scan);*/
				
				WifiConfiguration configuration = createWifiInfo(ssid, password, StringUtil.isBlank(password)?SECURITY_NONE:SECURITY_PSK);//getConfig(security, ssid, scan.BSSID, password);
				/*int wcgId=*/mWifiManager.addNetwork(configuration); 
				/*return mWifiManager.enableNetwork(wcgId, true); 
			}
		}
		return false;*/
	}
	public boolean enableNetwork(String ssid, String password, boolean reset) {
		if(isWifiApEnabled()) {
			setWifiApEnabled(null, false);
		}
		if(!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
		mWifiManager.startScan();  
		mWifiList=mWifiManager.getScanResults();
		boolean ssidScaned = false;
		for(ScanResult scan:mWifiList) {
			if(scan.SSID != null && (scan.SSID.equals("\""+ssid+"\"")  ||
            		scan.SSID.equals(ssid))) {
				ssidScaned = true;
				break;
			}
		}
		if(!ssidScaned) {
			return false;
		}
		mWifiConfigurations=mWifiManager.getConfiguredNetworks();
		if(mWifiConfigurations != null) {
			for(WifiConfiguration config:mWifiConfigurations) {
				if(config.SSID != null && (config.SSID.equals("\""+ssid+"\"")  ||
	            		config.SSID.equals(ssid))) {
					if(reset) {
				        mWifiManager.removeNetwork(config.networkId);    
				        int security = getSecurity(config);
						WifiConfiguration configuration = /*createWifiInfo(ssid, password, StringUtil.isBlank(password)?SECURITY_NONE:SECURITY_PSK);*/getConfig(security, ssid, config.BSSID, password);
						int wcgId=mWifiManager.addNetwork(configuration); 
						Log.i("roamtouch", "addNetwork ret="+wcgId);
						return mWifiManager.enableNetwork(wcgId, true);
					} else {
						return mWifiManager.enableNetwork(config.networkId, true);
					}
				}
			}
		}
		for(ScanResult scan:mWifiList) {
			if(scan.SSID != null && (scan.SSID.equals("\""+ssid+"\"")  ||
            		scan.SSID.equals(ssid))) {
				int security = getSecurity(scan);
				
				WifiConfiguration configuration = /*createWifiInfo(ssid, password, security);*/getConfig(security, ssid, scan.BSSID, password);
				int wcgId=mWifiManager.addNetwork(configuration); 
				return mWifiManager.enableNetwork(wcgId, true); 
			}
		}
		return false;
	}
}
