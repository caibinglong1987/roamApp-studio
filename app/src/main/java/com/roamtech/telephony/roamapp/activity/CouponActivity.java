package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.EleEvoucherAdapter;
import com.roamtech.telephony.roamapp.adapter.FavEvoucherAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.base.ServicePackageCallback;
import com.roamtech.telephony.roamapp.bean.Evoucher;
import com.roamtech.telephony.roamapp.bean.EvoucherRDO;
import com.roamtech.telephony.roamapp.bean.ServicePackage;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.bean.VoiceTalk;
import com.roamtech.telephony.roamapp.dialog.RechargeDialog;
import com.roamtech.telephony.roamapp.dialog.ImageViewDialog;
import com.roamtech.telephony.roamapp.enums.LoadingState;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.view.EmptyView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.roamtech.telephony.roamapp.enums.LoadingState.FAILED;
import static com.roamtech.telephony.roamapp.enums.LoadingState.LOADING;
import static com.roamtech.telephony.roamapp.enums.LoadingState.SESSION_TIME_OUT;


public class CouponActivity extends BaseActivity implements ExpandableListView.OnChildClickListener {
    private EleEvoucherAdapter mEleAdapter;
    private FavEvoucherAdapter mFavAdapter;
    private ExpandableListView mEleListView;
    private ExpandableListView mFavListView;
    private EmptyView mEleEmptyView;
    private RadioGroup mRadioGroup;
    private LinearLayout layoutNoTicket;
    private List<String> mFavTypes;
    private List<String> mEleTypes;
    private Map<String, List<Evoucher>> mFavMap;
    private Map<String, List<Evoucher>> mEleMap;

    @Override
    public int getLayoutId() {
        return R.layout.activity_coupon;
    }

    @Override
    public void initData() {
        mFavTypes = new ArrayList<>();
        mEleTypes = new ArrayList<>();
        mFavMap = new HashMap<>();
        mEleMap = new HashMap<>();
        mFavAdapter = new FavEvoucherAdapter(this, null, null);
        mEleAdapter = new EleEvoucherAdapter(this, null, null);
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        mRadioGroup = (RadioGroup) findViewById(R.id.rg_coupon);
        mFavListView = (ExpandableListView) findViewById(R.id.lv_fav_coupon);
        mEleListView = (ExpandableListView) findViewById(R.id.lv_ele_coupon);
        mEleEmptyView = (EmptyView) findViewById(R.id.eleEmptyView);
        layoutNoTicket = (LinearLayout) findViewById(R.id.layout_no_ticket);
        mEleListView.setAdapter(mEleAdapter);
        mFavListView.setAdapter(mFavAdapter);
        float scale = getResources().getDisplayMetrics().density;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int left = (int) (width - 38 * scale);
        int right = (int) (width - 8 * scale);
        //mEleListView.setIndicatorBounds(left, right);
       // mFavListView.setIndicatorBoundsRelative(left, right);
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mFavListView.setIndicatorBounds(left, right);
            mEleListView.setIndicatorBounds(left, right);
        } else {
            mFavListView.setIndicatorBoundsRelative(left, right);
            mEleListView.setIndicatorBoundsRelative(left, right);
        }
        mEleEmptyView.bindViewAndLoadListener(mEleEmptyView,
                LOADING, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        requestEleCoupon();
                    }
                });
        requestEleCoupon();
    }

    private void requestEleCoupon() {
        LinphoneActivity.instance().requestAllTrafficvoice(new ServicePackageCallback() {
            @Override
            public void handle(List<ServicePackage> sps, VoiceTalk vt) {
                List<Evoucher> evoucherList = new ArrayList<Evoucher>();
                for (ServicePackage servicePackage : sps) {
                    if (servicePackage.getType() != null && servicePackage.getType() == 2) {
                        Evoucher evoucher = new Evoucher();
                        evoucher.setName(getString(R.string.ele_flow));
                        evoucher.setAreaName(servicePackage.getAreaname());
                        evoucher.setStartTime(servicePackage.getStartTime());
                        evoucher.setEndTime(servicePackage.getEndTime());
                        evoucher.setOrderid(servicePackage.getOrderId());
                        int used = servicePackage.getSimids() == null ? 0 : servicePackage.getSimids().size();
                        int unused = servicePackage.getQuantity() - used;
                        for (int i = 0; i < unused; i++) {
                            evoucherList.add((Evoucher) evoucher.clone());
                        }
                        evoucher.setUsedTime((Date) evoucher.getStartTime().clone());
                        for (int j = 0; j < used; j++) {
                            evoucherList.add((Evoucher) evoucher.clone());
                        }
                    }
                }
                if (evoucherList.size() > 0) {
                    mEleTypes.add(getString(R.string.ele_flow));
                    mEleMap.put(getString(R.string.ele_flow), evoucherList);
                }
                requestCoupon();
            }
        }, true);
    }

    private void requestCoupon() {
        OkHttpUtil.postJsonRequest(Constant.EVOUCHER, getAuthJSONObject(), hashCode(), new OKCallback<EvoucherRDO>(new TypeToken<UCResponse<EvoucherRDO>>() {
        }) {
            @Override
            public void onResponse(int statuscode, UCResponse<EvoucherRDO> ucResponse) {
                if (isSucccess()) {
                    List<Evoucher> evouchers = ucResponse.getAttributes().getEvouchers(); //ucResponse.getResultData("evouchers", new TypeToken<List<Evoucher>>() {
                    //});
                    filterCoupon(evouchers);

                } else if (isSessionTimeout()) {
                    mEleEmptyView.setState(SESSION_TIME_OUT);
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                } else {
                    mEleEmptyView.setState(FAILED);
                }
            }

            @Override
            public void onFailure(IOException e) {
                mEleEmptyView.setState(LoadingState.getErrorState(e));
            }
        });
    }

    private void filterCoupon(List<Evoucher> data) {
        mEleEmptyView.setState(LoadingState.SUCCESS);
        if (data != null && data.size() > 0) {
            List<Evoucher> eleOther = new ArrayList<>();
            List<Evoucher> favOffline = new ArrayList<>();
            List<Evoucher> favOnline = new ArrayList<>();
            for (Evoucher evoucher : data) {
                switch (evoucher.getLocation()) {
                    case "offlinecoupon":
                        favOffline.add(evoucher);
                        break;
                    case "onlinecoupon":
                        favOnline.add(evoucher);
                        break;
                    case "member":
                        eleOther.add(evoucher);
                        break;
                    default:
                        eleOther.add(evoucher);
                        break;
                }
            }
            if (eleOther.size() > 0) {
                mEleTypes.add(getString(R.string.ele_other));
                mEleMap.put(getString(R.string.ele_other), eleOther);
            }
            if (favOffline.size() > 0) {
                mFavTypes.add(getString(R.string.fav_offline));
                mFavMap.put(getString(R.string.fav_offline), favOffline);
            }
            if (favOnline.size() > 0) {
                mFavTypes.add(getString(R.string.fav_online));
                mFavMap.put(getString(R.string.fav_online), favOnline);
            }
        }
        if (mEleTypes.size() == 0) {
            layoutNoTicket.setVisibility(View.VISIBLE);
        } else {
            layoutNoTicket.setVisibility(View.GONE);
        }
        mEleAdapter.refresh(mEleTypes, mEleMap);
        mFavAdapter.refresh(mFavTypes, mFavMap);
        int groupCount = mEleListView.getExpandableListAdapter().getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            mEleListView.expandGroup(i);
        }
        groupCount = mFavListView.getExpandableListAdapter().getGroupCount();
        for (int i = 0; i < groupCount; i++) {
            mFavListView.expandGroup(i);
        }
    }

    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        mFavListView.setOnChildClickListener(this);
        mEleListView.setOnChildClickListener(this);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                View eleParent = (View) mEleListView.getParent();
                View favParent = (View) mFavListView.getParent();
                if (checkedId == R.id.rb_ele) {
                    eleParent.setVisibility(View.VISIBLE);
                    favParent.setVisibility(View.INVISIBLE);
                } else {
                    eleParent.setVisibility(View.INVISIBLE);
                    favParent.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        if (parent == mFavListView) {
            Evoucher evoucher = mFavAdapter.getChild(groupPosition, childPosition);
            if (evoucher.getLocation().equals("offlinecoupon")) {
                new ImageViewDialog(this, evoucher).show();
            }
        } else if (parent == mEleListView) {
            Evoucher evoucher = mEleAdapter.getChild(groupPosition, childPosition);
            if ("member".equals(evoucher.getLocation())) {
                new ImageViewDialog(this, evoucher).show();
            } else {
                new RechargeDialog(this, evoucher, null).show();
            }
        }
        return true;
    }
}
