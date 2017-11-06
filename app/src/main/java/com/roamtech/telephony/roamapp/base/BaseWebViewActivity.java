package com.roamtech.telephony.roamapp.base;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.ApnSettingActivity;
import com.roamtech.telephony.roamapp.activity.CallTransferActivity;
import com.roamtech.telephony.roamapp.activity.CaptureActivity;
import com.roamtech.telephony.roamapp.activity.GlobalNetCardActivity;
import com.roamtech.telephony.roamapp.activity.LoginActivity;
import com.roamtech.telephony.roamapp.bean.PayParamsRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.enums.LoadingState;
import com.roamtech.telephony.roamapp.event.EventPayResult;
import com.roamtech.telephony.roamapp.helper.NetworkHelper;
import com.roamtech.telephony.roamapp.jsbridge.InjectedChromeClient;
import com.roamtech.telephony.roamapp.jsbridge.JsCallback;
import com.roamtech.telephony.roamapp.jsbridge.JsInterface;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.view.EmptyView;
import com.roamtech.telephony.roamapp.view.RDWebView;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import static com.roamtech.telephony.roamapp.enums.LoadingState.FAILED;
import static com.roamtech.telephony.roamapp.enums.LoadingState.LOADING;
import static com.roamtech.telephony.roamapp.enums.LoadingState.NETWORD_ERROR;
import static com.roamtech.telephony.roamapp.enums.LoadingState.SUCCESS;

public abstract class BaseWebViewActivity extends BaseActivity {
    public static final int ALI_PAY = 4;
    public static final int WX_PAY = 5;
    public static final String APP_ID = "wx0bf1f764ee9bf082";
    protected RDWebView mWebView;
    protected TextView mTvTitle;
    protected EmptyView mEmptyView;
    private String mFailingUrl;
    private JsCallback callback;
    private String orderId;
    private IWXAPI api;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        api = WXAPIFactory.createWXAPI(BaseWebViewActivity.this, APP_ID);
        api.registerApp(APP_ID);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mEmptyView = (EmptyView) findViewById(R.id.id_empty_view);
        if (mEmptyView != null) {
            mEmptyView.setExcludeSuccessBefore(false);
            mEmptyView.bindViewAndLoadListener(mEmptyView,
                    LOADING, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            mWebView.reload();
                        }
                    });
        }
        mTvTitle = (TextView) findViewById(R.id.id_titletext);
        mWebView = (RDWebView) findViewById(R.id.id_webView);
        WebSettings settings = mWebView.getSettings();
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);  //设置 缓存模式
        settings.setJavaScriptEnabled(true);
        //DomStorage存储数据要打开
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        //设置适配
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        mWebView.setWebChromeClient(new CustomChromeClient("nativeInterface", this));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //设置默认用webView打开
                //有的手机会用外部浏览器打开
                // view.loadUrl(url);
                Log.e("print",url);
                // toWebViewActivity(RDMallActivity.class,url);
                view.loadUrl(url);
                return true;
            }

            /**此方法在加载失败的时候调用，
             * 成功的时候不掉用，但onPageFinished会一直调用
             */
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mFailingUrl = failingUrl;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals(mFailingUrl)) {
                    mFailingUrl = null;
                    if (!NetworkHelper.isNetworkConnected(BaseWebViewActivity.this)) {
                        setEmptyViewState(NETWORD_ERROR);
                    } else {
                        setEmptyViewState(FAILED);
                    }

                } else {
                    setEmptyViewState(SUCCESS);
                }

            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        mWebView.loadUrl(getBundle().getString("url"));
    }

    public class CustomChromeClient extends InjectedChromeClient {

        public CustomChromeClient(String injectedName, BaseWebViewActivity instance) {
            super(injectedName, instance);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mTvTitle != null) {
                mTvTitle.setText(title);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * js页面调用结束窗体的方法
     */
    @JsInterface
    public void goBack() {
        if (!mWebView.canGoBack()) {
            finish();
        }
    }

    @JsInterface
    public void goMall() {
        finish();
    }

    @JsInterface
    public void setApn() {
        toActivity(ApnSettingActivity.class, null);
    }

    @JsInterface
    public void goPayOrder(final String orderId, final Integer payId, final JsCallback callback) {
        this.callback = callback;
        this.orderId = orderId;
        String url = Constant.PAY_ORDER;
        JSONObject json = new JSONObject();
        try {
            json.put("userid", getLoginInfo().getUserId());
            json.put("sessionid", getLoginInfo().getSessionId());
            json.put("orderid", orderId);
            json.put("payid", payId);
            OkHttpUtil.postJsonRequest(url, json, hashCode(), new OKCallback<PayParamsRDO>(new TypeToken<UCResponse<PayParamsRDO>>() {
            }) {
                @Override
                public void onResponse(int statuscode, @Nullable UCResponse<PayParamsRDO> ucResponse) {
                    if (isSucccess()) {
                        final String orderInfo = ucResponse.getAttributes().getPayParams();
                        if (!StringUtil.isTrimBlank(orderInfo)) {
                            if (payId == ALI_PAY) {
                                new Thread() {
                                    @Override
                                    public void run() {
                                        PayTask alipay = new PayTask(BaseWebViewActivity.this);
                                        final Map<String, String> result = alipay.payV2(orderInfo, true);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (result.get("resultStatus").equals("9000")) {
                                                    payResult(orderId, true);
                                                } else {
                                                    payResult(orderId, false);
                                                }
                                            }
                                        });
                                    }
                                }.start();
                            } else if (payId == WX_PAY) {
                                try {
                                    JSONObject json = new JSONObject(orderInfo);
                                    PayReq req = new PayReq();
                                    req.appId = json.getString("appid");
                                    req.partnerId = json.getString("partnerid");
                                    req.prepayId = json.getString("prepayid");
                                    req.nonceStr = json.getString("noncestr");
                                    req.timeStamp = json.getString("timestamp");
                                    req.packageValue = json.getString("package");
                                    req.sign = json.getString("sign");
                                    api.sendReq(req);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    payResult(orderId, false);
                                }
                            }
                        } else {
                            payResult(orderId, false);
                        }

                    } else {
                        payResult(orderId, false);
                    }

                }

                @Override
                public void onFailure(IOException e) {
                    payResult(orderId, false);
                }
            });
        } catch (JSONException ex) {
            payResult(orderId, false);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void WXPayResp(EventPayResult payResult) {
        payResult(orderId, payResult.isPaySuccess());
    }

    private void payResult(String orderId, boolean payResult) {
        try {
            if (callback != null) {
                callback.apply(orderId, payResult);
            }
        } catch (JsCallback.JsCallbackException e) {
            e.printStackTrace();
        }
    }

    @JsInterface
    public void setCallTransfer() {
        toActivity(CallTransferActivity.class, null);
    }

    private static final int GET_GLOBALCARD = 9;
    private static final int LOGIN_SUCCUESS = 10;
    private static final int GO_SCAN_CODE = 11;

    @JsInterface
    public void goLogin() {
        toActivityClearTopWithState(LoginActivity.class, LOGIN_SUCCUESS, null);
    }

    @JsInterface
    public void goScanCode() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("manualInput", false);
        toActivityForResult(CaptureActivity.class, GO_SCAN_CODE, bundle);
    }

    @JsInterface
    public void getGlobalCard() {
        toActivityForResult(GlobalNetCardActivity.class, GET_GLOBALCARD, null);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_SUCCUESS && resultCode == RESULT_OK) {
            mWebView.loadUrl("javascript:userInfo('" + getLoginInfo().getUserId() + "','" + getLoginInfo().getSessionId() + "')");
        } else if (requestCode == GO_SCAN_CODE && resultCode == RESULT_OK) {
            mWebView.loadUrl("javascript:codeResult('" + (data != null ? data.getStringExtra("cardId") : "") + "')");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
    }

    private void setEmptyViewState(LoadingState state) {
        if (mEmptyView != null) {
            mEmptyView.setState(state);
        }
    }
}
