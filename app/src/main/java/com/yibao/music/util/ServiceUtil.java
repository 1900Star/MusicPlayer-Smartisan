package com.yibao.music.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Author: Luoshipeng
 * @ Name:   ServiceUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2019/1/3/ 21:44
 * @ Des:    TODO
 */
public class ServiceUtil {
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if ((Constants.NULL_STRING).equals(ServiceName) || ServiceName == null) {
            return false;
        }
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (myManager != null) {
            List<ActivityManager.RunningServiceInfo> runningService = myManager.getRunningServices(200);
            for (int i = 0; i < runningService.size(); i++) {
                if (runningService.get(i).service.getClassName()
                        .equals(ServiceName)) {
                    return true;
                }
            }
        }
        return false;
    }

}
