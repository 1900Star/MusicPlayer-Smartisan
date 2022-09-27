package com.yibao.music.view.music

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.yibao.music.MusicApplication
import com.yibao.music.R
import com.yibao.music.activity.SearchActivity
import com.yibao.music.databinding.MusicToolbarBinding
import com.yibao.music.util.Constant
import com.yibao.music.util.SpUtils

/**
 * @author Luoshipeng
 * @ Name:   QqControlBar
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/11/ 21:50
 * @ Des:    TODO
 */
class MusicToolBar : LinearLayout, View.OnClickListener {

    private val mBinding =
        MusicToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        initData()
        initListener()
    }

    private fun initData() {}
    private fun initListener() {
        mBinding.tvEdit.setOnClickListener(this)
        mBinding.ivSearch.setOnClickListener(this)
        mBinding.tvMusicToolbarTitle.setOnClickListener(this)
        mBinding.tvMusicToolbarTitle.setOnLongClickListener {
            val sp = SpUtils(MusicApplication.getInstance(),Constant.MUSIC_CONFIG)
            val urlFlag = sp.getBoolean(Constant.PIC_URL_FLAG,false)
            sp.putValues(SpUtils.ContentValue(Constant.PIC_URL_FLAG,!urlFlag))

            true
        }
    }

    fun setTvEditText(resourceId: Int) {
        mBinding.tvEdit.setText(resourceId)
    }

    fun setTvEditVisibility(visibility: Boolean) {
        mBinding.tvEdit.visibility = if (visibility) VISIBLE else GONE
    }

    fun setToolbarTitle(toolbarTitle: String) {
        mBinding.tvMusicToolbarTitle.text = toolbarTitle
    }



    override fun onClick(v: View) {
        if (mListener != null) {
            when (v.id) {
                R.id.tv_edit -> {
                    mListener!!.clickEdit()
                }
                R.id.tv_music_toolbar_title -> {
                    mListener!!.switchMusicControlBar()
                }
                R.id.iv_search -> {
                    val intent = Intent(context, SearchActivity::class.java)
                    context.startActivity(intent)
                    (context as Activity).overridePendingTransition(R.anim.dialog_push_in, 0)
                }
            }
        }
    }

    private var mListener: OnToolbarClickListener? = null
    fun setClickListener(listener: OnToolbarClickListener?) {
        mListener = listener
    }

    interface OnToolbarClickListener {
        fun clickEdit()
        fun switchMusicControlBar()
        fun clickDelete()
    }
}