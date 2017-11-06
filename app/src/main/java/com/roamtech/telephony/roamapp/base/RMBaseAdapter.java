package com.roamtech.telephony.roamapp.base;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class RMBaseAdapter<T> extends BaseAdapter {
    protected BaseActivity mActivity;
    protected LayoutInflater inflater;
    protected Resources res;
    protected List<T> mData;
    /**
     * 报价展示保留两位小数
     **/
    public static final DecimalFormat df2 = new DecimalFormat("0.00");

    public RMBaseAdapter(BaseActivity activity, List<T> data) {
        // TODO Auto-generated constructor stub
        if (data == null) {
            data = new ArrayList<T>();
        }
        mData = data;
        mActivity = activity;
        inflater = mActivity.getLayoutInflater();
        res = mActivity.getResources();
    }

    protected Drawable getDrawable(int drawableId) {
        Drawable drawable = res.getDrawable(
                drawableId);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        return drawable;
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> data) {
        mData = data;
    }

    /****
     * 记忆排序需要的方法
     ***/
    public void remove(T t) {
        if (mData.remove(t)) {
            notifyDataSetChanged();
        }
    }

    public void removeAll(List<T> list) {
        mData.removeAll(list);
        notifyDataSetChanged();
    }

    public void insert(T t, int to) {
        mData.add(to, t);
        notifyDataSetChanged();
    }

    /**
     * 刷新数据
     */
    public void refreash(List<T> newData) {
        // TODO Auto-generated method stub
        mData.clear();
        if (newData != null && !newData.isEmpty()) {
            /** 此时表示清空列表数据 */
            mData.addAll(newData);
        }
        notifyDataSetChanged();
    }

    /**
     * 加载更多
     *
     * @param newData 数据
     */
    public void loadMore(List<T> newData) {
        if (newData != null && !newData.isEmpty()) {
            /** 此时表示清空列表数据 */
            mData.addAll(newData);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        // TODO Auto-generated method stub
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public abstract View getView(int position, View convertView, ViewGroup parent);

    protected SimpleDateFormat formatterToSecond = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    protected SimpleDateFormat formatterToDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    protected String format(String strDate) {
        Date date = parseStrDate(strDate);
        if (date != null) {
            return formatDate(date);
        }
        return null;
    }

    protected Date parseStrDate(String strDate) {
        try {
            return formatterToSecond.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String formatDate(Date date) {
        return formatterToDay.format(date);
    }
}
