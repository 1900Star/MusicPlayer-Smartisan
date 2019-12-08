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

    public static MusicService getMusicService(boolean b) {
        if (retrofit == null) {
            synchronized (RetrofitHelper.class) {
                if (retrofit == null) {
                    retrofit = new Retrofit.Builder().baseUrl(b ? Api.FIDDLER_BASE_QQ_URL : Api.SINGER_PIC_BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .client(OkHttpUtil.getClient())
                            .build();
                }

            }

        }
        return retrofit.create(MusicService.class);
    }

    public static void getArtistImg(Context context, String artist, OnImagePathListener listener) {
        String albumUrlHead = "http://y.gtimg.cn/music/photo_new/T002R500x500M000";
        getMusicService(false).getSingerImg(artist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<SingerImg>() {
                    @Override
                    public void onNext(SingerImg singerImg) {
                        String picUrl = singerImg.getResult().getArtists().get(0).getPicUrl();
//                        String picUrl = albumUrlHead + albummid + ".jpg";
                        LogUtil.d(TAG, "请求到的歌手图片地址 " + picUrl);
                        listener.imageUrl(picUrl);
                        ImageUitl.glideSaveImg(context, picUrl, 2, artist, artist);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(TAG, e.getMessage());
                    }
                });
    }

    public static void getAlbumImg(Context context, String artist, OnImagePathListener listener) {
        String albumUrlHead = "http://y.gtimg.cn/music/photo_new/T002R500x500M000";
        getMusicService(true).searchAlbum(artist, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Album>() {
                    @Override
                    public void onNext(Album album) {
                        String albumMid = album.getData().getAlbum().getList().get(0).getAlbumMID();
                        String picUrl = albumUrlHead + albumMid + ".jpg";
                        LogUtil.d(TAG, "请求到的图片地址 " + picUrl);
                        listener.imageUrl(picUrl);
                        ImageUitl.glideSaveImg(context, picUrl, 3, artist, artist);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(TAG, e.getMessage());
                    }
                });
    }

    public static void getSongLyrics(String songName, String artist) {
        getMusicService(true).search(songName, 1)
                .subscribeOn(Schedulers.io())
                .flatMap((Function<SearchSong, Observable<OnlineSongLrc>>) searchSong -> {
                    List<SearchSong.DataBean.SongBean.ListBean> list = searchSong.getData().getSong().getList();
                    SearchSong.DataBean.SongBean.ListBean listBean = list.get(0);
                    return getMusicService(true).getOnlineSongLrc(listBean.getSongmid());
                })
//                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<OnlineSongLrc>() {
                    @Override
                    public void onNext(OnlineSongLrc onlineSongLrc) {
                        String lyric = onlineSongLrc.getLyric();
                        if (lyric != null) {
                            if (lyric.contains(Constants.PURE_MUSIC)) {
                                boolean b = DownloadLyricsUtil.saveLyrics(lyric, songName, artist);
                                LyricDownBean lyricDownBean = new LyricDownBean(true, b ? Constants.PURE_MUSIC : Constants.NO_LYRICS);
                                RxBus.getInstance().post(Constants.MUSIC_LYRIC_OK, lyricDownBean);
                            } else {
                                boolean b = DownloadLyricsUtil.saveLyrics(lyric, songName, artist);
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
