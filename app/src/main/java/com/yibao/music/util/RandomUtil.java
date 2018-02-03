package com.yibao.music.util;

import com.yibao.music.model.music.MusicBean;

import java.util.ArrayList;
import java.util.Random;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/8/14 05:01
 */
public class RandomUtil {
    public static int getRandomPostion(ArrayList<MusicBean> list) {
        Random random = new Random();
        return random.nextInt(list.size()) + 1;

    }
}
