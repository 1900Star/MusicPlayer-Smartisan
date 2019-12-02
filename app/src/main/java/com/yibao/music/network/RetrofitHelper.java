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

    private static MusicService getMusicService() {
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

    public static void getSongAlbumImg(String songName) {
        String albumUrlHead = "http://y.gtimg.cn/music/photo_new/T002R180x180M000";
        getMusicService().search(songName, 1)
                .subscribeOn(Schedulers.io()).subscribe(new BaseObserver<SearchSong>() {
            @Override
            public void onNext(SearchSong searchSong) {
                String albummid = searchSong.getData().getSong().getList().get(0).getAlbummid();
                LogUtil.d(TAG, albumUrlHead + albummid + ".jpg");

            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LogUtil.d(TAG, e.getMessage());
            }
        });
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
//                            LogUtil.d(TAG, "歌词   " + onlineSongLrc.getLyric());
                            if (lyric.contains(Constants.PURE_MUSIC)) {
                                boolean b = DownloadLyricsUtil.writeTxtToFile(lyric, songName, artist);
                                LyricDownBean lyricDownBean = new LyricDownBean(true, b ? Constants.PURE_MUSIC : Constants.NO_LYRICS);
                                RxBus.getInstance().post(Constants.MUSIC_LYRIC_OK, lyricDownBean);
                            } else {
                                boolean b = DownloadLyricsUtil.writeTxtToFile(lyric, songName, artist);
                                LogUtil.d(TAG, "写入结果  " + b);
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
