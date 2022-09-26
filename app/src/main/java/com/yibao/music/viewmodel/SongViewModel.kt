package com.yibao.music.viewmodel

import com.yibao.music.MusicApplication
import com.yibao.music.base.BaseViewModel
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.MusicBean
import com.yibao.music.model.greendao.MusicBeanDao
import java.util.Collections.sort

class SongViewModel : BaseViewModel() {

    val listModel = SingleLiveEvent<List<MusicBean>>()

    /**
     * 获取歌曲列表
     * @param sortFlag 列表排序方式，  1 按歌名 、 2 按评分 、 3 按播放次数 、 4 按添加时间
     */
    fun getMusicList(sortFlag: Int) {
        val queryBuilder = MusicApplication.getInstance().musicDao.queryBuilder()

        when (sortFlag) {
            0 -> {
                listModel.postValue(sortMusicAbc(queryBuilder.build().list()))
            }
            1 -> {
                val scoreList = queryBuilder.orderDesc(MusicBeanDao.Properties.SongScore).build().list()

                listModel.postValue(scoreList)
            }
            2 -> {
                val frequencyList = queryBuilder.orderDesc(MusicBeanDao.Properties.PlayFrequency).build().list()
                listModel.postValue(frequencyList)
            }
            3 -> {
                val timeList = queryBuilder.orderDesc(MusicBeanDao.Properties.AddTime).build().list()
                listModel.postValue(timeList)

            }

        }

    }


    /**
     * 按ABCD 首字母排序
     *
     * @param musicList d
     */
    private fun sortMusicAbc(musicList: List<MusicBean>): List<MusicBean> {
        val str = "#"
        sort(
            musicList
        ) { m1: MusicBean, m2: MusicBean ->

            when (str) {
                m2.firstChar -> {
                    -1
                }
                m1.firstChar -> {
                    1
                }
                else -> {
                    m1.firstChar.compareTo(m2.firstChar)
                }
            }
        }
        return musicList
    }


}