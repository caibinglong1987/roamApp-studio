package com.roamtech.telephony.roamapp.bean;


/**
 * Created by long
 * on 15-10-21.
 */
public class CommonParseModel<T> extends CommonModel {
    public T data;

    @Override
    public String toString() {
        return "CommonParseModel{" +
                "data=" + data +
                '}';
    }
}
