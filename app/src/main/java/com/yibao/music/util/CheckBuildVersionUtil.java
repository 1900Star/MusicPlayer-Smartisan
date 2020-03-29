package com.yibao.music.util;

import android.os.Build;

/**
 * @author luoshipeng
 * createDate：2020/2/28 0028 17:18
 * className   BuildVersionUtil
 * Des：TODO
 */
public class CheckBuildVersionUtil {

    public static boolean checkAndroidVersionQ() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    public static boolean checkAndroidVersionN() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
}
