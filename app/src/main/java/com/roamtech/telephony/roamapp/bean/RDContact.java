package com.roamtech.telephony.roamapp.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;


import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.widget.sortlist.SortToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xincheng 2014年11月24日 下午5:36:29
 */
public class RDContact extends _SortModel implements Serializable {
    private static final long serialVersionUID = 3790717505065723499L;
    private long id = -1;
    //中英全[XIAO小MING明] 但是有可能有定制的rom的手机被篡改了
    private String sortKey;
    private long photoId = -1;
    private transient Bitmap headPhoto;

    //列表展示的号码 键盘搜索使用
    private RDContactPhone dialPhone;
    //一个联系人可能有很多电话号码
    private List<RDContactPhone> phoneList;
    private String searchKey = "";

    private SortToken sortToken;

    public SortToken getSortToken() {
        return sortToken;
    }

    public void setSortToken(SortToken sortToken) {
        this.sortToken = sortToken;
    }

    public RDContact() {
    }

    public RDContact(long id) {
        this.id = id;
    }

    //contain方法需要
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        RDContact contact = (RDContact) obj;
        if (contact.getId() == getId()) {
            return true;
        }
        return false;
    }

    public List<RDContactPhone> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<RDContactPhone> phoneList) {
        this.phoneList = phoneList;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPhotoId() {
        return photoId;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public void setPhotoId(long photoId) {
        this.photoId = photoId;
    }

    public void addPhone(RDContactPhone phone) {
        if (phoneList == null) {
            phoneList = new ArrayList<>();
        }
        phoneList.add(phone);
    }

    public Bitmap getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(Bitmap headPhoto) {
        this.headPhoto = headPhoto;
    }


    @Override
    public String toString() {
        return "contactId:" + id + " disPlayName:" + getDisplayName() + " simpleSpell:" + getSimpleSpell() + " wholeSpell:" + getWholeSpell() + " photoId:" + photoId + " sortKey:" + sortKey + " phoneList:" + phoneList;
    }

    public RDContactPhone getDialPhone() {
        return dialPhone;
    }

    public void setDialPhone(RDContactPhone dialPhone) {
        this.dialPhone = dialPhone;
    }

    /**
     * dialPhone是否匹配输入的字符串
     *
     * @param number
     * @return
     */
    public boolean matchDialPhone(String number) {
        if (number != null) {
            if (dialPhone != null) {
                String dialPhoneNumber = dialPhone.getNumber();
                if (!TextUtils.isEmpty(dialPhoneNumber)) {
                    return dialPhoneNumber.indexOf(number) != -1;
                }
            }
        }
        return false;
    }

    public boolean matchPhoneList(String number) {
        if (number != null) {
            if (phoneList != null && !phoneList.isEmpty()) {
                int size = phoneList.size();
                for (int i = 0; i < size; i++) {
                    RDContactPhone contactPhone = phoneList.get(i);
                    if (contactPhone != null) {
                        String dialPhoneNumber = contactPhone.getNumber();
                        if (!TextUtils.isEmpty(dialPhoneNumber)) {
                            if (dialPhoneNumber.indexOf(number) != -1) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public Bitmap getHeadPhoto(Context context, int defaultBitmapPosition) {
        if (headPhoto == null) {
            if (photoId > 0) {
                headPhoto = RDContactHelper.getContactHeadBitmap(context.getContentResolver(), photoId);
            } else {
                headPhoto = RDContactHelper.getCircleBitmapRandom(defaultBitmapPosition);
            }
        }
        return headPhoto;
    }

}
