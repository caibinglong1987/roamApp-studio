package com.roamtech.telephony.roamapp.event;

/**
 * Created by user on 6/8/2016.
 */
public class EventLinphoneRequest extends EventBase{
    //显示底部工具栏
    public static final String SHOWTOOL="showTool";
    //隐藏底部工具栏
    public static final String HIDETOOL="hideTool";
    public static final String DELETE="delete";
    public static final String SELECTALL="selectAll";
    public EventLinphoneRequest(String message) {
        super(message);
    }
}
