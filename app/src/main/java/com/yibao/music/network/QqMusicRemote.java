package com.yibao.music.network;

import android.content.Context;

import com.yibao.music.base.BaseObserver;
import com.yibao.music.base.listener.OnAlbumDetailListener;
import com.yibao.music.base.listener.OnImagePathListener;
import com.yibao.music.base.listener.OnSearchLyricsListener;
import com.yibao.music.model.LyricDownBean;
import com.yibao.music.model.qq.Album;
import com.yibao.music.model.qq.AlbumSong;
import com.yibao.music.model.qq.OnlineSongLrc;
import com.yibao.music.model.qq.SearchSong;
import com.yibao.music.model.qq.SingerImg;
import com.yibao.music.util.Constants;
import com.yibao.music.util.DownloadLyricsUtil;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RxBus;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author luoshipeng
 * createDate：2019/12/2 0002 11:26
 * className   QqMusicRemote
 * Des：TODO
 */
public class QqMusicRemote {
    private static final String TAG = "====" + QqMusicRemote.class.getSimpleName() + "    ";

    public static void getSongImg(Context context, String songName, OnImagePathListener listener) {
        String albumUrlHead = "http://y.gtimg.cn/music/photo_new/T002R500x500M000";
        RetrofitHelper.getMusicService().search(songName, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<SearchSong>() {
                    @Override
                    public void onNext(SearchSong searchSong) {
                        String albummid = searchSong.getData().getSong().getList().get(0).getAlbummid();
                        String imgUrl = albumUrlHead + albummid + ".jpg";
                        // 将专辑图片保存到本地

                        ImageUitl.glideSaveImg(context, imgUrl, 1, songName, songName);
                        LogUtil.d(TAG, "图片地址 " + imgUrl);
                        listener.imageUrl(imgUrl);

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(TAG, e.getMessage());
                        listener.imageUrl(null);
                    }
                });

    }

    public static void getArtistImg(Context context, String artist, OnImagePathListener listener) {
        RetrofitHelper.getSingerMusicService().getSingerImg(artist)
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

    public static void getAlbumImg(Context context, String key, OnImagePathListener listener) {
        String albumUrlHead = "http://y.gtimg.cn/music/photo_new/T002R500x500M000";
        RetrofitHelper.getMusicService().searchAlbum(key, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<Album>() {
                    @Override
                    public void onNext(Album album) {
                        String albumMid = album.getData().getAlbum().getList().get(0).getAlbumMID();
                        String picUrl = albumUrlHead + albumMid + ".jpg";
                        LogUtil.d(TAG, "请求到的图片地址 " + picUrl);
                        listener.imageUrl(picUrl);
                        ImageUitl.glideSaveImg(context, picUrl, 3, key, key);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(TAG, e.getMessage());
                    }
                });
    }

    public static void getSongLyrics(String songName, String artist) {
        RetrofitHelper.getMusicService().search(songName, 1)
                .subscribeOn(Schedulers.io())
                .flatMap((Function<SearchSong, Observable<OnlineSongLrc>>) searchSong -> {
                    List<SearchSong.DataBean.SongBean.ListBean> list = searchSong.getData().getSong().getList();
                    SearchSong.DataBean.SongBean.ListBean listBean = list.get(0);
                    return RetrofitHelper.getMusicService().getOnlineSongLrc(listBean.getSongmid());
                })
                .subscribe(new BaseObserver<OnlineSongLrc>() {
                    @Override
                    public void onNext(OnlineSongLrc onlineSongLrc) {
                        sendSearchLyricsResult(onlineSongLrc, songName, artist);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(TAG, "保存歌词出错 " + e.getMessage());
                    }
                });
    }

    public static void getAlbumDetail(String albumName, OnAlbumDetailListener listener) {
        LogUtil.d(TAG, "专辑详情 name  " + albumName);
        RetrofitHelper.getMusicService().searchAlbum(albumName, 1)
                .flatMap((Function<Album, ObservableSource<AlbumSong>>) album -> {
                    List<Album.DataBean.AlbumBean.ListBean> listBeans = album.getData().getAlbum().getList();
                    for (Album.DataBean.AlbumBean.ListBean listBean : listBeans) {
                        if (listBean.getAlbumName().equals(albumName)) {
                            LogUtil.d(TAG, "符合条件的Album  " + listBean.getAlbumName());
                            LogUtil.d(TAG, "符合条件的AlbumPic  " + listBean.getAlbumPic());
                            break;
                        }
                    }
                    String albumMid = album.getData().getAlbum().getList().get(0).getAlbumMID();
                    LogUtil.d(TAG, "albumId  " + albumMid);
                    return RetrofitHelper.getMusicService()
                            .getAlbumSong(albumMid);
                })
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseObserver<AlbumSong>() {
                    @Override
                    public void onNext(AlbumSong albumSong) {
                        super.onNext(albumSong);
                        AlbumSong.DataBean data = albumSong.getData();
                        List<AlbumSong.DataBean.ListBean> beanList = data.getList();
                        listener.getAlbumData(data);
                        LogUtil.d(TAG, data.getCompany());
                        LogUtil.d(TAG, data.getADate());
//                        LogUtil.d(TAG, data.getDesc());

                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        listener.getAlbumData(null);
                    }
                });
    }

    public static void getOnlineLyrics(String songMid, String songName, String artist) {
        LogUtil.d(TAG, songMid + songName + artist);
        RetrofitHelper.getMusicService().getOnlineSongLrc(songMid).subscribeOn(Schedulers.io())
                .subscribe(new BaseObserver<OnlineSongLrc>() {
                    @Override
                    public void onNext(OnlineSongLrc onlineSongLrc) {
                            sendSearchLyricsResult(onlineSongLrc, songName, artist);
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        LogUtil.d(TAG, "保存歌词出错 select  " + e.getLocalizedMessage());
                    }
                });
    }

    private static void sendSearchLyricsResult(OnlineSongLrc onlineSongLrc, String songName, String artist) {
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
}
