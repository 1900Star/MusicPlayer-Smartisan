package com.yibao.music.viewmodel

import androidx.lifecycle.viewModelScope
import com.yibao.music.MusicApplication
import com.yibao.music.base.BaseViewModel
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.ErrorBean
import com.yibao.music.model.MusicBean
import com.yibao.music.model.PlayListBean
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.model.greendao.PlayListBeanDao
import com.yibao.music.util.Constant
import com.yibao.music.util.LogUtil
import com.yibao.music.util.MusicDaoUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlayListViewModel : BaseViewModel() {

    val listModel = SingleLiveEvent<List<PlayListBean>>()
    val editModel = SingleLiveEvent<ErrorBean>()


    fun addPlayList(name: String, oprType: Int) {
        LogUtil.d(mTag, "添加名字：$name  ==  标识：$oprType")
        val beanList =
            playDao().queryBuilder().where(PlayListBeanDao.Properties.Title.eq(name))
                .list()
        if (beanList.isNotEmpty()) {
            fail("播放列表已存在")
        } else {
            if (oprType == Constant.NUMBER_ONE) {
                val insertOrReplaceId = playDao().insertOrReplace(
                    PlayListBean(
                        name,
                        System.currentTimeMillis()
                    )
                )
                if (insertOrReplaceId != 0L) {
                    ok(code = 0)
                } else {
                    fail("添加失败")
                }
            } else {
                // 重命名

            }
        }

    }


    fun editPlayList(currentPosition: Int) {

        if (getList().isNotEmpty()) {
            val currentTitle = getList()[currentPosition].title
            editModel.postValue(ErrorBean(0, currentTitle))
        }


    }


    fun getPlayList() {
        job = viewModelScope.launch(Dispatchers.IO) {
            val playListDao = MusicApplication.getInstance().playListDao
            val list = playListDao.queryBuilder().list()
            list.sort()
            listModel.postValue(list)
        }
    }

    private fun getList(): List<PlayListBean> {
        val playListDao = MusicApplication.getInstance().playListDao
        val list = playListDao.queryBuilder().list()
        list.sort()
        return list

    }

    private fun playDao(): PlayListBeanDao {
        return MusicApplication.getInstance().playListDao
    }

    /**
     * 单曲添加
     */

    fun insertSongSingle(playListBean: PlayListBean, songName: String) {
        job = viewModelScope.launch(Dispatchers.IO) {

            val beanList = musicDao().queryBuilder().where(
                MusicBeanDao.Properties.PlayListFlag.eq(playListBean.title),
                MusicBeanDao.Properties.Title.eq(songName)
            ).build().list()
            if (beanList.isNotEmpty()) {
                postError("")
            } else {
                insertSong(playListBean, songName)
            }
        }
    }

    /**
     * 批量添加歌曲到列表
     */
    fun insertSongsToList(playListBean: PlayListBean, nameArray: ArrayList<String>) {
        job = viewModelScope.launch(Dispatchers.IO) {
            nameArray.forEachIndexed { index, songName ->
                val musicBeanList: List<MusicBean> = musicDao().queryBuilder().where(
                    MusicBeanDao.Properties.PlayListFlag.eq(playListBean.title),
                    MusicBeanDao.Properties.Title.eq(songName)
                ).build().list()
                if (musicBeanList.isEmpty()) {
                    insertSong(playListBean, songName)
                }
                if (index == nameArray.size - 1) {
                    ok(code = 1)
                }
            }
        }
    }

    private fun insertSong(playListBean: PlayListBean, songName: String) {
        val musicBeans: List<MusicBean> =
            musicDao().queryBuilder().where(MusicBeanDao.Properties.Title.eq(songName)).build()
                .list()
        if (musicBeans.isNotEmpty()) {
            val musicBean = musicBeans[0]
            musicBean.playListFlag = playListBean.title
            musicBean.addListTime = System.currentTimeMillis()
            musicDao().update(musicBean)
            // 更新列表的歌曲数量
            playListBean.songCount += 1
            playDao().update(playListBean)
        }
    }

    fun deletePlayList(musicInfo: PlayListBean) {
        // 同步更新列表中，的歌曲的列表标识 (更新为“LSP_98”)
        MusicDaoUtil.setMusicListFlag(musicInfo)
        ok(code = 2)

    }
}