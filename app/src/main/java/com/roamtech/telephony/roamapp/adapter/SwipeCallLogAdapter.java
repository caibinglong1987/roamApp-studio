package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.CallDetailActivity;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.BaseCheckBoxAdapter;
import com.roamtech.telephony.roamapp.bean.CallLogData;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.view.RoundImageView;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuListView;

import java.util.List;


public class SwipeCallLogAdapter extends BaseCheckBoxAdapter<CallLogData> {

    private Context context;

    public SwipeCallLogAdapter(BaseActivity activity, SwipeMenuListView swipeMenuListView, List<CallLogData> mData) {
        super(activity, swipeMenuListView, mData);
        context = mActivity.getApplicationContext();
    }

    @Override
    protected View createSwipeItemLayout() {
        ViewHolder holder = new ViewHolder();
        ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.item_calllog_swipe_content, null);
        holder.contentInner = itemView.findViewById(R.id.id_contentInner_item);
        holder.handle = (ImageView) itemView.findViewById(R.id.id_handle_item);
        holder.user = (TextView) itemView.findViewById(R.id.tv_user);
        holder.attribution = (TextView) itemView.findViewById(R.id.tv_attribution);
        holder.callTime = (TextView) itemView.findViewById(R.id.tv_call_time);
        holder.headPhoto = (RoundImageView) itemView.findViewById(R.id.id_circle_image);
        holder.ivCallState = (ImageView) itemView.findViewById(R.id.iv_call_state);
        itemView.setTag(holder);
        return itemView;
    }

    @Override
    protected void bindSwipeItemData(SwipeViewHolder swipeViewHolder, int position) {
        ViewHolder holder = (ViewHolder) swipeViewHolder;
        CallLogData data = getItem(position);
        holder.ivCallState.setImageBitmap(data.getCallStatusBitmap());
        String toNumber = data.getToNumber();
        RDContact contact = RDContactHelper.findContactByPhone(toNumber);
        Bitmap bitmap;
        if (!data.isHasHeadPhoto()) {
            if (toNumber.equals(context.getString(R.string.roamPhone))) {
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_service);
                data.setHeadPhotoBitmap(bitmap);
                data.setHasHeadPhoto(true);
                contact = new RDContact();
                contact.setPhotoId(9999999);
            } else {
                bitmap = RDContactHelper.getHeadPhoto(context, contact, position);
                data.setHeadPhotoBitmap(bitmap);
                data.setHasHeadPhoto(true);
            }
        }

        String displayName = data.getUser();
        holder.headPhoto.setImageBitmap(data.getHeadPhotoBitmap());
        if (contact == null || contact.getPhotoId() <= 0) {
            /**设置头像显示首字母***/
            if (data.isHasUser()) {
                holder.headPhoto.setText(displayName.charAt(0) + "", 24);
            } else {
                holder.headPhoto.setText("未知", 12);
            }
        } else {
            //清除复用的view上photo的文字
            holder.headPhoto.setText("", 12);
        }
        if (displayName.equals(context.getString(R.string.roamPhone))) { //络漫客服电话
            holder.user.setText(context.getString(R.string.roamservice));
            //清除复用的view上photo的文字
            holder.headPhoto.setText("", 12);
        } else {
            if (data.getUser().equals("-1")) {
                holder.user.setText(context.getString(R.string.str_unknown_number));
            } else {
                holder.user.setText(data.getUser());
            }
        }
        holder.callTime.setText(data.getCallTime());
        holder.attribution.setText("未知");
        /** 根据是否选中选择按钮样式 **/
        holder.handle.setImageResource(data.isSelect() ? R.drawable.ic_choosed : R.drawable.ic_unchoose);

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
    }

}
