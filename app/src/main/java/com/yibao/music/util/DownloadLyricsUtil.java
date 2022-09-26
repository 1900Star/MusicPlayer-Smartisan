package com.yibao.music.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.yibao.music.MusicApplication;
import com.yibao.music.model.LyricDownBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Luoshipeng
 * @ Author: Luoshipeng
 * @ Name:   DownloadLyricsUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/10/3/ 20:18
 * @ Des:    下载网络歌词
 */
public class DownloadLyricsUtil {

    /**
     * 字符串写入本地txt
     *
     * @param content  文件内容
     * @param songName 文件地址
     * @param artist   文件名
     * @return 写入结果
     */
    public static boolean saveLyrics(String content, String songName, String artist) {
        boolean isSavaFile;
        String strContent = content + "\r\n";
        try {
            File file = FileUtil.getLyricsFile(songName, artist);
            LogUtil.d("lsp", file.getAbsolutePath());

            RandomAccessFile raf = new RandomAccessFile(file, "rwd");
            raf.seek(file.length());
            raf.write(strContent.getBytes());
            raf.close();
            isSavaFile = true;
        } catch (Exception e) {
            isSavaFile = false;
            Log.e("TestFile", "Error on write File:" + e);
        }
        return isSavaFile;
    }

}
