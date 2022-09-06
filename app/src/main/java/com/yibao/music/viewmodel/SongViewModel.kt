package com.yibao.music.viewmodel

import com.yibao.music.MusicApplication
import com.yibao.music.base.BaseViewModel
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.MusicBean
import com.yibao.music.model.greendao.MusicBeanDao

class SongViewModel : BaseViewModel() {

    val listModel = SingleLiveEvent<List<MusicBean>>()

    /**
     * 获取歌曲列表
     * @param sortFlag 列表排序方式，  1 按歌名 、 2 按评分 、 3 按播放次数 、 4 按添加时间
     */
    fun getMusicList(sortFlag: Int) {
        val queryBuilder = MusicApplication.getInstance().musicDao.queryBuilder()

        val dataList = when (sortFlag) {
            1 -> {
                queryBuilder.orderAsc(MusicBeanDao.Properties.Title).build().list()
            }
            2 -> queryBuilder.orderAsc(MusicBeanDao.Properties.SongScore).build().list()
            3 -> queryBuilder.orderAsc(MusicBeanDao.Properties.PlayFrequency).build().list()
            4 -> queryBuilder.orderAsc(MusicBeanDao.Properties.AddTime).build().list()
            else -> {
                queryBuilder.orderAsc(MusicBeanDao.Properties.Title).build().list()
            }
        }


        listModel.postValue(dataList)



    }
}