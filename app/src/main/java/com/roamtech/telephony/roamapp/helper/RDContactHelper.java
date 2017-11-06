package com.roamtech.telephony.roamapp.helper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.bean.RDContactPhone;
import com.roamtech.telephony.roamapp.bean.UserHeadBean;
import com.roamtech.telephony.roamapp.bean._SortModel;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.util.VerificationUtil;
import com.roamtech.telephony.roamapp.view.RoundImageView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.provider.ContactsContract.Contacts;
import static android.provider.ContactsContract.Data;
import static android.provider.ContactsContract.Intents;

/**
 * Created by xincheng
 * on 6/21/2016.
 */
public class RDContactHelper {
    private static Bitmap HEAD_BITMAP[];
    private static RDContact roamTechContact;
    private static RDContact roamCustomerService;
    private static final long roamTechPhotoId = 9999998;
    private static final long roamCustomerPhotoId = 9999999;

    static {
        initSrcCircleBitmap(50);
    }

    private static void initSrcCircleBitmap(int dpRadius) {
        int srcColors[] = {Color.parseColor("#ADBFEE"), Color.parseColor("#EEDCAD"), Color.parseColor("#D2ADEE"), Color.parseColor("#ADEEB3")};
        HEAD_BITMAP = new Bitmap[srcColors.length];
        for (int index = 0; index < srcColors.length; index++) {
            HEAD_BITMAP[index] = getCircleBitmap(srcColors[index], dpRadius);
        }
    }

    private static Bitmap getCircleBitmap(int color, int radius) {
        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(color);
        canvas.drawCircle(radius, radius, radius, p);
        return bitmap;
    }

    // 查询的字段
    private static final String[] PROJECTION = {Phone.CONTACT_ID,
            Phone.DISPLAY_NAME, Phone.PHOTO_ID, Phone.SORT_KEY_PRIMARY, Phone._ID,
            Phone.NUMBER, Phone.TYPE,
            Phone.LOOKUP_KEY};
    /**
     * 保存一份拷贝在本地 搜索的时候本地需要
     */
    private static List<RDContact> mOriginSystemContacts;
    private static List<RDContact> mSystemContacts; //一个联系人 对多个号码 显示多个

    /**
     * 添加联系人
     *
     * @param activity activity
     */
    public static void addContact(Activity activity, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType("vnd.android.cursor.dir/person");
        intent.setType("vnd.android.cursor.dir/contact");
        intent.setType("vnd.android.cursor.dir/raw_contact");
//		// 添加姓名
//		intent.putExtra(ContactsContract.Intents.Insert.NAME, "王梨");
//		// 添加手机
        intent.putExtra(Intents.Insert.PHONE_TYPE, Phone.TYPE_MOBILE);
        intent.putExtra(Intents.Insert.PHONE, phoneNumber);
        activity.startActivity(intent);
        ///
        //activity.startActivityForResult(intent, 1000);
    }

    /**
     * 编辑联系人
     *
     * @param activity activity
     */
    public static void editContact(Activity activity, long contactId, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_EDIT, Uri.parse("content://com.android.contacts/contacts/" + contactId));
        intent.putExtra("finishActivityOnSaveCompleted", true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 返回空的列表 如果没有数据则返回空列表new ArrayList<>()
     *
     * @param contentResolver contentResolver
     * @return List<RDContact>
     */
    //10mm左右
    private static List<RDContact> querySystemContacts(ContentResolver contentResolver) {
        Map<Long, RDContact> contactIds = new HashMap<>();
        List<RDContact> contacts = new ArrayList<>();
        // 按照sort_key 和 id的升序查詢
        Cursor phoneCursor = contentResolver.query(
                Phone.CONTENT_URI,
                PROJECTION, null, null, Phone.SORT_KEY_PRIMARY + " COLLATE LOCALIZED asc, " + Phone._ID + " asc");//
        if (phoneCursor == null || phoneCursor.getCount() == 0) {
            return contacts;
        }
        //得到手机号码
        while (phoneCursor.moveToNext()) {
            long contactId = phoneCursor.getLong(0);
            RDContact contact = contactIds.get(contactId);
            if (contact == null) {
                contact = new RDContact(contactId);
                //遍历所有的联系人下面所有的电话号码
                contact.setDisplayName(phoneCursor.getString(1));
                contact.setPhotoId(phoneCursor.getLong(2));
                contact.setSortKey(phoneCursor.getString(3));
                contacts.add(contact);
                contactIds.put(contactId, contact);
            }
            long phoneId = phoneCursor.getLong(4);
            String phoneNumber = phoneCursor.getString(5);
            int phoneType = phoneCursor.getInt(6);
            contact.addPhone(new RDContactPhone(phoneId, phoneType, phoneNumber.replace(" ", "").replace("-", "").replace("+86", "")));
        }
        phoneCursor.close();
        contactIds.clear();
        return contacts;
    }

    /**
     * 根据 联系人的id获取联系人对象
     *
     * @param contentResolver contentResolver
     * @param contactId       contactId
     * @return List<RDContactPhone>
     */
    public static RDContact queryContactById(ContentResolver contentResolver, long contactId) {
        // 查询的字段
        String[] projection = {Data._ID,
                Data.DISPLAY_NAME, Data.SORT_KEY_PRIMARY, Data.PHOTO_ID};
        Cursor cursor = contentResolver.query(Contacts.CONTENT_URI, projection, Data._ID + "=?", new String[]{String.valueOf(contactId)}, null);
        RDContact contact = null;
        if (cursor != null && cursor.moveToNext()) {
            contact = new RDContact(cursor.getLong(0));
            contact.setDisplayName(cursor.getString(1));
            contact.setSortKey(cursor.getString(2));
            contact.setPhotoId(cursor.getLong(3));
            contact.setPhoneList(queryContactPhonesById(contentResolver, contactId));
        }
        if (cursor != null) {
            cursor.close();
        }
        return contact;
    }

    /**
     * 获取某个联系人的电话号码列表
     *
     * @param contentResolver contentResolver
     * @param contactId       contactId
     * @return List<RDContactPhone>
     */
    private static List<RDContactPhone> queryContactPhonesById(ContentResolver contentResolver, long contactId) {
        // 查询的字段
        String[] projection = {Phone._ID, Phone.NUMBER, Phone.TYPE,};
        Cursor cursor = contentResolver.query(Phone.CONTENT_URI, projection, Data.CONTACT_ID + "=?", new String[]{String.valueOf(contactId)}, null);
        List<RDContactPhone> phones = null;
        if (cursor != null && cursor.getCount() > 0) {
            phones = new ArrayList<>();
            //得到手机号码
            while (cursor.moveToNext()) {
                RDContactPhone phone = new RDContactPhone();
                phone.setId(cursor.getLong(0));
                phone.setNumber(cursor.getString(1));
                phone.setType(cursor.getInt(2));
                phones.add(phone);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return phones;
    }

    public static void loadContacts(ContentResolver contentResolver) {
        mOriginSystemContacts = querySystemContacts(contentResolver);
        if (!isEmptyList(mOriginSystemContacts)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setSortContact(mOriginSystemContacts);
                }
            }).start();
        }
    }

    private static void setSortContact(List<RDContact> contactLists) {
        mSystemContacts = new ArrayList<>();
        List<RDContactPhone> phones;
        RDContact newContact;
        for (RDContact item : contactLists) {
            phones = item.getPhoneList();
            if (phones != null) {
                for (RDContactPhone phone : phones) {
                    newContact = new RDContact();
                    newContact.setDialPhone(phone);
                    newContact.setId(item.getId());
                    newContact.setPhotoId(item.getPhotoId());
                    newContact.setHeadPhoto(item.getHeadPhoto());
                    newContact.setDisplayName(item.getDisplayName());
                    mSystemContacts.add(newContact);
                }
            }
        }
        sortContacts(mOriginSystemContacts);
    }

    /**
     * 获取通讯录列表 如果没有返回null;
     *
     * @return if no contacts return null;
     */
    public static List<RDContact> getSystemContacts() {
        if (!isEmptyList(mOriginSystemContacts)) {
            List<RDContact> contacts = new ArrayList<>();
            int size = mOriginSystemContacts.size();
            for (int i = 0; i < size; i++) {
                contacts.add(mOriginSystemContacts.get(i));
            }
            return contacts;
        }
        return new ArrayList<>();
    }

    private static void sortContacts(List<RDContact> contactLists) {
        Collections.sort(contactLists, new Comparator<_SortModel>() {
            @Override
            public int compare(_SortModel o1, _SortModel o2) {
                //排序规则 1、# 在后面 2、同
                //比较是基于字符串中的每个字符的Unicode值
                //正数往后排
                if (o1.getSortLetter() == null && o2.getSortLetter() == null) {
                    o1.setSortLetters("#");
                    o2.setSortLetters("#");
                }
                boolean o1NotLetter = o1.getSortLetter().equals("#");
                boolean o2NotLetter = o2.getSortLetter().equals("#");
                // 根据a-z进行排序源数据
                if (o1NotLetter && o2NotLetter) {
                    return o1.getWholeSpell().compareTo(o2.getWholeSpell());
                } else if (!o1NotLetter && o2NotLetter) {
                    return -1;
                } else if (o1NotLetter && !o2NotLetter) {
                    return 1;
                } else {
                    if (o1.getSortLetter().equals(o2.getSortLetter())) {
                        //核心代码 按简拼排序
                        return o1.getWholeSpell().compareTo(o2.getWholeSpell());
                    }
                    return o1.getSortLetter().compareTo(o2.getSortLetter());
                }
            }
        });

    }

    /**
     * 搜索的时候需要,不要对其进行 clean add等操作
     *
     * @return
     */
    public static List<RDContact> getOriginSystemContacts() {
        if (!isEmptyList(mOriginSystemContacts)) {
            List<RDContact> contacts = new ArrayList<>();
            int size = mOriginSystemContacts.size();
            for (int i = 0; i < size; i++) {
                contacts.add(mOriginSystemContacts.get(i));
            }
            return contacts;
        }
        return new ArrayList<>();
    }

    /**
     * 搜索的时候需要,不要对其进行 clean add等操作
     *
     * @return List<RDContact>
     */
    public static List<RDContact> getAllSystemContacts() {
        if (!isEmptyList(mSystemContacts)) {
            List<RDContact> contacts = new ArrayList<>();
            int size = mSystemContacts.size();
            for (int i = 0; i < size; i++) {
                contacts.add(mSystemContacts.get(i));
            }
            return contacts;
        }
        return new ArrayList<>();
    }

    public static Uri getContactPictureUri(long contactId) {
        Uri person = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        return Uri.withAppendedPath(person, Contacts.Photo.CONTENT_DIRECTORY);
    }

    public static Uri getContactPhotoUri(long contactId) {
        Uri person = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        return Uri.withAppendedPath(person, Contacts.Photo.DISPLAY_PHOTO);
    }

    public static Bitmap getContactHeadBitmap(ContentResolver contentResolver, long contactId) {
        if (contactId == roamTechPhotoId && roamTechContact != null) {
            return roamTechContact.getHeadPhoto();
        }
        if (contactId == roamCustomerPhotoId && roamCustomerService != null) {
            return roamCustomerService.getHeadPhoto();
        }
        Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        InputStream input = Contacts.openContactPhotoInputStream(contentResolver, uri);
        return BitmapFactory.decodeStream(input);
    }

    public static boolean isEmptyList(List<?> list) {
        if (list == null || list.size() == 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取单个 RDContact
     *
     * @param phone phone
     * @return RDContact
     */
    public static RDContact findContactByPhone(@NonNull String phone) {
        if (phone.equals("roamtech")) {
            return roamTechContact;
        }
        if (roamCustomerService != null && roamCustomerService.getDialPhone() != null && phone.equals(roamCustomerService.getDialPhone().getNumber())) {
            return roamCustomerService;
        }
        if (!isEmptyList(mSystemContacts)) {
            for (RDContact contact : mSystemContacts) {
                if (contact != null && contact.getDialPhone() != null && contact.getDialPhone().getNumber().equals(phone)) {
                    return contact;
                }
            }
        }
        return null;
    }

    public static void initRoamTechContact(Context context) {
        //Uri photo = Uri.parse("file:///android_asset/roaming_data.png");
        Resources res = context.getResources();
        Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.roaming_data);

        roamTechContact = new RDContact();
        roamTechContact.setDisplayName(context.getString(R.string.roam_tech));
        roamTechContact.setHeadPhoto(bm);
        roamTechContact.setPhotoId(roamTechPhotoId);
        roamTechContact.setId(roamTechPhotoId);

        roamCustomerService = new RDContact();
        roamCustomerService.setHeadPhoto(BitmapFactory.decodeResource(res, R.drawable.ic_service));
        RDContactPhone rdContactPhone = new RDContactPhone();
        rdContactPhone.setNumber(context.getString(R.string.roamPhone));
        roamCustomerService.setDialPhone(rdContactPhone);
        roamCustomerService.setDisplayName(context.getString(R.string.roamservice));
        roamCustomerService.setPhotoId(roamCustomerPhotoId);
        roamCustomerService.setId(roamCustomerPhotoId);
    }

    /**
     * 模糊查询
     *
     * @param str search str
     * @return List<RDContact>
     */
    public static List<RDContact> search(String str) {
        List<RDContact> filterList = new ArrayList<>();// 过滤后的list
        //if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
        if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
            String simpleStr = str.replaceAll("\\-|\\s", "");
            for (RDContact contact : mOriginSystemContacts) {
                List<RDContactPhone> phones = contact.getPhoneList();
                if (phones != null && phones.size() > 0) {
                    for (RDContactPhone item : phones) {
                        if (item.getNumber().contains(simpleStr) || contact.getDisplayName().contains(str)) {
                            filterList.add(contact);
                            break;
                        }
                    }
                }
            }
        } else {
            for (RDContact contact : mOriginSystemContacts) {
                if (contact.getDisplayName() != null) {
                    //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                    if (contact.getDisplayName().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                            || contact.getSortLetter().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                            || contact.getSortKey().toLowerCase(Locale.CHINESE).replace(" ", "").contains(str.toLowerCase(Locale.CHINESE))
                            || contact.getSortToken().simpleSpell.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                        // || contact.sortToken.wholeSpell.toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                            ) {
                        if (!filterList.contains(contact)) {
                            filterList.add(contact);
                        }
                    }
                }
            }
        }
        return filterList;
    }

    /**
     * 搜索全部号码
     *
     * @param str search str
     * @return List<RDContact>
     */
    public static List<RDContact> searchAllPhone(String str) {
        List<RDContact> filterList = new ArrayList<>();// 过滤后的list
        if (!isEmptyList(mSystemContacts)) {
            //if (str.matches("^([0-9]|[/+])*$")) {// 正则表达式 匹配号码
            if (str.matches("^([0-9]|[/+]).*")) {// 正则表达式 匹配以数字或者加号开头的字符串(包括了带空格及-分割的号码)
                String simpleStr = str.replaceAll("\\-|\\s", "");
                RDContactPhone phone;
                for (RDContact contact : mSystemContacts) {
                    phone = contact.getDialPhone();
                    if (phone != null) {
                        if (phone.getNumber().contains(simpleStr) || contact.getDisplayName().contains(str)) {
                            filterList.add(contact);
                        }
                    }
                }
            } else {
                for (RDContact contact : mSystemContacts) {
                    if (contact.getDisplayName() != null) {
                        //姓名全匹配,姓名首字母简拼匹配,姓名全字母匹配
                        if (contact.getDisplayName().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                                || contact.getSimpleSpell().toLowerCase(Locale.CHINESE).contains(str.toLowerCase(Locale.CHINESE))
                                ) {
                            if (!filterList.contains(contact)) {
                                filterList.add(contact);
                            }
                        }
                    }
                }
            }
        }

        return filterList;
    }

    /**
     * 获取 通话记录 通讯录 头像 显示 对象
     *
     * @param context     context
     * @param phoneNumber phoneNumber
     * @param position    position
     * @return UserHeadBean
     */
    public static UserHeadBean getUserHeadBean(Context context, String phoneNumber, int position) {
        UserHeadBean userHeadBean = new UserHeadBean();
        if (phoneNumber.equals(context.getString(R.string.str_unknown)) || phoneNumber.equals("-1") || phoneNumber.length() == 0) {
            userHeadBean.headPhoto = getCircleBitmapRandom(position);
            userHeadBean.headPhotoText = "未知";
            userHeadBean.displayName = context.getString(R.string.str_unknown_number);
            userHeadBean.headTextSize = 12;
            return userHeadBean;
        }
        RDContact contact = findContactByPhone(phoneNumber);
        if (contact == null) {
            userHeadBean.headPhotoText = phoneNumber;
            userHeadBean.headPhoto = getCircleBitmapRandom(position);
            userHeadBean.displayName = phoneNumber;
            userHeadBean.isContact = false;
        } else {
            if (!StringUtil.isBlank(contact.getDisplayName())) {
                userHeadBean.headPhotoText = contact.getDisplayName();
                userHeadBean.displayName = contact.getDisplayName();
            } else {
                userHeadBean.headPhotoText = phoneNumber;
                userHeadBean.displayName = phoneNumber;
            }
            userHeadBean.headPhoto = getHeadPhoto(context, contact, position);
            userHeadBean.photoId = contact.getPhotoId();
            userHeadBean.isContact = true;
            userHeadBean.contactId = contact.getId();
        }
        if (userHeadBean.photoId <= 0) {
            if (phoneNumber.equals("10086") || phoneNumber.equals("10010") || phoneNumber.equals("10000")) {
                switch (phoneNumber) {
                    case "10086":
                        userHeadBean.headPhotoText = context.getString(R.string.china_mobile_m);
                        break;
                    case "10010":
                        userHeadBean.headPhotoText = context.getString(R.string.china_unicom_u);
                        break;
                    case "10000":
                        userHeadBean.headPhotoText = context.getString(R.string.china_telecom_t);
                        break;
                }
                userHeadBean.headTextSize = 12;
            } else if (VerificationUtil.matchNumberOrLetter(userHeadBean.headPhotoText)) {
                if (userHeadBean.headPhotoText.length() > 1) {
                    userHeadBean.headPhotoText = userHeadBean.headPhotoText.charAt(0) + "" + userHeadBean.headPhotoText.charAt(1);
                    userHeadBean.headTextSize = 20;
                } else {
                    userHeadBean.headPhotoText = userHeadBean.headPhotoText.charAt(0) + "";
                    userHeadBean.headTextSize = 24;
                }
            } else {
                userHeadBean.headPhotoText = userHeadBean.headPhotoText.charAt(0) + "";
                userHeadBean.headTextSize = 24;
            }
        } else {
            userHeadBean.headPhotoText = "";
        }
        return userHeadBean;
    }

    /**
     * 设置头像显示的逻辑
     *
     * @param rvHeadPhoto view
     * @param contact     contact
     * @param position    position
     */
    public static void setHeadPhotoDisplay(RoundImageView rvHeadPhoto, RDContact contact, int position) {
        Bitmap photo;
        long photoId = -1;
        String displayName = "";
        if (contact == null) {
            photo = getCircleBitmapRandom(position);
        } else {
            photo = getHeadPhoto(rvHeadPhoto.getContext(), contact, position);
            photoId = contact.getPhotoId();
            displayName = contact.getDisplayName();
        }
        rvHeadPhoto.setImageBitmap(photo);
        boolean hasDisplayName = !TextUtils.isEmpty(displayName);
        if (photoId <= 0) {
            /**设置头像显示首字母***/
            if (VerificationUtil.matchNumberOrLetter(displayName)) {
                if (displayName.length() > 1) {
                    rvHeadPhoto.setText(displayName.charAt(0) + "" + displayName.charAt(1), 20);
                } else {
                    rvHeadPhoto.setText(displayName.charAt(0) + "", 24);
                }
            } else {
                if (hasDisplayName) {
                    rvHeadPhoto.setText(displayName.charAt(0) + "", 24);
                } else {
                    if (contact != null && contact.getDialPhone() != null && contact.getDialPhone().getNumber() != null) {
                        rvHeadPhoto.setText(contact.getDialPhone().getNumber().charAt(0) + "" + contact.getDialPhone().getNumber().charAt(1), 20);
                    } else {
                        rvHeadPhoto.setText("未知", 12);
                    }
                }
            }
        } else {
            //清除复用的view上photo的文字
            rvHeadPhoto.setText("", 12);
        }
    }

    public static Bitmap getHeadPhoto(Context context, RDContact contact, int defaultBitmapPosition) {
        Bitmap headPhoto;
        if (contact == null || contact.getHeadPhoto() == null) {
            if (contact != null && contact.getPhotoId() > 0) {
                headPhoto = getContactHeadBitmap(context.getContentResolver(), contact.getId());
            } else {
                headPhoto = getCircleBitmapRandom(defaultBitmapPosition);
            }
        } else {
            headPhoto = contact.getHeadPhoto();
        }
        return headPhoto;
    }


    public static Bitmap getCircleBitmapRandom(int position) {
        return HEAD_BITMAP[position % HEAD_BITMAP.length];
    }

}
