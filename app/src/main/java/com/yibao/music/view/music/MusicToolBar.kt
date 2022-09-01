package com.yibao.music.view.music

import android.widget.LinearLayout
import android.widget.TextView
import android.view.LayoutInflater
import com.yibao.music.R
import android.view.View.OnLongClickListener
import com.yibao.music.util.SpUtil
import android.content.Intent
import com.yibao.music.activity.SearchActivity
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.yibao.music.util.Constants
import com.yibao.music.view.music.MusicToolBar.OnToolbarClickListener

/**
 * @author Luoshipeng
 * @ Name:   QqControlBar
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/11/ 21:50
 * @ Des:    TODO
 */
class MusicToolBar : LinearLayout, View.OnClickListener {
    private var mToolbarTitle: TextView? = null
    private var mTvEdit: TextView? = null
    private var mTvEditDelete: TextView? = null
    private var mIvSearch: ImageView? = null

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.music_toolbar_content, this, true)
        mToolbarTitle = findViewById(R.id.tv_music_toolbar_title)
        mTvEdit = findViewById(R.id.tv_edit)
        mTvEditDelete = findViewById(R.id.tv_edit_delete)
        mIvSearch = findViewById(R.id.iv_search)
        initData()
        initListener()
    }

    private fun initData() {}
    private fun initListener() {
        mTvEdit!!.setOnClickListener(this)
        mTvEditDelete!!.setOnClickListener(this)
        mIvSearch!!.setOnClickListener(this)
        mToolbarTitle!!.setOnClickListener(this)
        mToolbarTitle!!.setOnLongClickListener { v: View? ->
            SpUtil.setPicUrlFlag(context, !SpUtil.getPicUrlFlag(context, false))
            true
        }
    }

    fun setTvEditText(resourceId: Int) {
        mTvEdit!!.setText(resourceId)
    }

    fun setTvEditVisibility(visibility: Boolean) {
        mTvEdit!!.visibility = if (visibility) VISIBLE else GONE
    }

    fun setToolbarTitle(toolbarTitle: String?) {
        mToolbarTitle!!.text = toolbarTitle
    }

    fun setTvDeleteVisibility(visibility: Int) {
        mTvEditDelete!!.visibility = visibility
    }

    fun setIvSearchVisibility(visibility: Boolean) {
        mIvSearch!!.visibility = if (visibility) VISIBLE else GONE
    }

    override fun onClick(v: View) {
        if (mListener != null) {

            when (v.id) {
                R.id.tv_edit -> {mListener!!.clickEdit()}
                R.id.tv_music_toolbar_title -> {mListener!!.switchMusicControlBar()}
                R.id.iv_search -> {
                    val intent = Intent(context, SearchActivity::class.java)
                    intent.putExtra("pageType", Constants.NUMBER_ZERO)
                    context.startActivity(intent)
                    (context as Activity).overridePendingTransition(R.anim.dialog_push_in, 0)
                }
                R.id.tv_edit_delete -> {mListener!!.clickDelete()}
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