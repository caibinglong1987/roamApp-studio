package com.roamtech.telephony.roamapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;


/**
 * User: cbl
 * Date: 2016/10/22
 * Time: 14:12
 */
public abstract class SettingSingleSelectAdapter<T> extends BaseSingleSelectStatusAdapter<T> {
    private static OnItemClickListener mOnItemClickListener;

    public SettingSingleSelectAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SettingSingleSelectViewHolder(mLayoutInflater.inflate(R.layout.item_setting_select, parent, false), this);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SettingSingleSelectViewHolder) {
            ((SettingSingleSelectViewHolder) holder).bindViewData(getItemTitle(position), position);
        }
    }

    public abstract String getItemTitle(int position);

    static class SettingSingleSelectViewHolder extends RecyclerView.ViewHolder {
        TextView mTvName;
        ImageView mIvCheck;
        SettingSingleSelectAdapter mAdapter;

        SettingSingleSelectViewHolder(View view, SettingSingleSelectAdapter adapter) {
            super(view);
            mTvName = (TextView) view.findViewById(R.id.tv_name);
            mIvCheck = (ImageView) view.findViewById(R.id.iv_check);
            mAdapter = adapter;
        }

        public void bindViewData(String name, final int position) {
            mIvCheck.setVisibility((position == mAdapter.mCurrentSelect) ? View.VISIBLE : View.GONE);
            mTvName.setText(name);
            mTvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(v, position);
                    }
                }
            });
        }
    }


    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }
}
