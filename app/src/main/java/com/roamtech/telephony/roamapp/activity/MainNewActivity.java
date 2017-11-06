package com.roamtech.telephony.roamapp.activity;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.LinphoneActivity;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.Parameter.KeyValue;
import com.roamtech.telephony.roamapp.application.RoamApplication;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.event.EventUpPay;
import com.roamtech.telephony.roamapp.event.EventkeyboardTab;
import com.roamtech.telephony.roamapp.fragment.ContactsFragment;
import com.roamtech.telephony.roamapp.fragment.HomeFragment;
import com.roamtech.telephony.roamapp.fragment.KeyboardGroupFragment;
import com.roamtech.telephony.roamapp.fragment.MessageFragment;
import com.roamtech.telephony.roamapp.fragment.RDMallFragment;
import com.roamtech.telephony.roamapp.util.BadgeUtil;
import com.roamtech.telephony.roamapp.util.LocalDisplay;
import com.roamtech.telephony.roamapp.util.SPreferencesTool;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.linphone.LinphoneService;


/**
 * Created by caibinglong
 * on 2016/12/9.
 */

public class MainNewActivity extends BaseActivity {
    //private SlidingMenu slidingMenu;
    //fragment 修改加载方式
    private FragmentManager fragmentManager = null;
    private HomeFragment homeFragment;
    private ContactsFragment contactsFragment;
    //private KeyboardFragment keyboardFragment;
    private KeyboardGroupFragment keyboardFragment;
    private MessageFragment messageFragment;
    private RDMallFragment rdMallFragment;
    // 当前页卡编号
    public int currIndex = 0;
    private int lastIndex = 0;
    private LinearLayout layout_home, layout_contact,
            layout_keyboard, layout_message, layout_mall;
    private RelativeLayout layout_bottom_tab;
    private int tabWidth;
    private ImageView cursor;
    private DrawerLayout mDrawerLayout;
    private LayoutInflater layoutInflater;
    private boolean bKbdShow;
    private final String TAB_HOME_FRAGMENT = "home";
    private final String TAB_CONTACT_FRAGMENT = "contacts";
    private final String TAB_KEYBOARD_FRAGMENT = "keyboard";
    private final String TAB_MESSAGE_FRAGMENT = "message";
    private final String TAB_RD_MALL_FRAGMENT = "RD_mall";
    private TextView tv_message_number, tv_miss_call_number;
    private ImageView iv_keyboard;
    private static MainNewActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        initView();
    }

    private void initView() {
        layoutInflater = LayoutInflater.from(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setScrimColor(Color.parseColor("#22000000"));

        layout_home = (LinearLayout) findViewById(R.id.layout_home);
        layout_contact = (LinearLayout) findViewById(R.id.layout_contact);
        layout_keyboard = (LinearLayout) findViewById(R.id.layout_keyboard);
        layout_message = (LinearLayout) findViewById(R.id.layout_message);
        layout_mall = (LinearLayout) findViewById(R.id.layout_mall);
        layout_bottom_tab = (RelativeLayout) findViewById(R.id.layout_bottom_tab);
        tv_message_number = (TextView) findViewById(R.id.tv_message_number);
        tv_miss_call_number = (TextView) findViewById(R.id.tv_miss_call_number);
        iv_keyboard = (ImageView) findViewById(R.id.iv_keyboard);

        layout_home.setOnClickListener(this);
        layout_contact.setOnClickListener(this);
        layout_keyboard.setOnClickListener(this);
        layout_message.setOnClickListener(this);
        layout_mall.setOnClickListener(this);

        fragmentManager = getSupportFragmentManager();
        initFragment();
        //initMenu();
        setDefaultTab();
        initCursorImageView();
        setViewListener();
    }

    public static final MainNewActivity getInstance() {
        if (instance == null) {
            instance = new MainNewActivity();
        }
        return instance;
    }

    /**
     * 初始化Fragment;(可以选择当前那个页面显示)
     */
    private void initFragment() {
        homeFragment = new HomeFragment();
        contactsFragment = new ContactsFragment();
        keyboardFragment = new KeyboardGroupFragment();
        messageFragment = new MessageFragment();
        rdMallFragment = new RDMallFragment();

        FragmentTransaction homeTransaction = fragmentManager.beginTransaction();
        homeTransaction.add(R.id.contentFrame, homeFragment, TAB_HOME_FRAGMENT).hide(homeFragment);
        homeTransaction.commitAllowingStateLoss();

        FragmentTransaction contactTransaction = fragmentManager.beginTransaction();
        contactTransaction.add(R.id.contentFrame, contactsFragment, TAB_CONTACT_FRAGMENT).show(contactsFragment);
        contactTransaction.commitAllowingStateLoss();

        FragmentTransaction keyboardTransaction = fragmentManager.beginTransaction();
        keyboardTransaction.add(R.id.contentFrame, keyboardFragment, TAB_KEYBOARD_FRAGMENT).hide(keyboardFragment);
        keyboardTransaction.commitAllowingStateLoss();

        FragmentTransaction messageTransaction = fragmentManager.beginTransaction();
        messageTransaction.add(R.id.contentFrame, messageFragment, TAB_MESSAGE_FRAGMENT).hide(messageFragment);
        messageTransaction.commitAllowingStateLoss();

        FragmentTransaction rdTransaction = fragmentManager.beginTransaction();
        messageTransaction.add(R.id.contentFrame, rdMallFragment, TAB_RD_MALL_FRAGMENT).hide(rdMallFragment);
        rdTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            String tabIndex = intent.getStringExtra(KeyValue.TAB_TARGET);
            String showLeftMenu = intent.getStringExtra(KeyValue.SHOW_LEFT_MENU);//是否显示 左侧侧滑
            if (tabIndex != null && !tabIndex.isEmpty()) {
                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    if (showLeftMenu == null || !showLeftMenu.equals("true")) {
                        mDrawerLayout.closeDrawer(Gravity.LEFT);
                    }
                }
                setSelectedMenu(Integer.parseInt(tabIndex));
            }
        }
    }

    /**
     * 隐藏
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (homeFragment != null) {
            transaction.hide(homeFragment);
        }
        if (contactsFragment != null) {
            transaction.hide(contactsFragment);
        }
        if (keyboardFragment != null) {
            transaction.hide(keyboardFragment);
        }
        if (messageFragment != null) {
            transaction.hide(messageFragment);
        }
        if (rdMallFragment != null) {
            transaction.hide(rdMallFragment);
        }
    }

    public void setSelectedMenu(int viewId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideFragments(transaction);
        switch (viewId) {
            case 0:
            case R.id.layout_home:
                if (homeFragment == null) {
                    homeFragment = new HomeFragment();
                    transaction.add(R.id.contentFrame, homeFragment, TAB_HOME_FRAGMENT);
                } else {
                    transaction.show(homeFragment);
                }
                currIndex = 0;
                onTabChange();
                break;
            case 1:
            case R.id.layout_contact:
                if (contactsFragment == null) {
                    contactsFragment = new ContactsFragment();
                    transaction.add(R.id.contentFrame, contactsFragment, TAB_CONTACT_FRAGMENT);
                } else {
                    transaction.show(contactsFragment);
                }
                currIndex = 1;
                onTabChange();
                break;
            case 2:
            case R.id.layout_keyboard:
                if (keyboardFragment == null) {
                    keyboardFragment = new KeyboardGroupFragment();
                    transaction.add(R.id.contentFrame, keyboardFragment, TAB_KEYBOARD_FRAGMENT);
                } else {
                    keyboardFragment.onResume();
                    transaction.show(keyboardFragment);
                }
                currIndex = 2;
                onTabChange();
                break;
            case 3:
            case R.id.layout_message:
                if (messageFragment == null) {
                    messageFragment = new MessageFragment();
                    transaction.add(R.id.contentFrame, messageFragment, TAB_MESSAGE_FRAGMENT);
                } else {
                    messageFragment.onResume();
                    transaction.show(messageFragment);
                }
                if (LinphoneService.isReady()) {
                    LinphoneService.instance().resetMessageNotificationCount();
                }
                currIndex = 3;
                onTabChange();
                break;
            case 4:
            case R.id.layout_mall:
                if (rdMallFragment == null) {
                    rdMallFragment = new RDMallFragment();
                    transaction.add(R.id.contentFrame, rdMallFragment, TAB_RD_MALL_FRAGMENT);
                } else {
                    transaction.show(rdMallFragment);
                }
                currIndex = 4;
                onTabChange();
                break;
            default:
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    protected void setDefaultTab() {
        bKbdShow = true;
        setSelectedMenu(R.id.layout_home);
        layout_home.setSelected(true);
        //设置第一次是打开的状态一次选中
    }

    protected Fragment getCurrentFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    /**
     * 切换
     */
    private void onTabChange() {
        if (lastIndex != currIndex) {
            lastIndex = currIndex;
            Animation animation = new TranslateAnimation(tabWidth * currIndex, currIndex * tabWidth, 0, 0);
            animation.setFillAfter(true);// True:图片停在动画结束位置
            animation.setDuration(150);
            cursor.startAnimation(animation);
            changeBackground(currIndex);
        } else {
            if (lastIndex == 2) {//keyboard
                toggleKeyBoardState();
            }
        }
    }

    private Bitmap getTabLineBitmap() {
        int screenW = LocalDisplay.SCREEN_WIDTH_PIXELS;// 获取分辨率宽度
        tabWidth = screenW / 5;
        Bitmap bitmap = Bitmap.createBitmap(tabWidth, (int) getResources()
                .getDimension(R.dimen.tabline_height), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(getResources().getColor(R.color.roam_color));
        return bitmap;
    }

    private void initCursorImageView() {
        cursor = (ImageView) findViewById(R.id.iv_cursor);
        Bitmap lineBitmap = getTabLineBitmap();
        cursor.setImageBitmap(lineBitmap);
        Matrix matrix = new Matrix();
        matrix.postTranslate(0, 0);
        cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    /**
     * 初始化菜单选项
     */
//    private void initMenu() {
//        // configure the SlidingMenu
//        slidingMenu = new SlidingMenu(this);
//        slidingMenu.setMode(SlidingMenu.LEFT);// 设置触摸屏幕的模式
//        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
//        slidingMenu.setShadowDrawable(R.drawable.shadow);
//        // 设置滑动菜单视图的宽度
//        slidingMenu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
//        // 设置渐入渐出效果的值
//        slidingMenu.setFadeDegree(0.35f);
//        //把滑动菜单添加进所有的Activity中，可选值SLIDING_CONTENT ， SLIDING_WINDOW
//        slidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//        //为侧滑菜单设置布局
//        slidingMenu.setMenu(R.layout.frame_left_menu);
//        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
//        slidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
//            @Override
//            public void onOpen() {
//
//            }
//        });
//        menuFragment = new MenuFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.left_menu, menuFragment).commit();
//    }

//    public void toggleMenu(boolean show) {
//        slidingMenu.toggle(show);
//    }
    private void setViewListener() {
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int arg0) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mDrawerLayout.getChildAt(0);
                float scale = 1 - slideOffset;
                if (drawerView.getTag().equals("LEFT_MENU")) {
                    drawerView.setAlpha(0.6f + 0.4f * (1 - scale));
                    mContent.setTranslationX(drawerView.getMeasuredWidth() * (1 - scale));
                    mContent.setPivotX(0);
                    mContent.setPivotY(mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                }
            }

            @Override
            public void onDrawerOpened(View arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onDrawerClosed(View arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }


    /**
     * 改变样式
     *
     * @param position
     */
    private void changeBackground(int position) {
        layout_home.setSelected(false);
        layout_contact.setSelected(false);
        layout_message.setSelected(false);
        layout_mall.setSelected(false);
        layout_keyboard.setSelected(false);
        iv_keyboard.setImageResource(R.drawable.tab_keyboard_nor);
        switch (position) {
            case 0:
                layout_home.setSelected(true);
                break;
            case 1:
                layout_contact.setSelected(true);
                break;
            case 2:
                bKbdShow = false;
                layout_keyboard.setSelected(true);
                new EventkeyboardTab(true).isShow();
                toggleKeyBoardState();
                setMissCallNumber(0);
                int missCallNumber = getBadgeNumber(SPreferencesTool.login_badge_miss_call_number, 0);
                RoamApplication.badgeSumNumber -= missCallNumber;
                updateBadgeNumber(SPreferencesTool.login_badge_miss_call_number, 0);
                int number = getBadgeNumber(SPreferencesTool.login_badge_message_number, 0);
                if (number == 0) {
                    BadgeUtil.resetBadgeCount(getApplicationContext(), R.drawable.ic_launcher);
                }
                break;
            case 3:
                layout_message.setSelected(true);
                break;
            case 4:
                layout_mall.setSelected(true);
                break;
        }
    }

    private long preSetKeyBoardStateTime;

    public void toggleKeyBoardState() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - preSetKeyBoardStateTime > 120) {
            preSetKeyBoardStateTime = currentTime;
            bKbdShow = !bKbdShow;
            //保证  tab 和键盘统一;
            if (bKbdShow) {
                iv_keyboard.setImageResource(R.drawable.tab_keyboard_checked);
            } else {
                iv_keyboard.setImageResource(R.drawable.tab_keyboard_unchecked);
            }
            EventBus.getDefault().post(new EventkeyboardTab(bKbdShow));
        }
    }

    /**
     * keybordFragment 编辑或者列表滚动的时候隐藏
     */
    public void setKeyBoardHidden() {
        if (bKbdShow) {
            toggleKeyBoardState();
        }
    }

    /**
     * 显示 隐藏 tab 包含 删除 全选
     *
     * @param show
     */
    public void showBottomMenu(int show) {
        Log.e("显示tab", show + "");
        translationAnimRun(layout_bottom_tab, !(show == 1), 150);
        layout_bottom_tab.setVisibility(show);
    }


    /**
     * @param view
     * @param isShow
     * @param duration
     */
    public void translationAnimRun(final View view, final boolean isShow, final long duration) {
        ObjectAnimator animator = null;
        final int height = view.getHeight();
        if (height != 0) {
            if (isShow) {
                animator = ObjectAnimator.ofFloat(view, "translationY",
                        view.getHeight(), 0).setDuration(duration);
            } else {
                animator = ObjectAnimator.ofFloat(view, "translationY", 0,
                        view.getHeight()).setDuration(duration);
            }
            animator.start();
        } else {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    int preHeight = view.getHeight();
                    if (preHeight != 0) {
                        translationAnimRun(view, isShow, duration);
                    }
                    return true;
                }
            });
        }
    }

    public void setMessageNumber(final int number) {
        Log.e("消息数目：", number + "");
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (number > 0) {
                    tv_message_number.setVisibility(View.VISIBLE);
                    if (number >= 99) {
                        tv_message_number.setText(String.valueOf(number) + "+");
                    } else {
                        tv_message_number.setText(String.valueOf(number));
                    }
                } else {
                    tv_message_number.setVisibility(View.GONE);
                }
            }
        });
    }

    public void setMissCallNumber(final int number) {
        Log.e("未接电话数目：", number + "");
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (number > 0) {
                    tv_miss_call_number.setVisibility(View.VISIBLE);
                    if (number >= 99) {
                        tv_miss_call_number.setText("99+");
                    } else {
                        tv_miss_call_number.setText(String.valueOf(number));
                    }
                } else {
                    tv_miss_call_number.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * 更新XML 角标数据
     *
     * @param key    key
     * @param number number
     */
    public void updateBadgeNumber(final String key, final int number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SPreferencesTool.getInstance().putValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO, key, number);
                RoamApplication.badgeSumNumber = SPreferencesTool.getInstance().getIntValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO,
                        SPreferencesTool.login_badge_miss_call_number, 0) + SPreferencesTool.getInstance().getIntValue(getApplicationContext(), SPreferencesTool.LOGIN_INFO,
                        SPreferencesTool.login_badge_message_number, 0);
                BadgeUtil.setBadgeCount(getApplicationContext(), RoamApplication.badgeSumNumber, R.drawable.ic_launcher);
            }
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        if (data == null) {
            return;
        }
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        String str = data.getExtras().getString("pay_result");
        if (str != null) {
            // str =(success,fail,cancel)
            EventUpPay upPay = new EventUpPay();
            upPay.setPayResult(str);
            EventBus.getDefault().postSticky(upPay);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RoamApplication.badgeSumNumber > 0) {
            RoamApplication.badgeSumNumber = 0;
            BadgeUtil.resetBadgeCount(getApplicationContext(), R.drawable.ic_launcher);
        }
    }
}
