package com.roamtech.telephony.roamapp.adapter;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.base.BaseActivity;
import com.roamtech.telephony.roamapp.base.RMBaseAdapter;
import com.roamtech.telephony.roamapp.bean.WiFi;

public class WiFiAdapter extends RMBaseAdapter<WiFi> {
	private Drawable ic_selected;
	private Drawable ic_unSelected;
	public WiFiAdapter(BaseActivity activity, List<WiFi> data) {
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
		WiFi wifi = getItem(position);
		TextView tvWifi = (TextView) convertView;
		if (wifi.isSelected()) {
			tvWifi.setCompoundDrawables(null, null, ic_selected, null);
		}else {
			tvWifi.setCompoundDrawables(null, null, ic_unSelected, null);
		}
		tvWifi.setText(wifi.getSsid());
		return convertView;
	}

}
