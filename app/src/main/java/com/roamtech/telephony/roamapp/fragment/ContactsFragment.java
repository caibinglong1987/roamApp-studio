package com.roamtech.telephony.roamapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.BlacklistActivity;
import com.roamtech.telephony.roamapp.activity.ContactInfoActivity;
import com.roamtech.telephony.roamapp.activity.MainNewActivity;
import com.roamtech.telephony.roamapp.activity.SearchActivity;
import com.roamtech.telephony.roamapp.base.BaseFragment;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.event.EventLoadContactEnd;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;
import com.roamtech.telephony.roamapp.util.UriHelper;
import com.roamtech.telephony.roamapp.util.Utility;
import com.roamtech.telephony.roamapp.widget.sortlist.ClearEditText;
import com.roamtech.telephony.roamapp.widget.sortlist.ContactsSortAdapter;
import com.roamtech.telephony.roamapp.widget.sortlist.SideBar;
import com.roamtech.telephony.roamapp.widget.sortlist.SideBar.OnTouchingLetterChangedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.linphone.FragmentsAvailable;

import java.util.Locale;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

/**
 * @author xincheng 联系人
 */
public class ContactsFragment extends BaseFragment implements SectionIndexer {
    private ImageView ivUserIcon;
    private ImageView ivAddContact;
    private ListView mSortListView;
    private ClearEditText mClearEditText;
    private SideBar sideBar;
    private TextView dialog;
    private ContactsSortAdapter mAdapter;
    private boolean editConsumed = false, onlyDisplayChatAddress = false;
    private MainNewActivity mMainActivity;
    private TextView mTitleCatalog;
    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private LinearLayout headerLayout;
    private LinearLayout headerLayoutContent;

    private View getHeaderItem(int layoutId) {
        return LayoutInflater.from(getActivity()).inflate(layoutId, null);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_contact;
    }

    @Override
    public void initData() {
        super.initData();
        if (getArguments() != null) {
            onlyDisplayChatAddress = getArguments().getBoolean("ChatAddressOnly");
        }
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mMainActivity = (MainNewActivity) getBaseActivity();
        ivUserIcon = (ImageView) findViewById(R.id.id_circle_image);
        ivAddContact = (ImageView) findViewById(R.id.iv_addcontact);
        mTitleCatalog = (TextView) findViewById(R.id.title_catalog);
        sideBar = (SideBar) findViewById(R.id.sidebar);
        dialog = (TextView) findViewById(R.id.dialog);

        sideBar.setLetterDialog(dialog);
        mSortListView = (ListView) findViewById(R.id.lv_contacts);
        initHeader();
        /** 给ListView设置adapter **/
        mAdapter = new ContactsSortAdapter(mMainActivity, null);
        mSortListView.setAdapter(mAdapter);
        //执行stick 所以需要在ListView初始化完成之后调用
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void loadContactSuccess(EventLoadContactEnd eventLoadContactEnd) {
        mAdapter.refreash(RDContactHelper.getOriginSystemContacts());
    }

    @Override
    public void setListener() {
        super.setListener();
        ivUserIcon.setOnClickListener(this);
        ivAddContact.setOnClickListener(this);
        mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
        mClearEditText.setOnClickListener(this);
        mSortListView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem < mSortListView.getHeaderViewsCount()) {
                    mTitleCatalog.setVisibility(GONE);
                    return;
                } else {
                    mTitleCatalog.setVisibility(VISIBLE);
                }
                int section = getSectionForPosition(firstVisibleItem);
                int nextSection = getSectionForPosition(firstVisibleItem + 1);
                int nextSecPosition = getPositionForSection(+nextSection);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    MarginLayoutParams params = (MarginLayoutParams) mTitleCatalog.getLayoutParams();
                    params.topMargin = 0;
                    mTitleCatalog.setLayoutParams(params);
                    mTitleCatalog.setText((getItemAtPosition(getPositionForSection(section))).getSortLetter());
                }
                if (nextSecPosition == firstVisibleItem + 1) {
                    View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = mTitleCatalog.getHeight();
                        int bottom = childView.getBottom();
                        MarginLayoutParams params = (MarginLayoutParams) mTitleCatalog.getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            mTitleCatalog.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                mTitleCatalog.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }
        });
        //设置右侧[A-Z]快速导航栏触摸监听
        sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mSortListView.setSelection(position);
                }
            }
        });

        mSortListView.setOnItemClickListener(this);
    }

    private void initHeader() {
        headerLayout = new LinearLayout(getActivity());
        headerLayoutContent = new LinearLayout(getActivity());
        headerLayout.setOrientation(LinearLayout.VERTICAL);
        headerLayoutContent.setOrientation(LinearLayout.VERTICAL);
        headerLayout.addView(headerLayoutContent);
        mSortListView.addHeaderView(headerLayout, null, false);
        View itemCollection = getHeaderItem(R.layout.item_contact_addcollection);
        headerLayoutContent.addView(itemCollection);
        View blacklist = getHeaderItem(R.layout.item_blacklist_add_collection);
        blacklist.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BlacklistActivity.class));
            }
        });
        headerLayoutContent.addView(blacklist);
        View itemRoamservice = getHeaderItem(R.layout.item_roam_service);
        itemRoamservice.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                /**拨打客服**/
//                Toast.makeText(
//                        getActivity(),
//                        "拨打客服电话", Toast.LENGTH_SHORT).show();
                LinphoneActivity.instance().setAddressGoToDialerAndCall(getString(R.string.roamPhone), getString(R.string.roamservice), null);
            }
        });
        headerLayoutContent.addView(itemRoamservice);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
        Utility.closeKeyboard(mClearEditText, getActivity().getApplicationContext());
        Bundle extras = new Bundle();
        extras.putBoolean("ChatAddressOnly", onlyDisplayChatAddress);
        extras.putSerializable(RDContact.class.getName(), getItemAtPosition(position));
        mMainActivity.toActivity(ContactInfoActivity.class, extras);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (LinphoneActivity.isInstanciated()) {
            LinphoneActivity.instance().selectMenu(FragmentsAvailable.CONTACTS);
        }

        String head_url = SPreferencesTool.getInstance().getStringValue(getActivity().getApplicationContext(), SPreferencesTool.LOGIN_INFO, SPreferencesTool.login_headUrl);
        if (head_url == null || "".equals(head_url)) {
            ivUserIcon.setImageResource(R.drawable.nav_user_default);
        } else {
            ivUserIcon.setImageURI(UriHelper.obtainUri(head_url));
        }

        //mAdapter.refreash(ContactHelper.getSystemContacts());
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return getItemAtPosition(position).getSortLetter().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = mSortListView.getHeaderViewsCount(); i < mSortListView.getCount(); i++) {
            String sortStr = getItemAtPosition(i).getSortLetter();
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    public RDContact getItemAtPosition(int position) {
        return (RDContact) mSortListView.getItemAtPosition(position);
    }

    @Override
    public Object[] getSections() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == ivUserIcon) {
            mMainActivity.getDrawerLayout().openDrawer(
                    GravityCompat.START);
        }
        if (v == ivAddContact) {
            RDContactHelper.addContact(getBaseActivity(), "");
        }
        if (v == mClearEditText) {
            Bundle bundle = new Bundle();
            bundle.putInt("searchType", 2);
            mMainActivity.toActivity(SearchActivity.class, bundle);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {

        }
    }
}