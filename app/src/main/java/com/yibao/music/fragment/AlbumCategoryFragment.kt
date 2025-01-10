package com.yibao.music.fragment

import android.os.Bundle
import com.yibao.music.adapter.AlbumAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseMusicFragmentDev
import com.yibao.music.databinding.AlbumCategoryFragmentBinding
import com.yibao.music.model.AlbumInfo
import com.yibao.music.util.Constant
import com.yibao.music.util.MusicListUtil
import com.yibao.music.viewmodel.AlbumViewModel

/**
 * @author Luoshipeng
 * @ author: Luoshipeng
 * @ Name:   AlbumCategoryFragment
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/11/ 23:47
 * @ Des:    TODO
 */
class AlbumCategoryFragment : BaseMusicFragmentDev<AlbumCategoryFragmentBinding>() {
    private var mPosition = 0
    private lateinit var mAlbumList: List<AlbumInfo>

    private lateinit var mAlbumAdapter: AlbumAdapter


    override fun initView() {
        val arguments = arguments
        if (arguments != null) {
            mPosition = arguments.getInt(Constant.POSITION)
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
            override fun showDetailsView(bean: AlbumInfo, position: Int) {
                // AlbumFragment 接收
                mViewModel.postAlbum(bean)
            }
        })
    }


    companion object {
        lateinit var mViewModel: AlbumViewModel

        @JvmStatic
        fun newInstance(
            position: Int, albumViewModel: AlbumViewModel
        ): AlbumCategoryFragment {
            mViewModel = albumViewModel
            val args = Bundle()
            val fragment = AlbumCategoryFragment()
            args.putInt(Constant.POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

}