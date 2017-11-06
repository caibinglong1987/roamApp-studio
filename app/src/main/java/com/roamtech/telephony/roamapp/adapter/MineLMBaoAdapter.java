package com.roamtech.telephony.roamapp.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.db.model.TouchDBModel;


public class MineLMBaoAdapter extends RMBaseAdapter<TouchDBModel> {

	public MineLMBaoAdapter(BaseActivity activity, List<TouchDBModel> data) {
		super(activity, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder=null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_lmbao_list, parent,
					false);
			viewHolder=new ViewHolder();
			viewHolder.roamName=(TextView) convertView.findViewById(R.id.tv_romanname);
			viewHolder.phoneNumber=(TextView) convertView.findViewById(R.id.tv_phonenumber);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		TouchDBModel touch = getItem(position);
		viewHolder.roamName.setText(touch.getDevid());
		viewHolder.phoneNumber.setText(touch.getPhone());
		return convertView;
	}
	class ViewHolder {
		TextView roamName;
		TextView phoneNumber;
	}
}
