package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseGroupAdapter;
import com.roamtech.telephony.roamapp.bean.Evoucher;
import com.roamtech.telephony.roamapp.util.StringUtil;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class EleEvoucherAdapter extends BaseGroupAdapter<String, String, Evoucher> {

    public EleEvoucherAdapter(Context context, List<String> groups, Map<String, List<Evoucher>> map) {
        super(context, groups, map);
    }

//    @Override
//    public View getView(int position, View contentView, ViewGroup parent) {
//        ViewHolder holder = null;
//        if (contentView == null) {
//            holder = new ViewHolder();
//            contentView = inflater.inflate(
//                    R.layout.item_elecoupon, parent, false);
//            holder.tvName = (TextView) contentView.findViewById(R.id.tv_name);
//            holder.ivCoupon = (ImageView) contentView.findViewById(R.id.iv_coupon);
//            holder.tvEffectdate = (TextView) contentView.findViewById(R.id.tv_effectdate);
//            holder.ivStatus = (ImageView) contentView.findViewById(R.id.iv_status);
//            contentView.setTag(holder);
//        } else {
//            holder = (ViewHolder) contentView.getTag();
//        }
//        Evoucher evoucher = getItem(position);
//        holder.tvName.setText(evoucher.getName());


//        String name = evoucher.getName();
//        if (name.indexOf("流量") != -1) {
//            holder.ivCoupon.setImageResource(R.drawable.coupon_flow);
//        }
//        return contentView;
//    }

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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_elecoupon, null);
            holder.ivLogo = (ImageView) convertView.findViewById(R.id.iv_logo);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvArea = (TextView) convertView.findViewById(R.id.tv_area);
            holder.tvEffectDate = (TextView) convertView.findViewById(R.id.tv_effect_date);
            holder.ivStatus = (ImageView) convertView.findViewById(R.id.iv_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Evoucher evoucher = getChild(groupPosition, childPosition);
        if (evoucher != null) {
            holder.tvName.setText(evoucher.getName());
            if ("member".equals(evoucher.getLocation())) {
                holder.ivLogo.setImageResource(R.drawable.roam_benefit_green);
            } else {
                holder.ivLogo.setImageResource(R.drawable.roam_benefit);
            }
            if (StringUtil.isTrimBlank(evoucher.getAreaName())) {
                holder.tvArea.setVisibility(View.GONE);
            } else {
                holder.tvArea.setText(String.format("[%s]", evoucher.getAreaName()));
                holder.tvArea.setVisibility(View.VISIBLE);
            }
            Date effectDate = evoucher.getStartTime();
            Date failureDate = evoucher.getEndTime();
            if (effectDate != null && failureDate != null) {
                holder.tvEffectDate.setText(String.format("%s至%s", formatDate(effectDate), formatDate(failureDate)));
            } else if (failureDate == null) {
                holder.tvEffectDate.setText(context.getString(R.string.effectPermanent));
            }
            if (evoucher.getUsedTime() != null) {
                holder.ivStatus.setVisibility(View.VISIBLE);
                holder.ivStatus.setImageResource(R.drawable.ic_roamdata_used);
            } else if (failureDate != null && failureDate.before(new Date())) {
                holder.ivStatus.setVisibility(View.VISIBLE);
                holder.ivStatus.setImageResource(R.drawable.ic_roamdata_outofdate);
            } else {
                holder.ivStatus.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView tvGroup;
        ImageView ivLogo;
        TextView tvName;
        TextView tvArea;
        TextView tvEffectDate;
        ImageView ivStatus;
    }
}
