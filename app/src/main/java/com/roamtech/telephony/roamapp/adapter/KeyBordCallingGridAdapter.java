package com.roamtech.telephony.roamapp.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.helper.KeyboardCallingHelper.Key;

public class KeyBordCallingGridAdapter extends RMBaseAdapter<Key> {

    public KeyBordCallingGridAdapter(BaseActivity activity, List<Key> data) {
        super(activity, data);
        // TODO Auto-generated constructor stub
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_keyboard_calling_grid, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.tvCode = (TextView) convertView
                    .findViewById(R.id.tv_code);
            viewHolder.tvLetter = (TextView) convertView
                    .findViewById(R.id.tv_letter);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Key key = getItem(position);
        if (key.getCode() != null) {
            viewHolder.tvCode.setText(key.getCode());
        }
        if (key.getLetter() != null) {
            viewHolder.tvLetter.setText(key.getLetter());
        }
        return convertView;
    }

    class ViewHolder {
        TextView tvCode;
        TextView tvLetter;
    }
}
