package com.roamtech.telephony.roamapp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.BaseCheckBoxAdapter;
import com.roamtech.telephony.roamapp.db.model.BlacklistDBModel;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuListView;

import java.util.List;

/**
 * Created by caibinglong
 * on 2016/12/5.
 */

public class SwipeBlacklistAdapter extends BaseCheckBoxAdapter<BlacklistDBModel> {
    private boolean showChoose = false;

    public SwipeBlacklistAdapter(BaseActivity activity, SwipeMenuListView swipeMenuListView, List<BlacklistDBModel> simpleBlacklist) {
        super(activity, swipeMenuListView, simpleBlacklist);
    }

    @Override
    protected View createSwipeItemLayout() {
        SwipeBlacklistAdapter.ViewHolder holder = new SwipeBlacklistAdapter.ViewHolder();
        ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.item_blacklist, null);
        holder.contentInner = itemView.findViewById(R.id.id_contentInner_item);
        holder.handle = (ImageView) itemView.findViewById(R.id.id_handle_item);
        holder.number = (TextView) itemView.findViewById(R.id.tv_phone);
        holder.user = (TextView) itemView.findViewById(R.id.tv_address);
        itemView.setTag(holder);
        return itemView;
    }

    @Override
    protected void bindSwipeItemData(SwipeViewHolder swipeViewHolder, int position) {
        ViewHolder holder = (ViewHolder) swipeViewHolder;
        holder.position = position;
        BlacklistDBModel model = getItem(position);
        String number = model.phone;
        boolean selected = model.isSelect;
        holder.number.setText(number);
        holder.user.setText(model.area);
        /** 根据是否选中选择按钮样式 **/
        holder.handle.setVisibility(showChoose ? View.VISIBLE : View.INVISIBLE);
        holder.handle.setImageResource(selected ? R.drawable.ic_choosed : R.drawable.ic_unchoose);
    }

    public void setChooseState(boolean showChoose) {
        this.showChoose = showChoose;
    }

    class ViewHolder extends SwipeViewHolder {
        TextView number;
        /**** end ****/
        /***
         * 各属性值
         ***/
        TextView user;
    }
}
