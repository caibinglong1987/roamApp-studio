package com.roamtech.telephony.roamapp.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.roamtech.telephony.roamapp.HandlerMessag.MsgType;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.adapter.CustomerPageAdapter;
import com.roamtech.telephony.roamapp.adapter.SimpleFragmentPagerAdapter;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by long
 * on 2016/10/11.
 * 络漫宝 连接展示 列表
 */

public class LMBAOStatusActivity extends HeaderBaseActivity {
    private ViewPager viewPage_lbm_box, viewPage_lbm_box_list;
    private List<ImageView> imageViews = new ArrayList<>();
    private int viewPagePosition = 0;
    private SimpleFragmentPagerAdapter simpleFragmentPagerAdapter;
    private CustomerPageAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lmbao_status);
        initView();
        initViewpager();
    }

    private void initView() {
        viewPage_lbm_box = (ViewPager) findViewById(R.id.viewPage_lbm_box);
        viewPage_lbm_box_list = (ViewPager) findViewById(R.id.viewPage_lbm_box_list);
        viewPage_lbm_box.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPagePosition = position;
               // uiHandler.sendEmptyMessage(MsgType.HANDLER_VIEWPAGER_CHANGE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPage_lbm_box_list.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initViewpager() {
        int pageCount = 2;
        for (int i = 0; i < pageCount; i++) {
            final ImageView imageView = new ImageView(this);
            imageView.setImageResource(R.drawable.logo_default_userphoto);
            imageViews.add(imageView);
        }
        pagerAdapter = new CustomerPageAdapter(this, imageViews);
        viewPage_lbm_box.setAdapter(pagerAdapter);

        simpleFragmentPagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this, pageCount);
        viewPage_lbm_box_list.setAdapter(simpleFragmentPagerAdapter);
    }

    @Override
    public void doHandler(Message msg) {
        super.doHandler(msg);
        switch (msg.what) {
            case MsgType.HANDLER_VIEWPAGER_CHANGE:

                break;
        }
    }
}
