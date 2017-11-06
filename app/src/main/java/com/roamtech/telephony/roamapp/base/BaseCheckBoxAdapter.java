package com.roamtech.telephony.roamapp.base;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.roamtech.telephony.roamapp.widget.swipemenu.SwipeMenuListView;

import java.util.ArrayList;
import java.util.List;

/**单项选择的adapter
 * @param <T>
 */
public abstract class BaseCheckBoxAdapter<T> extends RMBaseAdapter<T> {
    private boolean isEdit;
    private List<SwipeViewHolder> swipeItemLists;
    private SwipeMenuListView mSwipeMenuListView;
    public BaseCheckBoxAdapter(BaseActivity activity,SwipeMenuListView swipeMenuListView, List<T> data) {
        super(activity, data);
        this.mSwipeMenuListView=swipeMenuListView;
        isEdit = false;
        swipeItemLists = new ArrayList<>();
    }
    public boolean isEdit() {
        return isEdit;
    }
    public void setEdit(boolean isEdit) {
        this.isEdit = isEdit;
        mSwipeMenuListView.setEditMode(isEdit);
        startEditAnim();
    }

    private void startEditAnim() {
        for (int i = 0; i < swipeItemLists.size(); i++) {
            SwipeViewHolder swipeItemLayout = swipeItemLists.get(i);
            startEditAnim(swipeItemLayout);
        }
    }
    //开始编辑动画
    public void startEditAnim(final SwipeViewHolder viewHolder) {
        int width = viewHolder.handle.getWidth();
        if (width == 0) {
            viewHolder.handle.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    viewHolder.handle.getViewTreeObserver().removeOnPreDrawListener(this);
                    int preWidth = viewHolder.handle.getWidth();
                    if (preWidth != 0) {
                        //第一次创建的时候就处于编辑状态 设置偏移显示
                        if (isEdit) {
                            viewHolder.handle.setTranslationX(0);
                            viewHolder.contentInner.setTranslationX(preWidth);
                        }else{
                            viewHolder.handle.setTranslationX(0);
                            viewHolder.contentInner.setTranslationX(0);
                        }
                    }
                    return true;
                }
            });
        } else {
            //如果处于展示状态则直接设置动画
            ObjectAnimator animator1 = null;
            ObjectAnimator animator2 = null;
            if (isEdit) {
                animator1 = createTransAnim( viewHolder.handle, - width, 0);
                animator2 = createTransAnim(viewHolder.contentInner, 0, width);
            } else {
                animator1 = createTransAnim( viewHolder.handle, 0, - width);
                animator2 = createTransAnim(viewHolder.contentInner, width, 0);
            }
            animator1.start();
            animator2.start();
        }
    }

    private ObjectAnimator createTransAnim(View view, float form, float to) {
        return ObjectAnimator.ofFloat(view, "translationX",
                form, to).setDuration(200);
    }
    /**第一次创建的时候获取item
     * @return
     */
    protected abstract View createSwipeItemLayout();

    /** 绑定数据
     * @param swipeViewHolder
     * @param position
     */
    protected abstract void bindSwipeItemData(SwipeViewHolder swipeViewHolder, int position);

    @Override
    public final View getView(int position, View contentView, ViewGroup arg2) {
        SwipeViewHolder holder = null;
        if (contentView == null) {
            contentView = createSwipeItemLayout();
            Object tag = contentView.getTag();
            if (!(tag instanceof SwipeViewHolder)) {
                throw new IllegalArgumentException("swipeItemLayout must set a tag with SwipeViewHolder instance");
            }
            holder = (SwipeViewHolder) tag;
            if (holder.handle == null) {
                throw new NullPointerException("SwipeViewHolder handle can not be null");
            }
            if (holder.contentInner == null) {
                throw new NullPointerException("SwipeViewHolder contentInner can not be null");
            }
            if (isEdit) {
                //如果为编辑状态则初始化的时候就要设置为编辑状态的位置
                startEditAnim(holder);
            }
            //将所有创建的View添加的需要动画的列表
            swipeItemLists.add(holder);
        } else {
            holder = (SwipeViewHolder) contentView.getTag();
        }
        //赋值操作
        holder.position = position;
        bindSwipeItemData(holder, position);
        return contentView;
    }
    public static class SwipeViewHolder {
        //需要动画的ID 为content的两部分
        public ImageView handle;
        public View contentInner;
        public int position;
    }
}
