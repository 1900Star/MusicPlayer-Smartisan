package com.yibao.music.util;

import com.yibao.music.MusicApplication;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.MusicBeanDao;

import java.io.FileNotFoundException;
import java.util.ArrayList;
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
     * @param musicBeanDao 查询音乐的Dao
     * @param key          查询的关键字
     * @return 查询结果以集合的类型返回
     */
    public static Observable<List<MusicBean>> getSearchResult(MusicBeanDao musicBeanDao, String key, int position) {


        return Observable.create((ObservableOnSubscribe<List<MusicBean>>) emitter -> {
            LogUtil.d("====", "=========== searchPosition    " + position + "  searchKey  " + key);
            List<MusicBean> dataList = new ArrayList<>();
            // 全部
            if (position == 0) {
                // 按歌手搜索
                List<MusicBean> artistList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(key)).build().list();
                if (artistList != null && artistList.size() > 0) {
                    dataList.addAll(artistList);
                }

            } else if (position == 1) {
                // 根据歌名精确搜索
                List<MusicBean> songList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Title.eq(key)).build().list();
                if (songList != null && songList.size() > 0) {
                    dataList.addAll(songList);
                } else {
                    // 模糊匹配搜索, % 加在前面为包含key，加在后面查询的结果是以 key 开头的数据。
                    List<MusicBean> searchSongList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Title.like("%" + key + "%")).list();
                    if (searchSongList.size() == 0) {
                        emitter.onError(new FileNotFoundException());
                    } else {
                        dataList.addAll(searchSongList);
                    }
                }

            } else if (position == 2) {
                // 按专辑搜索
                List<MusicBean> albumList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Album.eq(key)).build().list();
                if (albumList != null && albumList.size() > 0) {
                    dataList.addAll(albumList);
                }
            } else if (position == 3) {
                // 按歌手搜索
                List<MusicBean> artistList = musicBeanDao.queryBuilder().where(MusicBeanDao.Properties.Artist.eq(key)).build().list();
                if (artistList != null && artistList.size() > 0) {
                    dataList.addAll(artistList);
                }
            }
            emitter.onNext(dataList);
            emitter.onComplete();

        }).subscribeOn(Schedulers.io());
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
