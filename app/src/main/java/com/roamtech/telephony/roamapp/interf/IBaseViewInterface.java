package com.roamtech.telephony.roamapp.interf;

import android.os.Bundle;

/**
 * 
 * @author xincheng
 * 
 */
public interface IBaseViewInterface {
	/**
	 * 获取布局Layout的id
	 * */
	int getLayoutId();
	/**
	 * 初始化传递过来的Bundle数据
	 */
	void initData();

	/** 初始化View控件
	 * @param savedInstanceState 销毁保存参数
	 */
	void initView(Bundle savedInstanceState);
	/**
	 * 设置监听
	 */
	void setListener();
}
