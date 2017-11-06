package com.roamtech.telephony.roamapp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.bean.GlobalCard;
import com.roamtech.telephony.roamapp.bean.ServicePackage;

import java.util.Date;
import java.util.List;

public class GlobalCardAdapter extends RMBaseAdapter<GlobalCard> {

    public GlobalCardAdapter(BaseActivity activity, List<GlobalCard> mData) {
        super(activity, mData);
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder = null;
        if (contentView == null) {
            holder = new ViewHolder();
            contentView = inflater.inflate(
                    R.layout.item_global_card, parent, false);
            holder.tvId = (TextView) contentView.findViewById(R.id.tv_id);
            holder.tvPackageDetail = (TextView) contentView.findViewById(R.id.tv_package_detail);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }
        GlobalCard card = getItem(position);
        holder.tvId.setText(String.valueOf(card.getIccid()));
        ServicePackage first=card.getFirstPackage();
        if(first!=null){
            Date startTime=first.getStartTime();
            Date endTime=first.getEndTime();
            if (startTime != null && endTime != null) {
                holder.tvPackageDetail.setText(String.format("%s%s至%s",first.getAreaname()+":", formatDate(startTime), formatDate(endTime)));
            } else if(startTime != null){
                holder.tvPackageDetail.setText(String.format("%s%s开始生效",first.getAreaname()+":", formatDate(startTime)));
            }else if (endTime != null) {
                holder.tvPackageDetail.setText(String.format("%s请于%s前使用",first.getAreaname()+":", formatDate(endTime)));
            }
        }else{
            holder.tvPackageDetail.setText("暂无套餐");
        }
        return contentView;
    }

    class ViewHolder {
        //流量卡id
        TextView tvId;
        //使用时间
        TextView tvPackageDetail;
    }
}
