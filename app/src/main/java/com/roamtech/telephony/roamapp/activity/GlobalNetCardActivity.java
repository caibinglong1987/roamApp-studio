package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.Parameter.KeyValue;
import com.roamtech.telephony.roamapp.adapter.GlobalCardAdapter;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.OKCallback;
import com.roamtech.telephony.roamapp.base.ServicePackageCallback;
import com.roamtech.telephony.roamapp.bean.DataCardRDO;
import com.roamtech.telephony.roamapp.bean.GlobalCard;
import com.roamtech.telephony.roamapp.bean.ServicePackage;
import com.roamtech.telephony.roamapp.bean.UCResponse;
import com.roamtech.telephony.roamapp.bean.VoiceTalk;
import com.roamtech.telephony.roamapp.dialog.AddGlobalCardOptionDialog;
import com.roamtech.telephony.roamapp.enums.LoadingState;
import com.roamtech.telephony.roamapp.enums.MainTab;
import com.roamtech.telephony.roamapp.event.EventAddCard;
import com.roamtech.telephony.roamapp.util.Constant;
import com.roamtech.telephony.roamapp.util.OkHttpUtil;
import com.roamtech.telephony.roamapp.view.EmptyView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.roamtech.telephony.roamapp.enums.LoadingState.FAILED;
import static com.roamtech.telephony.roamapp.enums.LoadingState.LOADING;
import static com.roamtech.telephony.roamapp.enums.LoadingState.SESSION_TIME_OUT;
import static com.roamtech.telephony.roamapp.enums.LoadingState.SUCCESS;

/**
 * 络漫卡列表 界面
 */
public class GlobalNetCardActivity extends BaseActivity {
    private TextView tvSetnetCard;
    private LinearLayout layoutEmptyAddCard;
    private TextView tvEmptyAddCard;
    private LinearLayout layoutAddCard;
    private TextView tvBottomAddCard;
    private EmptyView mEmptyView;
    private GlobalCardAdapter mAdapter;
    private ListView mListView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_global_netcard;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.initView(savedInstanceState);
        EventBus.getDefault().register(this);
        mListView = (ListView) findViewById(R.id.lv_globalcard);
        mAdapter = new GlobalCardAdapter(this, null);
        mListView.setAdapter(mAdapter);
        mEmptyView = (EmptyView) findViewById(R.id.id_empty_view);
        mEmptyView.setExcludeSuccessBefore(false);
        mEmptyView.bindViewAndLoadListener(mEmptyView,
                LOADING, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        requestData();
                    }
                });
        tvSetnetCard = (TextView) findViewById(R.id.tv_setnetcard);
        layoutEmptyAddCard = (LinearLayout) findViewById(R.id.emptyLayout);
        tvEmptyAddCard = (TextView) findViewById(R.id.tvEmptyAddNetCard);
        layoutAddCard = (LinearLayout) findViewById(R.id.llyt_AddNetCard);
        tvBottomAddCard = (TextView) findViewById(R.id.tvAddNetCard);
        requestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddCardSuccess(EventAddCard event) {
        //重新请求上网卡数据
        requestData();
    }

    private void requestData() {
        OkHttpUtil.postJsonRequest(Constant.GLOBAL_CARD, getAuthJSONObject(), hashCode(), new OKCallback<DataCardRDO>(new TypeToken<UCResponse<DataCardRDO>>() {
        }) {
            @Override
            public void onResponse(int statuscode, UCResponse<DataCardRDO> ucResponse) {
                if (isSucccess()) {
                    mEmptyView.setState(SUCCESS);
                    List<GlobalCard> cards = ucResponse.getAttributes().getDatacards(); //ucResponse.getResultData("datacards", new TypeToken<List<GlobalCard>>() {
                    //});
                    if (!LoadingState.isEmptyData(cards)) {
                        requestTrafficvoice(cards);
                    } else {
                        mAdapter.refreash(null);
                        layoutEmptyAddCard.setVisibility(View.VISIBLE);
                        layoutAddCard.setVisibility(View.GONE);
                    }
                } else if (isSessionTimeout()) {
                    mEmptyView.setState(SESSION_TIME_OUT);
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

    private void requestTrafficvoice(final List<GlobalCard> cards) {
        LinphoneActivity.instance()
                .requestAllTrafficvoice(new ServicePackageCallback() {

                    @Override
                    public void handle(List<ServicePackage> sps, VoiceTalk vt) {
                        for (GlobalCard card : cards) {
                            String iccid = card.getIccid();
                            for (ServicePackage servicePackage : sps) {
                                Set<String> sims = servicePackage.getSimids();
                                Integer type = servicePackage.getType();
                                if (type != null && type == 2 && sims != null && sims.size() > 0) {
                                    for (Iterator<String> it = sims.iterator(); it.hasNext(); ) {
                                        String next = it.next();
                                        if (next.equals(iccid)) {
                                            card.addServicePackage(servicePackage);
                                            break;
                                        }
                                    }
                                }
                            }
                            if (card.getServicePackages() != null && !card.getServicePackages().isEmpty()) {
                                Collections.sort(card.getServicePackages());
                            }
                        }
                        mAdapter.refreash(cards);
                        layoutEmptyAddCard.setVisibility(View.GONE);
                        layoutAddCard.setVisibility(View.VISIBLE);
                    }
                }, true);
        /*JSONObject json = getAuthJSONObject();
        OkHttpUtil.postJsonRequest(Constant.ALLTRAFFICVOICE, json, hashCode(), new OKCallback<ServiceRDO>(new TypeToken<UCResponse<ServiceRDO>>(){}) {
            @Override
            public void onResponse(int statuscode, @Nullable  UCResponse<ServiceRDO> ucResponse) {
                if (isSucccess()) {
                    List<ServicePackage> packages = ucResponse.getAttributes().getServicePackages(); //ucResponse.getResultData("servicepackages", new TypeToken<List<ServicePackage>>() {
                    //});
                    for (GlobalCard card : cards) {
                        String iccid = card.getIccid();
                        for (ServicePackage servicePackage : packages) {
                            Set<String> sims = servicePackage.getSimids();
                            Integer type = servicePackage.getType();
                            if (type != null && type == 2 && sims != null && sims.size() > 0) {
                                for (Iterator<String> it = sims.iterator(); it.hasNext(); ) {
                                    String next = it.next();
                                    if (next.equals(iccid)) {
                                        card.addServicePackage(servicePackage);
                                        break;
                                    }
                                }
                            }
                        }
                        Collections.sort(card.getServicePackages());
                    }
                    mAdapter.refreash(cards);
                    layoutEmptyAddCard.setVisibility(View.GONE);
                    layoutAddCard.setVisibility(View.VISIBLE);
                } else if (isSessionTimeout()) {
                    mEmptyView.setState(SESSION_TIME_OUT);
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
        });*/
    }

    //测试解绑
    private void unBindCard(String iccid) {
        showDialog(getString(R.string.unbind_card));
        JSONObject json = getAuthJSONObject();
        try {
            json.put("datacardid", iccid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtil.postJsonRequest(Constant.DATA_CARD_UNBIND, json, hashCode(), new OKCallback<String>(new TypeToken<UCResponse<String>>() {
        }) {
            @Override
            public void onResponse(int statuscode, UCResponse<String> ucResponse) {
                dismissDialog();
                if (isSucccess()) {
                    showToast(getString(R.string.unbind_card_success));
                    requestData();
                } else if (isSessionTimeout()) {
                    showToast(SESSION_TIME_OUT.getText());
                    toActivityClearTopWithState(LoginActivity.class, null);
                } else {
                    if (ucResponse != null) {
                        showToast(ucResponse.getErrorInfo());
                    } else {
                        showToast(getString(R.string.unbind_card_faild));
                    }
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
        super.setListener();
        tvSetnetCard.setOnClickListener(this);
        tvEmptyAddCard.setOnClickListener(this);
        layoutAddCard.setOnClickListener(this);
        tvBottomAddCard.setOnClickListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String iccid = mAdapter.getItem(position).getIccid();
                unBindCard(iccid);
                return true;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        super.onItemClick(parent, view, position, id);
        Bundle bundle = new Bundle();
        bundle.putSerializable(GlobalCard.class.getName(), mAdapter.getItem(position));
        toActivity(GlobalNetCardDetailActivity.class, bundle);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        super.onClick(v);
        if (v == tvSetnetCard) {
            toActivity(ApnSettingActivity.class, null);
        } else if (v == tvEmptyAddCard || v == tvBottomAddCard) {
            showUsePackageOption(new AddGlobalCardOptionDialog.OnQuickOptionformClick() {
                @Override
                public void onQuickOptionClick(int id) {
                    if (id == R.id.tv_addown) {
                        toActivity(AddGlobalNetCardActivity.class, null);
                    } else if (id == R.id.tv_buyInMall) {
                        Bundle bundle = new Bundle();
                        bundle.putString(KeyValue.TAB_TARGET, KeyValue.TAB_RD_MALL);
                        toActivityClearTopWithState(LinphoneActivity.class, bundle);
                    }
                }
            });
        }
    }

    protected void showUsePackageOption(
            AddGlobalCardOptionDialog.OnQuickOptionformClick onQuickOptionformClick) {
        AddGlobalCardOptionDialog dialog = new AddGlobalCardOptionDialog(this);
        dialog.setOnQuickOptionformClickListener(onQuickOptionformClick);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
