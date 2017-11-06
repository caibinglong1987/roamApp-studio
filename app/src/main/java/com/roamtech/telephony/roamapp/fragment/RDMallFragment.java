package com.roamtech.telephony.roamapp.fragment;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.Receiver.WXPayResultReceiver;
import com.roamtech.telephony.roamapp.activity.ApnSettingActivity;
import com.roamtech.telephony.roamapp.activity.CallTransferActivity;
import com.roamtech.telephony.roamapp.activity.CaptureActivity;
import com.roamtech.telephony.roamapp.activity.CouponActivity;
import com.roamtech.telephony.roamapp.activity.LoginActivity;
import com.roamtech.telephony.roamapp.activity.MainNewActivity;
import com.roamtech.telephony.roamapp.activity.OrderListNewActivity;
import com.roamtech.telephony.roamapp.activity.WebViewActivity;
import com.roamtech.telephony.roamapp.activity.function.FunOrder;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.BaseFragment;
import com.roamtech.telephony.roamapp.bean.CommonModel;
import com.roamtech.telephony.roamapp.bean.LoginInfo;
import com.roamtech.telephony.roamapp.bean.OrderBean;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.enums.LoadingState;
import com.roamtech.telephony.roamapp.event.EventScanResult;
import com.roamtech.telephony.roamapp.event.EventUpPay;
import com.roamtech.telephony.roamapp.event.EventWebLoad;
import com.roamtech.telephony.roamapp.handler.CommonDoHandler;
import com.roamtech.telephony.roamapp.handler.CommonHandler;
import com.roamtech.telephony.roamapp.helper.NetworkHelper;
import com.roamtech.telephony.roamapp.jsbridge.InjectedChromeClient;
import com.roamtech.telephony.roamapp.jsbridge.JsCallback;
import com.roamtech.telephony.roamapp.jsbridge.JsInterface;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.UriHelper;
import com.roamtech.telephony.roamapp.view.EmptyView;
import com.roamtech.telephony.roamapp.view.RDWebView;
import com.roamtech.telephony.roamapp.view.ThirdPayDialog;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.roamtech.telephony.roamapp.enums.LoadingState.FAILED;
import static com.roamtech.telephony.roamapp.enums.LoadingState.LOADING;
import static com.roamtech.telephony.roamapp.enums.LoadingState.NETWORD_ERROR;
import static com.roamtech.telephony.roamapp.enums.LoadingState.SUCCESS;
import static com.roamtech.telephony.roamapp.web.HttpFunction.isSuc;

//商城界面
public class RDMallFragment extends BaseFragment implements CommonDoHandler {
    private RDWebView mWebView;
    private ImageView ivUserIcon;
    private String loadUrl;
    protected EmptyView mEmptyView;
    private String mFailingUrl;

    private TextView mTvTitle;
    private Button btnBack;
    private CommonHandler<RDMallFragment> handler;
    private JsCallback jsCallback;
    private WXPayResultReceiver wxPayResultReceiver;
    private RoamDialog roamDialog;
    private MainNewActivity mainNewActivity;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_mall;
    }

    @Override
    public void initData() {
        super.initData();
        handler = new CommonHandler<>(this);
        wxPayResultReceiver = new WXPayResultReceiver(getContext(), handler);
        wxPayResultReceiver.register(wxPayResultReceiver);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
//        loadUrl = getString(R.string.webRdmall_outer);
        mainNewActivity = (MainNewActivity) getBaseActivity();
        loadUrl = Constant.HOST_ROAM_MALL_HTTPS;
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        btnBack = (Button) findViewById(R.id.id_toback);
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
        ivUserIcon = (ImageView) findViewById(R.id.id_circle_image);
        mWebView = (RDWebView) findViewById(R.id.id_webView);
        WebSettings settings = mWebView.getSettings();
        //设置 缓存模式
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);
        //设置适配
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        StringBuilder userAgent = new StringBuilder(settings.getUserAgentString());
        userAgent.append(" ");
        userAgent.append(getBaseActivity().getHttpUserAgent());
        settings.setUserAgentString(userAgent.toString());
        //不加上，会显示白边
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setWebChromeClient(new CustomChromeClient("nativeInterface", this));
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith(loadUrl)) {
                    Bundle bundle = new Bundle();
                    bundle.putString("url", url);
                    getBaseActivity().toActivity(WebViewActivity.class, bundle);
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mFailingUrl = failingUrl;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                //接受证书 支持https 验证
                handler.proceed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals(mFailingUrl)) {
                    mFailingUrl = null;
                    if (!NetworkHelper.isNetworkConnected(getBaseActivity())) {
                        setEmptyViewState(NETWORD_ERROR);
                    } else {
                        setEmptyViewState(FAILED);
                    }
                } else {
                    setEmptyViewState(SUCCESS);
                }
            }
            // }
        });
//        mWebView.setWebChromeClient(new InjectedChromeClient("nativeInterface", this));
//        mWebView.loadUrl(loadUrl);
    }

    @Override
    public void setListener() {
        super.setListener();
        ivUserIcon.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    private void setEmptyViewState(final LoadingState state) {
        mainNewActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mEmptyView != null) {
                    mEmptyView.setState(state);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //返回刷新问题
        String head_url = SPreferencesTool.getInstance().getStringValue(mainNewActivity.getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_headUrl);
        if (head_url == null || "".equals(head_url)) {
            ivUserIcon.setImageResource(R.drawable.nav_user_default);
        } else {
            ivUserIcon.setImageURI(UriHelper.obtainUri(head_url));
        }
        if (RoamApplication.credTripStatus != 0) { //
            roamDialog = new RoamDialog(getActivity(), getString(R.string.loadinginfo));
            roamDialog.show();
            RoamApplication.credTripStatus = 0;
            getOrderById(RoamApplication.credTripOrderId);
        }
//       mWebView.reload();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == ivUserIcon) {
            mainNewActivity.getDrawerLayout().openDrawer(GravityCompat.START);
        } else if (v == btnBack) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            }
        }
    }

    private void getOrderById(long orderId) {
        JSONObject loginUser = mainNewActivity.getAuthJSONObject();
        try {
            loginUser.put("orderid", orderId + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new FunOrder(mainNewActivity.getApplicationContext()).getOrderList(loginUser, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                //super.onFailure(errorMap);
                //超时 当支付处理
                roamDialog.dismiss();
                fragmentHandler.sendEmptyMessage(MsgType.CRED_TRIP_PAY_CANCEL);
            }

            @Override
            public void onSuccess(String response) {
                CommonModel model = JsonUtil.fromJson(response, CommonModel.class);
                if (model != null) {
                    String code = String.valueOf(model.error_no);
                    if (isSuc(code)) {
                        if (!model.result.orders.isEmpty()) {
                            OrderBean orderBean = (OrderBean) model.result.orders.get(0);
                            if (orderBean.pay_status == 2) {
                                roamDialog.dismiss();
                                fragmentHandler.sendEmptyMessage(MsgType.CRED_TRIP_PAY_SUCCESS);
                            } else {
                                roamDialog.dismiss();
                                fragmentHandler.sendEmptyMessage(MsgType.CRED_TRIP_PAY_CANCEL);
                            }
                        }
                    } else {
                        roamDialog.dismiss();
                        fragmentHandler.sendEmptyMessage(MsgType.CRED_TRIP_PAY_CANCEL);
                    }
                } else {
                    roamDialog.dismiss();
                    fragmentHandler.sendEmptyMessage(MsgType.CRED_TRIP_PAY_CANCEL);
                }
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        if (jsCallback != null) {
            JSONObject jsonInfo = new JSONObject();
            switch (msg.what) {
                case MsgType.CRED_TRIP_PAY_SUCCESS:
                case MsgType.ZFB_PAY_SUCCESS:
                case MsgType.WEI_XIN_PAY_SUCCESS:
                case MsgType.UP_PAY_SUCCESS:
                    try {
                        jsonInfo.put("error_no", 0);
                        jsonInfo.put("error_info", getString(R.string.pay_success));
                        jsCallback.apply(jsonInfo);
                    } catch (JsCallback.JsCallbackException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MsgType.PAY_CANCEL:
                case MsgType.ZFB_PAY_CANCEL:
                case MsgType.WEI_XIN_PAY_CANCEL:
                case MsgType.CRED_TRIP_PAY_CANCEL:
                case MsgType.UP_PAY_CANCEL:
                    try {
                        jsonInfo.put("error_no", 2);
                        jsonInfo.put("error_info", getString(R.string.pay_cancle));
                        jsCallback.apply(jsonInfo);
                    } catch (JsCallback.JsCallbackException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case MsgType.ZFB_PAY_ERROR:
                case MsgType.WEI_XIN_PAY_ERROR:
                case MsgType.CRED_TRIP_PAY_ERROR:
                case MsgType.UP_PAY_ERROR:
                default:
                    try {
                        jsonInfo.put("error_no", 1);
                        jsonInfo.put("error_info", getString(R.string.pay_error));
                        jsCallback.apply(jsonInfo);
                    } catch (JsCallback.JsCallbackException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public class CustomChromeClient extends InjectedChromeClient {

        public CustomChromeClient(String injectedName, RDMallFragment instance) {
            super(injectedName, instance);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            new TipDialog(getContext(), getString(R.string.prompt), message).setRightButton(getString(R.string.button_ok), null).show();
            result.confirm();
            return true;
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mTvTitle != null) {
                mTvTitle.setText(title);
            }
            RoamApplication.webCanGoBack = false;
            if (title == null || title.equals("网页无法打开") || getString(R.string.rd_mall).equals(title)) {
                ivUserIcon.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.GONE);
                mainNewActivity.showBottomMenu(View.VISIBLE);
            } else {
                ivUserIcon.setVisibility(View.GONE);
                btnBack.setVisibility(View.VISIBLE);
                //((MainNewActivity) getBaseActivity()).showBottomMenu(View.GONE);
                if (view.canGoBack()) {
                    RoamApplication.webCanGoBack = true;
                }
            }
        }
    }

    public void goBack() {
        mWebView.goBack();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
        wxPayResultReceiver.unRegister(wxPayResultReceiver);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void scanResult(EventScanResult scanResult) {
        try {
            jsCallback.apply(scanResult.getIccid());
        } catch (JsCallback.JsCallbackException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateOrder(EventUpPay event) {
        String result = event.getPayResult();
        if (result.equalsIgnoreCase("success")) {
            // 结果result_data为成功时，去商户后台查询一下再展示成功
            fragmentHandler.sendEmptyMessage(MsgType.UP_PAY_SUCCESS);
        } else if (result.equalsIgnoreCase("fail")) {
            fragmentHandler.sendEmptyMessage(MsgType.UP_PAY_ERROR);
        } else if (result.equalsIgnoreCase("cancel")) {
            fragmentHandler.sendEmptyMessage(MsgType.UP_PAY_CANCEL);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void webLoad(EventWebLoad eventWebLoad) {
        if (mWebView != null) {
//            mWebView.reload();
            mWebView.loadUrl(loadUrl);
        }
    }

    @JsInterface
    public void getLoginInfo(JsCallback callback) {
        LoginInfo info = mainNewActivity.getLoginInfo();
        JSONObject jsonInfo = new JSONObject();
        try {
            if (info.getSessionId() != null && info.getUserId() != null) {
                jsonInfo.put("errorcode", 0);
                jsonInfo.put("errorinfo", getString(R.string.success));
                JSONObject loginInfo = new JSONObject();
                loginInfo.put("userId", info.getUserId());
                loginInfo.put("sessionId", info.getSessionId());
                jsonInfo.put("data", loginInfo);
            } else {
                jsonInfo.put("errorcode", 1);
                jsonInfo.put("errorinfo", getString(R.string.fail));
            }
            callback.apply(jsonInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (JsCallback.JsCallbackException e) {
            e.printStackTrace();
        }
    }

    @JsInterface
    public void goPayOrder(Long orderId, Double amount, JsCallback callback) {
        jsCallback = callback;
        ThirdPayDialog.Builder builder = new ThirdPayDialog.Builder(getActivity(), handler);
        OrderBean orderBean = new OrderBean();
        orderBean.id = orderId;
        orderBean.payable_amount = amount.toString();
        builder.setOrder(orderBean, getBaseActivity().getAuthJSONObject());
        ThirdPayDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @JsInterface
    public void go(String pageId) {
        if (pageId.equals("myOrder")) {
            mainNewActivity.toActivity(OrderListNewActivity.class, null);
        } else if (pageId.equals("coupon")) {
            mainNewActivity.toActivity(CouponActivity.class, null);
        }
    }

    @JsInterface
    public void goScanCode(JsCallback jsCallback) {
        this.jsCallback = jsCallback;
        Bundle bundle = new Bundle();
        bundle.putBoolean("manualInput", false);
        bundle.putString("origin", "RDMall");
        mainNewActivity.toActivity(CaptureActivity.class, bundle);
    }

    @JsInterface
    public void setApn() {
        mainNewActivity.toActivity(ApnSettingActivity.class, null);
    }

    @JsInterface
    public void setCallTransfer() {
        mainNewActivity.toActivity(CallTransferActivity.class, null);
    }

    @JsInterface
    public void goLogin() {
        LinphoneActivity.instance().toActivityClearTopWithState(LoginActivity.class, LinphoneActivity.FIRST_LOGIN_ACTIVITY, null);
    }

}
