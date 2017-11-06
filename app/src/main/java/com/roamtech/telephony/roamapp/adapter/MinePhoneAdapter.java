package com.roamtech.telephony.roamapp.adapter;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.bean.Phone;

public class MinePhoneAdapter extends RMBaseAdapter<Phone> {
	private Drawable ic_selected;
	private Drawable ic_unSelected;
	public MinePhoneAdapter(BaseActivity activity, List<Phone> data) {
		super(activity, data);
		// TODO Auto-generated constructor stub
		ic_selected = getDrawable(R.drawable.ic_choosed);
		ic_unSelected = getDrawable(R.drawable.ic_unchoose);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_simple_list, parent,
					false);
		}
		Phone phone = getItem(position);
		TextView tvPhone = (TextView) convertView;
		if (phone.isSelected()) {
			tvPhone.setCompoundDrawables(null, null, ic_selected, null);
		}else {
			tvPhone.setCompoundDrawables(null, null, ic_unSelected, null);
		}
		tvPhone.setText(phone.getAreaCode()+" "+phone.getPhoneNumber());
		return convertView;
	}

}
