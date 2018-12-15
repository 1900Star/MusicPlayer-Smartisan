package com.yibao.music.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.yibao.music.MusicApplication;

/**
 * @ Author: Luoshipeng
 * Des：${TODO}
 * Time:2017/4/7 15:52
 */
class NetworkUtil {
    /**
     * 判断是否有网络连接
     *
     * @return 网络状态
     */
    static boolean isNetworkConnected() {

        ConnectivityManager manager = (ConnectivityManager) MusicApplication.getIntstance().getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null) {
                return info.isAvailable();
            }
        }
        return false;
    }
}
