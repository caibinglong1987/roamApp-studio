package com.roamtech.telephony.roamapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.Receiver.WXPayResultReceiver;
import com.roamtech.telephony.roamapp.activity.Parameter.KeyValue;
import com.roamtech.telephony.roamapp.activity.function.FunOrder;
import com.roamtech.telephony.roamapp.adapter.CommonAdapter;
import com.roamtech.telephony.roamapp.adapter.ViewHolder;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.bean.CommonModel;
import com.roamtech.telephony.roamapp.bean.OrderBean;
import com.roamtech.telephony.roamapp.bean.OrderDetailsBean;
import com.roamtech.telephony.roamapp.db.model.ProductDBModel;
import com.roamtech.telephony.roamapp.dialog.RoamDialog;
import com.roamtech.telephony.roamapp.dialog.TipDialog;
import com.roamtech.telephony.roamapp.event.EventUpdateOrder;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.OrderUtil;
import com.roamtech.telephony.roamapp.util.UpdateManager;
import com.roamtech.telephony.roamapp.view.SwipyRefreshLayout;
import com.roamtech.telephony.roamapp.view.SwipyRefreshLayoutDirection;
import com.roamtech.telephony.roamapp.view.ThirdPayDialog;
import com.will.common.tool.time.DateTimeTool;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.roamtech.telephony.roamapp.enums.LoadingState.SESSION_TIME_OUT;
import static com.roamtech.telephony.roamapp.web.HttpFunction.isSessionTimeout;
import static com.roamtech.telephony.roamapp.web.HttpFunction.isSuc;

/**
 * Created by long on 2016/9/23 09:41
 */

public class OrderListNewActivity extends HeaderBaseActivity implements SwipyRefreshLayout.OnRefreshListener {
    private SwipyRefreshLayout swipyRefreshLayout;
    private final int MSG_LOAD_DATA_SUCCESS = 11;
    private final int MSG_LOAD_DATA_NO_MORE = 12;
    private final int MSG_LOAD_DATA_NO_DATA = 13;
    private final int MSG_LOAD_DATA_NOTIFY = 14;
    private final int MSG_LOAD_DATA_TIMEOUT = 15;
    private boolean IS_REFRESH = true;  //是否需要刷新
    private int pageIndex = 0;
    private int pageSize = 10;
    private List<OrderBean> data = new ArrayList<>();
    private ListView listView;
    private FunOrder funOrder;
    private RelativeLayout noDataLayout;
    private CommonAdapter<OrderBean> adapter;
    private JSONObject loginUser;
    private WXPayResultReceiver wxPayResultReceiver;
    private TipDialog tipDialog;
    private OrderUtil orderUtil;
    private RoamDialog roamDialog;
    private boolean isVirtual = false; //是否虚拟商品
    private UpdateManager updateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        initViewData();
        orderUtil = new OrderUtil(this);
        wxPayResultReceiver = new WXPayResultReceiver(this, uiHandler);
        wxPayResultReceiver.register(wxPayResultReceiver);
        EventBus.getDefault().register(this);
    }

    private void initViewData() {
        swipyRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.pullToRefreshView);
        listView = (ListView) findViewById(R.id.list_order);

        noDataLayout = (RelativeLayout) findViewById(R.id.no_data_layout);
        swipyRefreshLayout.setOnRefreshListener(this);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTH);
        headerLayout.showTitle(getString(R.string.activity_title_order));
        headerLayout.showLeftBackButton();
        funOrder = new FunOrder(getApplicationContext());
        updateManager = new UpdateManager(getApplicationContext());
        updateManager.getUserAddress(getAuthJSONObject());
        adapter = new CommonAdapter<OrderBean>(this, data, R.layout.item_order) {
            @Override
            public void convert(ViewHolder helper, final OrderBean item, final int position) {
                List<OrderDetailsBean> orderDetails = item.orderdetails;
                ProductDBModel model;
                final OrderDetailsBean details;
                if (orderDetails != null && orderDetails.size() > 0) {
                    details = orderDetails.get(0);
                    String startTime = "", endTime = "";
                    model = updateManager.getProductDataById(getAuthJSONObject(), details.productid);
                    if (model != null) {
                        switch (model.categoryid) {
                            case 1://全球芯
                                isVirtual = false;
                                helper.setImageResource(R.id.iv_product_icon, R.drawable.rd_card_m);
                                helper.setText(R.id.tv_product_name, getString(R.string.globalcard));
                                helper.hideView(R.id.tv_car_number);
                                helper.hideView(R.id.tv_product_time);
                                break;
                            case 2: //流量
                                isVirtual = true;
                                helper.setImageResource(R.id.iv_product_icon, R.drawable.traffic_packages);
                                helper.setText(R.id.tv_product_name, String.format(getString(R.string.country_traffic), details.areaname));
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
                                helper.showView(R.id.tv_car_number);
                                helper.showView(R.id.tv_product_time);
                                helper.setText(R.id.tv_product_time, String.format(getString(R.string.product_time), startTime, endTime));
                                if (genevoucherName.length() == 0) {
                                    if (simidNum.length() == 0) {
                                        helper.setText(R.id.tv_car_number, getString(R.string.buy_other_car));
                                    } else {
                                        helper.setText(R.id.tv_car_number, String.format(getString(R.string.traffic_car_number), simidNum));
                                    }
                                } else {
                                    helper.setText(R.id.tv_car_number, genevoucherName);
                                }
                                break;
                            case 3: //通话
                                isVirtual = true;
                                helper.setImageResource(R.id.iv_product_icon, R.drawable.phonetics);
                                helper.setText(R.id.tv_product_name, getString(R.string.call_time));
                                helper.showView(R.id.tv_product_time);
                                helper.setText(R.id.tv_product_time, String.format(getString(R.string.call_time_min), details.call_duration.toString()));
                                helper.hideView(R.id.tv_car_number);
                                break;
                            case 4://云号码
                                try {
                                    startTime = DateTimeTool.ConverToString(DateTimeTool.ConverToDate(details.effect_datetime));
                                    endTime = DateTimeTool.ConverToString(DateTimeTool.ConverToDate(details.failure_datetime));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                isVirtual = true;
                                helper.setImageResource(R.id.iv_product_icon, R.drawable.hand_holding_business_card);
                                helper.setText(R.id.tv_product_name, String.format(getString(R.string.hand_holding_product_name), ""));
                                helper.showView(R.id.tv_product_time);
                                helper.setText(R.id.tv_product_time, String.format(getString(R.string.product_time), startTime, endTime));
                                helper.hideView(R.id.tv_car_number);
                                break;
                            case 5:
                                try {
                                    startTime = DateTimeTool.ConverToString(DateTimeTool.ConverToDate(details.effect_datetime));
                                    endTime = DateTimeTool.ConverToString(DateTimeTool.ConverToDate(details.failure_datetime));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                isVirtual = false;
                                helper.setImageResource(R.id.iv_product_icon, R.drawable.roam_once_card_s_1);
                                helper.setText(R.id.tv_product_name, String.format(getString(R.string.internet_card), details.areaname));
                                helper.showView(R.id.tv_product_time);
                                helper.setText(R.id.tv_product_time, String.format(getString(R.string.product_time), startTime, endTime));
                                helper.hideView(R.id.tv_car_number);
                                break;
                            default:
                                helper.setImageUrlByNetworkImage(R.id.iv_product_icon, Constant.IMAGE_URL + model.image);
                                helper.setText(R.id.tv_product_name, model.name);
                                helper.hideView(R.id.tv_car_number);
                                helper.hideView(R.id.tv_product_time);
                                break;
                        }
                        helper.setText(R.id.tv_order_number, String.format(getString(R.string.order_number), String.valueOf(item.id)));
                        helper.setText(R.id.tv_unit_price, String.format(getString(R.string.price), details.unit_price));
                        helper.setText(R.id.total_number, String.format(getString(R.string.quantity_number), String.valueOf(details.quantity)));
                        helper.setText(R.id.tv_product_info, String.format(getString(R.string.product_total_number), String.valueOf(details.quantity)));
                        helper.setText(R.id.tv_total_price, String.format(getString(R.string.price), item.payable_amount));
                        switch (item.pay_status) {
                            case 1:
                            case 0: //未支付 显示取消订单和去支付
                                helper.showView(R.id.tv_cancel);
                                helper.setText(R.id.tv_go_pay, getString(R.string.order_go_pay));
                                helper.showView(R.id.tv_go_pay);
                                break;
                            case 3: //已退款 显示交易关闭
                                helper.hideView(R.id.tv_cancel);
                                helper.hideView(R.id.tv_go_pay);
                                helper.setText(R.id.tv_go_pay, getString(R.string.order_transaction_closed));
                                break;
                            case 2://已支付
                                if (item.shipping_status == 0) { //未发货 隐藏操作菜单
                                    helper.hideView(R.id.tv_cancel);
                                    helper.hideView(R.id.tv_go_pay);
                                } else if (item.shipping_status == 1) { //已发货 显示确认收货 隐藏按钮
                                    helper.showView(R.id.tv_go_pay);
                                    helper.hideView(R.id.tv_cancel);
                                    helper.setText(R.id.tv_go_pay, getString(R.string.order_confirm_receipt));
                                } else if (details.status == 1) { //已收货 显示交易完成 显示再次购买
                                    helper.hideView(R.id.tv_go_pay);
                                    helper.hideView(R.id.tv_cancel);
                                    helper.setText(R.id.tv_go_pay, getString(R.string.again_buy));
                                }
                                break;
                        }

                        if (item.order_status == 2 || item.order_status == 5) {
                            //订单已经取消或关闭
                            helper.hideView(R.id.tv_cancel);
                            helper.hideView(R.id.tv_go_pay);
                            helper.setText(R.id.tv_go_pay, getString(R.string.again_buy));
                        }
                        helper.setText(R.id.tv_pay_state, orderUtil.getLabState(item.order_status, item.pay_status, item.shipping_status, isVirtual));
                        helper.showView(R.id.tv_pay_state);
                        helper.setOnClick(R.id.tv_cancel, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //取消订单
                                tipDialog = new TipDialog(OrderListNewActivity.this, getString(R.string.cancel_confirm), "");
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
                                            loginUser.put("orderid", String.valueOf(item.id));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        funOrder.cancelOrder(loginUser, hashCode(), new HttpBusinessCallback() {
                                            @Override
                                            public void onSuccess(String response) {
                                                uiHandler.sendEmptyMessage(MSG_LOAD_DATA_NOTIFY);
                                            }

                                            @Override
                                            public void onFailure(Map<String, ?> errorMap) {
                                                super.onFailure(errorMap);
                                            }
                                        });
                                    }
                                });
                                tipDialog.show();
                            }
                        });

                        helper.setOnClick(R.id.tv_go_pay, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (item.order_status == 2 || item.order_status == 5) { //可以继续购买
                                    //toWebViewActivity(RDMallActivity.class, getString(R.string.webRdmall_outer));
                                } else if (item.shipping_status == 1) { //确认收货

                                    tipDialog = new TipDialog(OrderListNewActivity.this, getString(R.string.receiver_product), "");
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
                                                loginUser.put("orderid", String.valueOf(item.id));
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            funOrder.receivedOrder(loginUser, hashCode(), new HttpBusinessCallback() {
                                                @Override
                                                public void onSuccess(String response) {
                                                    uiHandler.sendEmptyMessage(MSG_LOAD_DATA_NOTIFY);
                                                }

                                                @Override
                                                public void onFailure(Map<String, ?> errorMap) {

                                                }
                                            });
                                        }
                                    }).show();
                                } else {
                                    //thirdPayDialog = new ThirdPayDialog(OrderListNewActivity.this);
                                    ThirdPayDialog.Builder builder = new ThirdPayDialog.Builder(OrderListNewActivity.this, uiHandler);
                                    loginUser = getAuthJSONObject();
                                    builder.setOrder(item, loginUser);
                                    builder.create().show();
                                }
                            }
                        });
                    }

                }
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("order", data.get(i));
                toActivity(OrderDetailsNewActivity.class, bundle);
            }
        });
        freshLoad();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MSG_LOAD_DATA_NO_DATA:
                roamDialog.dismiss();
                showNodataLayout();
                break;
            case MSG_LOAD_DATA_NO_MORE:
                Log.e("data--", "no-data");
                break;
            case MSG_LOAD_DATA_TIMEOUT:
                roamDialog.dismiss();
                showNodataLayout();
                break;
            case MSG_LOAD_DATA_SUCCESS:
                //ToastUtils.customizeToast(this,"数据请求","成功");
                listView.setVisibility(View.VISIBLE);
                noDataLayout.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
                break;
            case MsgType.ZFB_PAY_ERROR:
                Log.e("pay--", "error");
                break;
            case MsgType.ZFB_PAY_SUCCESS:
            case MsgType.WEI_XIN_PAY_SUCCESS: //微信充值
                //adapter.notifyDataSetChanged();
                freshLoad();
                break;
            case MSG_LOAD_DATA_NOTIFY: //刷新接口
                freshLoad();
                break;
        }
        roamDialog.dismiss();
    }

    private void showNodataLayout() {
        listView.setVisibility(View.GONE);
        noDataLayout.setVisibility(View.VISIBLE);
        noDataLayout.findViewById(R.id.hint_textview1).setVisibility(View.VISIBLE);
        ((TextView) noDataLayout.findViewById(R.id.hint_textview1)).setText(getString(R.string.no_order));
        ((TextView) noDataLayout.findViewById(R.id.hint_textview_desc2)).setText(getString(R.string.please_first_order));
        ((TextView) noDataLayout.findViewById(R.id.hint_textview2)).setText(getString(R.string.no_data_desc));
        noDataLayout.findViewById(R.id.hint_textview2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(KeyValue.TAB_TARGET, KeyValue.TAB_RD_MALL);
                toActivityClearTopWithState(LinphoneActivity.class, bundle);
            }
        });
    }

    /**
     * 获取订单列表数据
     */
    private void getResponseData() {
        roamDialog = new RoamDialog(this, getString(R.string.loadinginfo));
        roamDialog.show();
        loginUser = getAuthJSONObject();
        try {
            loginUser.put("pageindex", String.valueOf(pageIndex));
            loginUser.put("pagesize", String.valueOf(pageSize));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        funOrder.getOrderList(loginUser, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                CommonModel model = JsonUtil.fromJson(response, CommonModel.class);
                if (model != null) {
                    String code = String.valueOf(model.error_no);
                    //会话退出
                    if (isSessionTimeout(Integer.parseInt(code))) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                roamDialog.dismiss();
                                showToast(SESSION_TIME_OUT.getText());
                                toActivityClearTopWithState(LoginActivity.class, null);
                            }
                        });
                    }
                    if (isSuc(code)) {
                        if (!model.result.orders.isEmpty()) {
                            if (IS_REFRESH) {
                                data.clear();
                                pageIndex = 0;
                            }
                            data.addAll(model.result.orders);
                            uiHandler.sendEmptyMessage(MSG_LOAD_DATA_SUCCESS);
                        } else {
                            if (!IS_REFRESH) {
                                uiHandler.sendEmptyMessage(MSG_LOAD_DATA_NO_MORE);
                            } else {
                                uiHandler.sendEmptyMessage(MSG_LOAD_DATA_NO_DATA);
                            }
                        }
                    } else {
                        uiHandler.sendEmptyMessage(MSG_LOAD_DATA_NO_DATA);
                    }
                }
                IS_REFRESH = false;
            }

            @Override
            public void onFailure(Map<String, ?> errorMap) {
                uiHandler.sendEmptyMessage(MSG_LOAD_DATA_TIMEOUT);
            }
        });
    }

    @Override
    public void onRefresh(final SwipyRefreshLayoutDirection direction) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    freshLoad();
                } else {
                    moreLoad();
                }
                swipyRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateOrder(EventUpdateOrder event) {
        freshLoad();
    }

    //加载更多
    private void moreLoad() {
        IS_REFRESH = false;
        pageIndex++;
        getResponseData();
    }

    //刷新
    private void freshLoad() {
        IS_REFRESH = true;
        pageIndex = 0;
        getResponseData();
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
            } else if (str.equalsIgnoreCase("fail")) {
                msg = "支付失败！";
            } else if (str.equalsIgnoreCase("cancel")) {
                msg = "用户取消了支付";
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("支付结果通知");
            builder.setMessage(msg);
            builder.setInverseBackgroundForced(true);
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    freshLoad();
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
        }
        EventBus.getDefault().unregister(this);
    }
}
