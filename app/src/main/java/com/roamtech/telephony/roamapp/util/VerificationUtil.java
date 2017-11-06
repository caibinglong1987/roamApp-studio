package com.roamtech.telephony.roamapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: long
 * Date: 2016/7/14
 * Time: 14:17
 * 验证 工具类
 */
public class VerificationUtil {

    private final static Pattern emailPat = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private final static String lettersNumber = "^(?![^a-zA-Z]+$)(?!\\D+$).{6,22}$";
    private final static Pattern patternNumber = Pattern.compile("[0-9]*");
    private final static Pattern s = Pattern.compile("[a-zA-Z0-9]");
    private final static Pattern patten = Pattern.compile("[\u4e00-\u9fa5]");
    /**
     * 验证手机号
     *
     * @param str 字符串
     * @return bool
     */
    public static boolean isMobile(String str) {
        Pattern p = null;
        Matcher m = null;
        boolean b = false;
        p = Pattern.compile("^[1][3,4,5,8,7][0-9]{9}$");
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    public static boolean isChinese(String str){
        return patten.matcher(str).find();
    }

    public static boolean isNumber(String str) {
        return patternNumber.matcher(str).matches();
    }

    /**
     * 验证邮箱
     *
     * @param email 字符串
     * @return bool
     */
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailPat.matcher(email).matches();
    }

    /**
     * 验证 输入 是否为空
     *
     * @param input 输入字符
     * @return bool
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 验证码字符串 是否包含数字和字母
     *
     * @param password 密码
     * @return bool
     */
    public static boolean isContainLetterNumber(String password) {
        return Pattern.compile(lettersNumber).matcher(password).find();
    }

    /**
     * 判断IP 有效
     *
     * @param text 输入的字符
     * @return true 有效
     */
    public static boolean matches(String text) {
        if (text != null && !text.isEmpty()) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断 IP 地址是否与正则表达式匹配
            if (text.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        // 返回判断信息
        return false;
    }

    /**
     * 从字符串中截取连续6位数字
     * 用于从短信中获取动态密码
     *
     * @param str 短信内容
     * @return 截取得到的6位验证码
     */
    public static String getDynamicPassword(String str) {
        Pattern continuousNumberPattern = Pattern.compile("[0-9\\.]+");
        Matcher m = continuousNumberPattern.matcher(str);
        String dynamicPassword = "";
        while (m.find()) {
            if (m.group().length() == 6) {
                dynamicPassword = m.group();
            }
        }
        return dynamicPassword;
    }

    public static boolean matchNumberOrLetter(String str) {
        if (str == null || str.length() == 0)
            return false;
        String start = str.substring(0, 1);
        if (start.matches("[a-zA-Z]") || start.matches("[0-9]")) {
            return true;
        }
        return false;
    }
}
