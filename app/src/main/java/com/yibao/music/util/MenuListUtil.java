package com.yibao.music.util;

import com.yibao.music.R;
import com.yibao.music.model.MoreMenuBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Author: Luoshipeng
 * @ Name:   MenuListUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/12/1/ 23:15
 * @ Des:    TODO
 */
public class MenuListUtil {
    private static final int[] nameArr = {R.string.add_new_play_list, R.string.add_to_play_queue,
            R.string.favorite, R.string.tv_edit, R.string.tv_delete};
    private static final int[] picArr = {R.drawable.more_select_icon_addlist_down,
            R.drawable.more_select_icon_addplay_down, R.drawable.more_favorite_normal,
            R.drawable.more_select_icon_edit, R.drawable.more_select_icon_delete};

    public static List<MoreMenuBean> getMenuData(boolean isFavorite) {
        ArrayList<MoreMenuBean> beanArrayList = new ArrayList<>();
        for (int i = 0; i <= nameArr.length - 1; i++) {
            int favoriteId = isFavorite ? R.drawable.more_favorite_down : R.drawable.more_favorite_normal;
            beanArrayList.add(new MoreMenuBean(i == 2 ? favoriteId : picArr[i], nameArr[i]));
        }
        return beanArrayList;
    }
}
