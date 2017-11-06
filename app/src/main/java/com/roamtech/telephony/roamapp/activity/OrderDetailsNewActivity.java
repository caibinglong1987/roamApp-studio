package com.roamtech.telephony.roamapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.Receiver.WXPayResultReceiver;
import com.roamtech.telephony.roamapp.activity.function.EVoucher;
import com.roamtech.telephony.roamapp.activity.function.FunOrder;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.EVoucherBean;
import com.roamtech.telephony.roamapp.bean.EVoucherBeanRDO;
import com.roamtech.telephony.roamapp.bean.OrderBean;
import com.roamtech.telephony.roamapp.bean.OrderDetailsBean;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.db.dao.CommonDao;
import com.roamtech.telephony.roamapp.db.model.AddressDBModel;
import com.roamtech.telephony.roamapp.db.model.ProductDBModel;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.event.EventUpdateOrder;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.OrderUtil;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.util.UpdateManager;
import com.roamtech.telephony.roamapp.view.ThirdPayDialog;
import com.will.common.tool.time.DateTimeTool;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by long on 2016/9/23 10:20
 * 订单详细界面
 */

public class OrderDetailsNewActivity extends HeaderBaseActivity {
    private OrderBean itemOrder;
    private LinearLayout layout_address, layout_discount, layout_ship, layout_discount2;
    private TextView tv_pay_state, tv_order_were, tv_order_phone, tv_order_address, tv_order_number;
    private TextView tv_product_name, tv_product_time, tv_car_number, tv_unit_price, total_number;
    private TextView tv_total_price, tv_discount_price, tv_ship_price;
    private TextView tv_confirm, tv_customer_service, tv_order_time, tv_actual_total_price, tv_cancel;
    private TextView tv_promotions, tv_promotions2, tv_discount_price2;
    private ImageView iv_product_icon;
    private View view_address, view_address2;
    private WXPayResultReceiver wxPayResultReceiver = null;
    private TipDialog tipDialog;
    private JSONObject loginUser = new JSONObject();
    private FunOrder funOrder;

    private boolean isVirtual = false; //是否虚拟商品
    private List<EVoucherBean> eVoucherBeanList = new ArrayList<>();
    private UpdateManager updateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        itemOrder = (OrderBean) getIntent().getSerializableExtra("order");
        initView();
        wxPayResultReceiver = new WXPayResultReceiver(this, uiHandler);
        wxPayResultReceiver.register(wxPayResultReceiver);
    }

    private void initView() {
        tv_order_number = (TextView) findViewById(R.id.tv_order_number);
        layout_address = (LinearLayout) findViewById(R.id.layout_address);
        layout_discount = (LinearLayout) findViewById(R.id.layout_discount);
        layout_ship = (LinearLayout) findViewById(R.id.layout_ship);
        layout_discount2 = (LinearLayout) findViewById(R.id.layout_discount2);
        tv_pay_state = (TextView) findViewById(R.id.tv_pay_state);
        tv_order_were = (TextView) findViewById(R.id.tv_order_were);
        tv_order_phone = (TextView) findViewById(R.id.tv_order_phone);
        tv_order_address = (TextView) findViewById(R.id.tv_order_address);
        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        tv_product_time = (TextView) findViewById(R.id.tv_product_time);
        tv_car_number = (TextView) findViewById(R.id.tv_car_number);
        tv_unit_price = (TextView) findViewById(R.id.tv_unit_price);
        total_number = (TextView) findViewById(R.id.total_number);
        tv_total_price = (TextView) findViewById(R.id.tv_total_price);
        tv_discount_price = (TextView) findViewById(R.id.tv_discount_price);
        tv_ship_price = (TextView) findViewById(R.id.tv_ship_price);
        tv_promotions = (TextView) findViewById(R.id.tv_promotions);
        tv_promotions2 = (TextView) findViewById(R.id.tv_promotions2);
        tv_discount_price2 = (TextView) findViewById(R.id.tv_discount_price2);
        tv_customer_service = (TextView) findViewById(R.id.tv_customer_service);
        tv_confirm = (TextView) findViewById(R.id.tv_confirm);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_actual_total_price = (TextView) findViewById(R.id.tv_actual_total_price);
        tv_order_time = (TextView) findViewById(R.id.tv_order_time);
        iv_product_icon = (ImageView) findViewById(R.id.iv_product_icon);
        view_address = findViewById(R.id.view_address);
        view_address2 = findViewById(R.id.view_address2);
        funOrder = new FunOrder(getApplicationContext());
        headerLayout.showTitle(getString(R.string.order_details));
        headerLayout.showLeftBackButton();
        tv_confirm.setOnClickListener(this);
        tv_customer_service.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        getEVoucher(itemOrder.id);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.ZFB_PAY_SUCCESS://支付宝
            case MsgType.WEI_XIN_PAY_SUCCESS: //微信充值
                itemOrder.pay_status = 2;
                setData();
                EventBus.getDefault().post(new EventUpdateOrder());
                break;
            case MsgType.HANDLER_RECEIVED_ORDER:
                itemOrder.shipping_status = 2;
                setData();
                EventBus.getDefault().post(new EventUpdateOrder());
                break;
            case MsgType.ORDER_CANCEL_SUCCESS: //取消订单成功
                itemOrder.order_status = 2;
                setData();
                EventBus.getDefault().post(new EventUpdateOrder());
                break;
            case MsgType.MSG_GET_EVOUVHER_SUCCESS:
                setData();
                EventBus.getDefault().post(new EventUpdateOrder());
                break;
        }
    }


    private void setData() {
        updateManager = new UpdateManager(getApplicationContext());
        tv_order_number.setText(String.format(getString(R.string.order_number), String.valueOf(itemOrder.id)));
        if (itemOrder.ship_address == null) {
            layout_address.setVisibility(View.GONE);
            view_address.setVisibility(View.GONE);
            view_address2.setVisibility(View.GONE);
        } else {
            AddressDBModel addressDBModel = updateManager.getUserAddressById(getAuthJSONObject(), Integer.parseInt(itemOrder.ship_address));
            layout_address.setVisibility(View.VISIBLE);
            view_address.setVisibility(View.VISIBLE);
            view_address2.setVisibility(View.VISIBLE);

            if (addressDBModel != null && addressDBModel.city != null && addressDBModel.address != null) {
                String complete_address = addressDBModel.province + addressDBModel.city;
                if (addressDBModel.district != null) {
                    complete_address = complete_address + addressDBModel.district;
                }
                complete_address = complete_address + addressDBModel.address;
                tv_order_address.setText(complete_address);
                tv_order_phone.setText(addressDBModel.mobile);
                if (addressDBModel.consignee == null || addressDBModel.consignee.length() == 0) {
                    tv_order_were.setText(getString(R.string.anonymous));
                } else {
                    tv_order_were.setText(addressDBModel.consignee);
                }
            }
        }
        List<OrderDetailsBean> orderDetails = itemOrder.orderdetails;
        ProductDBModel model; //商品model
        OrderDetailsBean details; //订单详细 model
        if (orderDetails != null && orderDetails.size() > 0) {
            details = orderDetails.get(0);
            String startTime = "", endTime = "";
            model = updateManager.getProductDataById(getAuthJSONObject(), details.productid);
            if (model != null) {
                switch (model.categoryid) {
                    case 1://全球芯
                        iv_product_icon.setImageResource(R.drawable.rd_card_m);
                        tv_product_name.setText(getString(R.string.globalcard));
                        tv_car_number.setVisibility(View.GONE);
                        tv_product_time.setVisibility(View.GONE);
                        isVirtual = false;
                        break;
                    case 2: //流量
                        isVirtual = true;
                        iv_product_icon.setImageResource(R.drawable.traffic_packages);
                        tv_product_name.setText(String.format(getString(R.string.country_traffic), details.areaname));

                        try {
                            startTime = DateTimeTool.ConverToString(DateTimeTool.ConverToDate(details.effect_datetime));
                            endTime = DateTimeTool.ConverToString(DateTimeTool.ConverToDate(details.failure_datetime));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String simidNum = ""; //卡号
                        String genevoucherName = ""; //电子券 显示优先
                        for (int m = 0; m < orderDetails.get(0).odprdattrs.size(); m++) {
                            if (orderDetails.get(0).odprdattrs.get(m).varname.equals("genevoucher")) {
                                if (orderDetails.get(0).odprdattrs.get(m).value.equals("true")) {
                                    genevoucherName = orderDetails.get(0).odprdattrs.get(m).name;
                                }
                            }
                            if (orderDetails.get(0).odprdattrs.get(m).varname.equals("simid")) {
                                simidNum = orderDetails.get(0).odprdattrs.get(m).value;
                                break;
                            }
                        }

                        tv_car_number.setVisibility(View.VISIBLE);
                        tv_product_time.setVisibility(View.VISIBLE);
                        tv_product_time.setText(String.format(getString(R.string.product_time), startTime, endTime));
                        if (genevoucherName.length() == 0) {
                            if (simidNum.length() == 0) {
                                tv_car_number.setText(getString(R.string.buy_other_car));
                            } else {
                                tv_car_number.setText(String.format(getString(R.string.traffic_car_number), simidNum));
                            }
                        } else {
                            tv_car_number.setText(String.format(getString(R.string.traffic_car_number), genevoucherName));
                        }
                        break;
                    case 3: //通话
                        isVirtual = true;
                        iv_product_icon.setImageResource(R.drawable.phonetics);
                        tv_product_name.setText(getString(R.string.call_time));
                        tv_product_time.setText(String.format(getString(R.string.call_time_min), details.odprdattrs.get(0).value));
                        tv_car_number.setVisibility(View.GONE);
                        break;
                    case 4://云号码
                        try {
                            startTime = DateTimeTool.ConverToString(DateTimeTool.ConverToDate(details.effect_datetime));
                            endTime = DateTimeTool.ConverToString(DateTimeTool.ConverToDate(details.failure_datetime));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        isVirtual = true;
                        iv_product_icon.setImageResource(R.drawable.hand_holding_business_card);
                        tv_product_name.setText(String.format(getString(R.string.hand_holding_product_name), ""));
                        tv_product_time.setText(String.format(getString(R.string.product_time), startTime, endTime));
                        tv_car_number.setVisibility(View.GONE);
                        break;
                    case 5:
                        try {
                            startTime = DateTimeTool.ConverToString(DateTimeTool.ConverToDate(details.effect_datetime));
                            endTime = DateTimeTool.ConverToString(DateTimeTool.ConverToDate(details.failure_datetime));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        isVirtual = false;
                        iv_product_icon.setImageResource(R.drawable.roam_once_card_s_1);
                        tv_car_number.setVisibility(View.GONE);
                        tv_product_name.setText(String.format(getString(R.string.internet_card), details.areaname));
                        tv_product_time.setText(String.format(getString(R.string.product_time), startTime, endTime));
                        break;
                    default:
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .build();
                        ImageLoader.getInstance().displayImage(Constant.IMAGE_URL + model.image, new ImageViewAware(iv_product_icon), options);
                        //iv_product_icon.setImageUrlByNetworkImage(R.id.iv_product_icon,Constant.IMAGE_URL + model.image);
                        tv_product_name.setText(model.name);
                        tv_car_number.setVisibility(View.GONE);
                        tv_product_time.setVisibility(View.GONE);
                        isVirtual = false;
                        break;
                }
                if (eVoucherBeanList != null && eVoucherBeanList.size() > 0) {
                    double dis_price;
                    if (StringUtil.isBlank(itemOrder.shipping_fee)) {
                        itemOrder.shipping_fee = "0.00";
                    }
                    layout_discount.setVisibility(View.VISIBLE);
                    for (int k = 0; k < eVoucherBeanList.size(); k++) {
                        dis_price = Double.parseDouble(itemOrder.price) + Double.parseDouble(itemOrder.shipping_fee) - Double.parseDouble(itemOrder.discount) - Double.parseDouble(itemOrder.payable_amount);
                        BigDecimal decimal = new BigDecimal(dis_price);
                        BigDecimal setScale = decimal.setScale(2, BigDecimal.ROUND_HALF_DOWN);
                        if (k == 0) {
                            tv_promotions.setText(eVoucherBeanList.get(k).name);
                            tv_discount_price.setText(String.format(getString(R.string.discount_price), String.valueOf(setScale)));
                        } else {
//                            layout_discount2.setVisibility(View.VISIBLE);
//                            tv_promotions2.setText(eVoucherBeanList.get(k).name);
//                            if (order_price - eVoucherBeanList.get(0).money - dis_price > 0) {
//                                tv_discount_price2.setText(String.format(getString(R.string.discount_price), String.valueOf(dis_price)));
//                            } else {
//                                tv_discount_price2.setText(String.format(getString(R.string.discount_price), String.valueOf(order_price - eVoucherBeanList.get(0).money)));
//                            }
                            break;
                        }
                    }
                } else {
                    layout_discount.setVisibility(View.GONE);
                    layout_discount2.setVisibility(View.GONE);
                }
                tv_unit_price.setText(String.format(getString(R.string.price), details.unit_price));
                total_number.setText(String.format(getString(R.string.quantity_number), String.valueOf(details.quantity)));
                tv_ship_price.setText(String.format(getString(R.string.price), itemOrder.shipping_fee == null ? "0" : itemOrder.shipping_fee));

                if (StringUtil.isBlank(itemOrder.shipping_fee) || itemOrder.shipping_fee.equals("0.00")) {
                    layout_ship.setVisibility(View.GONE);
                } else {
                    layout_ship.setVisibility(View.VISIBLE);
                }
                //tv_discount_price.setText(String.format(getString(R.string.discount_price), itemOrder.discount));
                tv_order_time.setText(String.format(getString(R.string.order_time), itemOrder.createtime));
                tv_total_price.setText(String.format(getString(R.string.price), itemOrder.price));
                //double order_price = Double.parseDouble(itemOrder.price) + Double.parseDouble(itemOrder.shipping_fee) - sum_dis_price - Double.parseDouble(itemOrder.payable_amount);
                tv_actual_total_price.setText(String.format(getString(R.string.price), itemOrder.payable_amount));

                switch (itemOrder.pay_status) {
                    case 1:
                    case 0: //未支付 显示取消订单和去支付
                        tv_confirm.setText(getString(R.string.order_go_pay));
                        tv_confirm.setVisibility(View.VISIBLE);
                        tv_cancel.setVisibility(View.VISIBLE);
                        break;
                    case 3: //已退款 显示交易关闭
                        tv_confirm.setVisibility(View.INVISIBLE);
                        tv_confirm.setText(getString(R.string.again_buy));
                        break;
                    case 2://已支付
                        if (itemOrder.shipping_status == 0) { //未发货 隐藏操作菜单
                            tv_confirm.setVisibility(View.GONE);
                            tv_cancel.setVisibility(View.GONE);
                        } else if (itemOrder.shipping_status == 1) { //已发货 显示确认收货 隐藏按钮
                            tv_confirm.setVisibility(View.VISIBLE);
                            tv_cancel.setVisibility(View.INVISIBLE);
                            tv_confirm.setText(getString(R.string.order_confirm_receipt));
                        } else if (details.status == 1) { //已收货 显示交易完成 显示再次购买
                            tv_confirm.setVisibility(View.INVISIBLE);
                            tv_cancel.setVisibility(View.INVISIBLE);
                            tv_confirm.setText(getString(R.string.again_buy));
                        }
                        break;
                }
                if (itemOrder.order_status == 2 || itemOrder.order_status == 5) {
                    //订单已经取消或关闭
                    tv_confirm.setVisibility(View.INVISIBLE);
                    tv_cancel.setVisibility(View.INVISIBLE);
                    tv_confirm.setText(getString(R.string.again_buy));
                }
                tv_pay_state.setText(new OrderUtil(this).getLabState(itemOrder.order_status, itemOrder.pay_status, itemOrder.shipping_status, isVirtual));
            }
        }
    }

    /**
     * 获取电子券
     *
     * @param orderId 订单id
     */
    private void getEVoucher(Long orderId) {
        EVoucher eVoucher = new EVoucher(getApplicationContext());
        loginUser = getAuthJSONObject();
        try {
            loginUser.put("orderid", orderId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        eVoucher.getEVoucher(loginUser, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
                uiHandler.sendEmptyMessage(MsgType.MSG_GET_EVOUVHER_SUCCESS);
            }

            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
                if (response != null) {
                    UCResponse<EVoucherBeanRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<EVoucherBeanRDO>>() {
                    }.getType());
                    if (result != null && result.getAttributes() != null) {
                        eVoucherBeanList = result.getAttributes().evouchers;
                    }
                    uiHandler.sendEmptyMessage(MsgType.MSG_GET_EVOUVHER_SUCCESS);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                //取消订单
                tipDialog = new TipDialog(this, getString(R.string.cancel_confirm), "");
                tipDialog.setLeftButton(getString(R.string.rongw_point), new TipDialog.OnClickListener() {
                    @Override
                    public void onClick(int which) {
                        tipDialog.dismiss();
                    }
                });
                tipDialog.setRightButton(getString(R.string.button_ok), new TipDialog.OnClickListener() {
                    @Override
                    public void onClick(int which) {
                        loginUser = getAuthJSONObject();
                        try {
                            loginUser.put("orderid", String.valueOf(itemOrder.id));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        funOrder.cancelOrder(loginUser, hashCode(), new HttpBusinessCallback() {
                            @Override
                            public void onSuccess(String response) {
                                uiHandler.sendEmptyMessage(MsgType.ORDER_CANCEL_SUCCESS);
                            }

                            @Override
                            public void onFailure(Map<String, ?> errorMap) {
                                super.onFailure(errorMap);
                            }
                        });
                    }
                });
                tipDialog.show();
                break;
            case R.id.tv_confirm:
                loginUser = getAuthJSONObject();
                if (itemOrder.order_status == 2 || itemOrder.order_status == 5) {
                    toWebViewActivity(RDMallActivity.class, getString(R.string.webRdmall_outer));
                } else if (itemOrder.shipping_status == 1) {
                    try {
                        loginUser.put("orderid", String.valueOf(itemOrder.id));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    new FunOrder(this).receivedOrder(loginUser, hashCode(), new HttpBusinessCallback() {
                        @Override
                        public void onSuccess(String response) {
                        }

                        @Override
                        public void onFailure(Map<String, ?> errorMap) {

                        }
                    });

                    tipDialog = new TipDialog(OrderDetailsNewActivity.this, getString(R.string.receiver_product), "");
                    tipDialog.setLeftButton(getString(R.string.rongw_point), new TipDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {
                            tipDialog.dismiss();
                        }
                    });

                    tipDialog.setRightButton(getString(R.string.confirm_cancel), new TipDialog.OnClickListener() {
                        @Override
                        public void onClick(int which) {
                            loginUser = getAuthJSONObject();
                            try {
                                loginUser.put("orderid", String.valueOf(itemOrder.id));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            funOrder.receivedOrder(loginUser, hashCode(), new HttpBusinessCallback() {
                                @Override
                                public void onSuccess(String response) {
                                    itemOrder.shipping_status = 2;
                                    uiHandler.sendEmptyMessage(MsgType.HANDLER_RECEIVED_ORDER);
                                }

                                @Override
                                public void onFailure(Map<String, ?> errorMap) {

                                }
                            });
                        }
                    }).show();

                } else {
                    ThirdPayDialog.Builder builder = new ThirdPayDialog.Builder(this, uiHandler);
                    builder.setOrder(itemOrder, loginUser);
                    builder.create().show();
                }
                break;
            case R.id.tv_customer_service:
                LinphoneActivity.instance().setAddressGoToDialerAndCall(getString(R.string.customer_phone), null, null);//.newOutgoingCall(inputText,inputText);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
        String msg = "";
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str != null) {
            if (str.equalsIgnoreCase("success")) {
                // 结果result_data为成功时，去商户后台查询一下再展示成功
                msg = "支付成功！";
                itemOrder.pay_status = 2;
            } else if (str.equalsIgnoreCase("fail")) {
                msg = "支付失败！";
                itemOrder.order_status = 2;
            } else if (str.equalsIgnoreCase("cancel")) {
                msg = "用户取消了支付";
                itemOrder.order_status = 2;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("支付结果通知");
            builder.setMessage(msg);
            builder.setInverseBackgroundForced(true);
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setData();
                }
            });
            builder.create().show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (wxPayResultReceiver != null) {
            wxPayResultReceiver.unRegister(wxPayResultReceiver);
            wxPayResultReceiver = null;
        }
    }
}
