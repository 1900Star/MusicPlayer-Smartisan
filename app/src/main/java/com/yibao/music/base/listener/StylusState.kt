package com.yibao.music.base.listener

sealed class StylusState {
    // 归位/暂停状态
    object Reset : StylusState()

    // 播放/调整进度状态（progress 范围 0.0 ~ 1.0）
    data class Adjusting(val progress: Float) : StylusState()
}

// 接口回调
interface OnStylusChangeListener {
    fun onStateChanged(state: StylusState)
}
