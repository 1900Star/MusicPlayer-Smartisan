package com.yibao.music.util;

import android.os.Environment;

import com.yibao.music.base.listener.LyricDownCallBack;
import com.yibao.music.base.listener.LyricsCallBack;
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

    public static boolean checkLyricFile(String songName, String artist) {
        // 通过QQ音乐下载的歌曲名会带有这个字符串，需要将正确的歌名截取出来，才能用于网络歌词的匹配。
        String acturlSongName;
        String actualArtist;
        if (songName.contains(Constants.MQMS2)) {
            TitleAndArtistBean bean = TitleArtistUtil.getBean(songName);
            acturlSongName = bean.getSongName();
            actualArtist = bean.getSongArtist();
        } else {
            acturlSongName = songName;
            actualArtist = UNKNOWN_NAME.equals(artist) ? "Smartisan" : artist;
        }
        LogUtil.d(" 本地歌词信息  " + acturlSongName + " $$ " + actualArtist);
        String path = Constants.MUSIC_LYRICS_ROOT + acturlSongName + "$$" + actualArtist + ".lrc";
        File file = new File(path);
        return file.exists();
    }


    /**
     * 将歌词封装到list中
     *
     * @param songName 歌名
     * @param artist   歌手
     */
    public static void downloadLyricFile(String songName, String artist, LyricDownCallBack callBack) {
        String acturlSongName;
        String actualArtist;
        if (songName.contains(Constants.MQMS2)) {
            TitleAndArtistBean bean = TitleArtistUtil.getBean(songName);
            acturlSongName = bean.getSongName();
            actualArtist = bean.getSongArtist();
        } else {
            acturlSongName = songName;
            actualArtist = UNKNOWN_NAME.equals(artist) ? "Smartisan" : artist;
        }
        String path = Constants.MUSIC_LYRICS_ROOT + songName + "$$" + artist + ".lrc";
        File file = new File(path);
        if (!file.exists()) {
            if (NetworkUtil.isNetworkConnected()) {
                // 本地没有歌词，先获取网络歌词的下载地址Url,，如果没有歌词地址或者地址下载失败，返回" 暂无歌词"
                DownloadLyricsUtil.downloadLyric(acturlSongName, actualArtist, lyricsUrl -> {
                    if (lyricsUrl == null) {
//                        // 二次搜索，只用歌名搜索歌词
                        DownloadLyricsUtil.downloadLyric(acturlSongName, null, lastLyricUrl -> {
                            if (lastLyricUrl != null) {
                                LogUtil.d("     歌词地址last    ====    " + lastLyricUrl);
                                callBack.downLyric(DownloadLyricsUtil.getLyricsFile(lastLyricUrl, songName, actualArtist), "成功");
                            } else {
                                callBack.downLyric(false, "没有发现歌词");
                            }
                        });
                    } else {
                        callBack.downLyric(DownloadLyricsUtil.getLyricsFile(lyricsUrl, songName, actualArtist), "成功");
                    }


                });

            }
        }
    }

    /**
     * 将读取本地歌词文件，将歌词封装到List中。
     *
     * @return 返回歌词List
     */
    public static List<MusicLyricBean> getLyricList(String songName, String artist) {
        List<MusicLyricBean> lrcList = new ArrayList<>();
        String path = Constants.MUSIC_LYRICS_ROOT + songName + "$$" + artist + ".lrc";
        LogUtil.d("=====  path  "+path);
        File file = new File(path);
        if (!file.exists()) {
            lrcList.add(new MusicLyricBean(0, "没有发现歌词"));
        } else {
            ThreadPoolProxyFactory.newInstance().execute(() -> {
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

            });

        }
        Collections.sort(lrcList);
        LogUtil.d("歌词最终长度 ： " + lrcList.size());
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