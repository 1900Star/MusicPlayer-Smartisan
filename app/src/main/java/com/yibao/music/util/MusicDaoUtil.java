package com.yibao.music.util;

import com.yibao.music.MusicApplication;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.MusicBeanDao;

import java.io.FileNotFoundException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Luoshipeng
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
     * @param queryConditions 查询的关键字
     * @return 查询结果以集合的类型返回
     */
    public static Observable<List<MusicBean>> getSearchResult( MusicBeanDao musicBeanDao, String queryConditions) {
        return Observable.create((ObservableOnSubscribe<List<MusicBean>>) emitter -> {
            // 歌手搜索
            List<MusicBean> artistList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(queryConditions)).build().list();
            if (artistList != null && artistList.size() > 0) {
                insertSearchBean(emitter, artistList);
            } else {
                // 专辑搜索
                List<MusicBean> albumList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(queryConditions)).build().list();
                if (albumList != null && albumList.size() > 0) {
                    insertSearchBean(emitter, albumList);
                } else {
                    // 根据歌名精确搜索
                    List<MusicBean> songList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Title.eq(queryConditions)).build().list();
                    if (songList != null && songList.size() > 0) {
                        insertSearchBean(emitter, songList);
                    } else {
                        // 模糊匹配搜索, % 加在前面为包含queryConditions，加在后面查询的结果是以 queryConditions 开头的数据。
                        List<MusicBean> searchSongList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Title.like("%"+queryConditions + "%")).list();
                        if (searchSongList.size() == 0) {
                            emitter.onError(new FileNotFoundException());
                        } else {
                            insertSearchBean(emitter, searchSongList);
                        }
                    }


                }
            }

        }).subscribeOn(Schedulers.io());
    }

    /**
     * 将一个搜索结果保存到本地，同时做了重复保存的判断。
     */
    private static void insertSearchBean(ObservableEmitter<List<MusicBean>> emitter, List<MusicBean> beanList) {
        emitter.onNext(beanList);
        emitter.onComplete();
    }

    public static void setMusicListFlag(PlayListBean playListBean) {
        MusicBeanDao musicDao = MusicApplication.getInstance().getMusicDao();
        MusicApplication.getInstance().getPlayListDao().delete(playListBean);
        ThreadPoolProxyFactory.newInstance().execute(() -> {
            List<MusicBean> musicBeanList = musicDao.queryBuilder().where(MusicBeanDao.Properties.PlayListFlag.eq(playListBean.getTitle())).build().list();
            for (MusicBean musicBean : musicBeanList) {
                musicBean.setPlayListFlag(Constant.PLAY_LIST_BACK_FLAG);
                musicBean.setAddListTime(Constant.NUMBER_ZERO);
                musicDao.update(musicBean);
            }
        });
    }


}
