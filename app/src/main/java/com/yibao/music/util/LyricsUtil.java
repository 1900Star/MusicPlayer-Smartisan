package com.yibao.music.util;

import android.os.Environment;

import com.yibao.music.model.MusicLyricBean;
import com.yibao.music.model.TitleAndArtistBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.util
 * @文件名: LyricsUtil
 * @author: Stran
 * @Email: www.strangermy98@gmail.com
 * @创建时间: 2018/1/28 22:08
 * @描述： {将歌词封装到List里}
 */

public class LyricsUtil {

    private static BufferedReader br;
    private static final String UNKNOWN_NAME = "<unknown>";

    /**
     * 将歌词封装到list中
     *
     * @param songName 歌名
     * @param artist   歌手
     * @return 返回歌词list
     */
    public static synchronized List<MusicLyricBean> getLyricList(String songName, String artist) {
        // 通过QQ音乐下载的歌曲名会带有这个字符串，需要将正确的歌名截取出来，才能用于网络歌词的匹配。
        String acturlSongName;
        String actualArtist;
        if (songName.contains("[mqms2]")) {
            TitleAndArtistBean bean = TitleArtistUtil.getBean(songName);
            acturlSongName = bean.getSongName();
            actualArtist = bean.getSongArtist();
        } else {
            acturlSongName = songName;
            actualArtist = UNKNOWN_NAME.equals(artist) ? "Smartisan" : artist;
        }
        LogUtil.d(" 本地歌词信息  " + acturlSongName + " $$ " + actualArtist);
        List<MusicLyricBean> lrcList = new ArrayList<>();
        String str = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/lyric/";
        String path = str + acturlSongName + "$$" + actualArtist + ".lrc";
        File file = new File(path);
//        LogUtil.d("FilePath ==      " + (file.exists() ? "AAA" : "BBBBB"));
        if (!file.exists()) {
            if (NetworkUtil.isNetworkConnected()) {
                // 本地没有歌词，从网络下载，如果下载失败，返回" 暂无歌词"
                String lyricsUrl = DownloadLyricsUtil.getLyricsDownUrl(acturlSongName, actualArtist);
//                LogUtil.d("     歌词地址first    ====    " + lyricsUrl);
                if (lyricsUrl != null) {
                    loadLyrics(acturlSongName, actualArtist, lrcList, file, lyricsUrl);
                } else {
//                        // 二次搜索，只用歌名搜索歌词
//                        String lastUrl = DownloadLyricsUtil.getLyricsDownUrl(acturlSongName, null);
//                        if (lastUrl != null) {
//                            LogUtil.d("     歌词地址last    ====    " + lastUrl);
//                            loadLyrics(acturlSongName, actualArtist, lrcList, file, lastUrl);
//                        } else {
                    lrcList.add(new MusicLyricBean(0, "暂无歌词"));
//                        }
                }
            } else {
                lrcList.add(new MusicLyricBean(0, "无网络连接，连接网络后自动匹配歌词."));
            }


            return lrcList;

        } else {
            // 本地有歌词，直接返回。
            return lyricsFileToList(lrcList, file);
        }
    }

    private static void loadLyrics(String songName, String actualArtist, List<MusicLyricBean> lrcList, File file, String lyricsUrl) {
        boolean isSuccessful = DownloadLyricsUtil.getLyricsFile(lyricsUrl, songName, actualArtist);
        if (isSuccessful) {
            lyricsFileToList(lrcList, file);
        } else {
            lrcList.add(new MusicLyricBean(0, "暂无歌词"));
        }
    }

    /**
     * 将读取本地歌词文件，将歌词封装到List中。
     *
     * @param lrcList List
     * @param file    歌词文件
     * @return 返回歌词List
     */
    private static List<MusicLyricBean> lyricsFileToList(List<MusicLyricBean> lrcList, File file) {
        try {
            String charsetName = "utf-8";
            br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
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
            if (arr.length > i) {
                int startTime = parseTime(arr[i]);
                MusicLyricBean lrcBean = new MusicLyricBean(startTime, content);
                list.add(lrcBean);
            }
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
        int mtime = 0;
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
            if (arr.length > 1) {
                String sec = arr[1];
                boolean b = isContainsEnglishAndChinese(sec);
                mtime = (int) (Integer.parseInt(min) * 60 * 1000 + Float.parseFloat(b ? "1" : sec) * 1000);
            }
            return arr.length > 1 ? mtime : 1;
        }

    }

    private static boolean isContainsEnglishAndChinese(String str) {
        String regex = "^[a-z0-9A-Z\\\\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }
}