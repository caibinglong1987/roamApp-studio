package com.roamtech.telephony.roamapp.enums;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

public enum LoadingState {
    LOADING("正在加载中..."), SUCCESS("加载成功"), FAILED("加载失败"), NO_DATA("暂无电子券"), SESSION_TIME_OUT(
            "会话过期,请重新登陆!"), TIME_OUT("请求超时"), NETWORD_ERROR("网络异常"), SERVER_ERROR(
            "服务异常"), AUTH_FAILED_ERROR("验证出错"),NOAVAILABLE_VOICEPACKAGE("无可用语音套餐");// 请求要求身份验证401 403等拒绝访问
    private String text;

    private LoadingState(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * 刷新请求时成功时 根据是否获取到数据 来判断是否显示空白
     *
     * @param data
     * @return
     */
    public static LoadingState getStateByRefreshData(List<?> data) {
        if (data == null || data.size() == 0) {
            return NO_DATA;
        }
        return SUCCESS;
    }


    /**是否有数据
     * @param data
     * @return
     */
    public static boolean isEmptyData(List<?> data) {
        if (data == null || data.size() == 0) {
            return true;
        }
        return false;
    }
    public static LoadingState getErrorState(IOException e){
        if(e instanceof  UnknownHostException){
            return NETWORD_ERROR;
        }else if(e instanceof  SocketException){
            return SERVER_ERROR;
        }else if(e instanceof  SocketTimeoutException){
            return TIME_OUT;
        }else {
            return FAILED;
        }
    }
}