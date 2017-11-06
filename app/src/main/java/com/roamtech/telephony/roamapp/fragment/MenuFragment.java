package com.roamtech.telephony.roamapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.AboutActivity;
import com.roamtech.telephony.roamapp.activity.ApnSettingActivity;
import com.roamtech.telephony.roamapp.activity.CallTransferActivity;
import com.roamtech.telephony.roamapp.activity.CouponActivity;
import com.roamtech.telephony.roamapp.activity.GlobalNetCardActivity;
import com.roamtech.telephony.roamapp.activity.LMBAOIndexActivity;
import com.roamtech.telephony.roamapp.activity.LMBaoLinkagePagerActivity;
import com.roamtech.telephony.roamapp.activity.MainNewActivity;
import com.roamtech.telephony.roamapp.activity.MyRoamNumberActivity;
import com.roamtech.telephony.roamapp.activity.OrderListNewActivity;
import com.roamtech.telephony.roamapp.activity.RedeemCodeActivity;
import com.roamtech.telephony.roamapp.activity.SettingActivity;
import com.roamtech.telephony.roamapp.activity.UserInfoActivity;
import com.roamtech.telephony.roamapp.activity.function.RoamBoxFunction;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.BaseFragment;
import com.roamtech.telephony.roamapp.bean.CommonRoamBox;
import com.roamtech.telephony.roamapp.bean.LoginInfo;
import com.roamtech.telephony.roamapp.bean.TouchRDO;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.UriHelper;
import com.will.common.tool.PackageTool;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONObject;

import java.util.Map;


public class MenuFragment extends BaseFragment {
    /**
     * 用户名
     */
    private TextView tvUserName;
    private ImageView userPhoto;
    private TextView tvGlobalCard;
    private TextView tvRoamnumber;
    private TextView tvSetting;
    private TextView tvAbout;
    private TextView tvCardCoupon;
    private TextView tvOrder;
    private TextView tvRedeemCode;
    private MainNewActivity mActivity;
    private TextView tvSetApn;
    private TextView tvSetTransfer;
    private TextView tvAppVersion;
    private TextView tv_lmb;
    private RoamBoxFunction roamBoxFunction;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_menu;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mActivity = (MainNewActivity) getBaseActivity();

        tvUserName = (TextView) findViewById(R.id.tv_username);
        userPhoto = (ImageView) findViewById(R.id.id_circle_image);
        userPhoto.setOnClickListener(this);

        tvGlobalCard = (TextView) findViewById(R.id.tv_globalCard);
        tvRedeemCode = (TextView) findViewById(R.id.tv_redeem_code);
        tvSetting = (TextView) findViewById(R.id.tv_setting);
        tvSetting.setOnClickListener(this);
        tvAbout = (TextView) findViewById(R.id.tv_about);
        tvAbout.setOnClickListener(this);

        tvCardCoupon = (TextView) findViewById(R.id.tv_cardCoupon);
        tvOrder = (TextView) findViewById(R.id.tv_order);
        tvSetApn = (TextView) findViewById(R.id.tv_setApn);
        tvSetTransfer = (TextView) findViewById(R.id.tv_setTransfer);
        tvRoamnumber = (TextView) findViewById(R.id.tv_roamnumber);
        tvAppVersion = (TextView) findViewById(R.id.tv_appversion);
        tv_lmb = (TextView) findViewById(R.id.tv_lmb);
        tvAppVersion.setText(PackageTool.getVersionCode(getActivity()));
        roamBoxFunction = new RoamBoxFunction(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        LoginInfo loginInfo = getBaseActivity().getLoginInfo();
        if (loginInfo != null) {
            if (loginInfo.getUser_photo() == null || "".equals(loginInfo.getUser_photo())) {
                userPhoto.setImageResource(R.drawable.nav_user_default);
            } else {
                userPhoto.setImageURI(UriHelper.obtainUri(loginInfo.getUser_photo()));
            }
            tvUserName.setText(loginInfo.getPhone());
        }
        tvAppVersion.setText(String.format("V%s", getBaseActivity().getVersionName()));
    }

    @Override
    public void setListener() {
        super.setListener();
        tvGlobalCard.setOnClickListener(this);
        tvCardCoupon.setOnClickListener(this);
        tvOrder.setOnClickListener(this);
        tvSetTransfer.setOnClickListener(this);
        tvSetApn.setOnClickListener(this);
        tvRoamnumber.setOnClickListener(this);
        //tv_wifi.setOnClickListener(this);
        tv_lmb.setOnClickListener(this);
        tvRedeemCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        switch (v.getId()) {
            case R.id.id_circle_image:
                mActivity.toActivity(UserInfoActivity.class, null);
                break;
            case R.id.tv_globalCard:
                mActivity.toActivity(GlobalNetCardActivity.class, null);
                break;
            case R.id.tv_setting:
                mActivity.toActivity(SettingActivity.class, null);
                break;
            case R.id.tv_about:
                mActivity.toActivity(AboutActivity.class, null);
                break;
            case R.id.tv_cardCoupon:
                mActivity.toActivity(CouponActivity.class, null);
                break;
            case R.id.tv_setApn:
                mActivity.toActivity(ApnSettingActivity.class, null);
                break;
            case R.id.tv_setTransfer:
                mActivity.toActivity(CallTransferActivity.class, null);
                break;
            case R.id.tv_roamnumber:
                mActivity.toActivity(MyRoamNumberActivity.class, null);
                break;
            case R.id.tv_order:
                mActivity.toActivity(OrderListNewActivity.class, null);
                break;
            case R.id.tv_redeem_code:
                mActivity.toActivity(RedeemCodeActivity.class, null);
                break;
            case R.id.tv_lmb:
                //查看是否有未绑定成功的络漫宝
                roamBoxFunction.bindRoamBox(getActivity(), getBaseActivity().getAuthJSONObject());
                if (!RoamApplication.RoamBoxList.isEmpty()) {
                    mActivity.toActivity(LMBaoLinkagePagerActivity.class, null);
                } else {
                    mActivity.toActivity(LMBAOIndexActivity.class, null);
                }
                break;
        }
    }
}
