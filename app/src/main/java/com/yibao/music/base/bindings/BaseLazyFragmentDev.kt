package com.yibao.music.base.bindings

import androidx.viewbinding.ViewBinding

abstract class BaseLazyFragmentDev<T : ViewBinding> : BaseMusicFragmentDev<T>() {
    // 是否为第一次加载
    private var isFirstLoad = true


    override fun onResume() {
        super.onResume()
        if (isFirstLoad && isVisible && isResumed) {
            // 将数据加载逻辑放到onResume()方法中
            initData()
            isFirstLoad = false
        }
    }
}