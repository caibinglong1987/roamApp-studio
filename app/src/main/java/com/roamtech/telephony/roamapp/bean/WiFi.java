package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

/** 我的号码里绑定的电话列表对象
 * @author xincheng 
 *
 */
public class WiFi implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1002885528249250661L;
	private String ssid;
	private boolean password;
	private String phoneNumber;
	private boolean isSelected;
	public String getSsid() {
		return ssid;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public boolean isPassword() {
		return password;
	}
	public void setPassword(boolean password) {
		this.password = password;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	
	
}
