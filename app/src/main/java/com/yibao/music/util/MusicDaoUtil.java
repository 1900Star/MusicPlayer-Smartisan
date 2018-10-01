package com.yibao.music.util;

import android.content.Context;

import com.yibao.music.base.listener.OnSearchFlagListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.SearchHistoryBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.greendao.SearchHistoryBeanDao;

import java.io.FileNotFoundException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * @ Author: Luoshipeng
 * @ Name:   MusicDaoUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/14/ 16:38
 * @ Des:    搜索相关操作
 */
public class MusicDaoUtil {
    /**
     * 获取音乐搜索结果
     *
     * @param musicBeanDao    查询音乐的Dao
     * @param searchBeanDao   保存搜索记录的Dao
     * @param queryConditions 查询的关键字
     * @return 查询结果以集合的类型返回
     */
    public static Observable<List<MusicBean>> getSearchResult(OnSearchFlagListener listener, MusicBeanDao musicBeanDao, SearchHistoryBeanDao searchBeanDao, String queryConditions) {

        return Observable.create((ObservableOnSubscribe<List<MusicBean>>) emitter -> {
            List<MusicBean> songList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Title.eq(queryConditions)).build().list();
            if (songList != null && songList.size() > 0) {
                listener.setSearchFlag(3);
                insertSearchBean(searchBeanDao, queryConditions);
                emitter.onNext(songList);
                emitter.onComplete();
            } else {
                List<MusicBean> artistList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(queryConditions)).build().list();
                if (artistList != null && artistList.size() > 0) {
                    listener.setSearchFlag(1);
                    insertSearchBean(searchBeanDao, queryConditions);
                    emitter.onNext(artistList);
                    emitter.onComplete();
                } else {
                    List<MusicBean> albumList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(queryConditions)).build().list();
                    if (albumList != null && albumList.size() > 0) {
                        listener.setSearchFlag(2);
                        insertSearchBean(searchBeanDao, queryConditions);
                        emitter.onNext(albumList);
                        emitter.onComplete();
                    } else {
                        emitter.onError(new FileNotFoundException());
                    }
                }

            }

        }).subscribeOn(Schedulers.io());


    }

    /**
     * 将一个搜索结果保存到本地，同时做了重复保存的判断。
     *
     * @param searchBeanDao   搜索音乐的Dao
     * @param queryConditions 查询条件
     */
    private static void insertSearchBean(SearchHistoryBeanDao searchBeanDao, String queryConditions) {
        List<SearchHistoryBean> historyList = searchBeanDao.queryBuilder().where(SearchHistoryBeanDao.Properties.SearchContent.eq(queryConditions)).build().list();
        if (historyList.size() < 1) {
            searchBeanDao.insert(new SearchHistoryBean(queryConditions, Long.toString(System.currentTimeMillis())));
        }
    }
}
