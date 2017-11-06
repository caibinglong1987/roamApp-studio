package com.roamtech.telephony.roamapp.util;

import android.util.Log;

/**
 * Created by user on 6/30/2016.
 */
public class RunTime {
    private static long startTime;
    private static long endTime;

    public static  void start(){
        startTime=System.currentTimeMillis();

    }
    public static void end(String description){
        endTime=System.currentTimeMillis();
        Log.e("print",description+":"+(endTime-startTime));
    }
}
