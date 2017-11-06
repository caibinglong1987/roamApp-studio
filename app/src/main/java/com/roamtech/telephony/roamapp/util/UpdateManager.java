package com.roamtech.telephony.roamapp.util;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.activity.function.Category;
import com.roamtech.telephony.roamapp.activity.function.FunOrder;
import com.roamtech.telephony.roamapp.activity.function.GetPayment;
import com.roamtech.telephony.roamapp.activity.function.HomePager;
import com.roamtech.telephony.roamapp.activity.function.Product;
import com.roamtech.telephony.roamapp.activity.function.Shipping;
import com.roamtech.telephony.roamapp.application.AppInterfaceImpl;
import com.roamtech.telephony.roamapp.bean.CityBean;
import com.roamtech.telephony.roamapp.bean.CommonModel;
import com.roamtech.telephony.roamapp.bean.CommonParseModel;
import com.roamtech.telephony.roamapp.bean.HomePageRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.db.model.AddressDBModel;
import com.roamtech.telephony.roamapp.db.model.AddressModel;
import com.roamtech.telephony.roamapp.db.model.HomePageDBModel;
import com.roamtech.telephony.roamapp.db.model.PaymentDBModel;
import com.roamtech.telephony.roamapp.db.model.Prd_categoryDBModel;
import com.roamtech.telephony.roamapp.db.model.ProductDBModel;
import com.roamtech.telephony.roamapp.db.model.ShippingDBModel;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.roamtech.telephony.roamapp.web.HttpFunction.isSuc;

/**
 * Created by long
 * on 2016/9/26 14:07
 * 更新数据 管理
 */

public class UpdateManager {
    private Context context;
    private Handler handler;

    public UpdateManager(Context context) {
        this.context = context;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    /**
     * 一天获取一次数据 保存到 内存
     */
    public void getDataToCache(JSONObject loginMap) {
        getProductList(loginMap);
        new GetPayment(context).getPaymentWay(loginMap, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                CommonParseModel<CommonModel> dataModel = JsonUtil.fromJson(response, new TypeToken<CommonParseModel<CommonModel>>() {
                }.getType());
                if (dataModel != null) {
                    String code = dataModel.error_no;
                    if (isSuc(code)) {
                        AppInterfaceImpl.paymentCommon.deleteAll(PaymentDBModel.class);
                        for (int i = 0; i < dataModel.result.payments.size(); i++) {
                            AppInterfaceImpl.paymentCommon.add((PaymentDBModel) dataModel.result.payments.get(i));
                        }
                    }
                }
            }
        });

        new Shipping(context).getShipList(loginMap, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                CommonParseModel<CommonModel> dataModel = JsonUtil.fromJson(response, new TypeToken<CommonParseModel<CommonModel>>() {
                }.getType());
                if (dataModel != null) {
                    String code = dataModel.error_no;
                    if (isSuc(code)) {
                        AppInterfaceImpl.shipCommon.deleteAll(ShippingDBModel.class);
                        for (int i = 0; i < dataModel.result.shippings.size(); i++) {
                            AppInterfaceImpl.shipCommon.add((ShippingDBModel) dataModel.result.shippings.get(i));
                        }
                    }
                }
            }
        });

        new Category(context).getCategoryList(loginMap, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                CommonParseModel<CommonModel> dataModel = JsonUtil.fromJson(response, new TypeToken<CommonParseModel<CommonModel>>() {
                }.getType());
                if (dataModel != null) {
                    String code = dataModel.error_no;
                    if (isSuc(code)) {
                        AppInterfaceImpl.categoryCommon.deleteAll(Prd_categoryDBModel.class);
                        for (int i = 0; i < dataModel.result.prdcategorys.size(); i++) {
                            AppInterfaceImpl.categoryCommon.add((Prd_categoryDBModel) dataModel.result.prdcategorys.get(i));
                        }
                    }
                }
            }

            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }
        });
        getNewAddressList(loginMap);
    }

    /**
     * 获取 全新的商品信息
     *
     * @param loginMap
     */
    public void getProductList(JSONObject loginMap) {
        new Product(context).getProductList(loginMap, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                CommonParseModel<CommonModel> dataModel = JsonUtil.fromJson(response, new TypeToken<CommonParseModel<CommonModel>>() {
                }.getType());
                if (dataModel != null) {
                    String code = dataModel.error_no;
                    if (isSuc(code)) {
                        Log.e("database--->", "");
                        AppInterfaceImpl.productCommon.deleteAll(ProductDBModel.class);
                        for (int i = 0; i < dataModel.result.products.size(); i++) {
                            AppInterfaceImpl.productCommon.add((ProductDBModel) dataModel.result.products.get(i));
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取 用户收货 地址 判断本地是否已经存在数据
     * 验证单个 地址是否存在
     *
     * @param jsonUser jsonUser
     */
    public AddressDBModel getUserAddressById(JSONObject jsonUser, int AddressId) {
        AddressDBModel model = AppInterfaceImpl.addressCommon.queryItem(AddressId);
        if (model == null) {
            getNewAddressList(jsonUser);
        }
        return model;
    }

    /**
     * 获取 用户收货地址
     *
     * @param jsonUser json user
     */
    public void getUserAddress(JSONObject jsonUser) {
        List<AddressDBModel> modelList = AppInterfaceImpl.addressCommon.queryAll();
        if (modelList == null || modelList.size() == 0) {
            getNewAddressList(jsonUser);
        }
    }

    /**
     * 获取收货地址
     *
     * @param loginMap 登录信息
     */
    public void getNewAddressList(JSONObject loginMap) {
        try {
            loginMap.put("valid_addr", "false");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new FunOrder(context).getAddress(loginMap, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                CommonModel model = JsonUtil.fromJson(response, CommonModel.class);
                if (model != null) {
                    if (isSuc(model.error_no) && model.result != null && model.result.addresses != null) {
                        List<AddressDBModel> newAddressList = new ArrayList<>();
                        AddressDBModel addressDBModel;
                        List<AddressModel> addressModelList = model.result.addresses;
                        for (int i = 0; i < addressModelList.size(); i++) {
                            addressDBModel = new AddressDBModel();
                            addressDBModel.address = addressModelList.get(i).address;
                            addressDBModel.id = addressModelList.get(i).id;
                            addressDBModel.mobile = addressModelList.get(i).mobile;
                            addressDBModel.userid = addressModelList.get(i).userid;
                            addressDBModel.zipcode = addressModelList.get(i).zipcode;
                            List<CityBean> cityBeenList = addressModelList.get(i).cities;
                            addressDBModel.consignee = addressModelList.get(i).consignee;
                            for (int n = 0; n < cityBeenList.size(); n++) {
                                if (String.valueOf(cityBeenList.get(n).id).equals(addressModelList.get(i).province)) {
                                    addressDBModel.province = cityBeenList.get(n).name;
                                }
                                if (String.valueOf(cityBeenList.get(n).id).equals(addressModelList.get(i).city)) {
                                    addressDBModel.city = cityBeenList.get(n).name;
                                }
                                if (String.valueOf(cityBeenList.get(n).id).equals(addressModelList.get(i).district)) {
                                    addressDBModel.district = cityBeenList.get(n).name;
                                }
                            }
                            newAddressList.add(addressDBModel);
                        }
                        AppInterfaceImpl.addressCommon.deleteAll(AddressDBModel.class);
                        if (newAddressList.size() > 0) {
                            for (int k = 0; k < newAddressList.size(); k++) {
                                AppInterfaceImpl.addressCommon.add(newAddressList.get(k));
                            }
                        }
                    }
                }
            }
        });
    }

    /**
     * 获取 单个 商品信息
     *
     * @param jsonUser json user
     * @param proId    pro id
     * @return ProductDBModel
     */
    public ProductDBModel getProductDataById(JSONObject jsonUser, int proId) {
        ProductDBModel model = AppInterfaceImpl.productCommon.queryItem(proId);
        if (model == null) {
            getProductList(jsonUser);
        }
        return model;
    }

    /**
     * 获取 首页 数据
     *
     * @param jsonUser json user
     */
    public List<HomePageDBModel> getHomePageList(JSONObject jsonUser) {
        try {
            jsonUser.put("type", "1");
            jsonUser.put("location", "hotactivities");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        List<HomePageDBModel> listHomePage = AppInterfaceImpl.homeCommon.queryAll();
        if ((listHomePage == null || listHomePage.size() == 0)) {
            new HomePager(context).getListBanner(jsonUser, hashCode(), new HttpBusinessCallback() {
                @Override
                public void onFailure(Map<String, ?> errorMap) {
                    super.onFailure(errorMap);
                }

                @Override
                public void onSuccess(String response) {
                    if (response != null) {
                        UCResponse<HomePageRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<HomePageRDO>>() {
                        }.getType());
                        if (result != null && result.getAttributes() != null) {
                            AppInterfaceImpl.homeCommon.deleteAll(HomePageDBModel.class);
                            List<HomePageDBModel> homepages = result.getAttributes().homepages;
                            for (HomePageDBModel item : homepages) {
                                AppInterfaceImpl.homeCommon.add(item);
                            }
                            if (handler != null) {
                                handler.obtainMessage(MsgType.MSG_GET_HOME_PAGE_SUCCESS, homepages).sendToTarget();
                            }
                        }
                    }
                }
            });
        }
        return listHomePage;
    }
}
