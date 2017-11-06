package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

import android.graphics.Bitmap;
import android.net.Uri;

public class UserProfile implements Serializable {
	private static final long serialVersionUID = 1L;	
	private String name;
	private String gender;
	private String address;
	private transient Uri photoUri;
	private transient Uri thumbnailUri;
	private transient Bitmap photo;	
	public Uri getPhotoUri() {
		return photoUri;
	}
	public void setPhotoUri(Uri photoUri) {
		this.photoUri = photoUri;
	}
	public Uri getThumbnailUri() {
		return thumbnailUri;
	}
	public void setThumbnailUri(Uri thumbnailUri) {
		this.thumbnailUri = thumbnailUri;
	}
	public Bitmap getPhoto() {
		return photo;
	}
	public void setPhoto(Bitmap photo) {
		this.photo = photo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
