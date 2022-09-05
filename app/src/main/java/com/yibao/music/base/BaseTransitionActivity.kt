package com.yibao.music.base

import androidx.viewbinding.ViewBinding
import com.yibao.music.base.bindings.BaseBindingActivity
import com.yibao.music.base.listener.OnGlideLoadListener
import com.bumptech.glide.Glide
import com.yibao.music.util.SpUtil
import com.yibao.music.R
import com.yibao.music.util.Constant

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base
 * @文件名: BaseActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/20 13:07
 * @描述： {TODO}
 */
abstract class BaseTransitionActivity<T : ViewBinding> : BaseBindingActivity<T>(),
    OnGlideLoadListener {
    override fun onResume() {
        super.onResume()
    }

    override fun resumeRequests() {
        if (!isDestroyed) {
            Glide.with(this).resumeRequests()
        }
    }

    override fun pauseRequests() {
        if (!isDestroyed) {
            Glide.with(this).pauseRequests()
        }
    }

    override fun onPause() {
        super.onPause()
        SpUtil.setAddTodPlayListFlag(this, Constant.NUMBER_ZERO)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.dialog_push_out)
    }
}