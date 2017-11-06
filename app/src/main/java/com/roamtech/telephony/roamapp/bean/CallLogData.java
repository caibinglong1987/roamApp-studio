package com.roamtech.telephony.roamapp.bean;

import android.graphics.Bitmap;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by caibinglong
 * on 2017/2/13.
 */

public class CallLogData implements Serializable {
    private int id;
    private String toNumber;
    private Bitmap callStatusBitmap;
    private Bitmap headPhotoBitmap;
    private String user;
    private String callTime;
    private String attribution;
    private boolean isSelect;
    private boolean hasHeadPhoto;
    private boolean hasUser;
    private String callId;
    private int direction;
    private String caller;
    private String callee;
    private int callStatus;
    private String from;
    private String area;
    private String headPhotoText;

    public CallLogData() {
        super();
    }

    public String getHeadPhotoText() {
        return headPhotoText;
    }

    public void setHeadPhotoText(String headPhotoText) {
        this.headPhotoText = headPhotoText;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(int callStatus) {
        this.callStatus = callStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToNumber() {
        return toNumber;
    }

    public void setToNumber(String toNumber) {
        this.toNumber = toNumber;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public Bitmap getCallStatusBitmap() {
        return callStatusBitmap;
    }

    public void setCallStatusBitmap(Bitmap callStatusBitmap) {
        this.callStatusBitmap = callStatusBitmap;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Bitmap getHeadPhotoBitmap() {
        return headPhotoBitmap;
    }

    public void setHeadPhotoBitmap(Bitmap headPhotoBitmap) {
        this.headPhotoBitmap = headPhotoBitmap;
    }

    public String getAttribution() {
        return attribution;
    }

    public void setAttribution(String attribution) {
        this.attribution = attribution;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public boolean isHasHeadPhoto() {
        return hasHeadPhoto;
    }

    public void setHasHeadPhoto(boolean hasHeadPhoto) {
        this.hasHeadPhoto = hasHeadPhoto;
    }

    public boolean isHasUser() {
        return hasUser;
    }

    public void setHasUser(boolean hasUser) {
        this.hasUser = hasUser;
    }

}
