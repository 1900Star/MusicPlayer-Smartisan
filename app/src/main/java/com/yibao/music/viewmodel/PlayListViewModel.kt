package com.yibao.music.viewmodel

import com.yibao.music.MusicApplication
import com.yibao.music.base.BaseViewModel
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.PlayListBean
import com.yibao.music.util.LogUtil

class PlayListViewModel : BaseViewModel() {

    val listModel = SingleLiveEvent<List<PlayListBean>>()
    fun getPlayList() {

        val playListDao = MusicApplication.getInstance().playListDao

        val list = playListDao.queryBuilder().list()
        for (listBean in list) {
            LogUtil.d(mTag, listBean.toString())
        }

        listModel.postValue(list)
    }
}