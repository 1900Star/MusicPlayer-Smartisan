package com.yibao.music.util;

import android.hardware.Camera;

/**
 * @ Author: Luoshipeng
 * @ Name:   PermissionsUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2019/2/24/ 23:09
 * @ Des:    TODO
 */
public class PermissionsUtil {
    public static boolean cameraPermission() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
    }

}
