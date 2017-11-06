package com.roamtech.telephony.roamapp.bean;

import java.io.Serializable;

/** 我的号码里绑定的电话列表对象
 * @author xincheng 
 *
 */
public class CallLog implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 0代表未接
	 * 1 代表已结
	 * 2 代表播出
	 * 
	 **/
	private int  state;
	private String callTime;
	private boolean isSaveVoice;
	
	
	public CallLog(int state, String callTime, boolean isSaveVoice) {
		super();
		this.state = state;
		this.callTime = callTime;
		this.isSaveVoice = isSaveVoice;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getCallTime() {
		return callTime;
	}
	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}
	public boolean isSaveVoice() {
		return isSaveVoice;
	}
	public void setSaveVoice(boolean isSaveVoice) {
		this.isSaveVoice = isSaveVoice;
	}
}
