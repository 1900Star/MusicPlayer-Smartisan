package com.yibao.music.base

import com.bumptech.glide.Glide
import com.yibao.music.R
import com.yibao.music.base.listener.OnGlideLoadListener
import com.yibao.music.util.Constant
import com.yibao.music.util.SpUtils

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base
 * @文件名: BaseActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/20 13:07
 * @描述： {TODO}
 */
abstract class BaseTransitionActivity : BaseActivity(),
    OnGlideLoadListener {


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
        mSps.putValues(SpUtils.ContentValue(Constant.ADD_TO_PLAY_LIST_FLAG,Constant.NUMBER_ZERO))
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.dialog_push_out)
    }
}