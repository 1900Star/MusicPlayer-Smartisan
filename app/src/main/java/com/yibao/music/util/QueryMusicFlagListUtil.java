package com.yibao.music.util;

import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;

import java.util.List;

/**
 * @ Author: Luoshipeng
 * @ Name:   QueryMusicFlagListUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/5/6/ 18:02
 * @ Des:    //根据条件查询MusicList
 */
public class QueryMusicFlagListUtil {


    public static List<MusicBean> getMusicDataList(MusicBeanDao musicBeanDao, MusicBean musicBean, int sortListFlag, int dataFlag, String queryFlag) {
        // 按歌曲名
        if (sortListFlag == Constants.NUMBER_ONE) {
            return MusicListUtil.sortMusicAbc(musicBeanDao.queryBuilder().list());
            // 按评分
        } else if (sortListFlag == Constants.NUMBER_TWO) {
            LogUtil.d("");
            // 按播放次数
        } else if (sortListFlag == Constants.NUMBER_THRRE) {
            LogUtil.d("");
            // 按添加时间
        } else if (sortListFlag == Constants.NUMBER_FOUR) {
            return MusicListUtil.sortMusicAddTime(musicBeanDao.queryBuilder().list());
            // 收藏列表
        } else if (sortListFlag == Constants.NUMBER_EIGHT) {
            return musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsFavorite.eq(true)).build().list();
            // 艺术家、l
        } else if (sortListFlag == Constants.NUMBER_TEN) {
            // 按艺术家查询列表
            if (dataFlag == Constants.NUMBER_ONE) {
                musicBean.setArtist(queryFlag);
                return musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(musicBean.getArtist())).build().list();
                // 按专辑名查询列表
            } else if (dataFlag == Constants.NUMBER_TWO) {
                musicBean.setAlbum(queryFlag);
                return musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(musicBean.getAlbum())).build().list();
            }

        }
        return MusicListUtil.sortMusicAbc(musicBeanDao.queryBuilder().list());

    }

    /**
     * getSpMusicFlag()先获取上次播放列表的标记，根据标记初始化对应的列表数据 。
     * <p>
     * 1 歌曲名   2  评分   3  播放次数        4  添加时间
     *
     * @return h
     */
    public static List<MusicBean> getDataList(int spMusicFlag, MusicBeanDao musicBeanDao) {
        if (spMusicFlag == Constants.NUMBER_THRRE) {
            return MusicListUtil.sortMusicAddTime(musicBeanDao.queryBuilder().list());
        } else if (spMusicFlag == Constants.NUMBER_ONE) {
            return musicBeanDao.queryBuilder().list();
        } else if (spMusicFlag == Constants.NUMBER_TEN) {
            return musicBeanDao.queryBuilder().list();
        } else if (spMusicFlag == Constants.NUMBER_EIGHT) {
            return musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsFavorite.eq(true)).build().list();
        }
        return MusicListUtil.sortMusicAbc(musicBeanDao.queryBuilder().list());
    }

}
