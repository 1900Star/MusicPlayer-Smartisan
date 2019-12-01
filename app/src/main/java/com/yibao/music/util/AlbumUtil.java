package com.yibao.music.util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AlbumUtil {
    private static final String TAG = "====" + AlbumUtil.class.getSimpleName() + "    ";

    public static void getAlbumUrl(String singer) {
        String url = "http://music.163.com/api/search/get/web?s=" + singer + "&type=100";
        ThreadPoolProxyFactory.newInstance().execute(() -> OkHttpUtil.getAlbum(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                LogUtil.d(TAG, s);

            }
        }));
    }

    public static void getLyricsUrl(String singer) {
        String url = "https://c.y.qq.com/lyric/fcgi-bin/fcg_query_lyric_new.fcg?songmid=001X0PDf0W4lBq&format=json&nobase64=1";

        ThreadPoolProxyFactory.newInstance().execute(() -> OkHttpUtil.getLyrics(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d(TAG, e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String s = response.body().string();
                LogUtil.d(TAG, s);

            }
        }));
    }
}
