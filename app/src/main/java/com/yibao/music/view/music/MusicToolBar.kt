package com.yibao.music.view.music

import android.widget.LinearLayout
import android.view.LayoutInflater
import com.yibao.music.R
import com.yibao.music.util.SpUtil
import android.content.Intent
import com.yibao.music.activity.SearchActivity
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.yibao.music.databinding.MusicToolbarContentBinding
import com.yibao.music.util.Constant

/**
 * @author Luoshipeng
 * @ Name:   QqControlBar
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/11/ 21:50
 * @ Des:    TODO
 */
class MusicToolBar : LinearLayout, View.OnClickListener {

    private val mBinding =
        MusicToolbarContentBinding.inflate(LayoutInflater.from(context), this, true)

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
//        mBinding.tvDelete.setOnClickListener(this)
        mBinding.ivSearch.setOnClickListener(this)
        mBinding.tvMusicToolbarTitle.setOnClickListener(this)
        mBinding.tvMusicToolbarTitle.setOnLongClickListener {
            SpUtil.setPicUrlFlag(context, !SpUtil.getPicUrlFlag(context, false))
            true
        }
    }

    fun setTvEditText(resourceId: Int) {
        mBinding.tvEdit.setText(resourceId)
    }

    fun setTvEditVisibility(visibility: Boolean) {
        mBinding.tvEdit.visibility = if (visibility) VISIBLE else GONE
    }

    fun setToolbarTitle(toolbarTitle: String?) {
        mBinding.tvMusicToolbarTitle.text = toolbarTitle
    }

    fun setTvDeleteVisibility(visibility: Int) {
//        mBinding.tvDelete.visibility = visibility
    }

    fun setIvSearchVisibility(visibility: Boolean) {
        mBinding.ivSearch.visibility = if (visibility) VISIBLE else GONE
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
                    intent.putExtra(Constant.PAGE_TYPE, Constant.NUMBER_ZERO)
                    context.startActivity(intent)
                    (context as Activity).overridePendingTransition(R.anim.dialog_push_in, 0)
                }
//                R.id.tv_delete -> {
//                    mListener!!.clickDelete()
//                }
                else -> {}
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