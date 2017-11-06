package com.roamtech.telephony.roamapp.bean;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

/**
 * 我的号码里绑定的电话列表对象
 *
 * @author xincheng
 */
public class SimpleMessage implements Serializable {

    private static final long serialVersionUID = 8086384280650151021L;
    private String contact;
    private boolean isSelect;
    //是否为草稿
    private boolean isDraft;
    public long time = 0;
    public String message = "";
    public String from = "";
    public String to = "";
    public int number = 0;
    public int msgType = 0;
    public long id = 0;
    public String searchKey = null;
    public String callId;
    public SimpleMessage(boolean isSelect, boolean isDraft, String contact) {
        this.isSelect = isSelect;
        this.isDraft = isDraft;
        this.contact = contact;
    }

    public SimpleMessage(boolean isSelect, boolean isDraft, String contact, String message,
                         long time, String from, String to) {
        this.isSelect = isSelect;
        this.isDraft = isDraft;
        this.contact = contact;
        this.message = message;
        this.time = time;
        this.from = from;
        this.to = to;
    }



    public void setDbLogId(long id) {
        this.id = id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
        //发送短信告知有短信呗选中了
        EventBus.getDefault().post(select);
    }

    public boolean isDraft() {
        return isDraft;
    }

    public void setDraft(boolean draft) {
        isDraft = draft;
    }
}
