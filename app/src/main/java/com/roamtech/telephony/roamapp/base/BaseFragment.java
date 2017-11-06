package com.roamtech.telephony.roamapp.base;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.roamtech.telephony.roamapp.handler.CommonDoHandler;
import com.roamtech.telephony.roamapp.handler.CommonHandler;
import com.roamtech.telephony.roamapp.interf.IBaseViewInterface;

import org.greenrobot.eventbus.EventBus;

/**
 * 基础Fragment，提供公有方法
 *
 * @author xincheng @date:2014-8-4
 */
public abstract class BaseFragment extends Fragment implements IBaseViewInterface,
        OnItemClickListener, OnClickListener, CommonDoHandler {
    private View mRootView;
    /**
     * 是否inflater 需要缓存的RootView;
     */
    private boolean isInflaterCacheRootView;
    public static final String TAG = "BaseFragment";
    protected CommonHandler<BaseFragment> fragmentHandler = new CommonHandler<>(this);

    public void doHandler(Message msg) {
        fragmentHandler.handleMessage(msg);
    }

    @Override
    public void onAttach(Context context) {
        // TODO Auto-generated method stub
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (isCacheRootView()) {
            isInflaterCacheRootView = (mRootView == null);
            if (!isInflaterCacheRootView) {
                ViewGroup parent = (ViewGroup) mRootView.getParent();
                if (parent != null) {
                    parent.removeView(mRootView);
                }
            } else {
                mRootView = inflater.inflate(getLayoutId(), null);
            }
            return mRootView;
        } else {
            return inflater.inflate(getLayoutId(), null);
        }
    }

    @Override
    public final void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        //先判断是否需要缓存RootView
        if (!isCacheRootView() || isInflaterCacheRootView) {
            initData();
            initView(savedInstanceState);
            setListener();
        }
    }

    @Override
    public void initData() {
        // TODO Auto-generated method stub
    }

    @Override
    public void initView(Bundle savedInstanceState) {

    }

    /**
     * 添加监听 initView() 之后被调用
     */
    @Override
    public void setListener() {

    }

    /**
     * 是否需要缓存rootView 防止onCreteView 重新执行后重新加载数据
     *
     * @return
     */
    protected boolean isCacheRootView() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    protected View findViewById(int id) {
        return getView().findViewById(id);
    }

    //找 Activity中的ID
    protected View findViewInActivityById(int id) {
        return getActivity().findViewById(id);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }


    /**
     * @param view
     * @param isShow
     * @param duration
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
}
