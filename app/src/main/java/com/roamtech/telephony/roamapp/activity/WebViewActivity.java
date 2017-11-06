package com.roamtech.telephony.roamapp.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.MultiMap;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.util.UrlEncoded;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class WebViewActivity extends BaseActivity {
    protected WebView mWebView;
    protected TextView mTvTitle;
    private String mTitle;

    @Override
    public int getLayoutId() {
        return R.layout.activity_webview;
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        mTvTitle = (TextView) findViewById(R.id.id_titletext);
        mWebView = (WebView) findViewById(R.id.id_webView);
        WebSettings settings = mWebView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
        settings.setJavaScriptEnabled(true);
        //DomStorage存储数据要打开
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        //设置适配
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        mWebView.setWebChromeClient(new CustomChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //设置默认用webView打开
                //有的手机会用外部浏览器打开
                // view.loadUrl(url);
                //  Log.e("print",url);
                // toWebViewActivity(RDMallActivity.class,url);
                if (parseScheme(url)) {
                    Intent intent = null;
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setComponent(null);
                        startActivity(intent);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else {
                    view.loadUrl(url);
                }
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains(Constant.CRED_TRIP_CALLBACK_URL)) {
                    try {
                        URL uri = new URL(url);
                        MultiMap<String> values = new MultiMap<>();
                        if (uri.getQuery() != null) {
                            UrlEncoded.decodeTo(uri.getQuery(), values, "UTF-8", 1000);
                            String accountNo = values.getString("accountNo");
                            if (!StringUtil.isBlank(accountNo)) {
                                credtripOpenAccountDialog("赠送免费通话时长500分钟！");
                            } else {
                                finish();
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                if (url.contains(Constant.CRED_TRIP_PAY_CALLBACK_URL)) {
                    //信程支付成功
                    RoamApplication.credTripStatus = 4;
                    finish();
                }
            }
        });
        mTitle = getBundle().getString("title");
        if (mTitle != null) {
            mTvTitle.setText(mTitle);
        }
        String html = getBundle().getString("html");
        if (StringUtil.isBlank(html)) {
            mWebView.loadUrl(getBundle().getString("url"));
        } else {
            mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
        }
    }

    public class CustomChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mTvTitle != null && mTitle == null) {
                mTvTitle.setText(title);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            if (RoamApplication.credTripStatus == 1) { //待支付 返回 设置 取消支付
                RoamApplication.credTripStatus = 2;
            }
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    private void credtripOpenAccountDialog(String message) {
//        new AlertDialog.Builder(this).setCancelable(false).setTitle("信程账户开通成功").setMessage(message).setPositiveButton("知道了", credtripOpenListener).show();
        new TipDialog(this, "信程账户开通成功", message)
                .setRightButton("知道了", new TipDialog.OnClickListener() {
                    @Override
                    public void onClick(int which) {
                        finish();
                    }
                })
                .show();
    }
//    private DialogInterface.OnClickListener credtripOpenListener = new DialogInterface.OnClickListener() {
//        @Override
//        public void onClick(DialogInterface dialog, int which) {
//            if (which == DialogInterface.BUTTON_POSITIVE) {
//                finish();
//            }
//        }
//    };

    private boolean parseScheme(String url) {
        if (url.startsWith("alipays://") || url.startsWith("weixin://")) {
            return true;
        } else {
            return false;
        }
    }
}
