package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;

/**
 * Version    V1.0
 * Description: 单项基类适配器
 * User: cbl
 * Date: 2016/10/22
 */
public abstract class BaseSingleSelectStatusAdapter<T> extends BaseRecyclerAdapter<T> {
    protected int mCurrentSelect = -1;
    protected boolean isEnableSelect = true;

    public BaseSingleSelectStatusAdapter(Context context) {
        super(context);
    }

    public boolean isSelectDate() {
        return mCurrentSelect >= 0;
    }

    public void setEnableSelect(boolean isEnableSelect) {
        this.isEnableSelect = isEnableSelect;
    }

    public int getCurrentSelect() {
        return mCurrentSelect;
    }

    public void setCurrentSelect(int currentSelect) {
        notifyItemChanged(mCurrentSelect);
        this.mCurrentSelect = currentSelect;
        notifyItemChanged(mCurrentSelect);
    }
}
