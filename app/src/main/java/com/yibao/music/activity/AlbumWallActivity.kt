package com.yibao.music.activity

import com.yibao.music.adapter.AlbumWallAdapter
import com.yibao.music.base.bindings.BaseBindingActivity
import com.yibao.music.databinding.ActivityAlbumWallBinding
import com.yibao.music.util.MusicListUtil

class AlbumWallActivity : BaseBindingActivity<ActivityAlbumWallBinding>() {
    override fun initView() {
        initRecyclerView(mBinding.recyclerAlbumWall)
    }

    override fun initData() {
        val musicBeanList = mMusicDao.queryBuilder().list()
        val albumList = MusicListUtil.getAlbumList(musicBeanList)
        val adapter = AlbumWallAdapter(this, albumList)
        mBinding.recyclerAlbumWall.adapter = adapter

    }

    override fun initListener() {

    }


}