package com.yibao.music.util;

import android.view.inputmethod.InputMethodManager;

/**
 * @ Author: Luoshipeng
 * @ Name:   SoftKeybordUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/15/ 19:34
 * @ Des:    TODO
 */
public class SoftKeybordUtil {
    /**
     *
     * @param manager m
     * @param i 1 隐藏键盘 、 2 显示键盘
     * @param resultUnchangedShown r
     */
    public static void showAndHintSoftInput(InputMethodManager manager, int i, int resultUnchangedShown) {
        if (manager != null) {
            manager.toggleSoftInput(i,
                    resultUnchangedShown);
        }
    }
}
