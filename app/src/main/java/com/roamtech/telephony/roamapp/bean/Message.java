package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

/** 我的号码里绑定的电话列表对象
 * @author xincheng 
 *
 */
public class Message implements Serializable{

	private static final long serialVersionUID = 8086384280650151021L;
	/**
	 * 
	 */
	private String user;
	private String phoneNumber;
	/**归属地**/
	private String lastInfo;
	private String callTime;
	private boolean isSelected;
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getCallTime() {
		return callTime;
	}
	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}
	public boolean isSelected() {
		return isSelected;
	}
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	public String getLastInfo() {
		return lastInfo;
	}
	public void setLastInfo(String lastInfo) {
		this.lastInfo = lastInfo;
	}
	
}
