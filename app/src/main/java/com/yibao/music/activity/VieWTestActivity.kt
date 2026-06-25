package com.yibao.music.activity


import com.yibao.music.base.bindings.BaseBindingActivity
import com.yibao.music.base.listener.OnStylusChangeListener
import com.yibao.music.base.listener.StylusState
import com.yibao.music.base.listener.StylusState.Adjusting
import com.yibao.music.base.listener.StylusState.Reset
import com.yibao.music.databinding.ActivityMusicPlayerBinding
import com.yibao.music.util.LogUtil


class VieWTestActivity : BaseBindingActivity<ActivityMusicPlayerBinding>() {

    override fun initView() {

        mBinding.btnPlay.setOnClickListener {
            mBinding.ctv.updateProgress(110f, true)


        }
        mBinding.btnPlay2.setOnClickListener {
            mBinding.ctv.updateProgress(99f, true)
        }
        mBinding.btnPause.setOnClickListener {
            mBinding.ctv.resetStylus()
        }
    }

    override fun initData() {


    }

    override fun initListener() {

        mBinding.ctv.setOnClickListener {
            object : OnStylusChangeListener {
                override fun onStateChanged(state: StylusState) {
                    if (state is Reset) {
                        LogUtil.d(TAG, "进度重启：")
                    } else if (state is Adjusting) {
                        // 根据进度调整音乐播放位置
                        val adjustingState = state
                        // 指针旋转角度对应的播放进度条比例0～1,通过唱针旋转的角度，最大只能将播放进度调节到总时长的97%,剩余5、6秒钟，避免旋转到最大角度，自动下一曲
                        val progress = adjustingState.progress
                        val targetProgress = if (progress > 0.97) 0.97f else progress
                        LogUtil.d(TAG, "当前进度： $targetProgress")

                    }

                }
            }


        }

    }


}




