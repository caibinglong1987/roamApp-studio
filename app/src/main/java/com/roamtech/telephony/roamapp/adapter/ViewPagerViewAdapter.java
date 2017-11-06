package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * http://www.cnblogs.com/kobe8/p/4343478.html
 *
 * @author xincheng
 */
public class ViewPagerViewAdapter extends PagerAdapter {
    private Context mContext;
    private List<View> views;
    private LayoutInflater inflater;

    /**
     * @param mContext
     * @param views view
     */
    public ViewPagerViewAdapter(Context mContext, List<View> views) {
        super();
        this.mContext = mContext;
        this.views = views;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        view.addView(views.get(position), 0);
        return views.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
