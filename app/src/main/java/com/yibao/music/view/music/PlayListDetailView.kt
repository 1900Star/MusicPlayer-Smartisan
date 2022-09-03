package com.yibao.music.view.music

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.yibao.music.MusicApplication
import com.yibao.music.R
import com.yibao.music.adapter.DetailsViewAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter.OnOpenItemMoreMenuListener
import com.yibao.music.base.listener.OnMusicItemClickListener
import com.yibao.music.databinding.PlayListDetailBinding
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog
import com.yibao.music.model.MusicBean
import com.yibao.music.util.Constants
import com.yibao.music.util.RandomUtil
import com.yibao.music.util.SnakbarUtil
import com.yibao.music.util.SpUtil
import com.yibao.music.view.SwipeItemLayout.OnSwipeItemTouchListener

/**
 * @ Author: Luoshipeng
 * @ Name:   PlayListDetailView
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/12/7/ 11:59
 * @ Des:    TODO
 */
class PlayListDetailView : LinearLayout, View.OnClickListener {
    private var mQueryFlag: String? = null
    private var mListSize = 0
    private val mBinding =
        PlayListDetailBinding.inflate(LayoutInflater.from(context), this, true)

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    fun setQueryFlag(queryFlag: String?, listSize: Int) {
        mQueryFlag = queryFlag
        mListSize = listSize
    }

    fun setAdapter(adapter: DetailsViewAdapter) {
        val manager = LinearLayoutManager(MusicApplication.getInstance())
        manager.orientation = LinearLayoutManager.VERTICAL
        mBinding.recyclerDetail.isVerticalScrollBarEnabled = true
        mBinding.recyclerDetail.layoutManager = manager

        val divider =
            DividerItemDecoration(mBinding.recyclerDetail.context, DividerItemDecoration.VERTICAL)
        val drawable = ContextCompat.getDrawable(
            mBinding.recyclerDetail.context,
            R.drawable.shape_item_decoration
        )
        divider.setDrawable(drawable!!)
        mBinding.recyclerDetail.addItemDecoration(divider)
        mBinding.recyclerDetail.addOnItemTouchListener(OnSwipeItemTouchListener(mBinding.recyclerDetail.context))
        mBinding.recyclerDetail.setHasFixedSize(true)
        mBinding.recyclerDetail.adapter = adapter
        adapter.setOnItemMenuListener(object : OnOpenItemMoreMenuListener {
            override fun openClickMoreMenu(position: Int, musicBean: MusicBean) {
                MoreMenuBottomDialog.newInstance(musicBean, position, false, false)
                    .getBottomDialog(context)

            }
        })


    }

    private fun initView() {

        mBinding.tvRandomPlay.setOnClickListener(this)
        mBinding.tvDeletePlayList.setOnClickListener(this)
        mBinding.tvEditPlayList.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.tv_random_play) {
            if (mListSize > 0) {
                startMusic(RandomUtil.getRandomPostion(mListSize))
            }
        } else if (id == R.id.tv_delete_play_list) {
            SnakbarUtil.keepGoing(mBinding.tvRandomPlay)
        } else if (id == R.id.tv_edit_play_list) {
            SnakbarUtil.keepGoing(mBinding.tvEditPlayList)
        }
    }

    private fun startMusic(startPosition: Int) {
        if (context is OnMusicItemClickListener) {
            SpUtil.setSortFlag(context, Constants.NUMBER_TEN)
            (context as OnMusicItemClickListener).startMusicServiceFlag(
                startPosition,
                Constants.NUMBER_TEN,
                Constants.NUMBER_FOUR,
                mQueryFlag
            )
        }
    }
}