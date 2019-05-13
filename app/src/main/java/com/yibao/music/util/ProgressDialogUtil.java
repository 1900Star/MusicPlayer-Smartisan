package com.yibao.music.util;


import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.widget.ContentLoadingProgressBar;

/**
 * @author: Luoshipeng
 * @ Name:    ProgressDialogUtil
 * @ Email:   strangermy98@gmail.com
 * @ GitHub:  https://github.com/1900Star
 * @ Time:    2019/5/12/ 17:06
 * @ Des:     TODO
 */
public class ProgressDialogUtil {
    public static ProgressDialog getProgressDialog(Activity activity) {
        ContentLoadingProgressBar bar = new ContentLoadingProgressBar(activity);
//        bar.show();

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.setTitle("AAAA");
        progressDialog.setMessage("正在加载...");
        //false不能取消显示，true可以取消显示
        progressDialog.setCancelable(true);
        progressDialog.show();
        return progressDialog;
    }
}
