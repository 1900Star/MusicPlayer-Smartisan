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

    static void downFile(String url, Callback callback) {
        Request request = new Request.Builder().url(url).addHeader("Accept-Encoding", "identity")
                .build();
        OkHttpUtil.getClient()
                .newCall(request).enqueue(callback);

    }

    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            LogUtil.d(TAG, String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()));

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            LogUtil.d(TAG, String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()));

            return response;
        }
    }

}
