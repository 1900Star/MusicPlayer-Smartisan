package com.yibao.music.util;

import android.os.Environment;

import com.yibao.music.model.MusicLyrBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.util
 * @文件名: LyricsUtil
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/1/28 22:08
 * @描述： {TODO}
 */

public class LyricsUtil {

    private static BufferedReader br;

    public static ArrayList<MusicLyrBean> getLyricList(String songName, String artist) {
        ArrayList<MusicLyrBean> lrcList = new ArrayList<>();
        String str = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/lyric/";
        String path = str + songName + "$$" + artist + ".lrc";
        File file = new File(path);
        if (file == null || !file.exists()) {
            lrcList.add(new MusicLyrBean(0, "暂无歌词"));
            return lrcList;
        }
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line = br.readLine();
            while (line != null) {
                ArrayList<MusicLyrBean> been = parseLine(line);
                lrcList.addAll(been);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            lrcList.add(new MusicLyrBean(0, "歌词加载出错"));
            return lrcList;
        } finally {
            try {
                if (br != null) {
                    br.close();
                    br = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Collections.sort(lrcList);
        return lrcList;
    }


    private static ArrayList<MusicLyrBean> parseLine(String str) {
        ArrayList<MusicLyrBean> list = new ArrayList<>();
        String[] arr = str.split("]");
        String content = arr[arr.length - 1];

        for (int i = 0; i < arr.length - 1; i++) {

            int startTime = parseTime(arr[i]);
            MusicLyrBean lrcBean = new MusicLyrBean(startTime, content);
            list.add(lrcBean);
        }
        return list;
    }

    /**
     * 对歌词时间可能出现汉字或英文字母做了处理
     *
     * @param s
     * @return
     */
    private static int parseTime(String s) {
        boolean containsChinese = isContainsEnglishAndChinese(s);
        if (containsChinese) {
            LogUtil.d("============== 歌词解析异常！================");
            return 0;
        } else {
            String[] arr = s.split(":");
            String min = arr[0].substring(1);
            String sec = arr[1];
            return (int) (Integer.parseInt(min) * 60 * 1000 + Float.parseFloat(sec) * 1000);
        }

    }

    private static boolean isContainsEnglishAndChinese(String str) {
        String regex = "^[a-z0-9A-Z\\\\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }
}