package com.roamtech.telephony.roamapp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.bean.GlobalCard;
import com.roamtech.telephony.roamapp.bean.ServicePackage;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class MyEvoucherAdapter extends RMBaseAdapter<ServicePackage> {
    private GlobalCard mGlobalCard;
    public MyEvoucherAdapter(BaseActivity activity, List<ServicePackage> mData,GlobalCard globalCard) {
        super(activity, mData);
        this.mGlobalCard=globalCard;
    }


    /**
     * 是否被绑定了
     * quantity是包含的流量包的数量， sims小于quantity时才是有效的
     *
     * @param servicePackage
     * @return
     */
    private boolean hasBind(ServicePackage servicePackage) {
        Set<String> sims = servicePackage.getSimids();
        if (sims != null && sims.size() > 0) {
            if(servicePackage.getQuantity() == sims.size()){
                return true;
            }else if(sims.contains(mGlobalCard.getIccid())){
                return true;
            }
        }
        return false;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        ViewHolder holder = null;
        if (contentView == null) {
            holder = new ViewHolder();
            contentView = inflater.inflate(
                    R.layout.item_my_eleevoucher, parent, false);
            holder.tvName = (TextView) contentView.findViewById(R.id.tv_name);
            holder.ivCoupon = (ImageView) contentView.findViewById(R.id.iv_coupon);
            holder.tvEffectdate = (TextView) contentView.findViewById(R.id.tv_effectdate);
            holder.ivtrafficUsed= (ImageView) contentView.findViewById(R.id.iv_trafffic_used);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }
        ServicePackage servicePackage = getItem(position);
        holder.tvName.setText(servicePackage.getName());
        Date effectDate = servicePackage.getStartTime();
        Date failureDate = servicePackage.getEndTime();
        if (effectDate != null && failureDate != null) {
            holder.tvEffectdate.setText(String.format("%s至%s", formatDate(effectDate), formatDate(failureDate)));
        } else if (effectDate != null) {
            holder.tvEffectdate.setText(String.format("%s开始生效", formatDate(effectDate)));
        } else if (failureDate != null) {
            holder.tvEffectdate.setText(String.format("请于%s前使用", formatDate(failureDate)));
        }
        holder.ivCoupon.setImageResource(R.drawable.coupon_flow);
        if(hasBind(servicePackage)){
            holder.ivtrafficUsed.setImageResource(R.drawable.ic_roamdata_used);
        }else{
            holder.ivtrafficUsed.setImageResource(0);
        }
        return contentView;
    }
    class ViewHolder {
        //流量卡id
        TextView tvName;
        ImageView ivCoupon;
        //使用时间
        TextView tvEffectdate;
        ImageView ivtrafficUsed;
    }
}
