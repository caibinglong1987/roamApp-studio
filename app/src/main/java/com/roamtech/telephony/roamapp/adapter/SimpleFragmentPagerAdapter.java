package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.roamtech.telephony.roamapp.fragment.LMBAOStatusFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {
    private Map<String, Fragment> fragments = new HashMap<>();
    private Context context;
    private int fragmentCount;

    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context, int fragmentCount) {
        super(fm);
        this.context = context;
        this.fragmentCount = fragmentCount;
        init();
    }


    private void init() {
        if (context != null) {
            for (int i = 0; i < fragmentCount; i++) {
                fragments.put("Tab_" + fragmentCount, LMBAOStatusFragment.newInstance(i));
            }
        }
    }

    @Override
    public Fragment getItem(int position) {
        return LMBAOStatusFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return fragmentCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

}