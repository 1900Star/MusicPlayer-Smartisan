package com.yibao.music.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.yibao.music.base.listener.LyricsCallBack;
import com.yibao.music.model.LyricDownBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    private static final String ONLINE_LYRICS_URL = "http://geci.me/api/lyric/";


    private void writeFile(String directory, String s) {
        File file = new File(directory);

        //实例化一个输出流
        FileOutputStream out;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            out = new FileOutputStream(file, true);
            //把文字转化为字节数组
            byte[] bytes = s.getBytes();
            //写入字节数组到文件
            out.write(bytes);
            out.write("\n".getBytes());
            //关闭输入流
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * 字符串写入本地txt
     *
     * @param strcontent 文件内容
     * @param songName   文件地址
     * @param artist     文件名
     * @return 写入结果
     */
    public static boolean writeTxtToFile(String strcontent, String songName, String artist) {
        boolean isSavaFile;
        File lyricsFile = new File(Constants.MUSIC_LYRICS_ROOT);
        if (!lyricsFile.exists()) {
            lyricsFile.mkdirs();
        }
        String path = Constants.MUSIC_LYRICS_ROOT + songName + "$$" + artist + ".lrc";
        String strContent = strcontent + "\r\n";
        try {
            File file = new File(path);
            if (!file.exists()) {
                Log.d("TestFile", "Create the file:" + path);
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
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

    /**
     * 将网络歌词文件本地
     *
     * @param url 歌词缓冲地址
     */
    public static void downloadlyricsfile(String url, String songName, String artist) {

        String path = Constants.MUSIC_LYRICS_ROOT + songName + "$$" + artist + ".lrc";
        OkHttpUtil.downFile(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                sentResult(false, path, e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                byte[] buf = new byte[1024 * 2];
                int len;
                int off = 0;
                FileOutputStream fos;
                InputStream inputStream = getInputStream(response);
                if (inputStream != null) {
                    try {
                        fos = new FileOutputStream(FileUtil.getLyricsFile(songName, artist));
                        while ((len = inputStream.read(buf)) != -1) {
                            fos.write(buf, off, len);
                        }
                        fos.flush();
                        fos.close();
                        inputStream.close();
                        sentResult(true, null, "OK");
                        LogUtil.d("========= 歌词下载完成 ======final =====   ");
                    } catch (IOException e) {
                        e.printStackTrace();
                        sentResult(false, path, e.getMessage());
                        LogUtil.d("========= 歌词下载失败 ======final =====   ");
                    }
                }
            }


        });
    }


    /**
     * @param b   歌词是否下载成功
     * @param msg 下载的信息，下载成功为 “OK”，下载失败或者下载异常为 Exception.getMessage()
     */
    private static void sentResult(boolean b, String path, String msg) {
        LyricDownBean lyricDownBean = new LyricDownBean(b, msg);
        RxBus.getInstance().post(Constants.MUSIC_LYRIC_OK, lyricDownBean);
        if (!b) {
            // 下载失败或者下载异常，将已经创建的歌词文件删除，以便重新下载完整的歌词。
            FileUtil.deleteFile(new File(path));
        }
    }

    private static InputStream getInputStream(Response response) {
        ResponseBody body = response.body();
        if (body != null) {
            return body.byteStream();
        }
        return null;
    }

    // 对歌名和歌手名中的空格进行转码

    private static String encode(String str) {
        try {
            return URLEncoder.encode(str.trim(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

}
