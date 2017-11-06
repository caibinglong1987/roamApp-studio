package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.CallDetailActivity;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.BaseCheckBoxAdapter;
import com.roamtech.telephony.roamapp.bean.CallLogData;
import com.roamtech.telephony.roamapp.bean.UserHeadBean;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.view.RoundImageView;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuListView;

import java.util.List;


public class SwipeCallLogGroupAdapter extends BaseCheckBoxAdapter<CallLogData> {
    private Context context;

    public SwipeCallLogGroupAdapter(BaseActivity activity, SwipeMenuListView swipeMenuListView, List<CallLogData> mData) {
        super(activity, swipeMenuListView, mData);
        context = mActivity.getApplicationContext();
    }

    @Override
    protected View createSwipeItemLayout() {
        ViewHolder holder = new ViewHolder();
        ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.item_call_log, null);
        holder.contentInner = itemView.findViewById(R.id.id_contentInner_item);
        holder.handle = (ImageView) itemView.findViewById(R.id.id_handle_item);
        holder.user = (TextView) itemView.findViewById(R.id.tv_user);
        holder.callTime = (TextView) itemView.findViewById(R.id.tv_call_time);
        holder.headPhoto = (RoundImageView) itemView.findViewById(R.id.id_circle_image);
        holder.ivCallState = (ImageView) itemView.findViewById(R.id.iv_call_state);
        holder.layout_details = (LinearLayout) itemView.findViewById(R.id.layout_details);
        holder.tv_area = (TextView) itemView.findViewById(R.id.tv_area);
        itemView.setTag(holder);
        return itemView;
    }

    @Override
    protected void bindSwipeItemData(SwipeViewHolder swipeViewHolder,final int position) {
        ViewHolder holder = (ViewHolder) swipeViewHolder;
        final CallLogData data = getItem(position);
        holder.ivCallState.setImageBitmap(data.getCallStatusBitmap());
        final String toNumber = data.getToNumber();

        final UserHeadBean userHeadBean = RDContactHelper.getUserHeadBean(context, toNumber, position);
        holder.headPhoto.setImageBitmap(userHeadBean.headPhoto);
        holder.headPhoto.setText(userHeadBean.headPhotoText, userHeadBean.headTextSize);
        holder.user.setText(userHeadBean.displayName);
        holder.layout_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("phoneNumber", toNumber);
                bundle.putString("from", data.getFrom());
                bundle.putInt("position", position);
                bundle.putParcelable("bitmap", userHeadBean.headPhoto);
                mActivity.toActivity(CallDetailActivity.class, bundle);
            }
        });
        holder.callTime.setText(data.getCallTime());
        // holder.attribution.setText("未知");
        /** 根据是否选中选择按钮样式 **/
        holder.handle.setImageResource(data.isSelect() ? R.drawable.ic_choosed : R.drawable.ic_unchoose);
        holder.tv_area.setText(data.getArea());
        if (toNumber.equals(context.getString(R.string.roamPhone))) {
            holder.tv_area.setVisibility(View.GONE);
        } else {
            holder.tv_area.setVisibility(View.VISIBLE);
        }
    }

    class ViewHolder extends SwipeViewHolder {
        /***
         * 各属性值
         ***/
        TextView user;
        TextView attribution;
        RoundImageView headPhoto;
        TextView callTime;
        ImageView ivCallState;
        LinearLayout layout_details;
        TextView tv_area;
    }

}
