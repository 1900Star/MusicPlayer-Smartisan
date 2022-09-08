package com.yibao.music.util;

import android.os.Environment;

/**
 * 作者：Stran on 2017/3/23 15:26
 * 描述：${常量类}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
public class Constant {
    /**
     * 广播匹配
     */
    public final static String NOTIFY_BUTTON_ID = "notify_btn_id";
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
     * 排序标识 1 按照歌曲下载时间 ，2 按照歌曲收藏时间 , 3 按照播放次数 ，4 按照评分 ，5 按添加到自定义列表的时间。
     */
    public static final int SORT_DOWN_TIME = 1;
    public static final int SORT_FAVORITE_TIME = 2;
    public static final int SORT_FREQUENCY = 3;
    public static final int SORT_SCORE = 4;
    public static final int SORT_ADD_PALY_LIST_TIME = 5;

    /**
     * 倒计时结束，广播通知暂停播放音乐
     */
    public static final int COUNTDOWN_FINISH = 5;

    public static final int NUMBER_ZERO = 0;
    public static final int NUMBER_ONE = 1;
    public static final int NUMBER_TWO = 2;
    public static final int NUMBER_THREE = 3;
    public static final int NUMBER_FOUR = 4;
    public static final int NUMBER_FIVE = 5;
    public static final int NUMBER_SIX = 6;
    public static final int NUMBER_SEVEN = 7;
    public static final int NUMBER_EIGHT = 8;
    public static final int NUMBER_NINE = 9;
    public static final int NUMBER_TEN = 10;
    public static final int NUMBER_ELEVEN = 11;
    public static final int NUMBER_TWELVE = 12;
    public static final int NUMBER_THIRTEEN = 13;
    // RxBus post  flag

    public static final String HANDLE_BACK = "handle_back";
    public static final String ALBUM_FAG_EDIT = "album_edit";
    public static final String SONG_FAG_EDIT = "song_edit";
    public static final String FAVORITE_POSITION = "favorite_position";
    public static final String POSITION = "position";
    /**
     * 0 表示是通知栏控制播放和暂停
     * 1 表示在通知栏收藏音乐
     * 2 在通知栏关闭通知栏
     */
    public static final String PLAY_STATUS = "play_status";
    public static final String HEADER_PIC_URI = "header_uri";
    public static final String SEARCH_CONDITION = "search_condition";
    public static final String CLEAR_CONDITON = "clear_condition";

    static final char LETTER_A = 'A';
    static final char LETTER_Z = 'Z';
    public static final char LETTER_HASH = '#';
    public static final String SONG_NAME = "song_name";
    public static final String SONG_ARTIST = "song_artist";
    public static final String TIME_SERVICE_NAME = "com.yibao.music.service.CountdownService";
    public static final String LOAD_SERVICE_NAME = "com.yibao.music.service.LoadMusicDataService";
    public static final String NULL_STRING = "";
    public static final String SERVICE_MUSIC = "service_music";
    public static final String MUSIC_LYRIC_OK = "lyric_ok";
    public static final String MUSIC_LYRIC_FAIL = "lyric_fail";
    public static final String PURE_MUSIC = "此歌曲为没有填词的纯音乐，请您欣赏";
    public static final String NO_LYRICS = "暂无歌词";
    public static final String NO_NETWORK = "网络异常,稍后重试!";


    public static final String NO_FIND_LYRICS = "no_find_lyrics";
    public static final String NO_FIND_NETWORK = "no_find_network";
    public static final String PLAY_LIST_BACK_FLAG = "LSP_98";
    public static final String TIME_FLAG = "time_flag";
    public static final String TIME_FLAG_KEY = "time_flag_key";


    public static final String MUSIC_SETTING = "artist_music_setting";
    public static final int MODE_KEY = 0;
    public static final String MUSIC_LOAD = "music_load";
    public static final String PERMISSION_HINT = "permission_hint";

    public static final String MUSIC_LOAD_FLAG = "play_load_flag";

    /**
     * 用于存储和获取音乐的播放模式
     * PLAY_MODE
     * 0 全部  1 单曲   2 随机
     */
    public static final String PLAY_MODE = "play_mode";
    public static final String MUSIC_QUERY = "music_query";
    /**
     * 用于存储播放记录的查询标识, 歌曲的具体查询标识
     */
    public static final String MUSIC_QUERY_FLAG = "music_query_flag";


    /**
     * 用于标记详情页面是否打开
     * <
     * 0 在PlayListFragment弹出新建AddListDialog和删除DeleteDialog 时赋值为 0 。
     * 1  点击MoreMenuDialog-->添加到播放列表-->PlayListActivity弹出新建AddListDialog  时赋值为 1 。
     */
    public static final String ADD_TO_PLAY_LIST_FLAG = "detail_flag";

    /**
     * 用于存储程序退出或关闭音乐界面时，音乐列表的分类标记，。
     * 1 歌曲名   2  评分   3  播放次数    4  添加时间    8 收藏    10:  专辑 歌手等精确条件
     */
    public static final String MUSIC_DATA_FLAG = "music_data_flag";

    public static final String PIC_URL_FLAG = "pic_url_flag";

    /**
     * 查询音乐的标识
     * <p>
     * v 1 艺术家  2  专辑   3    歌曲   4 播放列表
     */
    public static final String MUSIC_DATA_QUERY = "music_data_query";

    /**
     * 用于存储程序退出或关闭音乐界面时，音乐播放的位置。
     */
    public static final String MUSIC_POSITION = "music_position";

    /**
     * 用于存储退出程序或关闭音乐界面时，音乐的播放状态 。 1：表示暂停时关闭 ， 2：表示播放时关闭
     */
    public static final String MUSIC_PLAY_STATE = "music_play_state";

    /**
     * 音频焦点管理
     */
    public static final String MUSIC_FOCUS = "music_focus";

    /**
     * 用于存储和获取用户是否有播放记录
     */
    public static final String MUSIC_INIT_FLAG = "music_init";


    public static final String MUSIC_DURATION_FLAG = "music_duration_flag";
    public static final String MUSIC_FILE_SIZE_FLAG = "music_file_size_flag";

    public static final String MUSIC_CONFIG = "music_config";
    public static final String MUSIC_REMENBER_FLAG = "music_remenber_flag";
    public static final String FRAGMENT_PLAYLIST = "PlayListFragment";
    public static final String FRAGMENT_ARTIST = "ArtistFragment";
    public static final String FRAGMENT_SONG_CATEGORY = "SongCategoryFragment";
    public static final String FRAGMENT_SONG = "SongFragment";
    public static final String FRAGMENT_ALBUM = "AlbumFragment";
    public static final String FRAGMENT_ALBUM_CATEGORY = "AlbumCategoryFragment";
    public static final String SCANNER_MEDIA = "scanner";
    public static final String ADD_TO_LIST = "add2List";
    public static final String FAVORITE_FLAG = "favoriteFlag";
    public static final String NO_NEED_FLAG = "no_need_flag";
    public static final String COUNTDOWN_TIME = "countdown_time";
    public static final String FINISH_TIME = "00:01";
    public static String MUSIC_LYRICS_DIR = "lyrics";
    public static String CRASH_DIR = "crash";
    public static String LOAD_FLAG = "load_flag";
    public static String AUTO_LOAD = "auto_load";
    public static String PAGE_TYPE = "page_type";
    public static String MUSIC_LYRICS_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/lyrics/";
    public static String MUSIC_SONG_ALBUM_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/songAlbum/";
    public static String MUSIC_ALBUM_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/album/";
    public static String MUSIC_ARITIST_IMG_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/artistImage/";
    static final String FAVORITE_FILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/smartisan/music/favorite.txt/";
    public static final String HEADER_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/yibao/photo/";
    // 崩溃日志本地保存地址
    public static final String CRASH_LOG_PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashLog/log/";
    public static final String DATA_TYPE_TXT = "text/plain";
    public static String UNSPLASH_URL = "https://picsum.photos/1080/1920/?image=";
    // 权限Code
    public static final int CODE_GALLERY_REQUEST = 0xa0;
    public static final int CODE_CAMERA_REQUEST = 0xa1;
    public static final int CODE_RESULT_REQUEST = 0xa2;
    // 头像文件

    public static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
    public static final String CROP_IMAGE_FILE_NAME = "header.jpg";
    public static final String MQMS2 = "[mqms2]";
    /**
     * 选择歌词完成
     */
    public static final int SELECT_LYRICS = 22;
    public static final String SONGMID = "song_mid";

    public static final String DELETE_SONG = "delete_song";
    public static final String SONG_LYRICS = "lyrics";
    public static final String MUSIC_BEAN = "musicBean";


}
