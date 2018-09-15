package com.yibao.music.util;

import android.annotation.SuppressLint;

import com.yibao.music.MusicApplication;
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
 * @ Des:    TODO
 */
public class MusicDaoUtil {
    public static Observable<List<MusicBean>> getQueryResult(MusicBeanDao musicBeanDao, SearchHistoryBeanDao searchBeanDao, String queryConditions) {

        return Observable.create((ObservableOnSubscribe<List<MusicBean>>) emitter -> {
            List<MusicBean> songList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Title.eq(queryConditions)).build().list();
            if (songList != null && songList.size() > 0) {
                inserchBean(searchBeanDao, queryConditions);
                emitter.onNext(songList);
                emitter.onComplete();
            } else {
                List<MusicBean> artistList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(queryConditions)).build().list();
                if (artistList != null && artistList.size() > 0) {
                    inserchBean(searchBeanDao, queryConditions);
                    emitter.onNext(artistList);
                    emitter.onComplete();
                } else {
                    List<MusicBean> albumList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(queryConditions)).build().list();
                    if (albumList != null && albumList.size() > 0) {
                        inserchBean(searchBeanDao, queryConditions);
                        emitter.onNext(albumList);
                        emitter.onComplete();
                    } else {
                        emitter.onError(new FileNotFoundException());
                    }
                }

            }

        }).subscribeOn(Schedulers.io());


    }

    private static void inserchBean(SearchHistoryBeanDao searchBeanDao, String queryConditions) {
        List<SearchHistoryBean> historyList = searchBeanDao.queryBuilder().where(SearchHistoryBeanDao.Properties.SearchContent.eq(queryConditions)).build().list();
        if (historyList.size() < 1) {
            searchBeanDao.insert(new SearchHistoryBean(queryConditions, StringUtil.getCurrentTime()));
        }
    }
}
