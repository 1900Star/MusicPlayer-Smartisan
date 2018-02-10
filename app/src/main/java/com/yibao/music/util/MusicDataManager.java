package com.yibao.music.util;

import android.content.Context;

import com.yibao.music.model.MusicBean;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.util
 * @文件名: MusicDataManager
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 22:20
 * @描述： {TODO}
 */

public class MusicDataManager {
    public static Observable<ArrayList<MusicBean>> getMusicData(Context context) {
        return Observable.just(MusicListUtil.getMusicDataList(context)).subscribeOn(Schedulers.io());


    }


}
