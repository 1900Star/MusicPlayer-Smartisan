package com.yibao.music.network;

import com.yibao.music.base.BaseObserver;
import com.yibao.music.model.LyricDownBean;
import com.yibao.music.model.qq.AlbumSong;
import com.yibao.music.model.qq.OnlineSongLrc;
import com.yibao.music.model.qq.SearchSong;
import com.yibao.music.service.MusicService;
import com.yibao.music.util.Api;
import com.yibao.music.util.Constants;
import com.yibao.music.util.DownloadLyricsUtil;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.OkHttpUtil;
import com.yibao.music.util.RxBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.CompositeDisposable;
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

    public static String getSongAlbumImg(String songName) {
        final String[] imgUrl = new String[1];
        String albumUrlHead = "http://y.gtimg.cn/music/photo_new/T002R500x500M000";
        getMusicService().search(songName, 1)
                .subscribeOn(Schedulers.io()).subscribe(new BaseObserver<SearchSong>() {
            @Override
            public void onNext(SearchSong searchSong) {
                String albummid = searchSong.getData().getSong().getList().get(0).getAlbummid();
                imgUrl[0] = albumUrlHead + albummid + ".jpg";
                LogUtil.d(TAG, "请求到的图片地址 " + imgUrl[0]);


            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LogUtil.d(TAG, e.getMessage());
            }
        });
        return imgUrl[0];
    }

    public static void getSongLyrics(String songName, String artist) {
        getMusicService().search(songName, 1)
                .subscribeOn(Schedulers.io())
                .flatMap((Function<SearchSong, Observable<OnlineSongLrc>>) searchSong -> {
                    List<SearchSong.DataBean.SongBean.ListBean> list = searchSong.getData().getSong().getList();
                    SearchSong.DataBean.SongBean.ListBean listBean = list.get(0);
                    return getMusicService().getOnlineSongLrc(listBean.getSongmid());
                })
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<OnlineSongLrc>() {
                    @Override
                    public void onNext(OnlineSongLrc onlineSongLrc) {
                        String lyric = onlineSongLrc.getLyric();
                        if (lyric != null) {
                            if (lyric.contains(Constants.PURE_MUSIC)) {
                                boolean b = DownloadLyricsUtil.writeTxtToFile(lyric, songName, artist);
                                LyricDownBean lyricDownBean = new LyricDownBean(true, b ? Constants.PURE_MUSIC : Constants.NO_LYRICS);
                                RxBus.getInstance().post(Constants.MUSIC_LYRIC_OK, lyricDownBean);
                            } else {
                                boolean b = DownloadLyricsUtil.writeTxtToFile(lyric, songName, artist);
                                LyricDownBean lyricDownBean = new LyricDownBean(b, b ? Constants.MUSIC_LYRIC_OK : Constants.MUSIC_LYRIC_FAIL);
                                RxBus.getInstance().post(Constants.MUSIC_LYRIC_OK, lyricDownBean);
                            }
                        } else {
                            RxBus.getInstance().post(Constants.MUSIC_LYRIC_OK, new LyricDownBean(false, Constants.MUSIC_LYRIC_FAIL));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }


}