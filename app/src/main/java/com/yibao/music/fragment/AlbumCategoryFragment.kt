package com.yibao.music.fragment

import android.os.Bundle
import com.yibao.music.adapter.AlbumAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseLazyFragmentDev
import com.yibao.music.databinding.CategoryFragmentBinding
import com.yibao.music.model.AlbumInfo
import com.yibao.music.util.Constants
import com.yibao.music.util.MusicListUtil

/**
 * @author Luoshipeng
 * @ author: Luoshipeng
 * @ Name:   AlbumCategoryFragment
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/11/ 23:47
 * @ Des:    TODO
 */
class AlbumCategoryFragment : BaseLazyFragmentDev<CategoryFragmentBinding>() {

    private var mPosition = 0
    private lateinit var mAlbumList: List<AlbumInfo>

    private lateinit var mAlbumAdapter: AlbumAdapter



    override fun initView() {
        val arguments = arguments
        if (arguments != null) {
            mPosition = arguments.getInt("position")
        }
        val musicBeanList = mMusicBeanDao.queryBuilder().list()
        mAlbumList = MusicListUtil.getAlbumList(musicBeanList)

    }

    override fun initData() {
        mAlbumAdapter = AlbumAdapter(mActivity, mAlbumList, mPosition)

        mBinding.musicView.setAdapter(
            mContext,
            if (mPosition == 0) 3 else 4,
            mPosition == 0,
            mAlbumAdapter
        )

        mAlbumAdapter.setItemListener(object : BaseBindingAdapter.OnItemListener<AlbumInfo> {
            override fun showDetailsView(bean: AlbumInfo, position: Int, isEditStatus: Boolean) {
                mBus.post(bean)
            }
        })
    }




    override fun handleDetailsBack(detailFlag: Int) {
        if (detailFlag == Constants.NUMBER_TEN) {
            mAlbumAdapter.setItemSelectStatus(false)
            mBus.post(Constants.FRAGMENT_ALBUM, Constants.NUMBER_ZERO)

        }
    }



    companion object {
        @JvmStatic
        fun newInstance(position: Int): AlbumCategoryFragment {
            val args = Bundle()
            val fragment = AlbumCategoryFragment()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }

    override val isOpenDetail: Boolean
        get() = false
}