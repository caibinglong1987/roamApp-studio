package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseGroupAdapter;
import com.roamtech.telephony.roamapp.bean.Evoucher;
import com.roamtech.telephony.roamapp.util.Constant;

import java.util.List;
import java.util.Map;

public class FavEvoucherAdapter extends BaseGroupAdapter<String, String, Evoucher> {

    public FavEvoucherAdapter(Context context, List<String> groups, Map<String, List<Evoucher>> map) {
        super(context, groups, map);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String group = groupList.get(groupPosition);
        if (group != null && map != null) {
            if (map.get(group) != null) {
                return map.get(group).size();
            }
        }
        return 0;
    }

    @Override
    public Evoucher getChild(int groupPosition, int childPosition) {
        String group = groupList.get(groupPosition);
        if (group != null && map != null) {
            if (map.get(group) != null) {
                return map.get(group).get(childPosition);
            }
        }
        return null;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_evoucher_group, null);
            holder.tvGroup = (TextView) convertView.findViewById(R.id.tv_group);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvGroup.setText(groupList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_favcoupon, null);
            holder.ivLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvFailureTime = (TextView) convertView.findViewById(R.id.tv_failure_time);
            holder.tvDescription = (TextView) convertView.findViewById(R.id.tv_description);
            holder.ivDetail = (ImageView) convertView.findViewById(R.id.iv_detail);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Evoucher evoucher = getChild(groupPosition, childPosition);
        if (evoucher != null) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(false)
                    .build();
            ImageLoader.getInstance().displayImage(Constant.IMAGE_URL + evoucher.getLogo(), new ImageViewAware(holder.ivLogo), options);
            if (evoucher.getLocation().equals("offlinecoupon")) {
                holder.tvName.setText(evoucher.getName());
                holder.tvDescription.setText(evoucher.getDescription());
                holder.ivDetail.setVisibility(View.VISIBLE);
            } else if (evoucher.getLocation().equals("onlinecoupon")) {
                holder.tvName.setText(context.getString(R.string.roam_fav));
                holder.tvDescription.setText(evoucher.getName());
                holder.ivDetail.setVisibility(View.GONE);
            }
            if (evoucher.getEndTime() != null) {
                holder.tvFailureTime.setText(String.format("有效期：%s", formatDate(evoucher.getEndTime())));
            } else {
                holder.tvFailureTime.setText(context.getString(R.string.effect_date) + context.getString(R.string.effectPermanent));
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView tvGroup;
        ImageView ivLogo;
        TextView tvName;
        TextView tvFailureTime;
        TextView tvDescription;
        ImageView ivDetail;
    }
}
