package com.roamtech.telephony.roamapp.widget.sortlist;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.bean.UserHeadBean;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.util.VerificationUtil;
import com.roamtech.telephony.roamapp.view.RoundImageView;

import java.util.List;
import java.util.Locale;


public class ContactsSortAdapter extends RMBaseAdapter<RDContact> implements SectionIndexer {
    public ContactsSortAdapter(BaseActivity context, List<RDContact> list) {
        super(context, list);
    }

    public View getView(final int position, View view, ViewGroup arg2) {
        ViewHolder viewHolder;
        final RDContact contact = getItem(position);
        if (view == null) {
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.item_contact, null);
            viewHolder.tvUser = (TextView) view.findViewById(R.id.tv_user);
            viewHolder.tv_phone_number = (TextView) view.findViewById(R.id.tv_phone_number);
            viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
            viewHolder.tvPhoto = (RoundImageView) view.findViewById(R.id.id_circle_image);
            viewHolder.lineView = view.findViewById(R.id.id_driverline);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        //根据position获取分类的首字母的Char ascii值
        int section = getSectionForPosition(position);

        //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(contact.getSortLetter());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }
        //分割线控制
        if (isLetterEnd(position)) {
            viewHolder.lineView.setVisibility(View.GONE);
        } else {
            viewHolder.lineView.setVisibility(View.VISIBLE);
        }
        RDContactHelper.setHeadPhotoDisplay(viewHolder.tvPhoto, contact, position);
        String displayName = contact.getDisplayName();
        if (!TextUtils.isEmpty(displayName)) {
            viewHolder.tvUser.setText(displayName);
        } else {
            viewHolder.tvUser.setText("未知");
        }
        return view;
    }

    //是否是个字的最后一项 则隐藏那个分割线
    private boolean isLetterEnd(int position) {
        int nextPosition = position + 1;
        if (getCount() > nextPosition) {
            //根据position获取分类的首字母的Char ascii值
            int nextSection = getSectionForPosition(nextPosition);
            if (nextPosition == getPositionForSection(nextSection)) {
                return true;
            }
        }
        return false;
    }

    class ViewHolder {
        TextView tvLetter;
        TextView tvUser;
        TextView tv_phone_number;
        RoundImageView tvPhoto;
        View lineView;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return getItem(position).getSortLetter().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = getItem(i).getSortLetter();
            char firstChar = sortStr.toUpperCase(Locale.CHINESE).charAt(0);
            if (firstChar == section) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}