package com.roamtech.telephony.roamapp.util;

import android.content.Context;

import com.roamtech.telephony.roamapp.R;

/**
 * Created by long
 * on 2016/9/30 12:39
 * 订单 操作类
 */

public class OrderUtil {
    private Context context;

    public OrderUtil(Context context) {
        this.context = context;
    }

    /**
     * 订单列表 右上角 交易状态
     * order_state 订单状态
     * pay_state 支付状态
     * shipping_status 发货状态
     */
    public String getLabState(int order_state, int pay_state, int shipping_status, boolean isVirtual) {
        if (order_state == 2) { //订单已取消
            return context.getString(R.string.been_cancel);
        }
        if (order_state == 3) { //退货中
            return context.getString(R.string.order_return_goods);
        }
        if (order_state == 4) { //退款中
            return context.getString(R.string.order_return_goods);
        }
        if (order_state == 5) { //订单关闭
            return context.getString(R.string.order_transaction_closed);
        }
        if (pay_state == 3) { //已退款
            return context.getString(R.string.order_refund);
        }
        if (pay_state == 2) { //已付款
            if (shipping_status == 2 || isVirtual) {
                return context.getString(R.string.order_complete);//已收货
            }
            if (shipping_status == 0 || shipping_status == 3) { //待发货
                return context.getString(R.string.order_not_shipped);
            }
            if (shipping_status == 1) { //已发货
                return context.getString(R.string.order_been_shipped);
            }
        }
        if (pay_state == 0 || pay_state == 1) { //未支付
            return context.getString(R.string.pay_state);
        }
        return "";
    }
}
