package com.yibao.music.util;

import com.yibao.music.R;
import com.yibao.music.model.MoreMenuBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @ Name:   MenuListUtil
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/12/1/ 23:15
 * @ Des:    TODO
 * @author Luoshipeng
 */
public class MenuListUtil {
    private static final int[] NAME_ARR = {R.string.add_to_play_list, R.string.add_to_play_queue,
            R.string.favorite, R.string.lyrics, R.string.timing_close_play, R.string.tv_delete};
    private static final int[] PIC_ARR = {R.drawable.more_select_icon_addlist_down,
            R.drawable.more_select_icon_addplay_down, R.drawable.more_favorite_normal,
            R.drawable.more_select_icon_lyric, R.drawable.more_select_icon_timer, R.drawable.more_select_icon_delete};

    public static List<MoreMenuBean> getMenuData(boolean isFavorite, boolean isNeedSetTime) {
        ArrayList<MoreMenuBean> beanArrayList = new ArrayList<>();
        int arrLength = NAME_ARR.length;
        for (int i = 0; i <= arrLength - 1; i++) {
            if (!isNeedSetTime && i == arrLength - 2) {
                continue;
            }
            int favoriteId = isFavorite ? R.drawable.more_favorite_down : R.drawable.more_favorite_normal;
            beanArrayList.add(new MoreMenuBean(i == 2 ? favoriteId : PIC_ARR[i], NAME_ARR[i]));
        }
        return beanArrayList;
    }
}
