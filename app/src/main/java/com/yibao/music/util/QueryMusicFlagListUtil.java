package com.yibao.music.util;

import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * @ Name:   QueryMusicFlagListUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/5/6/ 18:02
 * @ Des:    //根据条件查询MusicList
 * @author Luoshipeng
 */
public class QueryMusicFlagListUtil {

    /**
     * @param musicBeanDao dao
     *                     //     * @param musicBean    实体
     * @param sortListFlag 列表类型:  按歌ABC、评分、播放次数、添加时间(1、2、3、4)、收藏列表(8)、按条件查询(10)
     * @param dataFlag     1:艺术家、2：专辑、3：曲名 、4: 播放列表
     * @param queryFlag    查询关键字：艺术家、专辑、曲名 、播放列表
     * @return List
     */
    public static List<MusicBean> getMusicDataList(MusicBeanDao musicBeanDao, int sortListFlag, int dataFlag, String queryFlag) {
        // 按歌ABC
        if (sortListFlag == Constants.NUMBER_ONE) {
            return MusicListUtil.sortMusicAbc(musicBeanDao.queryBuilder().list());
            // 按评分
        } else if (sortListFlag == Constants.NUMBER_TWO) {
            return MusicListUtil.sortMusicList(musicBeanDao.queryBuilder().list(), Constants.SORT_SCORE);
            // 按播放次数
        } else if (sortListFlag == Constants.NUMBER_THRRE) {
            return MusicListUtil.sortMusicList(musicBeanDao.queryBuilder().list(), Constants.SORT_FREQUENCY);
            // 按添加时间
        } else if (sortListFlag == Constants.NUMBER_FOUR) {
            return MusicListUtil.sortMusicList(musicBeanDao.queryBuilder().list(), Constants.SORT_DOWN_TIME);
            // 收藏列表
        } else if (sortListFlag == Constants.NUMBER_EIGHT) {
            return MusicListUtil.sortMusicList(musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsFavorite.eq(true)).build().list(), Constants.SORT_FAVORITE_TIME);
            // 10表示按条件查询
        } else if (sortListFlag == Constants.NUMBER_TEN) {
            WhereCondition whereCondition = null;
            // 按艺术家查询列表
            if (dataFlag == Constants.NUMBER_ONE) {
                whereCondition = MusicBeanDao.Properties.Artist.eq(queryFlag);
                // 按专辑名查询列表
            } else if (dataFlag == Constants.NUMBER_TWO) {
                whereCondition = MusicBeanDao.Properties.Album.eq(queryFlag);
                // 按歌曲名查询
            } else if (dataFlag == Constants.NUMBER_THRRE) {
                whereCondition = MusicBeanDao.Properties.Title.eq(queryFlag);
                // 按播放列表查询
            } else if (dataFlag == Constants.NUMBER_FOUR) {
                whereCondition = MusicBeanDao.Properties.PlayListFlag.eq(queryFlag);
            }
            if (whereCondition != null) {
                return musicBeanDao.queryBuilder().where(whereCondition).build().list();
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
    public static List<MusicBean> getDataList(int spMusicFlag, int dataFlag, String queryFlag, MusicBeanDao musicBeanDao) {
        if (spMusicFlag == Constants.NUMBER_THRRE) {
            return MusicListUtil.sortMusicList(musicBeanDao.queryBuilder().list(), Constants.SORT_DOWN_TIME);
        } else if (spMusicFlag == Constants.NUMBER_ONE) {
            return MusicListUtil.sortMusicAbc(musicBeanDao.queryBuilder().list());
        } else if (spMusicFlag == Constants.NUMBER_EIGHT) {
            return musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsFavorite.eq(true)).build().list();
        } else if (spMusicFlag == Constants.NUMBER_TEN) {
            WhereCondition whereCondition = null;
            // 按艺术家查询列表
            if (dataFlag == Constants.NUMBER_ONE) {
                whereCondition = MusicBeanDao.Properties.Artist.eq(queryFlag);
                // 按专辑名查询列表
            } else if (dataFlag == Constants.NUMBER_TWO) {
                whereCondition = MusicBeanDao.Properties.Album.eq(queryFlag);
                // 按歌曲名查询
            } else if (dataFlag == Constants.NUMBER_THRRE) {
                whereCondition = MusicBeanDao.Properties.Title.eq(queryFlag);
                // 按播放列表查询
            } else if (dataFlag == Constants.NUMBER_FOUR) {
                whereCondition = MusicBeanDao.Properties.PlayListFlag.eq(queryFlag);
            }
            if (whereCondition != null) {
                return musicBeanDao.queryBuilder().where(whereCondition).build().list();
            }
        }
        return MusicListUtil.sortMusicAbc(musicBeanDao.queryBuilder().list());
    }

}
