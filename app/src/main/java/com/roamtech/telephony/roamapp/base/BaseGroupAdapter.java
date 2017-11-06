package com.roamtech.telephony.roamapp.base;

import android.content.Context;
import android.widget.BaseExpandableListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by chenblue23 on 2016/10/26.
 */

public abstract class BaseGroupAdapter<T, K, V> extends BaseExpandableListAdapter{
    protected Context context;
    protected List<T> groupList;
    protected Map<K, List<V>> map;
    private SimpleDateFormat formatterToDay = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public BaseGroupAdapter(Context context, List<T> groupList, Map<K, List<V>> map) {
        this.context = context;
        if (groupList == null) {
            groupList = new ArrayList<>();
        }
        if (map == null) {
            map = new HashMap<>();
        }
        this.groupList = groupList;
        this.map = map;
    }

    @Override
    public int getGroupCount() {
        return groupList != null ? groupList.size() : 0;
    }

    @Override
    public T getGroup(int groupPosition) {
        return groupList != null ? groupList.get(groupPosition) : null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void addData(K groupKey, V data) {
        List<V> listData = map.get(groupKey);
        listData.add(data);
        notifyDataSetChanged();
    }

    public void removeData(K groupKey, V data) {
        List<V> listData = map.get(groupKey);
        listData.remove(data);
        notifyDataSetChanged();
    }

    public void addListData(K groupKey, List<V> data) {
        List<V> listData = map.get(groupKey);
        listData.addAll(data);
        notifyDataSetChanged();
    }

    public void removeListData(K groupKey, List<V> data) {
        List<V> listData = map.get(groupKey);
        listData.removeAll(data);
        notifyDataSetChanged();
    }

    public void refresh(List<T> newData, Map<K, List<V>> newMap) {
        groupList.clear();
        map.clear();
        if (newData != null) {
            groupList.addAll(newData);
            if (newMap != null) {
                map.putAll(newMap);
            }
        }
        notifyDataSetChanged();
    }

    public void refresh(Map<K, List<V>> newMap) {
        map.clear();
        if(newMap != null) {
            map.putAll(newMap);
        }
        notifyDataSetChanged();
    }

    protected String formatDate(Date date) {
        return formatterToDay.format(date);
    }
}
