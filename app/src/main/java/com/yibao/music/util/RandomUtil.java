package com.yibao.music.util;

import com.yibao.music.model.MusicBean;

import java.util.List;
import java.util.Random;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/8/14 05:01
 *
 * @author Stran
 */
public class RandomUtil {
    public static int getRandomPostion(int listSize) {
        Random random = new Random();
        return random.nextInt(listSize);

    }

    public static String getRandomUrl() {
        Random random = new Random();
        int position = random.nextInt(Api.picUrlArr.length) + 1;
        return Api.picUrlArr[position];

    }


}
