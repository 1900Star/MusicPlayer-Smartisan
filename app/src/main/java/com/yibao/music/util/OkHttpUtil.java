package com.yibao.music.util;

import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
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
}
