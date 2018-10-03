package com.yibao.music.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;


/**
 * 作者：Stran on 2017/3/23 15:12
 * 描述：${文件操作工具类}
 * 邮箱：strangermy@outlook.com
 */

public class FileUtil {
    private static String MUSIC_LYRICS_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/lyric/";


    public static boolean getFavoriteFile() {
        File file = new File(Constants.FAVORITE_FILE);
        return file.exists();
    }

    public static File getLyricsFile(String songName,String songArtisa) {

        File file = new File(MUSIC_LYRICS_ROOT);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LogUtil.d("===创建失败");
            }
        }
        return new File(file + "/", songName + "$$" + songArtisa + ".lrc");
    }


    public static long getId(String str) {
        //        String str="2017-06-12T10:22:59.890Z";

        return Long.parseLong(str.substring(11, 19)
                .replaceAll(":", ""));
    }

}