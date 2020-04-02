package com.yibao.music.util;

import com.yibao.music.model.MusicBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author luoshipeng
 * createDate：2020/4/2 0002 15:05
 * className   CollectionUtil
 * Des：TODO
 */
public class CollectionUtil {

    private CollectionUtil() {
    }

    /**
     * 找出两个集合中不同的元素
     *
     * @param newList new
     * @param oldList old
     * @return List<MusicBean>
     */
    public static Collection getDifferent(List<MusicBean> newList, List<MusicBean> oldList) {
        //使用LinkedList防止差异过大时,元素拷贝

        Collection csReturn = new LinkedList();
        Collection max = newList;
        Collection min = oldList;
        //先比较大小,这样会减少后续map的if判断次数
        if (newList.size() < oldList.size()) {
            max = oldList;
            min = newList;
        }
        //直接指定大小,防止再散列
        Map<Object, Integer> map = new HashMap<>(max.size());
        for (Object object : max) {
            map.put(object, 1);
        }
        for (Object object : min) {
            if (map.get(object) == null) {
                csReturn.add(object);
            } else {
                map.put(object, 2);
            }
        }
        for (Map.Entry<Object, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                csReturn.add(entry.getKey());
            }
        }
        return csReturn;
    }

}
