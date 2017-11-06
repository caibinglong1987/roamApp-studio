package com.roamtech.telephony.roamapp.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.StringFormatUtil;
import com.roamtech.telephony.roamapp.view.RoundImageView;


public class ContactSearchAdapter extends RMBaseAdapter<RDContact> {
    private String searchText;
    private List<RDContact> mSelectedList;
    private boolean isShowChoose = false;

    public ContactSearchAdapter(BaseActivity activity, List<RDContact> mData, List<RDContact> selectContacts) {
        super(activity, mData);
        mSelectedList = new ArrayList<>();
        mSelectedList.addAll(selectContacts);
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setShowChoose(boolean isShow) {
        this.isShowChoose = isShow;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup arg2) {

        ViewHolder holder;
        if (contentView == null) {
            holder = new ViewHolder();
            contentView = LayoutInflater.from(mActivity).inflate(R.layout.item_contact_search, null);
            holder.user = (TextView) contentView.findViewById(R.id.tv_user);
            holder.phoneNumber = (TextView) contentView.findViewById(R.id.tv_phonenumber);
            holder.areaCode = (TextView) contentView.findViewById(R.id.tv_areacode);
            holder.headPhoto = (RoundImageView) contentView.findViewById(R.id.id_circle_image);
            holder.ivSelect = (ImageView) contentView.findViewById(R.id.iv_select);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }
        RDContact contact = getItem(position);
        RDContactHelper.setHeadPhotoDisplay(holder.headPhoto, contact, position);

        /***设置textView 字体显示两种颜色*/
        String wholeStr = contact.getDialPhone() != null ? contact.getDialPhone().getNumber() : "";
        StringFormatUtil spanStr = new StringFormatUtil(mActivity, wholeStr, searchText, R.color.roam_color).fillColor();
        //holder.tvAreaCode.setText(phone.getAreaCode());
        if (spanStr != null) {
            holder.phoneNumber.setText(spanStr.getResult());
        } else {
            holder.phoneNumber.setText(wholeStr);
        }
        spanStr = new StringFormatUtil(mActivity, contact.getDisplayName(), searchText, R.color.roam_color).fillColor();
        if (spanStr != null) {
            holder.user.setText(spanStr.getResult());
        } else {
            holder.user.setText(contact.getDisplayName());
        }
        if (isShowChoose) {
            holder.ivSelect.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelect.setVisibility(View.GONE);
        }
        if (isSelected(contact)) {
            holder.ivSelect.setImageResource(R.drawable.ic_choosed);
        } else {
            holder.ivSelect.setImageResource(R.drawable.ic_unchoose);
        }
        return contentView;
    }

    public static class ViewHolder {
        TextView user;
        TextView areaCode;
        TextView phoneNumber;
        RoundImageView headPhoto;
        public ImageView ivSelect;
    }

    private boolean isSelected(RDContact model) {
        return mSelectedList.contains(model);
        //return true;
    }

    public boolean toggleChecked(int position) {
        if (isSelected(mData.get(position))) {
            removeSelected(position);
            return false;
        } else {
            setSelected(position);
            return true;
        }

    }

    private void setSelected(int position) {
        if (!mSelectedList.contains(mData.get(position))) {
            mSelectedList.add(mData.get(position));
        }
    }

    private void removeSelected(int position) {
        if (mSelectedList.contains(mData.get(position))) {
            mSelectedList.remove(mData.get(position));
        }
    }

    public List<RDContact> getSelectedList() {
        return mSelectedList;
    }
}
