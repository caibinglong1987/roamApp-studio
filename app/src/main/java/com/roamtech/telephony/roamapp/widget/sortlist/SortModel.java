package com.roamtech.telephony.roamapp.widget.sortlist;

import org.linphone.Contact;

import android.graphics.Bitmap;
import android.net.Uri;


public class SortModel extends Contact {

	public SortModel(Contact contact) {
		super(contact.getID(),contact.name,contact.number,contact.sortKey,contact.getPhotoUri(),contact.getThumbnailUri(),contact.getPhoto());
	}
	public SortModel(String id, String name, String number, String sortKey) {
		super(id, name, number, sortKey);
	}
	public SortModel(String id, String name, String number, String sortKey, Uri photo, Uri thumbnail, Bitmap picture) {
		super(id, name, number, sortKey, photo, thumbnail, picture);
	}
	public String sortLetters; //显示数据拼音的首字母

	public SortToken sortToken=new SortToken();
}
