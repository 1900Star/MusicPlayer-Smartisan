package com.yibao.music.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import butterknife.ButterKnife;

public abstract class BaseLazyFragment extends BaseMusicFragment {
    // 是否为第一次加载
    private boolean isFirstLoad = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(getContentViewId(), null);
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        return view;
    }

    /**
     * 初始化视图
     *
     * @param view v
     */

    protected abstract void initView(View view);

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            // 将数据加载逻辑放到onResume()方法中
            initData();
            isFirstLoad = false;
        }
        initRxBusData();
    }

    protected void initRxBusData() {

    }

    /**
     * 设置布局资源id
     *
     * @return id
     */
    protected abstract int getContentViewId();



    /**
     * 初始化数据
     */
    protected void initData() {

    }

}
