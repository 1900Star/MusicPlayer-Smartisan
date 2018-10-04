package com.yibao.music.util;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.yibao.music.model.OnlineLyricBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
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
    private static final String queryLrcURLRoot = "http://geci.me/api/lyric/";
    private static boolean isDownloadSucssce = false;

    /**
     * 获取网络歌词的下载地址
     *
     * @param songName 歌名
     * @param artist   歌手
     * @return 返回下载地址
     */
    public static String getLyricsUrl(String songName, String artist) {
        String queryLrcURL = getQueryLrcURL(songName, artist);
        try {
            URL url = new URL(queryLrcURL);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String temp;
            while ((temp = in.readLine()) != null) {
                sb.append(temp);
            }
            Gson gson = new Gson();
            OnlineLyricBean o = gson.fromJson(sb.toString(), OnlineLyricBean.class);
            int count1 = o.getCount();
            int randomPostion = RandomUtil.getRandomPostion(count1);
            int index = count1 == 0 ? 0 : randomPostion;
            List<OnlineLyricBean.ResultBean> result = o.getResult();
            return result.get(index).getLrc();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getQueryLrcURL(String title, String artist) {
        String str = queryLrcURLRoot + Encode(title);
        return artist == null ? str : str + "/" + Encode(artist);
    }

    // 歌词文件网络地址，将歌词文件本地缓冲地址
    public static boolean getLyricsFile(String url, final String songName, String artist) {
        Request request = new Request.Builder().url(url).addHeader("Accept-Encoding", "identity")
                .build();
        OkHttpUtil.getClient()
                .newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        isDownloadSucssce = false;
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) {
                        InputStream is;
                        byte[] buf = new byte[1024 * 4];
                        int len;
                        int off = 0;
                        FileOutputStream fos;
                        ResponseBody body = response.body();
                        if (body != null) {
                            is = body.byteStream();
                            try {
                                fos = new FileOutputStream(FileUtil.getLyricsFile(songName, artist));
                                while ((len = is.read(buf)) != -1) {
                                    fos.write(buf, off, len);
                                }
                                fos.flush();
                                fos.close();
                                is.close();
                                isDownloadSucssce = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
        return isDownloadSucssce;

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
