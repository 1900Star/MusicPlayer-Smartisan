package com.yibao.music.network;

import com.yibao.music.util.Api;
import com.yibao.music.util.OkHttpUtil;

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
