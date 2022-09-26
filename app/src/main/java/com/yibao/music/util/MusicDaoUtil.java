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
