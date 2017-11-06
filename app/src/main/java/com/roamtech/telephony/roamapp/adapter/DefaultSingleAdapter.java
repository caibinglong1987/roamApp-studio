package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;

/**
 * * User: cbl
 * Date: 2016/4/22
 * Time: 14:12
 */
public class DefaultSingleAdapter extends SettingSingleSelectAdapter<String> {
    public DefaultSingleAdapter(Context context) {
        super(context);
    }

    @Override
    public String getItemTitle(int position) {
        return getItemData(position);
    }

}
