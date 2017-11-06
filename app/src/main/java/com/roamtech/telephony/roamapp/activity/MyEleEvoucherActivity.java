package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.MyEvoucherAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.bean.GlobalCard;
import com.roamtech.telephony.roamapp.bean.ServicePackage;
import com.roamtech.telephony.roamapp.bean.ServiceRDO;
import com.roamtech.telephony.roamapp.bean.SingleServiceRDO;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.enums.LoadingState;
import com.roamtech.telephony.roamapp.event.EventAddCard;
import com.roamtech.telephony.roamapp.event.EventBindTraffic;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.view.EmptyView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.roamtech.telephony.roamapp.enums.LoadingState.FAILED;
import static com.roamtech.telephony.roamapp.enums.LoadingState.LOADING;
import static com.roamtech.telephony.roamapp.enums.LoadingState.SESSION_TIME_OUT;


public class MyEleEvoucherActivity extends BaseActivity {
    private EmptyView mEmptyView;
    private MyEvoucherAdapter mAdapter;
    private ListView mListView;
    private GlobalCard mGlobalCard;

    @Override
    public void initData() {
        super.initData();
        mGlobalCard = (GlobalCard) getBundle().getSerializable(GlobalCard.class.getName());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_eleevoucher;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        mListView = (ListView) findViewById(R.id.lv_globalcard);
        mAdapter = new MyEvoucherAdapter(this, null,mGlobalCard);
        mListView.setAdapter(mAdapter);
        mEmptyView = (EmptyView) findViewById(R.id.id_empty_view);
        mEmptyView.bindViewAndLoadListener(mEmptyView,
                LOADING, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        requestTrafficvoice();
                    }
                });
        requestTrafficvoice();
    }

    private void requestTrafficvoice() {
        JSONObject json = getAuthJSONObject();
        OkHttpUtil.postJsonRequest(Constant.ALLTRAFFICVOICE, json, hashCode(), new OKCallback<ServiceRDO>(new TypeToken<UCResponse<ServiceRDO>>(){}) {
            @Override
            public void onResponse(int statuscode, @Nullable UCResponse<ServiceRDO> ucResponse) {
                List<ServicePackage> unBindPackages = new ArrayList<>();
                if (isSucccess()) {
                    List<ServicePackage> packages = ucResponse.getAttributes().getServicePackages(); //ucResponse.getResultData("servicepackages", new TypeToken<List<ServicePackage>>() {
                    //});
                    if (packages != null && packages.size() > 0) {
                        for (ServicePackage servicePackage : packages) {
                            Integer type = servicePackage.getType();
                            //type==2代表是流量卡
                            if (type != null && type == 2) {
                                unBindPackages.add(servicePackage);
                            }
                        }
                        mAdapter.refreash(unBindPackages);
                    }
                    mEmptyView.setState(LoadingState.getStateByRefreshData(unBindPackages));
                } else if (isSessionTimeout()) {
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                } else {
                    mEmptyView.setState(FAILED);
                }
            }

            @Override
            public void onFailure(IOException e) {
                mEmptyView.setState(LoadingState.getErrorState(e));
            }
        });

    }

    private void bindCardCardtraffic(Long orderdetailid) {
        showDialog("绑定...");
        JSONObject json = getAuthJSONObject();
        try {
            json.put("orderdetailid", orderdetailid);
            json.put("simid", mGlobalCard.getIccid());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtil.postJsonRequest(Constant.CARDTRAFFIC_BIND, json, hashCode(), new OKCallback<SingleServiceRDO>(new TypeToken<UCResponse<SingleServiceRDO>>(){}) {
            @Override
            public void onResponse(int statuscode, @Nullable UCResponse<SingleServiceRDO> ucResponse) {
//                if(ucResponse!=null){
//                    showToast(ucResponse.getErrorInfo());
//                }
                dismissDialog();
                if (isSucccess()) {
                    ServicePackage servicePackage = ucResponse.getAttributes().getServicePackage(); //ucResponse.getResultData("servicepackage", new TypeToken<ServicePackage>() {
                    //});
                    showToast("全球流量券使用成功");
                    EventBus.getDefault().post(new EventBindTraffic(servicePackage));
                    EventBus.getDefault().post(new EventAddCard("套餐发生改变也需要刷新哦"));
                    finish();
                } else if (isSessionTimeout()) {
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                }
            }

            @Override
            public void onFailure(IOException e) {
                dismissDialog();
                showToast(LoadingState.getErrorState(e).getText());
            }
        });
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        ServicePackage servicePackage=mAdapter.getItem(position);
        if(hasBind(servicePackage)){
            showToast("全球流量券使用失败");
        }else {
            bindCardCardtraffic(mAdapter.getItem(position).getOrderdetailId());
        }
    }
    /**
     * 是否被绑定了
     * quantity是包含的流量包的数量， sims小于quantity时才是有效的
     *
     * @param servicePackage
     * @return
     */
    private boolean hasBind(ServicePackage servicePackage) {
        Set<String> sims = servicePackage.getSimids();
        if (sims != null && sims.size() > 0) {
            if(servicePackage.getQuantity() == sims.size()){
                return true;
            }else if(sims.contains(mGlobalCard.getIccid())){
                return true;
            }
        }
        return false;
    }
}
