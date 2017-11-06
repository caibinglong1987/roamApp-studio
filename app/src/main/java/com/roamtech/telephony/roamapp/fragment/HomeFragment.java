package com.roamtech.telephony.roamapp.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.CallTransferActivity;
import com.roamtech.telephony.roamapp.activity.CaptureActivity;
import com.roamtech.telephony.roamapp.activity.CommunicationSettingActivity;
import com.roamtech.telephony.roamapp.activity.LoginActivity;
import com.roamtech.telephony.roamapp.activity.MainNewActivity;
import com.roamtech.telephony.roamapp.activity.StartActivity;
import com.roamtech.telephony.roamapp.activity.WebViewActivity;
import com.roamtech.telephony.roamapp.activity.function.HomePager;
import com.roamtech.telephony.roamapp.adapter.CommonAdapter;
import com.roamtech.telephony.roamapp.adapter.ViewHolder;
import com.roamtech.telephony.roamapp.adapter.ViewPagerViewAdapter;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.BaseFragment;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.base.ServicePackageCallback;
import com.roamtech.telephony.roamapp.base.VoiceAvailableCallback;
import com.roamtech.telephony.roamapp.bean.AppNewVersionRDO;
import com.roamtech.telephony.roamapp.bean.CredtripOpenRDO;
import com.roamtech.telephony.roamapp.bean.HomePageRDO;
import com.roamtech.telephony.roamapp.bean.ServicePackage;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.bean.VoiceNumber;
import com.roamtech.telephony.roamapp.bean.VoiceTalk;
import com.roamtech.telephony.roamapp.db.model.HomePageDBModel;
import com.roamtech.telephony.roamapp.dialog.CardDialog;
import com.roamtech.telephony.roamapp.dialog.TalkTimeDialog;
import com.roamtech.telephony.roamapp.helper.AsyBlurHelper;
import com.roamtech.telephony.roamapp.util.BitmapTools;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.JsonUtil;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.ScreenUtils;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.util.UpdateManager;
import com.roamtech.telephony.roamapp.util.UploadApp;
import com.roamtech.telephony.roamapp.util.UriHelper;
import com.roamtech.telephony.roamapp.view.CircleIndicator;
import com.roamtech.telephony.roamapp.view.RoundProgressBar;
import com.umeng.analytics.MobclickAgent;
import com.will.common.tool.PackageTool;
import com.will.common.tool.time.DateTimeTool;
import com.will.web.handle.HttpBusinessCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.mediastream.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author xincheng
 */
public class HomeFragment extends BaseFragment {
    private RoundProgressBar mFlowRateProgressUp,
            mFlowRateProgressDown;
    private ImageView ivTop;
    private ImageView ivUser;
    private ImageView ivQrcode;
    private FrameLayout rlytProgress;
    private TextView tvXiecheng;
    private TextView tvSetCommunication;

    private ViewPager mViewPager;
    private CircleIndicator mCircleIndicator;
    private View flytDetail;
    private ScrollView scrollView;
    private View flytViewPager;
    // 标记是否在动画
    private boolean isAnim = false;
    private LayoutInflater inflater;
    private TextView tvVoiceRemainTime;
    private TextView tvTrafficReaminTime;
    private TextView tvTrafficAreaName;
    private LinearLayout mLlTalkTime;
    private LinearLayout mLlFlowSurplus;
    private LinearLayout mLLActivity;
    private LinearLayout mLLOnSale;
    private TextView tv_actives, tv_on_sale, tv_on_sale_desc, tv_actives_desc;
    private List<ServicePackage> mTrafficPkgs;
    private TextView mTvRightNow;
    private TextView mTvNoAvaiableFlow;
    private VoiceTalk mVoiceAvailableTalk;
    private TextView mTvBuyExcusiveNum;
    private VoiceNumber mVoiceNumber;
    private TextView mTvExclusiveNumRemainTime;
    private RelativeLayout mRlNoExculusiveNum;
    private TextView mTvRechargeVoiceTime;
    private BitmapDrawable progressbarBg;
    //是否有专属号
    private LinearLayout mLlRemainVoiceTalk;
    private TextView mTvFlowToBeEffective;

    private ArrayList<ServicePackage> mAllBindingCardServicePage;
    private GridView grid_view_home;
    private CommonAdapter<HomePageDBModel> homeAdapter;
    private List<HomePageDBModel> listHomePage = new ArrayList<>();
    private List<HomePageDBModel> listHeadLine = new ArrayList<>();
    private UpdateManager updateManager;
    private MainNewActivity mMainActivity;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mMainActivity = (MainNewActivity) getBaseActivity();
        this.inflater = getLayoutInflater(savedInstanceState);
        ivTop = (ImageView) findViewById(R.id.iv_homebg);
        mLLActivity = (LinearLayout) findViewById(R.id.tv_activity);
        mLLOnSale = (LinearLayout) findViewById(R.id.layout_on_sale);
        tv_actives = (TextView) findViewById(R.id.tv_actives);
        tv_on_sale = (TextView) findViewById(R.id.tv_on_sale);
        tv_actives_desc = (TextView) findViewById(R.id.tv_actives_desc);
        tv_on_sale_desc = (TextView) findViewById(R.id.tv_on_sale_desc);
        ivQrcode = (ImageView) findViewById(R.id.iv_qrcode);
        ivQrcode.setOnClickListener(this);
        ivUser = (ImageView) findViewById(R.id.id_circle_image);
        ivUser.setOnClickListener(this);
        rlytProgress = (FrameLayout) findViewById(R.id.rlyt_progress);
        tvXiecheng = (TextView) findViewById(R.id.tv_xiecheng);
        grid_view_home = (GridView) findViewById(R.id.grid_view_home);
        updateManager = new UpdateManager(mMainActivity.getApplicationContext());
        updateManager.setHandler(fragmentHandler);
        initViewPager();
        flytDetail = findViewById(R.id.flyt_detail);
        flytDetail.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                // 只在 down时的时候执行 防止反复执行
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (!isAnim) {
                        mFlowRateProgressUp.startAnimTimer(100, 60, 10, 0.2f,
                                500);
                        mFlowRateProgressDown.startAnimTimer(100, 80, 10, 0.4f,
                                500);
                        tvXiecheng.performClick();
                    }
                }
                // 屏蔽掉所有向下的事件
                return true;
            }
        });
        flytViewPager = findViewById(R.id.flyt_viewPager);
        tvXiecheng.setOnClickListener(this);
        applyBlur();
        tvSetCommunication = (TextView) findViewById(R.id.tv_setCommunication);
        tvSetCommunication.setOnClickListener(this);
        mFlowRateProgressUp = (RoundProgressBar) findViewById(R.id.flowRateProgress2);
        mFlowRateProgressUp.setSweepGradientColor(Color.parseColor("#8bdF67"),
                Color.parseColor("#53E2AA"), true);
        //mFlowRateProgressUp.startAnimTimer(100, 60, 10, 1f, 500);
        mFlowRateProgressDown = (RoundProgressBar) findViewById(R.id.flowRateProgress3);
        mFlowRateProgressDown.setSweepGradientColor(Color.parseColor("#63B3E1"),
                Color.parseColor("#CA93E6"), false);
        // mFlowRateProgressDown.startAnimTimer(100, 80, 10, 1f, 500);
        //初始化进度条
        addProgressInfoView();
        homeAdapter = new CommonAdapter<HomePageDBModel>(getActivity(), listHomePage, R.layout.item_home_page) {
            @Override
            public void convert(ViewHolder helper, HomePageDBModel item, int position) {
                helper.setText(R.id.item_text, item.name);
                helper.setText(R.id.item_desc, item.description);
                helper.setImageUrlByNetworkImage(R.id.item_image, Constant.IMAGE_URL + item.logo);
            }
        };
        grid_view_home.setAdapter(homeAdapter);
        grid_view_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                if (listHomePage.get(position).name.equals("信程卡")) {
                    requestCredtripOpenAccount(listHomePage.get(position).url_title);
                } else {
                    bundle.putString("title", listHomePage.get(position).url_title);
                    bundle.putString("url", listHomePage.get(position).url);
                    bundle.putString("name", listHomePage.get(position).name);
                    mMainActivity.toActivity(WebViewActivity.class, bundle);
                }
            }
        });
        fragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    getLocationData();
                    getHomeHeadLines();
                    checkAppUpgrade();
                }
            }
        }, 4000);

        fragmentHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAdded()) {
                    updateEveryDay();
                }
            }
        }, 10 * 1000);
    }

    private ViewPagerViewAdapter mViewPagerViewAdapter;

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mCircleIndicator = (CircleIndicator) findViewById(R.id.id_circle_indicator);
        List<View> views = new ArrayList<>();
        views.add(inflater.inflate(R.layout.order_detail_one, null));
        views.add(inflater.inflate(R.layout.order_detail_two, null));
        views.add(inflater.inflate(R.layout.order_detail_three, null));
        mViewPagerViewAdapter = new ViewPagerViewAdapter(mMainActivity.getApplicationContext(), views);
        mViewPager.setAdapter(mViewPagerViewAdapter);
        mCircleIndicator.setViewPager(mViewPager, false);
    }

    private int calcRemainDays(Date endTime) {
        Date now = new Date();
        return (int) (((endTime.getTime() - now.getTime()) / 1000 + 86400) / 86400);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == tvXiecheng) {
            Bundle bundle = new Bundle();
            bundle.putString("url", Constant.HTML_XIE_CHENG);
            mMainActivity.toActivity(WebViewActivity.class, bundle);
        } else if (v == ivUser) {
            mMainActivity.getDrawerLayout().openDrawer(GravityCompat.START);
        } else if (v == ivQrcode) {
            mMainActivity.toActivity(CaptureActivity.class, null);
        } else if (v == tvSetCommunication) {
            mMainActivity.toActivity(CommunicationSettingActivity.class, null);
        } else if (v == mLLActivity && listHeadLine != null && listHeadLine.size() > 0) {
            // getBaseActivity().toActivity(FollowActivity.class, null);
            Bundle bundle = new Bundle();
            bundle.putString("title", listHeadLine.get(0).url_title);
            bundle.putString("url", listHeadLine.get(0).url);
            mMainActivity.toActivity(WebViewActivity.class, bundle);
        } else if (v == mLLOnSale && listHeadLine != null && listHeadLine.size() > 1) {
            Bundle bundle = new Bundle();
            bundle.putString("title", listHeadLine.get(1).url_title);
            bundle.putString("url", listHeadLine.get(1).url);
            mMainActivity.toActivity(WebViewActivity.class, bundle);

        } else if (v == mLlRemainVoiceTalk) {
            showTalkDialog();
        } else if (v == mTvRechargeVoiceTime) {
            mMainActivity.setSelectedMenu(R.id.layout_mall);
        } else if (v == mTvBuyExcusiveNum) {
            mMainActivity.setSelectedMenu(R.id.layout_mall);
        }//待生效的流量套餐详情
        else if (v == mTvFlowToBeEffective) {
            final CardDialog cardDialog = new CardDialog(mMainActivity.getApplicationContext(), R.style.dialog_card);
            ServicePackage sp = mAllBindingCardServicePage.get(0);
            cardDialog.show();
            Date startTime = sp.getStartTime();
            Date endTime = sp.getEndTime();
            int remian = (int) (((endTime.getTime() - startTime.getTime()) / 1000 + 86400) / 86400);
            cardDialog.setReamintime(remian);
            cardDialog.setTvStartTime(DateTimeTool.ConverToString(startTime));
            cardDialog.setTvEndTime(DateTimeTool.ConverToString(endTime));
            cardDialog.setCountryName(sp.getAreaname());
            //上网卡账号
            String simid = sp.getSimid();
            cardDialog.setTvSimsNum(simid);

            cardDialog.setOnShowHomeListener(new CardDialog.OnShowHomepageListener() {
                @Override
                public void showHome() {
                    //在首页显示
                    cardDialog.dismiss();

                }
            });
        }
        //剩余流量
        else if (v == mTvRightNow) {
            mMainActivity.setSelectedMenu(R.id.layout_mall);
        } else if (v == mLlFlowSurplus) {
            if ((mAllBindingCardServicePage != null && !mAllBindingCardServicePage.isEmpty())) {
                final CardDialog cardDialog = new CardDialog(mMainActivity.getApplicationContext(), R.style.dialog_card);
                ServicePackage sp = mAllBindingCardServicePage.get(0);
                cardDialog.show();
                final int lRemainTime = calcRemainDays(sp.getEndTime());//((sp.getEndTime().getTime() - mNow.getTime()) / 1000 + 86400) / 86400;
                cardDialog.setReamintime(lRemainTime);
                cardDialog.setTvStartTime(DateTimeTool.ConverToString(sp.getStartTime()));
                cardDialog.setTvEndTime(DateTimeTool.ConverToString(sp.getEndTime()));
                cardDialog.setCountryName(sp.getAreaname());
                //上网卡账号
                String simid = sp.getSimid();
                if (simid != null) {
                    cardDialog.setTvSimsNum(simid);
                }
                cardDialog.setOnShowHomeListener(new CardDialog.OnShowHomepageListener() {
                    @Override
                    public void showHome() {
                        //在首页显示
                        tvTrafficReaminTime.setText(lRemainTime + "");
                        cardDialog.dismiss();
                    }
                });
            }
        }

    }

    public void alphaAnimRun(final View view, final boolean isShow) {
        ObjectAnimator animator = null;
        if (isShow) {
            flytDetail.setVisibility(View.VISIBLE);
            animator = ObjectAnimator//
                    .ofFloat(view, "alpha", 2.0f, 1f)//
                    .setDuration(200);//
        } else {
            animator = ObjectAnimator//
                    .ofFloat(view, "alpha", 1, 0.2f)//
                    .setDuration(200);//
        }
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                super.onAnimationEnd(animation);
                if (!isShow) {
                    view.setVisibility(View.GONE);
                }
                isAnim = false;
            }

        });
        animator.start();
    }

    private void applyBlur() {
        // 大概30毫秒左右
        AsyBlurHelper.startBlur(ivTop, rlytProgress, "#4c000000", 4, 4, 0, new AsyBlurHelper.OnBlurListener() {
            @Override
            public void OnBlurEnd(Bitmap bitmap) {
                Bitmap blur = BitmapTools.makeRoundCorner(bitmap);
                progressbarBg = new BitmapDrawable(getResources(), blur);
                rlytProgress.setBackgroundDrawable(progressbarBg);
                // 设置viewPager高度个ScrollView一样
                ViewGroup.LayoutParams viewPagerLp = mViewPager.getLayoutParams();
                // viewPagerLp.height = scrollView.getHeight();
                mViewPager.setLayoutParams(viewPagerLp);
                bitmap.recycle();
            }
        });
    }

    private void addProgressInfoView() {
        int size = (int) (mFlowRateProgressDown.getSizeByRadiusPercent() * 2 - mFlowRateProgressDown
                .getRoundWidth());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size, size);
        lp.gravity = Gravity.CENTER;
        View progressinfo = inflater
                .inflate(R.layout.order_progress_info, null);

        //待生效的流量
        mTvFlowToBeEffective = (TextView) progressinfo.findViewById(R.id.tv_flow_to_be_effective);
        mTvFlowToBeEffective.setOnClickListener(this);
        //通话时长
        mLlTalkTime = (LinearLayout) progressinfo.findViewById(R.id.ll_talktime);
        mLlTalkTime.setOnClickListener(this);

//剩余的通话时长
        mLlRemainVoiceTalk = (LinearLayout) progressinfo.findViewById(R.id.ll_voicetalk_time);
        mLlRemainVoiceTalk.setOnClickListener(this);

        mTvRechargeVoiceTime = (TextView) progressinfo.findViewById(R.id.tv_recharge_voice_time);
        mTvRechargeVoiceTime.setOnClickListener(this);
        //流量剩余
        mLlFlowSurplus = (LinearLayout) progressinfo.findViewById(R.id.ll_flow_surplus);
        mLlFlowSurplus.setOnClickListener(this);
        mTvRightNow = (TextView) progressinfo.findViewById(R.id.tv_rightnow_buy);
        mTvRightNow.setOnClickListener(this);
        tvVoiceRemainTime = (TextView) progressinfo.findViewById(R.id.tv_voiceremaintime);
        mTvNoAvaiableFlow = (TextView) progressinfo.findViewById(R.id.tv_no_available_flow);

        tvTrafficReaminTime = (TextView) progressinfo.findViewById(R.id.tv_trafficremaintime);
        tvTrafficAreaName = (TextView) progressinfo.findViewById(R.id.tv_areaname);

        mTvExclusiveNumRemainTime = (TextView) progressinfo.findViewById(R.id.tv_exclusive_num_remain_time);
        mTvBuyExcusiveNum = (TextView) progressinfo.findViewById(R.id.tv_buy_excusive_num);
        mTvBuyExcusiveNum.setOnClickListener(this);
        mRlNoExculusiveNum = (RelativeLayout) progressinfo.findViewById(R.id.rl_no_exculusive_num);

        //获取用户可用时长和专属套餐
        if (LinphoneActivity.isInstanciated()) {
            refreshAvalilableAndExclusiveData();
            refreshAllTrafficVoiceData();
        }

        rlytProgress.addView(progressinfo, lp);
        rlytProgress.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                // 只在 down时的时候执行 防止反复执行
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // refreshTrafficVoiceData();
                }
                // 屏蔽掉所有向下的事件
                return true;
            }
        });
    }

    private void refreshAvalilableAndExclusiveData() {
        if (!LinphoneActivity.isInstanciated()) {
            mMainActivity.toActivityClearTopWithState(StartActivity.class, null);
            return;
        }
        LinphoneActivity.instance().requestVoiceAvailable(null, null, new VoiceAvailableCallback() {
            @Override
            public void handle(VoiceNumber vn, VoiceTalk vt) {
                mVoiceNumber = vn;
                Log.i("refreshexclusive", vn.toString());
                if (mVoiceNumber == null || StringUtil.isBlank(mVoiceNumber.getPhone())) {
                    mTvBuyExcusiveNum.setVisibility(View.VISIBLE);
                    mRlNoExculusiveNum.setVisibility(View.GONE);
                } else {
                    if (isAdded()) {
                        mMainActivity.setRoamPhone(mVoiceNumber.getPhone());
                    }
                    mTvBuyExcusiveNum.setVisibility(View.GONE);
                    mRlNoExculusiveNum.setVisibility(View.VISIBLE);
                    mTvExclusiveNumRemainTime.setText(String.valueOf(calcRemainDays(vn.getEndTime())));
                }
                if (vt != null) {
                    tvVoiceRemainTime.setText(String.valueOf(vt.getRemaindertime()));
                    if (0 >= vt.getRemaindertime()) {
                        mLlRemainVoiceTalk.setVisibility(View.GONE);
                        mTvRechargeVoiceTime.setVisibility(View.VISIBLE);
                    } else {
                        mLlRemainVoiceTalk.setVisibility(View.VISIBLE);
                        mTvRechargeVoiceTime.setVisibility(View.GONE);
                    }

                }

            }
        });

    }

    private void refreshAllTrafficVoiceData() {
        if (!LinphoneActivity.isInstanciated()) {
            mMainActivity.toActivityClearTopWithState(LoginActivity.class, null);
            return;
        }
        LinphoneActivity.instance().requestAllTrafficvoice(new ServicePackageCallback() {
            @Override
            public void handle(List<ServicePackage> sps, VoiceTalk vt) {
                mTrafficPkgs = LinphoneActivity.instance().getAvailTrafficPackages(sps);

                //用户可用的语音时长
                mVoiceAvailableTalk = vt;
                Long totaltime = mVoiceAvailableTalk.getTotaltime();
                Long remaintime = mVoiceAvailableTalk.getRemaindertime();

                //设置进度条
                double doublepercent = (double) remaintime / totaltime;
                int intpercent = (int) Math.ceil(doublepercent * 100);

                if (remaintime <= 0) {
                    mFlowRateProgressUp.startAnimTimer(100, 0, 10, 1f, 500);
                } else {
                    mFlowRateProgressUp.startAnimTimer(100, intpercent, 10, 1f, 500);
                }

                if (mTrafficPkgs != null && !mTrafficPkgs.isEmpty()) {
                    //排序
                    Collections.sort(mTrafficPkgs);
                    //用户所有已绑卡的套餐
                    mAllBindingCardServicePage = new ArrayList<>();
                    //未绑卡的套餐
                    ArrayList<ServicePackage> mAllNoBindCardServicePage = new ArrayList<>();
                    for (ServicePackage trafficPkg : mTrafficPkgs) {
                        //得到已绑卡的所有套餐
                        if (!(trafficPkg.getSimid() == null)) {
                            mAllBindingCardServicePage.add(trafficPkg);
                        } else if (trafficPkg.getSimid() == null) {
                            mAllNoBindCardServicePage.add(trafficPkg);
                        }
                        Log.i("traffic", mAllNoBindCardServicePage.toString());
                    }

                    if (!mAllBindingCardServicePage.isEmpty() && calcRemainDays(mAllBindingCardServicePage.get(0).getStartTime()) <= 0) {
                        ServicePackage servicePackage = mAllBindingCardServicePage.get(0);
                        int i = calcRemainDays(servicePackage.getEndTime());
                        mLlFlowSurplus.setVisibility(View.VISIBLE);
                        tvTrafficAreaName.setVisibility(View.VISIBLE);
                        mTvRightNow.setVisibility(View.GONE);
                        mTvFlowToBeEffective.setVisibility(View.GONE);
                        mTvNoAvaiableFlow.setVisibility(View.GONE);
                        tvTrafficReaminTime.setText(String.valueOf(i));
                        tvTrafficAreaName.setText(servicePackage.getAreaname());

                        //进度条
                        int endtime = calcRemainDays(servicePackage.getEndTime());
                        int starttime = calcRemainDays(servicePackage.getStartTime());
                        int totaldaynum = endtime - starttime;
                        Double remainder = servicePackage.getRemainder();
                        int remaindaynum = (int) Math.ceil(remainder);
                        int progress = 100 * remaindaynum / totaldaynum;
                        //流量
                        mFlowRateProgressDown.startAnimTimer(100, progress, 10, 1f, 500);
                        //用户待生效的套餐
                    } else if (!mAllBindingCardServicePage.isEmpty() && calcRemainDays(mAllBindingCardServicePage.get(0).getStartTime()) > 0) {
                        ServicePackage servicePackage = mAllBindingCardServicePage.get(0);
                        mFlowRateProgressDown.startAnimTimer(100, 0, 10, 1f, 500);
                        tvTrafficAreaName.setText(servicePackage.getAreaname());
                        mTvFlowToBeEffective.setVisibility(View.VISIBLE);
                        mTvFlowToBeEffective.setText(String.format(getString(R.string.traffic_effect_time), DateTimeTool.DateFormat(servicePackage.getStartTime(), DateTimeTool.FormatString)));
                        tvTrafficAreaName.setVisibility(View.VISIBLE);
                        mLlFlowSurplus.setVisibility(View.GONE);
                        mTvNoAvaiableFlow.setVisibility(View.GONE);
                        mTvRightNow.setVisibility(View.GONE);
                    } else if ((!mAllNoBindCardServicePage.isEmpty()) && mAllBindingCardServicePage.isEmpty()) {
                        //用户购买了套餐但未绑卡的情况
                        mFlowRateProgressDown.startAnimTimer(100, 0, 10, 1f, 500);
                        mTvFlowToBeEffective.setVisibility(View.GONE);
                        tvTrafficAreaName.setVisibility(View.GONE);
                        mLlFlowSurplus.setVisibility(View.GONE);
                        mTvNoAvaiableFlow.setVisibility(View.VISIBLE);
                        mTvRightNow.setVisibility(View.VISIBLE);
                    }

                } else {
                    mLlFlowSurplus.setVisibility(View.GONE);
                    tvTrafficAreaName.setVisibility(View.GONE);
                    mTvFlowToBeEffective.setVisibility(View.GONE);
                    mTvNoAvaiableFlow.setVisibility(View.VISIBLE);
                    mTvRightNow.setVisibility(View.VISIBLE);
                    mFlowRateProgressDown.startAnimTimer(100, 0, 10, 1f, 500);
                }
            }
        }, true);
    }

    /**
     * 获取该账号是否开通信程账户
     *
     * @param url_title
     */
    private void requestCredtripOpenAccount(final String url_title) {
        OkHttpUtil.postJsonRequest(Constant.CREDTRIP_OPEN, mMainActivity.getAuthJSONObject(), hashCode(), new OKCallback<CredtripOpenRDO>(new TypeToken<UCResponse<CredtripOpenRDO>>() {
        }) {
            @Override
            public void onResponse(int statusCode, @Nullable UCResponse<CredtripOpenRDO> ucResponse) {
                if (isSucccess() && ucResponse != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("title", url_title);
                    bundle.putString("html", ucResponse.getAttributes().getHtml());
                    mMainActivity.toActivity(WebViewActivity.class, bundle);
                } else if (ucResponse != null && ucResponse.getErrorNo() == 1025) {
                    //credtripAccountExistDialog("信程账户已开通");
                    Bundle bundle = new Bundle();
                    bundle.putString("title", url_title);
                    bundle.putString("url", Constant.CRED_TRIP_LOGIN);
                    mMainActivity.toActivity(WebViewActivity.class, bundle);
                }
            }

            @Override
            public void onFailure(IOException e) {

            }
        });
    }

    @Override
    public void setListener() {
        super.setListener();
        mLLActivity.setOnClickListener(this);
        mLLOnSale.setOnClickListener(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            refreshAvalilableAndExclusiveData();
            refreshAllTrafficVoiceData();
            if (progressbarBg != null && rlytProgress.getBackground() != progressbarBg) {
                MobclickAgent.reportError(mMainActivity.getApplicationContext(), "首页圆环背景异变！" + progressbarBg + "   " + rlytProgress.getBackground());
            }
            if (progressbarBg != null) {
                rlytProgress.setBackgroundDrawable(progressbarBg);
            }
        } else {
            //相当于Fragment的onPause
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LinphoneActivity.isInstanciated()) {
            refreshAvalilableAndExclusiveData();
            refreshAllTrafficVoiceData();
            if (progressbarBg != null && rlytProgress.getBackground() != progressbarBg) {
                MobclickAgent.reportError(mMainActivity.getApplicationContext(), "首页圆环背景异变！" + progressbarBg + "   " + rlytProgress.getBackground());
            }
            rlytProgress.setBackgroundDrawable(progressbarBg);
        }
        String head_url = SPreferencesTool.getInstance().getStringValue(mMainActivity.getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_headUrl);
        if (head_url == null || "".equals(head_url)) {
            ivUser.setImageResource(R.drawable.logo_default_userphoto);
        } else {
            ivUser.setImageURI(UriHelper.obtainUri(head_url));
        }
        getLocationData();
    }


    public void showTalkDialog() {
        final TalkTimeDialog talkTimeDialog = new TalkTimeDialog(mMainActivity, R.style.dialog_card);
        talkTimeDialog.show();
        talkTimeDialog.setOnRightNowListener(new TalkTimeDialog.OnRightNowSetListener() {
            @Override
            public void doSet() {
                //跳转来电转移界
                mMainActivity.toActivity(CallTransferActivity.class, null);
                talkTimeDialog.dismiss();
            }
        });


        if (!(mVoiceNumber == null || StringUtil.isBlank(mVoiceNumber.getPhone()))) {
            String phone = mVoiceNumber.getPhone();

            Date startTime = mVoiceNumber.getStartTime();
            Date endTime = mVoiceNumber.getEndTime();
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
            //专属号套餐开始时间
            String exclusiveNumberStartTime = sd.format(startTime);
            String exclusiveNumberEndTime = sd.format(endTime);
            Double remainder = mVoiceNumber.getRemainder();


            talkTimeDialog.setTvExclusiveNumber(phone);
            talkTimeDialog.setTvTaklTimeStart(exclusiveNumberStartTime);
            talkTimeDialog.setTvTalkTimeEn(exclusiveNumberEndTime);
            talkTimeDialog.setTvRemanTalkTime(remainder + "");
            talkTimeDialog.setTvTalkTimeReminDay(calcRemainDays(endTime) + "");
        } else {
            talkTimeDialog.setTvExclusiveNumber("暂无络漫可用专属号");
            talkTimeDialog.setTvTaklTimeStart("----");
            talkTimeDialog.setTvTalkTimeEn("----");
            talkTimeDialog.setRlRemainderTimeGone();
        }
        if (!(mVoiceAvailableTalk == null)) {
            //剩余分钟
            talkTimeDialog.setTvRemanTalkTime(mVoiceAvailableTalk.getRemaindertime() + "");
        }
    }

    /**
     * 每日更新数据
     */
    private void updateEveryDay() {
        JSONObject LoginUser = mMainActivity.getAuthJSONObject();
        if (LoginUser != null) {
            long nowTime = DateTimeTool.GetDateTimeNowlong(); //毫秒
            long lastTime = SPreferencesTool.getInstance().getLongValue(mMainActivity.getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.every_day_update, 0l);
            long hours = DateTimeTool.getCompareValue(lastTime, nowTime, DateTimeTool.FORMAT_HOUR);
            if (lastTime == 0 || hours > 24) {
                new UpdateManager(mMainActivity.getApplicationContext()).getDataToCache(LoginUser);
                SPreferencesTool.getInstance().putValue(mMainActivity.getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.every_day_update, nowTime);
            }
        }
    }

    private void getLocationData() {
        if (listHomePage == null || listHomePage.size() == 0) {
            listHomePage = updateManager.getHomePageList(mMainActivity.getAuthJSONObject());
            fragmentHandler.obtainMessage(MsgType.MSG_GET_HOME_PAGE_SUCCESS, listHomePage).sendToTarget();
        }
    }

    /**
     *
     */
    private void getHomeHeadLines() {
        JSONObject json = mMainActivity.getAuthJSONObject();
        try {
            json.put("type", "1");
            json.put("location", "headlines");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new HomePager(mMainActivity.getApplicationContext()).getListBanner(json, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                fragmentHandler.sendEmptyMessage(MsgType.MSG_GET_HOME_HEAD_LINE_TIMEOUT);
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    UCResponse<HomePageRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<HomePageRDO>>() {
                    }.getType());
                    if (result != null && result.getAttributes() != null) {
                        listHeadLine = result.getAttributes().homepages;
                        fragmentHandler.sendEmptyMessage(MsgType.MSG_GET_HOME_HEAD_LINE_SUCCESS);
                    }
                }
            }
        });
    }

    @Override
    public void doHandler(Message msg) {
        switch (msg.what) {
            case MsgType.MSG_GET_HOME_PAGE_SUCCESS:
                //设置水平横向滑动的参数
                listHomePage = (List<HomePageDBModel>) msg.obj;
                if (isAdded() && listHomePage != null) {
                    int size = listHomePage.size();
                    int screenWidth = ScreenUtils.getScreenWidth(mMainActivity.getApplicationContext());
                    int itemWidth = screenWidth / 3;
                    int gridViewWidth = itemWidth * size + 18;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridViewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
                    grid_view_home.setLayoutParams(params); //设置GirdView布局参数,横向布局的关键
                    grid_view_home.setColumnWidth(itemWidth);
                    grid_view_home.setHorizontalSpacing(1);
                    grid_view_home.setStretchMode(GridView.NO_STRETCH);
                    grid_view_home.setNumColumns(size);
                    homeAdapter.setData(listHomePage);
                    homeAdapter.notifyDataSetChanged();
                }
                break;
            case MsgType.MSG_GET_HOME_PAGE_TIMEOUT:
                break;
            case MsgType.MSG_GET_HOME_HEAD_LINE_SUCCESS:
                if (listHeadLine != null && !listHeadLine.isEmpty() && listHeadLine.size() >= 2) {
                    tv_actives.setText(listHeadLine.get(0).name);
                    tv_actives_desc.setText(listHeadLine.get(0).description);
                    tv_on_sale.setText(listHeadLine.get(1).name);
                    tv_on_sale_desc.setText(listHeadLine.get(1).description);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fragmentHandler.removeCallbacksAndMessages(null);
    }

    /**
     * 应用升级检查
     */
    private void checkAppUpgrade() {
        JSONObject jsonObject = mMainActivity.getAuthJSONObject();
        try {
            jsonObject.put("version", PackageTool.getVersionCode(mMainActivity.getApplicationContext()));
            jsonObject.put("type", "2");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new HomePager(mMainActivity.getApplicationContext()).validApp(jsonObject, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }

            @Override
            public void onSuccess(String response) {
                if (response != null) {
                    UCResponse<AppNewVersionRDO> result = JsonUtil.fromJson(response, new TypeToken<UCResponse<AppNewVersionRDO>>() {
                    }.getType());
                    if (result != null && result.getAttributes() != null) {
                        final AppNewVersionRDO appNewVersionRDO = result.getAttributes();
                        if (appNewVersionRDO.getNeeded()) {
                            //强制升级
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SPreferencesTool.getInstance().saveUpLoadApk(mMainActivity.getApplicationContext(), true, appNewVersionRDO.getVersionName(), appNewVersionRDO.getDescription(), appNewVersionRDO.getUrl());
                                    UploadApp uploadApp = new UploadApp(RoamApplication.FILEPATH_UPAPK);//Utility.getSDCardDir(getActivity(),
                                    uploadApp.showUpApk(getActivity(), appNewVersionRDO.getDescription(), appNewVersionRDO.getUrl(), appNewVersionRDO.isForce_upgrade());
                                }
                            });
                        }

                    }
                }
            }
        });

    }

}