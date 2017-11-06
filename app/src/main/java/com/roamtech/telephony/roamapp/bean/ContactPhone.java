package com.roamtech.telephony.roamapp.bean;

public class ContactPhone {
	private String number;
	private int type;/*1 -- home, 2 -- mobile, 3 -- work, 4 -- fax*/
	public ContactPhone(String number, int type) {
		setNumber(number);
		setType(type);
	}

	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}	
