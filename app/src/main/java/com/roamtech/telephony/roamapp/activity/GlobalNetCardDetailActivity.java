package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.Parameter.KeyValue;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.bean.GlobalCard;
import com.roamtech.telephony.roamapp.bean.ServicePackage;
import com.roamtech.telephony.roamapp.dialog.UsePackageOptionDialog;
import com.roamtech.telephony.roamapp.enums.MainTab;
import com.roamtech.telephony.roamapp.event.EventBindTraffic;
import com.roamtech.telephony.roamapp.util.Constant;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class GlobalNetCardDetailActivity extends BaseActivity {
    //private TextView tvSetnetCard;
    private TextView tvBottomUsePackage;
    private TextView tvTipPackageDescribe;
    private GlobalCard mGlobalCard;
    private List<ServicePackage> mPackages;
    private int remainDay;
    private TextView tvCardid;
    private TextView tvFirstPackage;
    private TextView tvFirstState;
    private ServicePackage mFirstServicePackage;
    private LinearLayout mLayoutPackage;


    protected SimpleDateFormat formatterToDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    protected String formatDate(Date date) {
        return formatterToDay.format(date);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_global_netcard_detail;
    }
    private int calcRemainDays(Date endTime) {
        Date now = new Date();
        return (int)(((endTime.getTime() - now.getTime()) / 1000 + 86400) / 86400);
    }
    @Override
    public void initData() {
        super.initData();
        mGlobalCard = (GlobalCard) getBundle().getSerializable(GlobalCard.class.getName());
        mPackages = mGlobalCard.getServicePackages();
        if(mPackages == null) {
            mPackages = new ArrayList<ServicePackage>();
        }
        mFirstServicePackage = sortPackage();
        if (mFirstServicePackage != null) {
            Date endTime = mFirstServicePackage.getEndTime();
            //四舍五入
            remainDay = calcRemainDays(endTime);//(int) (0.5+(endTime.getTime() - System.currentTimeMillis()) / 1000 / 60 / 60 / 24);
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        EventBus.getDefault().register(this);
        //tvSetnetCard = (TextView) findViewById(R.id.tv_setnetcard);
        tvBottomUsePackage = (TextView) findViewById(R.id.tv_use_package);
        tvCardid = (TextView) findViewById(R.id.tv_cardid);
        tvTipPackageDescribe = (TextView) findViewById(R.id.tv_tip_packageDescribe);
        tvFirstPackage = (TextView) findViewById(R.id.tv_first_package);
        tvFirstState = (TextView) findViewById(R.id.tv_state);
        mLayoutPackage= (LinearLayout) findViewById(R.id.layout_package);
        // requestData();
        bindViewData();
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bindTrafficSucccess(EventBindTraffic event){
        mPackages.add(event.getServicePackage());
        Collections.sort(mPackages);
        if(mFirstServicePackage == null) {
            mFirstServicePackage = mPackages.get(0);//sortPackage();
            if (mFirstServicePackage != null) {
                Date endTime = mFirstServicePackage.getEndTime();
                //四舍五入
                remainDay = calcRemainDays(endTime);//(int) (0.5 + (endTime.getTime() - System.currentTimeMillis()) / 1000 / 60 / 60 / 24);
                tvTipPackageDescribe.setText(String.format(Locale.CHINA, "剩余使用时间:%d天", remainDay));
            } else {
                tvTipPackageDescribe.setText("暂未使用任何套餐");
            }
        }
        showServicePackage();
    }
    private void bindViewData() {
        tvCardid.setText(mGlobalCard.getIccid());
        if (mFirstServicePackage == null) {
            tvTipPackageDescribe.setText("暂未使用任何套餐");
        } else {
            tvTipPackageDescribe.setText(String.format(Locale.CHINA, "剩余使用时间:%d天", remainDay));
        }
        showServicePackage();
    }
    private void showServicePackage(){
        mLayoutPackage.removeAllViews();
        if(mPackages!=null&&mPackages.size()!=0){
            ServicePackage first=mPackages.get(0);
            tvFirstPackage.setText(String.format("%s [%s至%s] ",first.getAreaname(),formatDate(first.getStartTime()),formatDate(first.getEndTime())));
            tvFirstState.setText(isInUse(first)?"使用中":"待生效");
            for(int index=1;index<mPackages.size();index++){
                ServicePackage sp=mPackages.get(index);
                LinearLayout itemPackage= (LinearLayout) LayoutInflater.from(this).inflate(R.layout.item_package,mLayoutPackage,false);
                TextView tvDate= (TextView) itemPackage.getChildAt(0);
                tvDate.setText(String.format("%s [%s至%s] ",sp.getAreaname(),formatDate(sp.getStartTime()),formatDate(sp.getEndTime())));
                TextView tvState= (TextView) itemPackage.getChildAt(1);
                tvState.setText(isInUse(sp)?"使用中":"待生效");
                mLayoutPackage.addView(itemPackage);
            }
        }else{
            tvFirstPackage.setText("暂未使用任何套餐");
            tvFirstState.setText("");
        }
    }
    /**
     * 将正在使用的排行到第一条
     *
     * @return 是否有正在使用的套餐
     */
    private ServicePackage sortPackage() {
        if (mPackages != null && mPackages.size() > 0) {
            for (int index = 0; index < mPackages.size(); index++) {
                ServicePackage temp = mPackages.get(index);
                if (isInUse(temp)) {
                    //使用中的展示在第一条
                    mPackages.set(0, temp);
                    return temp;
                }
            }
        }
        return null;
    }
    private boolean isInUse(ServicePackage servicePackage) {
        Date start = servicePackage.getStartTime();
        Date end = servicePackage.getEndTime();
        Date date = new Date();
        if (end.compareTo(date) > 0 && start.compareTo(date) < 0) {
            //使用中
            return true;
        }
        return false;
    }


    @Override
    public void setListener() {
        // TODO Auto-generated method stub
        super.setListener();
        tvBottomUsePackage.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        /*if (v == tvSetnetCard) {
            Bundle bundle=new Bundle();
            bundle.putSerializable(GlobalCard.class.getName(),mGlobalCard);
            toActivity(FeedbackActivity.class, bundle);
        } else*/ if (v == tvBottomUsePackage) {
            showUsePackageOption(new UsePackageOptionDialog.OnQuickOptionformClick() {
                @Override
                public void onQuickOptionClick(int id) {
                    if(id==R.id.tv_useEleCoupon){
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(GlobalCard.class.getName(), mGlobalCard);
                       toActivity(MyEleEvoucherActivity.class,bundle);
                    }else if(id==R.id.tv_buyInMall){
                        Bundle bundle=new Bundle();
                        bundle.putString(KeyValue.TAB_TARGET, KeyValue.TAB_RD_MALL);
                        toActivityClearTopWithState(LinphoneActivity.class,bundle);
                    }
                }
            });
        }
    }
    protected void showUsePackageOption(
            UsePackageOptionDialog.OnQuickOptionformClick onQuickOptionformClick) {
        UsePackageOptionDialog dialog = new UsePackageOptionDialog(this);
        dialog.setOnQuickOptionformClickListener(onQuickOptionformClick);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

}
