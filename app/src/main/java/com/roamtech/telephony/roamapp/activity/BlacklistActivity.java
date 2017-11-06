package com.roamtech.telephony.roamapp.activity;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.roamtech.telephony.roamapp.R;
import com.roamtech.telephony.roamapp.activity.function.Blacklist;
import com.roamtech.telephony.roamapp.adapter.SwipeBlacklistAdapter;
import com.roamtech.telephony.roamapp.base.HeaderBaseActivity;
import com.roamtech.telephony.roamapp.db.model.BlacklistDBModel;
import com.roamtech.telephony.roamapp.event.EventBlacklist;
import com.roamtech.telephony.roamapp.helper.numberAttr.NumberHelper;
import com.roamtech.telephony.roamapp.util.LocalDisplay;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenu;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuCreator;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuItem;
import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuListView;
import com.will.web.handle.HttpBusinessCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.roamtech.telephony.roamapp.util.CallMessageUtil.blackDao;

/**
 * Created by caibinglong
 * on 2017/3/8.
 * 黑名单 功能 展示
 */

public class BlacklistActivity extends HeaderBaseActivity {
    private SwipeBlacklistAdapter adapter;
    private SwipeMenuListView listView;
    private TextView tvSelectAll, tvDelete;
    private LinearLayout line_message_tool;
    private ArrayList<BlacklistDBModel> deleteModelList = new ArrayList<>();
    private Blacklist blacklistFun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        headerLayout.showTitle(getString(R.string.contact_blacklist));
        headerLayout.showRightSubmitButton(R.string.button_edit, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.isEmpty()) {
                    return;
                }
                setEditState(!adapter.isEdit());
            }
        });
        listView = (SwipeMenuListView) findViewById(R.id.lv_blacklist);
        tvSelectAll = (TextView) findViewById(R.id.tv_select_all);
        tvDelete = (TextView) findViewById(R.id.tv_delete);
        line_message_tool = (LinearLayout) findViewById(R.id.llyt_message_tool);
        setData();
        setViewListener();
        blacklistFun = new Blacklist(getApplicationContext());
    }

    //监听选中的黑名单条数的辩护
    public void onSelectSizeChange() {
        int selectCount = getSelectBlacklist().size();
        //全部选中
        if (selectCount == adapter.getCount()) {
            tvSelectAll.setText("反选");
        } else {
            tvSelectAll.setText("全选");
        }
        if (selectCount > 0) {
            tvDelete.setEnabled(true);
        } else {
            tvDelete.setEnabled(false);
        }
    }

    /**
     * 获取选中的黑名单记录
     */
    private ArrayList<String> getSelectBlacklist() {
        ArrayList<String> blacklist = new ArrayList<>();
        deleteModelList = new ArrayList<>();
        for (BlacklistDBModel model : adapter.getData()) {
            if (model.isSelect) {
                deleteModelList.add(model);
                blacklist.add(model.phone);
            }
        }
        return blacklist;
    }

    /**
     * 设置 为isSelelct
     *
     * @param isSelect
     */
    private void setAllBlacklistSelect(boolean isSelect) {
        for (BlacklistDBModel model : adapter.getData()) {
            if (model.isSelect != isSelect) {
                model.isSelect = isSelect;
                model.isShow = isSelect;
            }
        }
        onSelectSizeChange();
    }

    private void setData() {
        adapter = new SwipeBlacklistAdapter(BlacklistActivity.this, listView, null);
        listView.setAdapter(adapter);
        listView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
                // set item width
                openItem.setWidth(LocalDisplay.dp2px(90));
                // set item title
                openItem.setTitle(getString(R.string.delete));
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        });
        List<BlacklistDBModel> dbModelList = blackDao.queryAll();
        if (dbModelList != null && dbModelList.size() > 0) {
            for (BlacklistDBModel item : dbModelList) {
                item.area = NumberHelper.getDisplayNameByNumber(getApplicationContext(), item.phone);
            }
        }
        adapter.refreash(dbModelList);
    }

    private void setViewListener() {
        tvSelectAll.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                BlacklistDBModel model = (BlacklistDBModel) adapterView.getItemAtPosition(position);
                if (adapter.isEdit()) {
                    //如果为编辑状态
                    model.isSelect = !model.isSelect;
                    onSelectSizeChange();
                    //每次都刷新影响效率
                    ImageView handleView = (ImageView) view.findViewById(R.id.id_handle_item);
                    handleView.setImageResource(model.isSelect ? R.drawable.ic_choosed : R.drawable.ic_unchoose);
                }
            }
        });
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(int position, SwipeMenu menu, int index) {
                BlacklistDBModel model = adapter.getItem(position);
                ArrayList<String> phoneList = new ArrayList<>();
                phoneList.add(model.phone);
                deleteModelList = new ArrayList<>();
                deleteModelList.add(model);
                removeBlacklist(phoneList);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_select_all:
                if (tvSelectAll.getText().equals("全选")) {
                    setAllBlacklistSelect(true);
                } else {
                    setAllBlacklistSelect(false);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.tv_delete:
                ArrayList<String> phoneList = getSelectBlacklist();
                removeBlacklist(phoneList);
                setEditState(false);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 顶部按钮状态变化
     *
     * @param isEdit boolean
     */
    private void setEditState(boolean isEdit) {
        adapter.setChooseState(isEdit);
        if (isEdit) {
            /** 编辑状态 点击 全部还原不选中状态 ***/
            setAllBlacklistSelect(false);
            /**每次编辑的时候再次设置 in case**/
            tvDelete.setOnClickListener(this);
            tvSelectAll.setOnClickListener(this);
        }
        if (line_message_tool.getVisibility() != View.VISIBLE) {
            line_message_tool.setVisibility(View.VISIBLE);
        } else {
            line_message_tool.setVisibility(View.GONE);
        }
        translationAnimRun(line_message_tool, isEdit, 200);
        adapter.setEdit(isEdit);
        adapter.notifyDataSetChanged();
        ((TextView) headerLayout.findViewById(R.id.submit)).setText(!isEdit ? R.string.edit : R.string.cancel);
    }

    /**
     * @param view     view
     * @param isShow   显示是否
     * @param duration 时间
     */
    public void translationAnimRun(final View view, final boolean isShow, final long duration) {
        ObjectAnimator animator = null;
        final int height = view.getHeight();
        if (height != 0) {
            if (isShow) {
                animator = ObjectAnimator.ofFloat(view, "translationY",
                        view.getHeight(), 0).setDuration(duration);
            } else {
                animator = ObjectAnimator.ofFloat(view, "translationY", 0,
                        view.getHeight()).setDuration(duration);
            }
            animator.start();
        } else {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    int preHeight = view.getHeight();
                    if (preHeight != 0) {
                        translationAnimRun(view, isShow, duration);
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 删除黑名单
     */
    private void removeBlacklist(ArrayList<String> phoneList) {
        blackDao.deleteMultiple(deleteModelList);
        for (BlacklistDBModel item : deleteModelList) {
            if (adapter != null) {
                for (BlacklistDBModel model : adapter.getData()) {
                    if (item.phone.equals(model.phone)) {
                        adapter.remove(model);
                        break;
                    }
                }
            }
        }
        JSONObject jsonObject = getAuthJSONObject();
        try {
            jsonObject.put("phones", phoneList);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().postSticky(new EventBlacklist());
        blacklistFun.deleteBlacklist(jsonObject, hashCode(), new HttpBusinessCallback() {
            @Override
            public void onSuccess(String response) {
                super.onSuccess(response);
            }

            @Override
            public void onFailure(Map<String, ?> errorMap) {
                super.onFailure(errorMap);
            }
        });
    }
}
