package com.roamtech.telephony.roamapp.helper.numberAttr;

import android.content.Context;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.bean.RDContactPhone;
import com.roamtech.telephony.roamapp.db.PhoneAddressDatabaseHelper;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by caibinglong
 * on 2017/2/14.
 */

public class NumberHelper {
    //用于匹配手机号码
    private final static String REGEX_MOBILE_PHONE = "^0?1[3458]\\d{9}$";
    //用于匹配固定电话号码
    private final static String REGEX_FIXED_PHONE = "^(010|02\\d|0[3-9]\\d{2})?\\d{6,8}$";
    //用于获取固定电话中的区号
    private final static String REGEX_ZIP_CODE = "^(010|02\\d|0[3-9]\\d{2})\\d{6,8}$";

    private static Pattern PATTERN_MOBILE_PHONE;
    private static Pattern PATTERN_FIXED_PHONE;
    private static Pattern PATTERN_ZIP_CODE;

    static {
        PATTERN_FIXED_PHONE = Pattern.compile(REGEX_FIXED_PHONE);
        PATTERN_MOBILE_PHONE = Pattern.compile(REGEX_MOBILE_PHONE);
        PATTERN_ZIP_CODE = Pattern.compile(REGEX_ZIP_CODE);
    }

    public static String getAddressByNumber(Context context, String number) {
        String address = "";
        RDContact contact = RDContactHelper.findContactByPhone(number);
        if (contact != null) {
            if (contact.getDialPhone() != null) {
                RDContactPhone contactPhone = contact.getDialPhone();
                switch (contactPhone.getType()) {
                    case 1:
                        address = context.getString(R.string.house);
                        break;
                    case 2:
                        address = context.getString(R.string.phone);
                        break;
                    case 3:
                        address = context.getString(R.string.work);
                        break;
                    case 4:
                        address = context.getString(R.string.fax);
                        break;
                    default:
                        address = context.getString(R.string.other);
                        break;
                }
                return address;
            }
        }
        if (number.equals("10086")) {
            return context.getString(R.string.china_mobile);
        } else if (number.equals("10010")) {
            return context.getString(R.string.china_unicom);
        } else if (number.equals("10000")) {
            return context.getString(R.string.china_telecom);
        }
        Number num = checkNumber(number);
        if (num.getCode() != null) {
            PhoneAddressDatabaseHelper databaseHelper = new PhoneAddressDatabaseHelper(context);
            return databaseHelper.queryData(num.getCode());
        }
        return context.getString(R.string.unknown_incoming_call_name);
    }

    public static String getDisplayNameByNumber(Context context, String number) {
        String address = "";
        RDContact contact = RDContactHelper.findContactByPhone(number);
        if (contact != null) {
            if (contact.getDisplayName() != null) {
                return contact.getDisplayName();
            } else {
                return address;
            }
        }
        if (number.equals("10086")) {
            return context.getString(R.string.china_mobile);
        } else if (number.equals("10010")) {
            return context.getString(R.string.china_unicom);
        } else if (number.equals("10000")) {
            return context.getString(R.string.china_telecom);
        }
        Number num = checkNumber(number);
        if (num.getCode() != null) {
            PhoneAddressDatabaseHelper databaseHelper = new PhoneAddressDatabaseHelper(context);
            return databaseHelper.queryData(num.getCode());
        }
        return context.getString(R.string.unknown_incoming_call_name);
    }


    public static enum PhoneType {
        /**
         * 手机
         */
        CELLPHONE,
        /**
         * 固定电话
         */
        FIXEDPHONE,
        /**
         * 非法格式号码
         */
        INVALIDPHONE
    }

    /**
     * 检查号码类型，并获取号码前缀，手机获取前7位，固话获取区号
     *
     * @param paraNumber
     * @return
     */
    public static Number checkNumber(String paraNumber) {
        String number = paraNumber;
        Number rtNum = null;

        if (number != null && number.length() > 0) {
            if (isCellPhone(number)) {
                //如果手机号码以0开始，则去掉0
                if (number.charAt(0) == '0') {
                    number = number.substring(1);
                }
                rtNum = new Number(PhoneType.CELLPHONE, number.substring(0, 7), paraNumber);
            } else if (isFixedPhone(number)) {
                //获取区号
                String zipCode = getZipFromHomePhone(number);
                rtNum = new Number(PhoneType.FIXEDPHONE, zipCode, paraNumber);
            } else {
                rtNum = new Number(PhoneType.INVALIDPHONE, null, paraNumber);
            }
        }
        return rtNum;
    }

    public static class Number {
        private PhoneType type;
        /**
         * 如果是手机号码，则该字段存储的是手机号码 前七位；如果是固定电话，则该字段存储的是区号
         */
        private String code;
        private String number;

        public Number(PhoneType _type, String _code, String _number) {
            this.type = _type;
            this.code = _code;
            this.number = _number;
        }

        public PhoneType getType() {
            return type;
        }

        public String getCode() {
            return code;
        }

        public String getNumber() {
            return number;
        }

        public String toString() {
            return String.format("[number:%s, type:%s, code:%s]", number, type.name(), code);
        }
    }

    /**
     * 判断是否为手机号码
     *
     * @param number 手机号码
     * @return
     */
    public static boolean isCellPhone(String number) {
        Matcher match = PATTERN_MOBILE_PHONE.matcher(number);
        return match.matches();
    }

    /**
     * 判断是否为固定电话号码
     *
     * @param number 固定电话号码
     * @return
     */
    public static boolean isFixedPhone(String number) {
        Matcher match = PATTERN_FIXED_PHONE.matcher(number);
        return match.matches();
    }


    /**
     * 获取固定号码号码中的区号
     *
     * @param strNumber
     * @return
     */
    public static String getZipFromHomePhone(String strNumber) {
        Matcher matcher = PATTERN_ZIP_CODE.matcher(strNumber);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
