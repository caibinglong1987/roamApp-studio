package com.roamtech.telephony.roamapp.bean;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by caibinglong
 * on 2017/2/14.
 * 用户头像 字体大小 头像中间文字 用户昵称（名字或手机号码）
 */

public class UserHeadBean implements Serializable {
    public Bitmap headPhoto;
    public String headPhotoText;
    public int headTextSize;
    public long photoId;
    public String displayName; //显示名字或者手机号码
    public boolean isContact;
    public long contactId;
}
