package com.yibao.music.base;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yibao.music.util.LogUtil;

import butterknife.ButterKnife;


/**
 * @author luoshipeng
 * createDate：2019/4/22 0022 16:57
 * className   BaseLazyFragment
 * Des：TODO
 */
public abstract class BaseLazyFragment extends BaseFragment {
    private View mContentView;

    private boolean mIsLoadedData = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 避免多次从xml中加载布局文件
        if (mContentView == null) {
            initView(savedInstanceState);
//            processLogic(savedInstanceState);
        } else {
            ViewGroup parent = (ViewGroup) mContentView.getParent();
            if (parent != null) {
                parent.removeView(mContentView);
            }
        }
        return mContentView;
    }

    protected void setContentView(@LayoutRes int layoutResId) {
        mContentView = LayoutInflater.from(mActivity).inflate(layoutResId, null);
        unbinder = ButterKnife.bind(this, mContentView);


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            viewStatusProcessing(isVisibleToUser);
        }
    }


    /**
     * 处理对用户是否可见
     *
     * @param isVisibleToUser v
     */
    private void viewStatusProcessing(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            // 对用户可见
            if (!mIsLoadedData) {
                mIsLoadedData = true;
                onLazyLoadData();
            }
            onVisibleToUser();
        } else {
            // 对用户不可见
            onInvisibleToUser();
        }
    }

    /**
     * 懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法
     */
    protected void onLazyLoadData() {
        LogUtil.d(TAG, "开始加载数据");
    }

    /**
     * 对用户可见时触发该方法。如果只想在对用户可见时才加载数据，在子类中重写该方法
     */
    protected void onVisibleToUser() {

    }

    /**
     * 对用户不可见时触发该方法
     */
    protected void onInvisibleToUser() {
    }


    /**
     * 初始化View控件
     */
    protected abstract void initView(Bundle savedInstanceState);

    /**
     * 处理业务逻辑，状态恢复等操作 ,如：intent 获取值等
     *
     * @param savedInstanceState
     */
//    protected abstract void processLogic(Bundle savedInstanceState);
    /**
     * 查找View
     *
     * @param id   控件的id
     * @param <VT> View类型
     * @return v
     */
    protected <VT extends View> VT getViewById(@IdRes int id) {
        return mContentView.findViewById(id);
    }

}
