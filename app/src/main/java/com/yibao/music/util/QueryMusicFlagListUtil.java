package com.yibao.music.util;

import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

/**
 * @author Luoshipeng
 * @ Name:   QueryMusicFlagListUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/5/6/ 18:02
 * @ Des:    //根据条件查询MusicList
 */
public class QueryMusicFlagListUtil {

    /**
     * @param queryBuilder queryBuilder
     * @param pageFlag     页面标识:  1 歌曲名列表、2 评分、3 播放次数、4 添加时间、5 自定义播放列表详情 、6 艺术家列表、7 专辑列表、
     *                     8 收藏列表、    【  搜索类别：  11歌曲 、 12专辑 、 13 艺术家 、14 全部 】
     * @param condition    查询关键字：艺术家、专辑、曲名 、播放列表
     * @return List
     */
    public static List<MusicBean> getMusicDataList(QueryBuilder<MusicBean> queryBuilder, int pageFlag, String condition) {
        boolean b = condition != null && !condition.equals("");

//        LogUtil.d("lsp", "  数据源 pageType  ==   " + pageFlag + "  condition  =  " + condition + " = " + b);

        if (condition != null && !condition.equals("")) {
            WhereCondition whereCondition = null;
            if (pageFlag == Constant.NUMBER_FIVE) {
                // 自定义播放列表
                whereCondition = MusicBeanDao.Properties.PlayListFlag.eq(condition);
            } else if (pageFlag == Constant.NUMBER_SIX) {
                // 艺术家列表数据
                whereCondition = MusicBeanDao.Properties.Artist.eq(condition);
                // 专辑列表数据
            } else if (pageFlag == Constant.NUMBER_SEVEN) {
                whereCondition = MusicBeanDao.Properties.Album.eq(condition);
            } else if (pageFlag == Constant.NUMBER_ELEVEN) {
                // 11 表示按歌曲搜索，歌曲 like 关键字
                whereCondition = MusicBeanDao.Properties.Title.like(condition);
            } else if (pageFlag == Constant.NUMBER_TWELVE) {
                // 12 表示按专辑搜索
                whereCondition = MusicBeanDao.Properties.Album.eq(condition);
            } else if (pageFlag == Constant.NUMBER_THIRTEEN) {
                // 13 表示按艺术家搜索
                whereCondition = MusicBeanDao.Properties.Artist.eq(condition);
            } else if (pageFlag == Constant.NUMBER_FOURTEEN) {
                // 14 全部 ，暂时先按歌曲名 like
                whereCondition = MusicBeanDao.Properties.Title.like(condition);
            }
            if (whereCondition != null) {
                return MusicListUtil.sortByAbc(queryBuilder.where(whereCondition).build().list());
            }
        } else {
            if (pageFlag == Constant.NUMBER_ONE) {
                // 按歌ABC
                return MusicListUtil.sortByAbc(queryBuilder.list());
            } else if (pageFlag == Constant.NUMBER_TWO) {
                // 按评分
                return queryBuilder.orderDesc(MusicBeanDao.Properties.SongScore).build().list();
            } else if (pageFlag == Constant.NUMBER_THREE) {
                // 按播放次数
                return queryBuilder.orderDesc(MusicBeanDao.Properties.PlayFrequency).build().list();
            } else if (pageFlag == Constant.NUMBER_FOUR) {
                // 按添加时间
                return queryBuilder.orderDesc(MusicBeanDao.Properties.AddTime).build().list();
            } else if (pageFlag == Constant.NUMBER_EIGHT) {
                // 收藏列表
                return queryBuilder.where(MusicBeanDao.Properties.IsFavorite.eq(true)).orderDesc(MusicBeanDao.Properties.Time).build().list();
            }
        }
        LogUtil.d("lsp","=====AAA====== 默认数据 =========AAA======");
        return MusicListUtil.sortByAbc(queryBuilder.build().list());
    }

}
