package com.yibao.music.util;

import android.os.Environment;

/**
 * 作者：Stran on 2017/3/23 15:26
 * 描述：${常量类}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class Constants {
    /**
     * 广播匹配
     */
    public final static String BUTTON_ID = "ButtonId";
    public final static String ACTION_MUSIC = "MUSIC";

    /**
     * 音乐通知栏
     */
    public static final int FAVORITE = 0;
    public static final int PREV = 1;
    public static final int PLAY = 2;
    public static final int NEXT = 3;
    public static final int CLOSE = 4;

    /**
     * 倒计时结束，广播通知暂停播放音乐
     */
    public static final int COUNTDOWN_fINISH = 5;

    public static final int NUMBER_ZERO = 0;
    public static final int NUMBER_ONE = 1;
    public static final int NUMBER_TWO = 2;
    public static final int NUMBER_THRRE = 3;
    public static final int NUMBER_FOUR = 4;
    public static final int NUMBER_FIEV = 5;
    public static final int NUMBER_SIX = 6;
    public static final int NUMBER_SEVEN = 7;
    public static final int NUMBER_EIGHT = 8;
    public static final int NUMBER_NINE = 9;
    public static final int NUMBER_TEN = 10;
    public static final int NUMBER_ELEVEN = 11;
    public static final int NUMBER_TWELVE = 12;
    public static final int NUMBER_THIRTEEN = 13;
    static final char LETTER_A = 'A';
    static final char LETTER_Z = 'Z';
    public static final char LETTER_HASH = '#';
    public static final String SONG_NAME = "song_name";
    public static final String TIME_SERVICE_NAME = "com.yibao.music.service.CountdownService";
    public static final String NULL_STRING = "";
    public static final String PLAY_LIST_BACK_FLAG = "LSP_98";
    public static final String TIME_FLAG = "time_flag";
    public static final String TIME_FLAG_KEY = "time_flag_key";
    static final int MODE_KEY = 0;
    static final String MUSIC_LOAD = "music_load";

    static final String MUSIC_LOAD_FLAG = "play_load_flag";

    static final String MUSIC_MODE = "music_mode";
    static final String PLAY_MODE_KEY = "play_mode";
    static final String MUSIC_QUERY = "music_query";
    static final String MUSIC_QUERY_FLAG = "music_query_flag";

    static final String DETAIL_FLAG = "detail_flag";
    static final String DETAIL_FLAG_KEY = "detail_flag_key";

    static final String ADD_TO_PLAY_LIST_FLAG = "detail_flag";
    static final String ADD_TO_PLAY_LIST_FLAG_KEY = "detail_flag_key";

    static final String MUSIC_DATA_FLAG = "music_data_flag";
    static final String MUSIC_DATA_LIST_FLAG = "music_data_list_flag";
    static final String PIC_URL_FLAG = "pic_url_flag";
    static final String PIC_URL_LIST_FLAG = "pic_url_list_flag";

    static final String MUSIC_DATA_QUERY = "music_data_query";
    static final String MUSIC_DATA_QUERY_FLAG = "music_data_query_key";

    static final String MUSIC_POSITION = "music_position";
    static final String MUSIC_ITEM_POSITION = "music_item_position";

    static final String MUSIC_PLAY_STATE = "music_play_state";
    static final String MUSIC_PLAY_STATE_KEY = "music_play_state_key";
    static final String MUSIC_FOCUS = "music_focus";
    static final String MUSIC_FOCUS_KEY = "music_FOCUS_key";

    static final String MUSIC_CONFIG = "music_config";
    static final String MUSIC_REMENBER_FLAG = "music_remenber_flag";
    public static final String FRAGMENT_PLAYLIST = "PlayListFragment";
    public static final String FRAGMENT_ARTIST = "ArtistFragment";
    public static final String FRAGMENT_SONG_CATEGORY = "SongCategoryFragment";
    public static final String FRAGMENT_SONG= "SongFragment";
    public static final String FRAGMENT_ALBUM = "AlbumFragment";
    public static final String FRAGMENT_ALBUM_CATEGORY = "AlbumCategoryFragment";
    public static final String SCANNER_MEDIA = "scanner";
    public static final String ADD_TO_LIST = "add2List";
    public static final String FAVORITE_FLAG = "favoriteFlag";
    public static final String NO_NEED_FLAG = "no_need_flag";
    public static final String COUNTDOWN_TIME = "countdown_time";
    public static final String FINISH_TIME = "00:01";

    static final String FAVORITE_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/favorite.txt/";
    public static final String HEADER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yibao/photo/";
    // 崩溃日志本地保存地址
    public static final String CRASH_LOG_PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/CrashLog/log/";
    public static final String DATA_TYPE_TXT = "text/plain";
    public static String UNSPLASH_URL = "https://picsum.photos/1080/1920/?image=";
    // 权限Code
    public static final int CODE_GALLERY_REQUEST = 0xa0;
    public static final int CODE_CAMERA_REQUEST = 0xa1;
    public static final int CODE_RESULT_REQUEST = 0xa2;
    // 头像文件
    public static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    public static final String CROP_IMAGE_FILE_NAME = "header.jpg";
}
