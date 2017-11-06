package com.roamtech.telephony.roamapp.bean;

import com.roamtech.telephony.roamapp.helper.pinyin.ChineseHelper;
import com.roamtech.telephony.roamapp.helper.pinyin.PinyinFormat;
import com.roamtech.telephony.roamapp.helper.pinyin.PinyinHelper;
import com.roamtech.telephony.roamapp.util.StringUtil;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by user on
 * 6/23/2016.
 */
public class _SortModel implements Serializable {
    private static final long serialVersionUID = -720216627725238529L;

    private String displayName;
    //显示数据拼音的首字母
    private String sortLetters;
    //待用
    //简拼
    private String simpleSpell;
    //全拼
    private String wholeSpell;

    public String getSortLetter() {
        if (sortLetters == null) {
            sortLetters = "#";
            if (StringUtil.isTrimBlank(displayName)) {
                return sortLetters;
            }
            //汉字转换成拼音
            String pinyin = PinyinHelper.convertToPinyinString(displayName.replace(" ", ""), "", PinyinFormat.WITHOUT_TONE).trim();
            String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINESE);
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortLetters = sortString;
            }
        }
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getSimpleSpell() {
        if (simpleSpell == null) {
            simpleSpell = displayName;
            if (StringUtil.isTrimBlank(displayName)) {
                simpleSpell = StringUtil.EMPTY_STRING;
                return simpleSpell;
            } else if (!ChineseHelper.containsChinese(displayName)) {
                return simpleSpell;
            }
            //汉字转换成拼音
            simpleSpell = PinyinHelper.getShortPinyin(displayName);
        }
        return simpleSpell;
    }

    public String getWholeSpell() {
        if (wholeSpell == null) {
            wholeSpell = displayName;
            if (StringUtil.isTrimBlank(displayName)) {
                wholeSpell = StringUtil.EMPTY_STRING;
                return wholeSpell;
            } else if (!ChineseHelper.containsChinese(displayName)) {
                return wholeSpell;
            }
            //汉字转换成拼音
            wholeSpell = PinyinHelper.convertToPinyinString(displayName.replace(" ", ""), "", PinyinFormat.WITHOUT_TONE);
        }
        return wholeSpell;
    }

    /**
     * displayName是否匹配输入的字符串
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     * @return
     */
    public boolean filter(String filterStr) {
        String name = getDisplayName();
        if (name != null) {
            if (name.contains(filterStr)) {
                return true;
            } else {
                String lowerFilter = PinyinHelper.toChangeLowerCase(filterStr);
                String simple = getSimpleSpell();
                String whole = getWholeSpell();
                if (simple.contains(lowerFilter)) {
                    return true;
                } else if (whole.contains(lowerFilter)) {
                    return true;
                } else if (PinyinHelper.containCase(simple) && PinyinHelper.toChangeLowerCase(simple).contains(lowerFilter)) {
                    return true;
                } else if (PinyinHelper.containCase(whole) && PinyinHelper.toChangeLowerCase(whole).contains(lowerFilter)) {
                    return true;
                }
            }
        }
        return false;
    }
}
