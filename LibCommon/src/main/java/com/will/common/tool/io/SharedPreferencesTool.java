package com.will.common.tool.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.DropBoxManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.security.KeyStore;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SharedPreferencesTool {
    public static final String STRINGTYPE = "String";
    public static final String INTTYPE = "Integer";
    public static final String LONGTYPE = "Long";
    public static final String BOOLEANTYPE = "Boolean";

    public static void clearPreferences(Context ctx, String profileName) {
        SharedPreferences properties = ctx.getSharedPreferences(profileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = properties.edit();
        editor.clear();
        editor.apply();
    }

    public static void putValue(Context ctx, String prefName, String key, Object value) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = properties.edit();
        if (value == null) {
            return;
        }
        String typeName = value.getClass().getSimpleName();
        if (INTTYPE.equals(typeName)) {
            editor.putInt(key, (Integer) value);
        } else if (BOOLEANTYPE.equals(typeName)) {
            editor.putBoolean(key, (Boolean) value);
        } else if (STRINGTYPE.equals(typeName)) {
            editor.putString(key, (String) value);
        } else if (LONGTYPE.equals(typeName)) {
            editor.putLong(key, (Long) value);
        } else {
            throw new RuntimeException();
        }
        editor.apply();
    }

    public static int getIntValue(Context ctx, String prefName, String key, int defVal) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getInt(key, defVal);
    }

    public static String getStringValue(Context ctx, String prefName, String key) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getString(key, null);
    }

    public static String getStringValue(Context ctx, String prefName, String key, String defValue) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getString(key, defValue);
    }

    public static boolean getBooleanValue(Context ctx, String prefName, String key) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getBoolean(key, true);
    }

    public static boolean getBooleanValue(Context ctx, String prefName, String key, boolean defVal) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getBoolean(key, defVal);
    }

    public static long getLongValue(Context ctx, String prefName, String key, long defVal) {
        SharedPreferences properties = ctx.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return properties.getLong(key, defVal);
    }

    /**
     * desc:保存对象
     *
     * @param context
     * @param key
     * @param obj     要保存的对象，只能保存实现了serializable的对象
     *                modified:
     */
    public static void saveObject(Context context, String prefName, String key, Object obj) {
        try {
            // 保存对象
            SharedPreferences.Editor sharedata = context.getSharedPreferences(prefName, 0).edit();
            //先将序列化结果写到byte缓存中，其实就分配一个内存空间
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(bos);
            //将对象序列化写入byte缓存
            os.writeObject(obj);
            //将序列化的数据转为16进制保存
            String bytesToHexString = bytesToHexString(bos.toByteArray());
            //保存该16进制数组
            sharedata.putString(key, bytesToHexString);
            sharedata.commit();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("", "保存obj失败");
        }
    }

    /**
     * desc:将数组转为16进制
     *
     * @param bArray
     * @return modified:
     */
    public static String bytesToHexString(byte[] bArray) {
        if (bArray == null) {
            return null;
        }
        if (bArray.length == 0) {
            return "";
        }
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }


    /**
     * desc:获取保存的Object对象
     *
     * @param context
     * @param key
     * @return modified:
     */
    public static Object readObject(Context context, String prefName, String key) {
        try {
            SharedPreferences sharedData = context.getSharedPreferences(prefName, 0);
            if (sharedData.contains(key)) {
                String string = sharedData.getString(key, "");
                if (TextUtils.isEmpty(string)) {
                    return null;
                } else {
                    //将16进制的数据转为数组，准备反序列化
                    byte[] stringToBytes = StringToBytes(string);
                    ByteArrayInputStream bis = new ByteArrayInputStream(stringToBytes);
                    ObjectInputStream is = new ObjectInputStream(bis);
                    //返回反序列化得到的对象
                    Object readObject = is.readObject();
                    return readObject;
                }
            }
        } catch (StreamCorruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //所有异常返回null
        return null;

    }

    /**
     * desc:将16进制的数据转为数组
     *
     * @param data
     * @return modified:
     */
    public static byte[] StringToBytes(String data) {
        String hexString = data.toUpperCase().trim();
        if (hexString.length() % 2 != 0) {
            return null;
        }
        byte[] retData = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i++) {
            int int_ch;  // 两位16进制数转化后的10进制数
            char hex_char1 = hexString.charAt(i); ////两位16进制数中的第一位(高位*16)
            int int_ch1;
            if (hex_char1 >= '0' && hex_char1 <= '9')
                int_ch1 = (hex_char1 - 48) * 16;   //// 0 的Ascll - 48
            else if (hex_char1 >= 'A' && hex_char1 <= 'F')
                int_ch1 = (hex_char1 - 55) * 16; //// A 的Ascll - 65
            else
                return null;
            i++;
            char hex_char2 = hexString.charAt(i); ///两位16进制数中的第二位(低位)
            int int_ch2;
            if (hex_char2 >= '0' && hex_char2 <= '9')
                int_ch2 = (hex_char2 - 48); //// 0 的Ascll - 48
            else if (hex_char2 >= 'A' && hex_char2 <= 'F')
                int_ch2 = hex_char2 - 55; //// A 的Ascll - 65
            else
                return null;
            int_ch = int_ch1 + int_ch2;
            retData[i / 2] = (byte) int_ch;//将转化后的数放入Byte里
        }
        return retData;
    }

}