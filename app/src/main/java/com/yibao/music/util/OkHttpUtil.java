package com.yibao.music.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * @ Author: Luoshipeng
 * @ Name:   OkHttpUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/10/3/ 21:08
 * @ Des:    TODO
 */
public class OkHttpUtil {
    private static OkHttpClient okHttpClient;

    public static OkHttpClient getClient() {
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
}
