package com.yibao.music.service;

import com.yibao.music.model.qq.Album;
import com.yibao.music.model.qq.AlbumSong;
import com.yibao.music.model.qq.OnlineSongLrc;
import com.yibao.music.model.qq.SearchSong;
import com.yibao.music.model.qq.SingerImg;
import com.yibao.music.model.qq.SongLrc;
import com.yibao.music.util.Api;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * @author luoshipeng
 * createDate：2019/12/2 0002 10:37
 * className   MusicService
 * Des：TODO
 */
public interface MusicService {
    /**
     *  搜索歌曲：https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=2&n=2&w=想起&format=json
     */
    @GET(Api.SEARCH_SONG)
    Observable<SearchSong> search(@Query("w") String seek, @Query("p")int offset);

    /**
     * 搜索专辑：https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=1&n=2&w=林宥嘉&format=json&t=8
     * @param seek 搜索关键字
     * @param offset 页数
     */
    @GET(Api.SEARCH_ALBUM)
    Observable<Album> searchAlbum(@Query("w") String seek, @Query("p")int offset);

    /**
     * 专辑详细：https://c.y.qq.com/v8/fcg-bin/fcg_v8_album_info_cp.fcg?albummid=004YodY33zsWTT&format=json
     * @param id 专辑mid
     */
    @GET(Api.ALBUM_DETAIL)
    Observable<AlbumSong> getAlbumSong(@Query("albummid")String id);


    /**
     * 根据songmid获取歌词：https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=000wocYU11tSzS&format=json&nobase64=1
     * headers中的Referer是qq用来防盗链的
     */
    @Headers(Api.HEADER_REFERER)
    @GET(Api.ONLINE_SONG_LRC)
    Observable<OnlineSongLrc> getOnlineSongLrc(@Query("songmid") String songId);

    /**
     * 搜索歌词：https://c.y.qq.com/soso/fcgi-bin/client_search_cp?p=1&n=1&w=说谎&format=json&t=7
     * @param seek 关键词
     */
    @GET(Api.SONG_LRC)
    Observable<SongLrc> getLrc(@Query("w") String seek);

    /**
     *                                                      api/search/get/web?csrf_token=&type=100
     * 得到歌手照片，主要用于本地音乐：http://music.163.com/api/search/get/web?s=林俊杰&type=100
     * @param singer 歌手名字
     */
    @Headers(Api.HEADER_USER_AGENT)
    @POST(Api.SINGER_PIC)
    @FormUrlEncoded
    Observable<SingerImg> getSingerImg(@Field("s")String singer);
}
