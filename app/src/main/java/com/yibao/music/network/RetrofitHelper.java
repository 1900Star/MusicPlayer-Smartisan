package com.yibao.music.network;

import android.content.Context;
import android.widget.ImageView;

import com.yibao.music.base.BaseObserver;
import com.yibao.music.base.listener.OnImagePathListener;
import com.yibao.music.model.LyricDownBean;
import com.yibao.music.model.qq.Album;
import com.yibao.music.model.qq.OnlineSongLrc;
import com.yibao.music.model.qq.SearchSong;
import com.yibao.music.model.qq.SingerImg;
import com.yibao.music.service.MusicService;
import com.yibao.music.util.Api;
import com.yibao.music.util.Constants;
import com.yibao.music.util.DownloadLyricsUtil;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.OkHttpUtil;
import com.yibao.music.util.RxBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author luoshipeng
 * createDate：2019/12/2 0002 11:14
 * className   RetrofitHelper
 * Des：TODO
 */
public class RetrofitHelper {
    private static final String TAG = "====" + RetrofitHelper.class.getSimpleName() + "    ";
    private static Retrofit retrofit;

    public static MusicService getMusicService() {
        if (retrofit == null) {
            synchronized (RetrofitHelper.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder().baseUrl(Api.FIDDLER_BASE_QQ_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(OkHttpUtil.getClient())
                            .build();
                }

            }

        }
        return retrofit.create(MusicService.class);
    }

    public static MusicService getSingerMusicService() {
        if (retrofit == null) {
            synchronized (RetrofitHelper.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder().baseUrl(Api.SINGER_PIC_BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(OkHttpUtil.getClient())
                            .build();
                }

            }

        }
        return retrofit.create(MusicService.class);
    }




}
