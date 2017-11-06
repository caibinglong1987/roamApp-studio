package com.roamtech.telephony.roamapp.util;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;

import com.roamtech.telephony.roamapp.bean.RoamCall;

import org.linphone.LinphoneManager;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneProxyConfig;

public class CallUtil {

    public static final String SCHEME_TEL = "tel";
    public static final String SCHEME_SMSTO = "smsto";
    public static final String SCHEME_MAILTO = "mailto";
    public static final String SCHEME_IMTO = "imto";
    public static final String SCHEME_SIP = "sip";
    private static final String SCHEME_FROM = "from=";
    private static final String SCHEME_TO = "to=";
    private static final String SCHEME_USER_ID = "userid=";
    private static final String SCHEME_DOMAIN = "@120.55.192.228";

    public static final ComponentName CALL_INTENT_DESTINATION = new ComponentName(
            "com.android.phone", "com.android.phone.PrivilegedOutgoingCallBroadcaster");

    /**
     * Copied from PhoneApp. See comments in Phone app for more detail.
     */
    public static final String EXTRA_CALL_ORIGIN = "com.android.phone.CALL_ORIGIN";

    /**
     * Return an Intent for making a phone call. Scheme (e.g. tel, sip) will be determined
     * automatically.
     */
    public static Intent getCallIntent(String number) {
        return getCallIntent(number, null);
    }

    /**
     * Return an Intent for making a phone call. A given Uri will be used as is (without any
     * sanity check).
     */
    public static Intent getCallIntent(Uri uri) {
        return getCallIntent(uri, null);
    }

    /**
     * A variant of {@link #getCallIntent(String)} but also accept a call origin. For more
     * information about call origin, see comments in Phone package (PhoneApp).
     */
    public static Intent getCallIntent(String number, String callOrigin) {
        //return getCallIntent(getCallUri(number), callOrigin);
        return getCallIntent(number, callOrigin, Constants.DIAL_NUMBER_INTENT_NORMAL);
    }

    public static Intent getCallIntent(String number, String callOrigin, int type) {
        return getCallIntent(getCallUri(number), callOrigin, type);
    }

    /**
     * A variant of {@link #getCallIntent(Uri)} but also accept a call origin. For more
     * information about call origin, see comments in Phone package (PhoneApp).
     */
    public static Intent getCallIntent(Uri uri, String callOrigin) {
        /*final Intent intent = new Intent(Intent.ACTION_CALL_PRIVILEGED, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (callOrigin != null) {
            intent.putExtra(DialtactsActivity.EXTRA_CALL_ORIGIN, callOrigin);
        }
        return intent;*/
        return getCallIntent(uri, callOrigin, Constants.DIAL_NUMBER_INTENT_NORMAL);
    }

    public static Intent getCallIntent(Uri uri, String callOrigin, int type) {
        final Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (callOrigin != null) {
            intent.putExtra(EXTRA_CALL_ORIGIN, callOrigin);
        }
        if ((type & Constants.DIAL_NUMBER_INTENT_IP) != 0) {
            intent.putExtra(Constants.EXTRA_IS_IP_DIAL, true);
        }

        if ((type & Constants.DIAL_NUMBER_INTENT_VIDEO) != 0) {
            intent.putExtra(Constants.EXTRA_IS_VIDEO_CALL, true);
        }
        return intent;
    }

    /**
     * Return Uri with an appropriate scheme, accepting Voicemail, SIP, and usual phone call
     * numbers.
     */
    public static Uri getCallUri(String number) {
        /*if (PhoneNumberUtils.isUriNumber(number)) {
             return Uri.fromParts(SCHEME_SIP, number, null);
        }*/
        return Uri.fromParts(SCHEME_TEL, number, null);
    }

    public static String[] getRealNumber(String sip) {
        sip = sip.substring(sip.indexOf("<"), sip.indexOf(">") + 1);
        String newSip = sip.replace("<sip:T", "").replace(">", "").replace("<sip:", "").replace("+86", "");
        String[] str = newSip.split(";|@|=");
        return str;
    }

    /**
     * 获取 from 或者 caller
     *
     * @param sipFrom sip from
     * @return str
     */
    public static String getFromNumber(String sipFrom) {
        if (sipFrom == null || sipFrom.isEmpty() || sipFrom.equals("unknown")) {
            return "-1";
        }
        sipFrom = sipFrom.replace("<", "").replace("sip:T", "").replace(">", "").replace("sip:", "").replace("+86", "").replace(" ", "");
        String[] str = sipFrom.split(";|@|=");

        if (str.length > 0) {
            String returnStr = str[0];
            if (returnStr.contains("\"")) {
                return returnStr.split("\"")[1];
            }
            return returnStr;
        }
        return "-1";
    }

    public static String getRealToNumber(String sipto) {
        return getRealToNumber(null, sipto, "to");
    }

    public static String getSipUriParam(String sipuri, String param) {
        sipuri = sipuri.replace("+86", "").replace(" ", "");
        int index = sipuri.indexOf(param + "=");
        if (index > 0) {
            int skiplen = index + param.length() + 1;
            String tonumber = sipuri.indexOf(">") > 0 ? sipuri.substring(skiplen, sipuri.indexOf(">")) : sipuri.substring(skiplen);
            if (tonumber.indexOf(";") > 0) {
                return tonumber.split(";")[0];
            }
            return tonumber;
        }
        return null;
    }

    public static String getRealToNumber(String sipfrom, String sipto, String param) {
        String tonumber = getSipUriParam(sipto, param);
        if (tonumber != null) {
            return tonumber;
        } else {
            try {
                String sipuri = sipfrom != null ? sipfrom : sipto;
                if (sipuri.startsWith("sip:")) {
                    String toNumber = LinphoneCoreFactory.instance().createLinphoneAddress(sipuri).getUserName();
                    if (toNumber.startsWith("T")) {
                        return toNumber.substring(1);
                    }
                    return toNumber;
                } else {
                    return sipuri;
                }
            } catch (LinphoneCoreException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

            }
        }
        return "10010";
    }

    public static String getSipTo() {
        try {
            if (LinphoneManager.getLc().getDefaultProxyConfig() != null) {
                String number = LinphoneManager.getLc().getDefaultProxyConfig().getIdentity();//.getPrimaryContactUsername();
                return getSipTo(number);
            }
        } catch (Exception ex) {
            return null;
        }
        return null;
    }

    public static String getSipTo(String sipUri) {
        try {
            LinphoneAddress address = LinphoneCoreFactory.instance().createLinphoneAddress(sipUri);
            if (address == null || address.getUserName() == null) {
                return null;
            }
            if (address.getUserName().startsWith("A")) {
                sipUri = address.getUserName().substring(1);//remove 'T' prefix
            } else {
                sipUri = address.getUserName();
            }
        } catch (LinphoneCoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LinphoneProxyConfig proxyConfig = LinphoneManager.getLc().getDefaultProxyConfig();
        if (proxyConfig == null) {
            return null;
        }
        return "sip:T" + sipUri + "@" + proxyConfig.getDomain();
    }

    public static String getCallerNumber(String caller, String callee, String loginUserId) {
        //callee--> <sip:18758589130@120.55.192.228;from=17682303503;userid=490>
        // caller-->" 17682303503" <sip:T18758589130@120.55.192.228>

        //callee--> <sip:Tautodispatch@120.55.192.228;to=15958112371;userid=217>
        //caller--->sip:18767120943@120.55.192.228

        //callee-->"蔡" <sip:T18758589130@120.55.192.228;to=18767120943;userid=490>
        //caller--->sip:15958112371@120.55.192.228
        RoamCall roamCall = new RoamCall();
        callee = "<sip:18758589130@120.55.192.228;from=17682303503;userid=490>";
        String newCallee = callee.replace("<", "").replace(">", "").replace("T", "").replace("sip:", "");
        String regex = ";|\\s|@";
        String[] strArray = newCallee.split(regex);

        String callerPhone;
        String calleePhone;
        String phone;
        for (int m = 0; m < strArray.length; m++) {
            if (strArray[m].indexOf(SCHEME_USER_ID) > 0) {
                roamCall.userId = strArray[m].replace(SCHEME_USER_ID, "");

            }
            if (strArray[m].indexOf(SCHEME_FROM) > 0) {
                roamCall.caller = strArray[m].replace(SCHEME_FROM, "");
                calleePhone = "";
            }

            if (strArray[m].indexOf(SCHEME_TO) > 0) {
                calleePhone = strArray[m].replace(SCHEME_TO, "");
            }
        }
        return null;
    }


}
