package com.roamtech.telephony.roamapp.bean;

import com.roamtech.telephony.roamapp.db.model.AddressModel;
import com.roamtech.telephony.roamapp.db.model.PaymentDBModel;
import com.roamtech.telephony.roamapp.db.model.Prd_categoryDBModel;
import com.roamtech.telephony.roamapp.db.model.ProductDBModel;
import com.roamtech.telephony.roamapp.db.model.ShippingDBModel;

import java.util.List;

/**
 * Created by long on 2016/9/26 13:30
 */

public class Result<T> {
    public List<OrderBean> orders; //获取订单
    public List<T> prdbrands; //
    public List<ProductDBModel> products; //商品列表
    public List<PaymentDBModel> payments;// 支付方式列表
    public List<ShippingDBModel> shippings;//配送方式列表
    public List<Prd_categoryDBModel> prdcategorys;//获取产品类目
    public List<AddressModel> addresses;//获取收货地址
    //public List<CallHistoryDBModel> historyList;
    public List<CallMessageBean> historyList;
    public T data;
}
