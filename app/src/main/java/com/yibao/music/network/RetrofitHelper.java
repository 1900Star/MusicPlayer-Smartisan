package com.yibao.music.network;

import com.yibao.music.base.BaseObserver;
import com.yibao.music.model.qq.OnlineSongLrc;
import com.yibao.music.model.qq.SearchSong;
import com.yibao.music.model.qq.SingerImg;
import com.yibao.music.service.MusicService;
import com.yibao.music.util.Api;
import com.yibao.music.util.DownloadLyricsUtil;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.OkHttpUtil;
import com.yibao.music.util.ThreadPoolProxyFactory;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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

    public static Disposable getSongAlbumImg(String singer) {
        return getMusicService().getSingerImg(singer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<SingerImg>() {
                    @Override
                    public void accept(SingerImg singerImg) throws Exception {
                        LogUtil.d(TAG, singerImg.toString());
                    }
                });
    }

    public static void searchSong(String singer, int offset) {
        Disposable subscribe = getMusicService().search(singer, offset)
                .subscribeOn(Schedulers.io())
                .flatMap((Function<SearchSong, Observable<OnlineSongLrc>>) searchSong -> {
                    List<SearchSong.DataBean.SongBean.ListBean> list = searchSong.getData().getSong().getList();
                    SearchSong.DataBean.SongBean.ListBean listBean = list.get(0);
                    LogUtil.d(TAG, "");
                    return getMusicService().getOnlineSongLrc(listBean.getSongmid());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onlineSongLrc -> {
                    LogUtil.d(TAG, onlineSongLrc.getLyric());
                    ThreadPoolProxyFactory.newInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            boolean b = DownloadLyricsUtil.writeTxtToFile(onlineSongLrc.getLyric(), "AAAA", "BBBB");
                            LogUtil.d(TAG, "写入结果  " + b);
                        }
                    });

                });
    }

    public static void getSongLyrics(String songName, String artist) {
        getMusicService().search(songName, 1)
                .subscribeOn(Schedulers.io())
                .flatMap((Function<SearchSong, Observable<OnlineSongLrc>>) searchSong -> {
                    List<SearchSong.DataBean.SongBean.ListBean> list = searchSong.getData().getSong().getList();
                    SearchSong.DataBean.SongBean.ListBean listBean = list.get(0);
                    LogUtil.d(TAG, listBean.toString());
                    return getMusicService().getOnlineSongLrc(listBean.getAlbummid());
                })
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<OnlineSongLrc>() {
                    @Override
                    public void onNext(OnlineSongLrc onlineSongLrc) {
                        boolean b = DownloadLyricsUtil.writeTxtToFile(onlineSongLrc.getLyric(), songName, artist);
                        LogUtil.d(TAG, "写入结果  " + b);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    //匹配歌词
    private static void matchLrc(List<SearchSong.DataBean.SongBean.ListBean> listBeans, long duration) {
        boolean isFind = false;
        for (SearchSong.DataBean.SongBean.ListBean listBean : listBeans) {
            if (duration == listBean.getInterval()) {
                isFind = true;
//                mView.setLocalSongId(listBean.getSongmid());
            }
        }
        //如果找不到歌曲id就传输找不到歌曲的消息
        if (!isFind) {
//            mView.getLrcError(Constant.SONG_ID_UNFIND);
        }
    }


}
