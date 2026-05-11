package com.yibao.music.activity

import android.os.Handler
import android.os.Looper
import com.yibao.music.base.bindings.BaseBindingActivity
import com.yibao.music.databinding.ActivityTestBinding

class TestActivity : BaseBindingActivity<ActivityTestBinding>() {


    override fun initView() {

    }

    private var fakePosition = 0L
    override fun initData() {


        // 模拟播放进度更新
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                fakePosition += 1000
                handler.postDelayed(this, 1000)
            }
        })




    }

    override fun initListener() {

    }
}