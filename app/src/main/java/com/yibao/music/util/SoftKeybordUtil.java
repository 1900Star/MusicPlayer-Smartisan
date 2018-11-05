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
    public static void showAndHintSoftInput(InputMethodManager manager, int i, int resultUnchangedShown) {
        if (manager != null) {
            manager.toggleSoftInput(i,
                    resultUnchangedShown);
        }
    }
}
