package com.yibao.music.util;

import android.util.Log;

import com.yibao.music.BuildConfig;

/**
 * Log统一管理类
 *
 * @author Stran
 */
public class LogUtil {

    private LogUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }


    private static boolean isDebug = true;

    // 下面是传入自定义tag的函数

    public static void i(String tag, String msg) {
        if (isDebug) {
            Log.i(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (isDebug) {
            Log.e(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (isDebug) {
            Log.v(tag, msg);
        }
    }
}