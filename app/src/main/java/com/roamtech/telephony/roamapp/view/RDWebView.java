package com.roamtech.telephony.roamapp.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by user on 5/30/2016.
 */
public class RDWebView extends WebView{
    private static final String FLAG_MOBILE="mob=android";
    public RDWebView(Context context) {
        super(context);
    }

    public RDWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RDWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //添加移动端标签
    public void loadUrlWithMobFlag(String url){
        if(url.contains("?")&&url.contains("=")){
            if(url.contains(FLAG_MOBILE)){
                loadUrl(url);
            }else{
                loadUrl(url+"&"+FLAG_MOBILE);
            }
        }else{
            loadUrl(url+"?"+FLAG_MOBILE);
        }

    }
}
