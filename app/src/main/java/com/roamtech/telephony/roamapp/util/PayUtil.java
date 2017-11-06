package com.roamtech.telephony.roamapp.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;

import com.alipay.sdk.app.PayTask;
import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.activity.WebViewActivity;
import com.roamtech.telephony.roamapp.activity.function.FunOrder;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.bean.PayParamsRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.bean.WxOrderBean;
import com.roamtech.telephony.roamapp.web.HttpFunction;
import com.roamtech.telephony.roamapp.wxapi.WXInterface;
import com.unionpay.UPPayAssistEx;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by long
 * on 2016/9/30 17:53
 * 支付 工具
 */

public class PayUtil {
    public static Activity context;
    private Handler uiHandler;

    public PayUtil(Activity context, Handler handler) {
        PayUtil.context = context;
        this.uiHandler = handler;
    }

    /**
     * 获取支付参数
     *
     * @param loginInfo 登录信息
     * @param payId     支付ID
     */
    public void getPayParams(JSONObject loginInfo, final long orderId, final int payId) {
        try {
            loginInfo.put("orderid", String.valueOf(orderId));
            loginInfo.put("payid", String.valueOf(payId));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new FunOrder(context).getPayParams(loginInfo, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                UCResponse<PayParamsRDO> payParams = JsonUtil.fromJson(response, new TypeToken<UCResponse<PayParamsRDO>>() {
                }.getType());
                if (payParams != null) {
                    if (HttpFunction.isSuc(String.valueOf(payParams.getErrorNo()))) {
                        String orderInfo = payParams.getAttributes().getPayParams();
                        goPayOrder(orderInfo, payId, orderId);
                    }
                }
            }

            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }
        });
    }

    public void goPayOrder(final String params, int payId, long orderId) {
        switch (payId) {
            case 4: //支付宝
                if (!StringUtil.isTrimBlank(params)) {
                    new Thread() {
                        @Override
                        public void run() {
                            PayTask aliPay = new PayTask(PayUtil.context);
                            final Map<String, String> result = aliPay.payV2(params, true);
                            context.runOnUiThread(new Runnable() {
                                @Override
/*                              返回码	含义
                                9000	订单支付成功
                                8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                                4000	订单支付失败
                                5000	重复请求
                                6001	用户中途取消
                                6002	网络连接出错
                                6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                                其它	其它支付错误*/
                                public void run() {
                                    switch (result.get("resultStatus")) {
                                        case "9000":
                                            if (uiHandler != null) {
                                                uiHandler.sendEmptyMessage(MsgType.ZFB_PAY_SUCCESS);
                                            }
                                            break;
                                        case "6001":
                                            if (uiHandler != null) {
                                                uiHandler.sendEmptyMessage(MsgType.ZFB_PAY_CANCEL);
                                            }
                                            break;
                                        default:
                                            if (uiHandler != null) {
                                                uiHandler.sendEmptyMessage(MsgType.ZFB_PAY_ERROR);
                                            }
                                            break;
                                    }
                                }
                            });
                        }
                    }.start();
                }
                break;
            case 5: //微信
                if (!StringUtil.isTrimBlank(params)) {
                    try {
                        WXInterface wxInterface = new WXInterface(context);
                        JSONObject json = new JSONObject(params);
                        WxOrderBean wxOrderBean = new WxOrderBean();
                        wxOrderBean.appId = json.getString("appid");
                        wxOrderBean.partnerId = json.getString("partnerid");
                        wxOrderBean.prepayId = json.getString("prepayid");
                        wxOrderBean.nonceStr = json.getString("noncestr");
                        wxOrderBean.timeStamp = json.getString("timestamp");
                        wxOrderBean.packageValue = json.getString("package");
                        wxOrderBean.sign = json.getString("sign");
                        wxInterface.payWxClient(wxOrderBean);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case 6:
                Intent intent = new Intent(context, WebViewActivity.class);
                RoamApplication.credTripStatus = 1;
                RoamApplication.credTripOrderId = orderId;
                intent.putExtra("html", params);
                context.startActivity(intent);
                break;
            case 7:
                try {
                    JSONObject json = new JSONObject(params);
                    final String tn = json.optString("tn");
                    final String serverMode = json.optString("serverMode");//00 正式  01 测试环境
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UPPayAssistEx.startPay(context, null, null, tn, serverMode);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
