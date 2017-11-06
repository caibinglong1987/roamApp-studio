package com.roamtech.telephony.roamapp.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.BaseCheckBoxAdapter;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.bean.SimpleMessage;
import com.roamtech.telephony.roamapp.bean.UserHeadBean;
import com.roamtech.telephony.roamapp.helper.CallLogDataHelper;
import com.roamtech.telephony.roamapp.helper.RDContactHelper;
import com.roamtech.telephony.roamapp.view.RoundImageView;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuListView;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by caibinglong
 * on 2016/12/5.
 */

public class SwipeMessageNewAdapter extends BaseCheckBoxAdapter<SimpleMessage> {
    public SwipeMessageNewAdapter(BaseActivity activity, SwipeMenuListView swipeMenuListView, List<SimpleMessage> simpleMessageList) {
        super(activity, swipeMenuListView, simpleMessageList);
    }

    @Override
    protected View createSwipeItemLayout() {
        SwipeMessageNewAdapter.ViewHolder holder = new SwipeMessageNewAdapter.ViewHolder();
        ViewGroup itemView = (ViewGroup) inflater.inflate(R.layout.item_message_swipe_content, null);
        holder.contentInner = itemView.findViewById(R.id.id_contentInner_item);
        holder.handle = (ImageView) itemView.findViewById(R.id.id_handle_item);
        holder.user = (TextView) itemView.findViewById(R.id.tv_user);
        holder.lastinfo = (TextView) itemView.findViewById(R.id.tv_last_message);
        holder.callTime = (TextView) itemView.findViewById(R.id.tv_call_time);
        holder.headPhoto = (RoundImageView) itemView.findViewById(R.id.id_circle_image);
        holder.messageNumber = (TextView) itemView.findViewById(R.id.tv_message_number);
        itemView.setTag(holder);
        return itemView;
    }

    @Override
    protected void bindSwipeItemData(SwipeViewHolder swipeViewHolder, int position) {
        ViewHolder holder = (ViewHolder) swipeViewHolder;
        holder.position = position;
        SimpleMessage simpleMessage = getItem(position);
        String number = simpleMessage.getContact();
        boolean selected = simpleMessage.isSelect();
        holder.number = number;
        RDContact contact = RDContactHelper.findContactByPhone(number);
        String message = simpleMessage.message;
        long time = simpleMessage.time;
        if (simpleMessage.msgType == 1) {
            holder.user.setText(mActivity.getString(R.string.str_message_system));
        } else {
            if (contact != null) {
                holder.user.setText(getTextWithHttpLinks(contact.getDisplayName(), simpleMessage.searchKey));
            } else {
                holder.user.setText(getTextWithHttpLinks(number, simpleMessage.searchKey));
            }
        }
        holder.lastinfo.setText(getTextWithHttpLinks(message, simpleMessage.searchKey));
        UserHeadBean userHeadBean = RDContactHelper.getUserHeadBean(mActivity, number, position);
        holder.headPhoto.setImageBitmap(userHeadBean.headPhoto);
        holder.headPhoto.setText(userHeadBean.headPhotoText, userHeadBean.headTextSize);

        /** 根据是否选中选择按钮样式 **/
        holder.handle.setImageResource(selected ? R.drawable.ic_choosed : R.drawable.ic_unchoose);
        holder.callTime.setText(CallLogDataHelper.timestampToHumanDate(mActivity.getResources(), time));
        if (simpleMessage.number > 0) {
            holder.messageNumber.setVisibility(View.VISIBLE);
            holder.messageNumber.setText(String.valueOf(simpleMessage.number));
        } else {
            holder.messageNumber.setVisibility(View.GONE);
        }
    }

    public SpannableString getTextWithHttpLinks(String text, String keyword) {

        //text = text.replace("\n", "<br/>");

        /*if (text.contains("<")) {
            text = text.replace("<", "&lt;");
		}
		if (text.contains(">")) {
			text = text.replace(">", "&gt;");
		}*/
        if (text.contains("http://")) {
            int indexHttp = text.indexOf("http://");
            int indexFinHttp = text.indexOf(" ", indexHttp) == -1 ? text.length() : text.indexOf(" ", indexHttp);
            String link = text.substring(indexHttp, indexFinHttp);
            String linkWithoutScheme = link.replace("http://", "");
            text = text.replaceFirst(link, "<a href=\"" + link + "\">" + linkWithoutScheme + "</a>");
        }
        if (text.contains("https://")) {
            int indexHttp = text.indexOf("https://");
            int indexFinHttp = text.indexOf(" ", indexHttp) == -1 ? text.length() : text.indexOf(" ", indexHttp);
            String link = text.substring(indexHttp, indexFinHttp);
            String linkWithoutScheme = link.replace("https://", "");
            text = text.replaceFirst(link, "<a href=\"" + link + "\">" + linkWithoutScheme + "</a>");
        }

        return setKeyWordColor(text, keyword);
        // return Html.fromHtml(text);
    }

    /**
     * 设置搜索关键字高亮
     *
     * @param content 原文本内容
     * @param keyword 关键字
     */
    private SpannableString setKeyWordColor(String content, String keyword) {
        SpannableString s = new SpannableString(content);
        if (keyword != null) {
            Pattern p = Pattern.compile(keyword);
            Matcher m = p.matcher(s);
            while (m.find()) {
                int start = m.start();
                int end = m.end();
                s.setSpan(new ForegroundColorSpan(Color.parseColor("#0bd3a6")), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            return s;
        }
        return s;
    }

    class ViewHolder extends SwipeViewHolder {
        String number;
        /**** end ****/
        /***
         * 各属性值
         ***/
        TextView user;
        TextView lastinfo;
        RoundImageView headPhoto;
        TextView callTime;
        TextView messageNumber;
    }
}
