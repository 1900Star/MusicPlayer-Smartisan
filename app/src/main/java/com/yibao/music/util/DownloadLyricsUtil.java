package com.yibao.music.util;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @ Author: Luoshipeng
 * @ Name:   DownloadLyricsUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/10/3/ 20:18
 * @ Des:    下载网络歌词
 */
public class DownloadLyricsUtil {
    private static final String ONLINE_LYRICS_URL = "http://geci.me/api/lyric/";
    private static boolean isDownloadSucssce = false;
    private static String lyricsUrl;

    /**
     * 获取网络歌词的下载地址
     *
     * @param songName 歌名
     * @param artist   歌手
     * @return 返回下载地址
     */
    public static synchronized String getLyricsDownUrl(String songName, String artist) {
        String queryLrcURL = getQueryLrcURL(songName, artist);
        LogUtil.d("     查询歌词地址    ====    " + queryLrcURL);
        OkHttpUtil.downFile(queryLrcURL, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                LogUtil.d("歌词下载失败   ==  " + e.toString());
                lyricsUrl = null;
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                InputStream inputStream = getInputStream(response);
                if (inputStream != null) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            inputStream));
                    StringBuilder sb = new StringBuilder();
                    String temp;
                    while ((temp = in.readLine()) != null) {
                        sb.append(temp);
                    }
                    try {
                        JSONObject jObject = new JSONObject(sb.toString());
                        int count = jObject.getInt("count");
                        int index = count == 0 ? 0 : new Random().nextInt() % count;
                        JSONArray jArray = jObject.getJSONArray("result");
                        JSONObject obj = jArray.getJSONObject(index);
                        lyricsUrl = obj.getString("lrc");
                        LogUtil.d("CCCCCCCCCCCCCCCCCC   ==  "+lyricsUrl);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        LogUtil.d("歌词下载地址   ====  AA  " + lyricsUrl);
        return lyricsUrl;
    }

    private static String getQueryLrcURL(String title, String artist) {
        String str = ONLINE_LYRICS_URL + Encode(title);
        return artist == null ? str : str + "/" + Encode(artist);
    }

    /**
     * 将网络歌词文件本地
     *
     * @param url      歌词缓冲地址
     * @param songName name
     * @param artist   artist
     * @return 是否下载成功
     */
    public static boolean getLyricsFile(final String url, final String songName, final String artist) {
        LogUtil.d(" 歌词下载信息  " + songName + " $$ " + artist);
        LogUtil.d("     歌词下载地址url    ====    " + url);
        OkHttpUtil.downFile(url, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                isDownloadSucssce = false;
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
                        isDownloadSucssce = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        isDownloadSucssce = false;
                    }
                }
            }

        });
        return isDownloadSucssce;

    }

    private static InputStream getInputStream(Response response) {
        ResponseBody body = response.body();
        if (body != null) {
            return body.byteStream();
        }
        return null;
    }

    // 对歌名和歌手名中的空格进行转码
    private static String Encode(String str) {
        try {
            return URLEncoder.encode(str.trim(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

}
