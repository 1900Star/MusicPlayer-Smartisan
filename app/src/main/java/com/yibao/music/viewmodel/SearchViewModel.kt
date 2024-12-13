package com.yibao.music.viewmodel

import androidx.lifecycle.viewModelScope
import com.yibao.music.MusicApplication
import com.yibao.music.base.BaseViewModel
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.MusicBean
import com.yibao.music.model.SearchHistoryBean
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.model.greendao.SearchHistoryBeanDao
import com.yibao.music.model.qq.SearchLyricsBean
import com.yibao.music.network.RetrofitHelper
import com.yibao.music.util.Constant
import com.yibao.music.util.LogUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel : BaseViewModel() {

    val searchViewModel = SingleLiveEvent<List<MusicBean>>()

    val lrcViewModel = SingleLiveEvent<List<SearchLyricsBean>>()


    fun searchMusic(key: String, position: Int) {

        val musicDao = MusicApplication.getInstance().musicDao

        LogUtil.d("====", "=========== searchPosition    $position  searchKey  $key");
        val dataList = ArrayList<MusicBean>()
        // 全部
        when (position) {

            11 -> {
                // 根据歌名精确搜索
                val songList =
                    musicDao.queryBuilder().where(MusicBeanDao.Properties.Title.eq(key)).build()
                        .list()
                if (songList.isNotEmpty()) {
                    dataList.addAll(songList)
                } else {
                    // 模糊匹配搜索, % 加在前面为包含key，加在后面查询的结果是以 key 开头的数据。
                    val searchSongList: List<MusicBean> = musicDao.queryBuilder().where(
                        MusicBeanDao.Properties.Title.like("%$key%")
                    ).list()
                    if (searchSongList.isNotEmpty()) {
                        dataList.addAll(searchSongList)
                    }
                }
            }

            12 -> {
                // 按专辑搜索
                val albumList =
                    musicDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(key)).build()
                        .list()
                if (albumList.isNotEmpty()) {
                    dataList.addAll(albumList)
                }
            }

            13 -> {
                // 按歌手搜索
                val artistList =
                    musicDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(key)).build()
                        .list()
                if (artistList.isNotEmpty()) {
                    dataList.addAll(artistList)
                }
            }

            14 -> {
                // 按歌手搜索
                val artistList =
                    musicDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(key)).build()
                        .list()
                if (artistList.isNotEmpty()) {
                    dataList.addAll(artistList)
                }
            }
        }

        searchViewModel.postValue(dataList)
    }

    val historyViewModel = SingleLiveEvent<List<SearchHistoryBean>>()

    fun getHistory() {
        val hisDao = MusicApplication.getInstance().searchDao
        val historyList =
            hisDao.queryBuilder().orderDesc(SearchHistoryBeanDao.Properties.SearchTime).build()
                .list()
        val hisList = historyList.distinctBy { it.searchContent }

        historyViewModel.postValue(hisList)
    }

    fun searchLyrics(songName: String, singer: String, isNeedArtist: Boolean) {
        job = viewModelScope.launch(Dispatchers.IO) {
            val mLyricsBeanList = ArrayList<SearchLyricsBean>()
            RetrofitHelper.getMusicService().getLrc(songName).subscribe { songLrc ->
                val lrcList = songLrc.data.lyric.list
                for (listBean in lrcList) {
                    val content = listBean.content
                    val songSinger = listBean.singer[0].name
                    val songNames = listBean.songname
                    if (isNeedArtist) {
                        if (Constant.NO_LYRICS != content && Constant.PURE_MUSIC != content && songName == songNames && songSinger.contains(
                                singer
                            )
                        ) {
                            val lyricsBean =
                                SearchLyricsBean(listBean.songmid, listBean.content)
                            if (!mLyricsBeanList.contains(lyricsBean)) {
                                mLyricsBeanList.add(lyricsBean)
                            }
                        }
                    } else {
                        if (Constant.NO_LYRICS != content && Constant.PURE_MUSIC != content && songName == songNames) {
                            val lyricsBean =
                                SearchLyricsBean(listBean.songmid, listBean.content)
                            if (!mLyricsBeanList.contains(lyricsBean)) {
                                mLyricsBeanList.add(lyricsBean)
                            }
                        }
                    }
                }

                lrcViewModel.postValue(mLyricsBeanList)

            }
        }

    }

}