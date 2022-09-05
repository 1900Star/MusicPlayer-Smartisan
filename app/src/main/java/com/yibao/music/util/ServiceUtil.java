package com.yibao.music.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * @ Author: Luoshipeng
 * @ Name:   ServiceUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2019/1/3/ 21:44
 * @ Des:    查看加载地音乐的服务是否在运行
 */
public class ServiceUtil {
    public static boolean isServiceRunning(Context context, String serviceName) {
        if ((Constant.NULL_STRING).equals(serviceName) || serviceName == null) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (myManager != null) {
            List<ActivityManager.RunningServiceInfo> runningService = myManager.getRunningServices(200);
            for (ActivityManager.RunningServiceInfo runningServiceInfo : runningService) {
                if (runningServiceInfo.service.getClassName().equals(serviceName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
