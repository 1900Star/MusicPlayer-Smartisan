package com.yibao.music.util;

import com.yibao.music.network.RetrofitHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @ Author: Luoshipeng
 * @ Name:   OkHttpUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/10/3/ 21:08
 * @ Des:    TODO
 */
public class OkHttpUtil {
    private static final String TAG = "====" + OkHttpUtil.class.getSimpleName() + "    ";
    private static OkHttpClient okHttpClient;

    public static OkHttpClient getClient() {
        if (okHttpClient == null) {
            synchronized ("OkHttpUtil") {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            .connectTimeout(3, TimeUnit.SECONDS)
                            .writeTimeout(3, TimeUnit.SECONDS)
                            .readTimeout(3, TimeUnit.SECONDS).addInterceptor(new LoggingInterceptor())
                            .build();
                }
            }
        }
        return okHttpClient;

    }

    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            LogUtil.d(TAG, String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));
            Response response = chain.proceed(request);
//            LogUtil.d(TAG, " body   " + response.body().string());
            LogUtil.d(TAG, response.request().url() + request.headers().toString());
            return response;
        }
    }

}
