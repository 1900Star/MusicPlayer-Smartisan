package com.yibao.music.util;

import android.os.Environment;

import com.yibao.music.model.MusicLyricBean;

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
 * @Email: www.strangermy@outlook.com / www.strangermy98@gmail.com
 * @创建时间: 2018/1/28 22:08
 * @描述： {将歌词封装到List里}
 */

public class LyricsUtil {

    private static BufferedReader br;

    public static ArrayList<MusicLyricBean> getLyricList(String songName, String artist) {
        String actualArtist = "<unknown>".equals(artist) ? "Smartisan" : artist;
        ArrayList<MusicLyricBean> lrcList = new ArrayList<>();
        String str = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/lyric/";
        String path = str + songName + "$$" + actualArtist + ".lrc";
        File file = new File(path);
        if (!file.exists()) {
            new Thread(() -> {
                // 本地没有歌词，从网络下载，如果下载失败，返回" 暂无歌词"
                String lyricsUrl = DownloadLyricsUtil.getLyricsUrl(songName, actualArtist);
                if (lyricsUrl != null) {
                    loadLtyrics(songName, actualArtist, lrcList, file, lyricsUrl);
                } else {
                    // 只用歌名搜索歌词
                    String lastUrl = DownloadLyricsUtil.getLyricsUrl(songName, null);
                    if (lastUrl != null) {
                        loadLtyrics(songName, actualArtist, lrcList, file, lastUrl);
                    } else {
                        lrcList.add(new MusicLyricBean(0, "暂无歌词"));
                    }
                }
            }).start();

            return lrcList;

        } else {
            // 本地有歌词，直接返回。
            return loadLyrics(lrcList, file);
        }
    }

    private static void loadLtyrics(String songName, String actualArtist, ArrayList<MusicLyricBean> lrcList, File file, String lyricsUrl) {
        boolean isSuccessful = DownloadLyricsUtil.getLyrics(lyricsUrl, songName, actualArtist);
        if (isSuccessful) {
            loadLyrics(lrcList, file);
        } else {
            lrcList.add(new MusicLyricBean(0, "暂无歌词"));
        }
    }

    private static ArrayList<MusicLyricBean> loadLyrics(ArrayList<MusicLyricBean> lrcList, File file) {
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            String line = br.readLine();
            while (line != null) {
                ArrayList<MusicLyricBean> been = parseLine(line);
                lrcList.addAll(been);
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            lrcList.add(new MusicLyricBean(0, "歌词加载出错"));
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


    private static ArrayList<MusicLyricBean> parseLine(String str) {
        ArrayList<MusicLyricBean> list = new ArrayList<>();
        String[] arr = str.split("]");
        String content = arr[arr.length - 1];

        for (int i = 0; i < arr.length - 1; i++) {

            int startTime = parseTime(arr[i]);

            MusicLyricBean lrcBean = new MusicLyricBean(startTime, content);
            list.add(lrcBean);
        }
        return list;
    }

    /**
     * 对歌词时间可能出现汉字或英文字母做了处理
     *
     * @param s s
     * @return d
     */
    private static int parseTime(String s) {
        boolean containsChinese = isContainsEnglishAndChinese(s);
        String braces = "[";
        if (containsChinese) {
            LogUtil.d("============== 歌词时间解析异常！================");
            return 0;
        } else {
            String[] arr = s.split(":");
            String min = arr[0].substring(1);
            if (min.contains(braces)) {
                min = "00";
            }
            String sec = arr[1];
            return (int) (Integer.parseInt(min) * 60 * 1000 + Float.parseFloat(sec) * 1000);
        }

    }

    private static boolean isContainsEnglishAndChinese(String str) {
        String regex = "^[a-z0-9A-Z\\\\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }
}