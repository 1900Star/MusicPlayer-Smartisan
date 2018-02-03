package com.yibao.music.util;

/*
 *  @项目名：  BigGirl 
 *  @包名：    com.yibao.biggirl.util
 *  @文件名:   UrlUtil
 *  @创建者:   Stran
 *  @创建时间:  2017/12/20 18:59
 *  @描述：    TODO
 */

import java.util.List;

/**
 * @author Stran
 * 获取Unsplash图片地址
 */
public class UrlUtil {
    public static List<String> getUnsplashUrl(List<String> list) {

       for (int i = 10; i < 1000; i++) {
           list.add(Constants.UNSPLASH_URL + i);
       }
        return list;
    }




}
