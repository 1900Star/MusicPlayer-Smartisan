package com.yibao.music.model;

import java.util.List;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/9/8 00:24
 */
public interface MusicDataSource {

    interface InsertFavMusicCallBack {
        void insertStatus(Long insertStatus);

    }


    interface QueryAllFavMusicCallBack {
        void queryAllFavMusic(List<MusicBean> list);

    }

    interface QueryConditionalMusicCallBack {
        void quetyConditional(List<MusicBean> list);

    }



    void insertFavMusic(MusicBean info, InsertFavMusicCallBack callBack);

    void cancelFavMusic(MusicBean info);

    void queryAllFavMusic(QueryAllFavMusicCallBack callBack);

    void quetyConditionalMusic(String title, QueryConditionalMusicCallBack callBack);



}
