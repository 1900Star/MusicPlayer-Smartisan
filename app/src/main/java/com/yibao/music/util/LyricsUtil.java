package com.yibao.music.util;

import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicLyricBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
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
    private static final String TAG = "====" + LyricsUtil.class.getSimpleName() + "    ";
    private static BufferedReader br;
    private static final String UNKNOWN_NAME = "<unknown>";

    public static boolean checkLyricFile(String songName, String songArtist) {

        File file = FileUtil.getLyricsFile(songName, songArtist);
        boolean b = VersionUtil.checkAndroidVersionQ() ? FileUtil.isAndroidQFileExists(file.getAbsolutePath()) : file.exists();
        LogUtil.d(TAG, " 本地歌词信息  " + songName + " $$ " + songArtist + " == 是否存在    " + b);
        return file.exists();
    }


    /**
     * 当前歌词不正确，删除重新下载。
     *
     * @param name
     * @param artist
     */
    public static void deleteCurrentLyric(String name, String artist) {
        String songName = StringUtil.getSongName(name);
        String songArtist = StringUtil.getArtist(artist);
        String path = Constant.MUSIC_LYRICS_ROOT + songName + "$$" + songArtist + ".lrc";
        LogUtil.d(TAG, " 删除当前 歌词    " + path);
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 删除本地歌词list长度小于2的歌词文件，以便播放时重新下载正确的歌词。
     */
    public static void clearLyricList() {
        File file = FileUtil.getLyricsDir();
        File[] files = file.listFiles();
        if (files != null) {
            int nu = 0;
            for (File f : files) {
                List<String> lylist = getLyricsList(f);
                if (lylist.size() < 2) {
                    LogUtil.d(TAG, " 歌词长度小于4的 : " + "\n" + f.getAbsolutePath());
                    nu++;
                    f.delete();
                }
            }
            LogUtil.d(TAG, "  无效歌词的长度   " + nu);
        }

    }

    private static List<String> getLyricsList(File file) {
        List<String> strings = new ArrayList<>();
        try {
            String charsetName = "utf-8";
            br = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), charsetName));
            String line = br.readLine();
            while (line != null) {
                strings.add(line);
                if (br != null) {
                    line = br.readLine();
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            strings.add("歌词加载出错");

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
        return strings;
    }

    /**
     * 将读取本地歌词文件，将歌词封装到List中。
     *
     * @return 返回歌词List
     */
    public static List<MusicLyricBean> getLyricList(MusicBean musicBean) {
        List<MusicLyricBean> lrcList = new ArrayList<>();

        try {
            File file = FileUtil.getLyricsFile(musicBean.getTitle(), musicBean.getArtist());
            LogUtil.d(TAG, file.getAbsolutePath());
            String charsetName = "utf-8";
            br = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), charsetName));
            String line = br.readLine();
            while (line != null) {
                ArrayList<MusicLyricBean> been = parseLine(line);
                lrcList.addAll(been);
                if (br != null) {
                    line = br.readLine();
                }
            }
            br.close();
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
        int mtime = 0;
        boolean containsChinese = isContainsEnglishAndChinese(s);
        String braces = "[";
        if (containsChinese) {
            LogUtil.d(TAG, "============== 歌词时间解析异常！================");
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
        String regex = "^[a-z0-9A-Z\\\\u4e00-龥]+$";
        return str.matches(regex);
    }
}