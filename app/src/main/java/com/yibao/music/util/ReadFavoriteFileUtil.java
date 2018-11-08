package com.yibao.music.util;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * @ Author: Luoshipeng
 * @ Name:   ReadFavoriteFile
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/30/ 16:59
 * @ Des:    读取歌曲收藏文件
 */
public class ReadFavoriteFileUtil {
    private static final String FAVORITE_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/favorite.txt/";

    /**
     * 将当前收藏的歌曲从本地收藏文件中删除
     *
     * @param content 需要删除的歌曲名
     * @return 是否删除成功
     */
    public static synchronized Observable<Boolean> deleteFavorite(final String content) {

        return Observable.just(stringToSet(content))
                .map(ReadFavoriteFileUtil::againWrite)
                .subscribeOn(Schedulers.io());
    }

    /**
     * @return 将本地收藏文件转换成Set集合
     */
    public static Set<String> stringToSet() {
        Set<String> stringSet = new HashSet<>();

        File file = new File(FAVORITE_FILE);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            for (; ; ) {
                String text = reader.readLine();
                if (text == null) {
                    break;
                } else {
                    stringSet.add(text);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringSet;


    }

    /**
     * 取消收藏时，将本地文件(favorite.txt)中的歌名也删除。
     *
     * @param str 要删除的歌名
     * @return list
     */
    private static Set<String> stringToSet(String str) {
        Set<String> stringSet = new HashSet<>();

        File file = new File(FAVORITE_FILE);
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
            for (; ; ) {
                String text = reader.readLine();
                if (text == null) {
                    break;
                } else {
                    if (text.startsWith(str) || text.contains(str)) {
                        continue;
                    }
                    stringSet.add(text);
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringSet;


    }

    // 不同版本之间对收藏文件兼容处理
    public static void backFile() {
        String time = StringUtil.getCurrentTime();
        String currentTime = "T" + time;
        Set<String> stringSet = backStringToSet(currentTime, true);
        againWrite(stringSet);


    }

    private static Set<String> backStringToSet(String str, Boolean aBoolean) {
        Set<String> list = new HashSet<>();
        if (!aBoolean) {
            return list;
        } else {
            File file = new File(FAVORITE_FILE);
            BufferedReader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
                for (; ; ) {
                    String text = reader.readLine();
                    if (text == null) {
                        break;

                    } else {
//                        if (text.equals(str)) {
//                            continue;
//                        }
                        list.add(text + str);
                    }
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return list;

        }

    }

    private static boolean againWrite(Set<String> list) {
        String nullString = "";
        FileOutputStream outputStream;
        try {
            File file = new File(ReadFavoriteFileUtil.FAVORITE_FILE);
            outputStream = new FileOutputStream(file, false);
            if (!file.exists()) {
                return false;
            } else if (list == null || list.size() < 1) {
                outputStream.write(nullString.getBytes());
                return false;
            } else {
                for (String s : list) {
                    outputStream.write(s.getBytes());
                    outputStream.write("\n".getBytes());
                }
                outputStream.close();
                return true;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static synchronized void writeFile(String s) {
        File file = new File(FAVORITE_FILE);

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


    public static List<String> diffrentList(List<String> listA, List<String> listB) {
        long begin = System.nanoTime();//纳秒
        List<String> favoriteList = new ArrayList<>();
        Map<String, Integer> map = new HashMap<>();
        for (String strA : listA) {
            map.put(strA, 1);
        }
        for (String strB : listB) {
            Integer value = map.get(strB);
            if (value != null) {
                map.put(strB, ++value);
                continue;
            }
            map.put(strB, 1);
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            //value = 1 获取不同元素集合,value != 1 取出相同的元素集合
            if (entry.getValue() != 1) {
                favoriteList.add(entry.getKey());
            }
        }
        long end = System.nanoTime();
        System.out.println("take " + (end - begin) + " time ");
        return favoriteList;
    }
}
