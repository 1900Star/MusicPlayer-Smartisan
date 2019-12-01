package com.yibao.music.util;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * @ Author: Luoshipeng
 * @ Name:   OkHttpUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/10/3/ 21:08
 * @ Des:    TODO
 */
class OkHttpUtil {
    private static OkHttpClient okHttpClient;

    private static OkHttpClient getClient() {
        if (okHttpClient == null) {
            synchronized ("OkHttpUtil") {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(3, TimeUnit.SECONDS)
                            .writeTimeout(3, TimeUnit.SECONDS)
                            .readTimeout(3, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return okHttpClient;

    }

    static void downFile(String url, Callback callback) {
        Request request = new Request.Builder().url(url).addHeader("Accept-Encoding", "identity")
                .build();
        OkHttpUtil.getClient()
                .newCall(request).enqueue(callback);

    }

    public static final String HEADER_USER_AGENT = "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36";
    public static final String HEADER_REFERER= "Referer:https://y.qq.com/portal/player.html";

    static void getAlbum(String url, Callback callback) {
        //获取歌手图片需要添加user-agent的表头
        FormBody formBody = new FormBody.Builder().build();

        Request request = new Request.Builder().url(url).
                addHeader("User-Agent", HEADER_USER_AGENT).post(formBody).build();
        OkHttpUtil.getClient()
                .newCall(request).enqueue(callback);

    }

    static void getLyrics(String url, Callback callback) {
        //获取歌手图片需要添加user-agent的表头
        FormBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(url).
                addHeader("Referer", HEADER_REFERER).post(formBody).build();
        OkHttpUtil.getClient()
                .newCall(request).enqueue(callback);

    }
}
