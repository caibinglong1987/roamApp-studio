package com.roamtech.telephony.roamapp.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.bean.CallLog;

import java.util.List;


public class CallLogInfoAdapter extends RMBaseAdapter<CallLog> {

    public CallLogInfoAdapter(BaseActivity activity, List<CallLog> data) {
        super(activity, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_calllog_info, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.ivStateicon = (ImageView) convertView.findViewById(R.id.ivStateicon);
            viewHolder.tvStateText = (TextView) convertView.findViewById(R.id.tvStateText);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.ivSaveVoice = (ImageView) convertView.findViewById(R.id.ivsaveVoice);
            viewHolder.tvCallTime = (TextView) convertView.findViewById(R.id.tv_call_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CallLog calllog = getItem(position);
        if (calllog.getState() == 0) {
            viewHolder.ivStateicon.setImageResource(R.drawable.ic_call_no_recieve);
            viewHolder.tvStateText.setText("未接来电");
            viewHolder.tvStateText.setTextColor(res.getColor(R.color.red_dark));
        } else if (calllog.getState() == 1) {
            viewHolder.ivStateicon.setImageResource(R.drawable.ic_call_in);

            viewHolder.tvStateText.setText("已接来电");
            viewHolder.tvStateText.setTextColor(res.getColor(R.color.text_black));
        } else if (calllog.getState() == 2) {
            viewHolder.ivStateicon.setImageResource(R.drawable.ic_call_out);
            viewHolder.tvStateText.setText("呼出电话");
            viewHolder.tvStateText.setTextColor(res.getColor(R.color.green));
        }
        if (calllog.isSaveVoice()) {
            viewHolder.ivSaveVoice.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ivSaveVoice.setVisibility(View.GONE);
        }
        viewHolder.tvTime.setText("00:59");
        viewHolder.tvCallTime.setText(calllog.getCallTime());
        return convertView;
    }

    class ViewHolder {
        ImageView ivStateicon;
        TextView tvStateText;
        TextView tvTime;
        ImageView ivSaveVoice;
        TextView tvCallTime;

    }
}
