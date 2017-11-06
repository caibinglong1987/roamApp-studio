package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.ChattingActivity;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.view.ProgressBarItem;
import com.will.common.tool.time.DateTimeTool;

import org.linphone.Contact;
import org.linphone.core.LinphoneChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;


/**
 * 消息ListView的Adapter
 *
 * @author way
 */
public class ChatMsgViewAdapter extends RMBaseAdapter<LinphoneChatMessage> implements  OnClickListener {
    private static final HashMap<String, Integer> emoticons = new HashMap<String, Integer>();

    static {
        emoticons.put(":)", R.drawable.emo_im_happy);
        emoticons.put(":-)", R.drawable.emo_im_happy);
        emoticons.put(":(", R.drawable.emo_im_sad);
        emoticons.put(":-(", R.drawable.emo_im_sad);
        emoticons.put(":-P", R.drawable.emo_im_tongue_sticking_out);
        emoticons.put(":P", R.drawable.emo_im_tongue_sticking_out);
        emoticons.put(";-)", R.drawable.emo_im_winking);
        emoticons.put(";)", R.drawable.emo_im_winking);
        emoticons.put(":-D", R.drawable.emo_im_laughing);
        emoticons.put(":D", R.drawable.emo_im_laughing);
        emoticons.put("8-)", R.drawable.emo_im_cool);
        emoticons.put("8)", R.drawable.emo_im_cool);
        emoticons.put("O:)", R.drawable.emo_im_angel);
        emoticons.put("O:-)", R.drawable.emo_im_angel);
        emoticons.put(":-*", R.drawable.emo_im_kissing);
        emoticons.put(":*", R.drawable.emo_im_kissing);
        emoticons.put(":-/", R.drawable.emo_im_undecided);
        emoticons.put(":/ ", R.drawable.emo_im_undecided); // The space after is needed to avoid bad display of links
        emoticons.put(":-\\", R.drawable.emo_im_undecided);
        emoticons.put(":\\", R.drawable.emo_im_undecided);
        emoticons.put(":-O", R.drawable.emo_im_surprised);
        emoticons.put(":O", R.drawable.emo_im_surprised);
        emoticons.put(":-@", R.drawable.emo_im_yelling);
        emoticons.put(":@", R.drawable.emo_im_yelling);
        emoticons.put("O.o", R.drawable.emo_im_wtf);
        emoticons.put("o.O", R.drawable.emo_im_wtf);
        emoticons.put(":'(", R.drawable.emo_im_crying);
        emoticons.put("$.$", R.drawable.emo_im_money_mouth);
    }

    public static interface IMsgViewType {
        int IMVT_COM_MSG = 0;// 收到对方的消息
        int IMVT_TO_MSG = 1;// 自己发送出去的消息
    }

    private static final int ITEMCOUNT = 2;// 消息类型的总数
    private Contact mContact;
    private boolean mEditting = false;
    private int mSelectId;
    private Drawable ic_selected;
    private Drawable ic_unSelected;
    private List<LinphoneChatMessage> mSelectedList;
    private int mSelectPos;
    private iCallback callback;
    private boolean isSelf = true;
    public ChatMsgViewAdapter(BaseActivity activity, List<LinphoneChatMessage> data, Contact contact) {
        super(activity, data);
        mContact = contact;
        mSelectedList = new ArrayList<>();
    }

    public void setCallback(iCallback callback) {
        this.callback = callback;
    }

    public void setEditting(boolean editting, int selectId) {
        mEditting = editting;
        mSelectId = selectId;

        for (LinphoneChatMessage msg : mData) {
            if (msg.getStorageId() == mSelectId) {
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

    /**
     * 得到Item的类型，是对方发过来的消息，还是自己发送出去的
     */
    public int getItemViewType(int position) {
        LinphoneChatMessage entity = mData.get(position);

        if (entity.isOutgoing()) {//自己发送的消息
            return IMsgViewType.IMVT_TO_MSG;
        } else {//收到的消息
            return IMsgViewType.IMVT_COM_MSG;
        }
    }

    /**
     * Item类型的总数
     */
    public int getViewTypeCount() {
        return ITEMCOUNT;
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).getStorageId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
       final LinphoneChatMessage entity = mData.get(position);
        boolean isOutMsg = entity.isOutgoing();
        ViewHolder viewHolder;
        if (convertView == null) {
            if (!isOutMsg) {
                convertView = inflater.inflate(R.layout.item_chatting_msg_text_left, parent, false);
            } else {
                convertView = inflater.inflate(R.layout.item_chatting_msg_text_right, parent, false);
            }
            viewHolder = new ViewHolder();
            viewHolder.cbChecked = (CheckBox) convertView.findViewById(R.id.cbChecked);
            viewHolder.ivUserPhoto = (ImageView) convertView.findViewById(R.id.iv_userhead);
            viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            viewHolder.isComMsg = !isOutMsg;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        convertView.setId(entity.getStorageId());
        viewHolder.position = position;
        if (mEditting) {
            convertView.setClickable(true);
            convertView.setOnClickListener(this);
        }
        long thisTime = entity.getTime();
        if (position == 0) {
            viewHolder.tvSendTime.setVisibility(View.VISIBLE);
            viewHolder.tvSendTime.setText(timestampToHumanDate(mActivity, thisTime));
        } else {
            long lastTime = mData.get(position - 1).getTime();
            if (DateTimeTool.getCompareValue(lastTime, thisTime, DateTimeTool.FORMAT_MINUTE) > 2) {
                viewHolder.tvSendTime.setVisibility(View.VISIBLE);
                viewHolder.tvSendTime.setText(timestampToHumanDate(mActivity, thisTime));
            } else {
                viewHolder.tvSendTime.setVisibility(View.GONE);
            }
        }

//        viewHolder.tvSendTime.setText(timestampToHumanDate(mActivity, entity.getTime()));
//		viewHolder.tvUserName.setText(entity.getName());
        if (!isOutMsg && mContact != null && mContact.getPhoto() != null) {
            viewHolder.ivUserPhoto.setImageBitmap(mContact.getPhoto());
        } else {
            viewHolder.ivUserPhoto.setImageResource(R.drawable.logo_default_userphoto);
        }
        viewHolder.ivUserPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null){
                    callback.userPhotoClick(entity);
                }
            }
        });
        LinphoneChatMessage.State status = entity.getStatus();
        ProgressBarItem statusView = (ProgressBarItem) convertView.findViewById(R.id.iv_chatstate);
        if (statusView != null) {
            if (status == LinphoneChatMessage.State.Delivered) {
                statusView.setVisibility(View.GONE);//.setImageResource(R.drawable.chat_message_delivered);
            } else if (status == LinphoneChatMessage.State.NotDelivered) {
                statusView.setVisibility(View.VISIBLE);
                statusView.setBackgroundResource(R.drawable.ic_message_fail);
                statusView.setImageDrawable(null);
                if (!mEditting) {
                    statusView.setOnClickListener(this);
                }
                statusView.setTag(convertView);
            } else {
                statusView.setVisibility(View.VISIBLE);
                statusView.setBackgroundResource(R.drawable.loading_bg);
                statusView.setImageResource(R.drawable.list_loading);
                //statusView.setImageResource(R.drawable.chat_message_inprogress);
            }
        }
        TextView msgView = viewHolder.tvContent;
        if (msgView != null) {
            Spanned text = null;
            String msg = entity.getText();
            if (msg != null) {
                /*if (mActivity.getResources().getBoolean(R.bool.emoticons_in_messages)) {
                    text = getSmiledText(mActivity, getTextWithHttpLinks(msg));
    	    	} else*/
                {
                    text = getTextWithHttpLinks(msg);
                }
                msgView.setText(text);
                msgView.setMovementMethod(LinkMovementMethod.getInstance());
                msgView.setVisibility(View.VISIBLE);
            }
            if (!mEditting) {
                msgView.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (callback != null) {
                            callback.longClick(view);
                        }
                        return false;
                    }
                });
            }
            msgView.setTag(convertView);
        }
        if (mEditting) {
            viewHolder.cbChecked.setVisibility(View.VISIBLE);
            viewHolder.cbChecked.setChecked(isSelected(entity));
        }
        //viewHolder.tvContent.setText(entity.getText());
        return convertView;
    }

    public static Spannable getSmiledText(Context context, Spanned spanned) {
        SpannableStringBuilder builder = new SpannableStringBuilder(spanned);
        String text = spanned.toString();

        for (Entry<String, Integer> entry : emoticons.entrySet()) {
            String key = entry.getKey();
            int indexOf = text.indexOf(key);
            while (indexOf >= 0) {
                int end = indexOf + key.length();
                builder.setSpan(new ImageSpan(context, entry.getValue()), indexOf, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                indexOf = text.indexOf(key, end);
            }
        }

        return builder;
    }

    public static Spanned getTextWithHttpLinks(String text) {
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

    private String timestampToHumanDate(Context context, long timestamp) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(timestamp);

            SimpleDateFormat dateFormat;
            if (DateTimeTool.isToday(cal)) {
                dateFormat = new SimpleDateFormat(context.getString(R.string.today_date_format));
            } else if (DateTimeTool.isYesterday(cal)){
                dateFormat = new SimpleDateFormat(context.getString(R.string.messages_yesterday_format));
            } else {
                dateFormat = new SimpleDateFormat(context.getString(R.string.messages_date_format));
            }

            return dateFormat.format(cal.getTime());
        } catch (NumberFormatException nfe) {
            return String.valueOf(timestamp);
        }
    }

    public static class ViewHolder {
        public ImageView ivUserPhoto;
        public CheckBox cbChecked;
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public int position;
        public boolean isComMsg = true;
    }

    @Override
    public void onClick(View v) {
        if (!mEditting) {
           // ((ChattingActivity) mActivity).resendMessage(((View) v.getTag()).getId());
        } else {
            ViewHolder viewHolder = (ViewHolder) v.getTag();
            toggleChecked(viewHolder.position);
            viewHolder.cbChecked.setChecked(!viewHolder.cbChecked.isChecked());
        }
    }

    private boolean isSelected(LinphoneChatMessage msg) {
        return mSelectedList.contains(msg);
    }

    public void toggleChecked(int position) {
        if (isSelected(mData.get(position))) {
            removeSelected(position);
        } else {
            setSelected(position);
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

    public List<LinphoneChatMessage> getSelectedList() {
        return mSelectedList;
    }

    public void selectAll(boolean bSelectAll) {
        mSelectedList.clear();
        if (bSelectAll) {
            mSelectedList.addAll(mData);
        }
    }

    /**
     * 事件监听
     */
    public interface iCallback {
        void longClick(View view);
        void userPhotoClick(LinphoneChatMessage message);
    }
}
