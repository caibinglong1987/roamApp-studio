package com.roamtech.telephony.roamapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.StringFormatUtil;
import com.roamtech.telephony.roamapp.view.RoundImageView;

import java.util.List;

public class KeyboardSearchAdapter extends RMBaseAdapter<RDContact> {
    private String searchText;

    public KeyboardSearchAdapter(BaseActivity activity, List<RDContact> mData) {
        super(activity, mData);
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup arg2) {

        ViewHolder holder = null;
        if (contentView == null) {
            holder = new ViewHolder();
            contentView = LayoutInflater.from(mActivity).inflate(
                    R.layout.item_keyboard_search, null);
            holder.user = (TextView) contentView.findViewById(R.id.tv_user);
            holder.phoneNumber = (TextView) contentView.findViewById(R.id.tv_phonenumber);
            holder.areaCode = (TextView) contentView.findViewById(R.id.tv_areacode);
            holder.headPhoto = (RoundImageView) contentView.findViewById(R.id.id_circle_image);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }
        RDContact contact = getItem(position);
        holder.user.setText(contact.getDisplayName());
        /***设置textView 字体显示两种颜色*/
        String wholeStr = contact.getDialPhone().getNumber();
        StringFormatUtil spanStr = new StringFormatUtil(mActivity, wholeStr,
                searchText, R.color.roam_color).fillColor();
        //holder.tvAreaCode.setText(phone.getAreaCode());
        if (spanStr != null) {
            holder.phoneNumber.setText(spanStr.getResult());
        } else {
            holder.phoneNumber.setText(contact.getDialPhone().getNumber());
        }
        RDContactHelper.setHeadPhotoDisplay(holder.headPhoto, contact, position);
        return contentView;
    }

    class ViewHolder {
        TextView user;
        TextView areaCode;
        TextView phoneNumber;
        RoundImageView headPhoto;
    }

}
