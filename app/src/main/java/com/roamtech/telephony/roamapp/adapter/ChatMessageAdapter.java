package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.bean.RDContact;
import com.roamtech.telephony.roamapp.db.model.ChatDBModel;
import com.roamtech.telephony.roamapp.util.StringFormatUtil;
import com.roamtech.telephony.roamapp.util.StringUtil;
import com.roamtech.telephony.roamapp.view.ProgressBarItem;
import com.will.common.tool.time.DateTimeTool;

import org.linphone.Contact;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by caibinglong
 * on 2016/12/6.
 */

public class ChatMessageAdapter extends RMBaseAdapter<ChatDBModel> {

    private RDContact mContact = null;
    private boolean mEditing = false;
    private int mSelectId;
    private int mSelectPos;
    private iCallback callback;
    private List<ChatDBModel> mSelectedList = new ArrayList<>();

    public ChatMessageAdapter(BaseActivity activity, List<ChatDBModel> data) {
        super(activity, data);
    }

    public void setContact(RDContact contact) {
        this.mContact = contact;
    }

    public void setCallback(iCallback callback) {
        this.callback = callback;
    }

    public void setEditing(boolean editing, int selectId) {
        mEditing = editing;
        mSelectId = selectId;

        for (ChatDBModel msg : mData) {
            if (msg.logId == mSelectId) {
                if (!mSelectedList.contains(msg)) {
                    mSelectPos = mData.indexOf(msg);
                    mSelectedList.add(msg);
                }
            }
        }
    }

    public int getSelectPos() {
        return mSelectPos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ChatDBModel model = mData.get(position);
        ViewHolder viewHolder;
        viewHolder = new ViewHolder();
        if (model.direction == 0) {
            convertView = inflater.inflate(R.layout.item_chatting_msg_text_left, parent, false);
        } else {
            convertView = inflater.inflate(R.layout.item_chatting_msg_text_right, parent, false);
        }
        viewHolder.cbChecked = (CheckBox) convertView.findViewById(R.id.cbChecked);
        viewHolder.ivUserPhoto = (ImageView) convertView.findViewById(R.id.iv_userhead);
        viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
        viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
        viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
        viewHolder.progressBarItem = (ProgressBarItem) convertView.findViewById(R.id.iv_chatstate);
        convertView.setTag(viewHolder);
        viewHolder.position = position;

        if (position == 0) {
            viewHolder.tvSendTime.setVisibility(View.VISIBLE);
            viewHolder.tvSendTime.setText(timestampToHumanDate(mActivity, model.timestamp));
        } else {
            long lastTime = mData.get(position - 1).timestamp;
            if (DateTimeTool.getCompareValue(lastTime, model.timestamp, DateTimeTool.FORMAT_MINUTE) > 2) {
                viewHolder.tvSendTime.setVisibility(View.VISIBLE);
                viewHolder.tvSendTime.setText(timestampToHumanDate(mActivity, model.timestamp));
            } else {
                viewHolder.tvSendTime.setVisibility(View.GONE);
            }
        }
        if (model.direction == 0 && mContact != null && mContact.getHeadPhoto() != null) {
            viewHolder.ivUserPhoto.setImageBitmap(mContact.getHeadPhoto());
        } else {
            viewHolder.ivUserPhoto.setImageResource(R.drawable.logo_default_userphoto);
        }

        if (model.direction != 0) {
            switch (model.messageStatus) {  //1发送成功
                case 0:
                    viewHolder.progressBarItem.setVisibility(View.VISIBLE);
                    viewHolder.progressBarItem.setBackgroundResource(R.drawable.ic_message_fail);
                    viewHolder.progressBarItem.setImageDrawable(null);
                    if (!mEditing) {
                        viewHolder.progressBarItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (callback != null) {
                                    callback.resendMessage(model);
                                }
                            }
                        });
                    }
                    break;
                case 1:
                    viewHolder.progressBarItem.setVisibility(View.GONE);
                    break;
                case 2:
                    viewHolder.progressBarItem.setVisibility(View.VISIBLE);
                    viewHolder.progressBarItem.setBackgroundResource(R.drawable.loading_bg);
                    viewHolder.progressBarItem.setImageResource(R.drawable.list_loading);
                    break;
            }
        }

        if (!StringUtil.isBlank(model.message)) {
            Spanned text = getTextWithHttpLinks(model.message);
            viewHolder.tvContent.setText(text);
            viewHolder.tvContent.setVisibility(View.VISIBLE);

            if (!mEditing) {
                viewHolder.tvContent.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (callback != null) {
                            callback.longClick(view, model);
                        }
                        return false;
                    }
                });
            }
            viewHolder.tvContent.setTag(convertView);
        } else {
            viewHolder.tvContent.setVisibility(View.GONE);
        }

        viewHolder.ivUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.userPhotoClick(model);
                }
            }
        });

        if (mEditing) {
            viewHolder.cbChecked.setVisibility(View.VISIBLE);
            viewHolder.cbChecked.setChecked(isSelected(model));
            convertView.setClickable(true);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!mEditing) {
                        if (callback != null) {
                            callback.resendMessage(model);
                        }
                    } else {
                        ViewHolder viewHolder = (ViewHolder) view.getTag();
                        toggleChecked(viewHolder.position);
                        viewHolder.cbChecked.setChecked(!viewHolder.cbChecked.isChecked());
                    }
                }
            });
        }
        return convertView;
    }

    public static class ViewHolder {
        ImageView ivUserPhoto;
        CheckBox cbChecked;
        TextView tvSendTime;
        TextView tvUserName;
        TextView tvContent;
        ProgressBarItem progressBarItem;
        int position;
    }

    public void toggleChecked(int position) {
        if (isSelected(mData.get(position))) {
            if (mSelectedList.contains(mData.get(position))) {
                mSelectedList.remove(mData.get(position));
            }
        } else {
            if (!mSelectedList.contains(mData.get(position))) {
                mSelectedList.add(mData.get(position));
            }
        }

    }

    private Spanned getTextWithHttpLinks(String text) {
        text = text.replace("\n", "<br/>");
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

        return Html.fromHtml(text);
    }

    private boolean isSelected(ChatDBModel msg) {
        return mSelectedList.contains(msg);
    }

    public void selectAll(boolean bSelectAll) {
        mSelectedList.clear();
        if (bSelectAll) {
            mSelectedList.addAll(mData);
        }
    }

    public List<ChatDBModel> getSelectedList() {
        return mSelectedList;
    }

    private String timestampToHumanDate(Context context, long timestamp) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);
            SimpleDateFormat dateFormat;
            if (DateTimeTool.isToday(cal)) {
                dateFormat = new SimpleDateFormat(context.getString(R.string.today_date_format));
            } else if (DateTimeTool.isYesterday(cal)) {
                dateFormat = new SimpleDateFormat(context.getString(R.string.messages_yesterday_format));
            } else {
                dateFormat = new SimpleDateFormat(context.getString(R.string.messages_date_format));
            }
            return dateFormat.format(cal.getTime());
        } catch (NumberFormatException nfe) {
            return String.valueOf(timestamp);
        }
    }

    public interface iCallback {
        void longClick(View view, ChatDBModel model);

        void resendMessage(ChatDBModel message);

        void userPhotoClick(ChatDBModel message);
    }
}
