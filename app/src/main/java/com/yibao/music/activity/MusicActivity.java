package com.yibao.music.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationBarView;
import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.adapter.MainViewPagerAdapter;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.base.listener.OnGlideLoadListener;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.OnUpdateTitleListener;
import com.yibao.music.databinding.ActivityMusicBinding;
import com.yibao.music.model.MoreMenuStatus;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicLyricBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.service.MusicPlayService;
import com.yibao.music.util.Constant;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.HandleBackUtil;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.LyricsUtil;
import com.yibao.music.util.QueryMusicFlagListUtil;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.TitleArtistUtil;
import com.yibao.music.util.ToastUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * @author lsp
 * Des：${主Activity}
 * Time:2017/5/30 13:27
 */
public class MusicActivity
        extends BaseActivity
        implements OnMusicItemClickListener, OnUpdateTitleListener, OnGlideLoadListener {
    private static MusicPlayService.AudioBinder audioBinder;
    private AudioServiceConnection mConnection;
    private MusicBean mCurrentMusicBean;
    private int mCurrentPosition;
    private boolean mMusicConfig;
    private boolean isShowQqBar;
    private int mPlayState;
    private int lyricsPlayPosition = 0;
    private MusicBean mQqBarBean;
    private int mHandleDetailFlag;
    private Uri mContentUri;

    private ActivityMusicBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMusicBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        initData();
        initMusicConfig();
        initListener();
    }


    private void initData() {
        List<MusicBean> initMusicList = QueryMusicFlagListUtil.getDataList(SpUtil.getSortFlag(this), SpUtil.getDataQueryFlag(this), SpUtil.getQueryFlag(this), mMusicDao);
        mCurrentPosition = SpUtil.getMusicPosition(this);
        if (initMusicList != null && initMusicList.size() > 0) {
            mCurrentMusicBean = initMusicList.get(mCurrentPosition >= initMusicList.size() ? 0 : mCurrentPosition);
        } else {
            LogUtil.d(TAG, "==========================NoThing=555555555555555555");
        }
        // 初始化 MusicPagerAdapter 主页面
        MainViewPagerAdapter pagerAdapter = new MainViewPagerAdapter(this);
        mBinding.musicViewpager2.setAdapter(pagerAdapter);
        mBinding.musicViewpager2.setCurrentItem(Constant.NUMBER_TWO, false);
        mBinding.musicViewpager2.setOffscreenPageLimit(5);
        mBinding.musicViewpager2.setUserInputEnabled(false);
    }

    private void initMusicConfig() {
        mMusicConfig = SpUtil.getMusicConfig(this, false);
        if (mMusicConfig) {
            mPlayState = SpUtil.getMusicPlayState(this);
            LogUtil.d(TAG, "======= mPlayState  " + mPlayState);
            if (mPlayState == Constant.NUMBER_ONE) {
                // 读取用户的播放记录，设置UI显示，做好播放的准备。(暂停和播放两种状态)
                if (mCurrentMusicBean != null) {
                    setMusicInfo(mCurrentMusicBean);
                }
            } else if (mPlayState == Constant.NUMBER_TWO) {
                startServiceAndAnimation();
            }
        } else {
            LogUtil.d(TAG, "用户 ++++  nothing ");
        }


    }

    private void startServiceAndAnimation() {
        int sortFlag = SpUtil.getSortFlag(this);
        int detailFlag = SpUtil.getDataQueryFlag(this);
        if (detailFlag == Constant.NUMBER_EIGHT) {
            startMusicServiceFlag(mCurrentPosition, sortFlag, detailFlag, Constant.FAVORITE_FLAG);
        } else if (detailFlag == Constant.NUMBER_TEN) {
            startMusicServiceFlag(mCurrentPosition, sortFlag, detailFlag, SpUtil.getQueryFlag(this));
        } else {
            startMusicServiceFlag(mCurrentPosition, sortFlag, detailFlag, Constant.NO_NEED_FLAG);
        }
        mBinding.smartisanControlBar.setPlayButtonState(R.drawable.btn_playing_pause_selector);
        mBinding.qqControlBar.setPlayButtonState(R.drawable.btn_playing_pause_selector);
        mPlayState = Constant.NUMBER_THREE;
    }

    @Override
    protected void refreshBtnAndNotify(int playStatus) {
        switch (playStatus) {
            case Constant.NUMBER_ZERO:
                mBinding.smartisanControlBar.animatorOnResume(audioBinder.isPlaying());
                updatePlayBtnStatus();
                break;
            case Constant.NUMBER_ONE:
                checkCurrentSongIsFavorite(mCurrentMusicBean, mBinding.qqControlBar, mBinding.smartisanControlBar);
                break;
            case Constant.NUMBER_TWO:
                updatePlayBtnStatus();
                mBinding.smartisanControlBar.animatorOnPause();
                break;
            default:
                break;
        }
    }


    private void initListener() {
        mBinding.bnvMusic.setSelectedItemId(R.id.navigation_song);
        mBinding.bnvMusic.setOnItemSelectedListener(mOnNavigationItemSelectedListener);
        mBinding.musicNavigationBar.setOnNavigationBarListener(this::setCurrentPosition);
        mBinding.smartisanControlBar.setClickListener(clickFlag -> {
            if (mMusicConfig) {
                if (clickFlag == Constant.NUMBER_THREE) {
                    switchPlayState();
                } else {
                    if (audioBinder != null) {
                        switch (clickFlag) {
                            case Constant.NUMBER_ONE:
                                audioBinder.updateFavorite();
                                checkCurrentSongIsFavorite(mCurrentMusicBean, mBinding.qqControlBar, mBinding.smartisanControlBar);
                                break;
                            case Constant.NUMBER_TWO:
                                clearDisposableProgress();
                                audioBinder.playPre();
                                break;
                            case Constant.NUMBER_FOUR:
                                clearDisposableProgress();
                                audioBinder.playNext();
                                break;
                            default:
                                break;
                        }
                    } else {
                        SnakbarUtil.firstPlayMusic(mBinding.smartisanControlBar);
                    }
                }
            } else {
                ToastUtil.showNoMusic(MusicActivity.this);
            }
        });
        mBinding.qqControlBar.setOnButtonClickListener(clickFlag -> {
            if (mMusicConfig) {
                switch (clickFlag) {
                    case Constant.NUMBER_ONE:
                        switchPlayState();
                        break;
                    case Constant.NUMBER_TWO:
                        if (audioBinder != null) {
                            audioBinder.updateFavorite();
                            checkCurrentSongIsFavorite(mCurrentMusicBean, mBinding.qqControlBar, mBinding.smartisanControlBar);
                        } else {
                            SnakbarUtil.firstPlayMusic(mBinding.smartisanControlBar);
                        }
                    default:
                        break;
                }
            } else {
                ToastUtil.showNoMusic(MusicActivity.this);
            }
        });
        mBinding.qqControlBar.setOnPagerSelectListener(position -> {
            int sortFlag = SpUtil.getSortFlag(this);
            MusicActivity.this.disposableQqLyric();
            if (mHandleDetailFlag > 0) {
                if (mHandleDetailFlag == Constant.NUMBER_EIGHT) {
                    startMusicServiceFlag(mCurrentPosition, sortFlag, mHandleDetailFlag, Constant.FAVORITE_FLAG);
                } else if (mHandleDetailFlag == Constant.NUMBER_TEN) {
                    startMusicServiceFlag(mCurrentPosition, sortFlag, mHandleDetailFlag, SpUtil.getQueryFlag(this));
                } else {
                    startMusicServiceFlag(mCurrentPosition, sortFlag, mHandleDetailFlag, Constant.NO_NEED_FLAG);
                }
            } else {
                MusicActivity.this.startMusicService(position);
            }
//            updateQqBar();
        });
        mBinding.musicViewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mBinding.bnvMusic.getMenu().getItem(position).setChecked(true);
            }
        });
    }

    private final NavigationBarView.OnItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_play_list) {
            mBinding.musicViewpager2.setCurrentItem(0, false);
            return true;
        } else if (itemId == R.id.navigation_artist) {
            mBinding.musicViewpager2.setCurrentItem(1, false);
            return true;
        } else if (itemId == R.id.navigation_song) {
            mBinding.musicViewpager2.setCurrentItem(2, false);
            return true;
        } else if (itemId == R.id.navigation_album) {
            mBinding.musicViewpager2.setCurrentItem(3, false);
            return true;
        } else if (itemId == R.id.navigation_about) {
            mBinding.musicViewpager2.setCurrentItem(4, false);
            return true;
        }
        return false;
    };

    private void setCurrentPosition(int position) {
        mBinding.musicViewpager2.setCurrentItem(position, false);
    }

    /**
     * 切换当前播放状态
     * mPlayState  将音乐的播放状态记录到本地，方便用户下次打开时进行UI初始化操作。
     * <p>
     * mPlayState = 1 ：表示用户点击暂停后，并退出音乐播放器。下次打开播放器的界面时，
     * 不会自动播放上一次记录的歌曲，需要点击播放按钮，才能播放上一次记录的歌曲。
     * <p>
     * mPlayState = 2 ：表示在播放时退出音乐播放器的界面，只是短暂的离开，但并没有退出程序(程序并没有被后台杀死)，
     * 下次打开播放器的界面时，继续自动播放当前的歌曲。
     */
    private void switchPlayState() {
        LogUtil.d(TAG, "switchPlayState   =====  " + mPlayState);
        if (mPlayState == Constant.NUMBER_ONE) {
            startServiceAndAnimation();
        } else if (mPlayState == Constant.NUMBER_TWO) {
            mPlayState = Constant.NUMBER_THREE;
        } else {
            if (audioBinder == null) {
                ToastUtil.showNoMusic(this);
            } else if (audioBinder.isPlaying()) {
                // 当前播放  暂停
                audioBinder.pause();
                clearDisposableProgress();
            } else if (!audioBinder.isPlaying()) {
                // 当前暂停  播放
                audioBinder.start();
                upDataPlayProgress();

            }
            if (audioBinder != null) {
                mBinding.smartisanControlBar.animatorOnResume(audioBinder.isPlaying());
            }
            //更新播放状态按钮
            updatePlayBtnStatus();
        }
    }


    /**
     * 在主列表播放音乐
     * 开启服务，播放音乐并且将数据标记传送过去
     *
     * @param position 当前点击的曲目
     */
    @Override
    public void startMusicService(int position) {
        int sortFlag = SpUtil.getSortFlag(this);
        mCurrentPosition = position;
        Intent musicIntent = new Intent(this, MusicPlayService.class);
        musicIntent.putExtra("sortFlag", sortFlag);
        musicIntent.putExtra("position", mCurrentPosition);
        mConnection = new AudioServiceConnection();
        bindService(musicIntent, mConnection, Context.BIND_AUTO_CREATE);
        startServiceIntent(musicIntent);
    }

    /**
     * 在详情页面播放音乐回调
     * <p>
     * sortFlag        列表的排序方式
     *
     * @param position  播放位置
     * @param dataFlag  数据列表的标识  1:艺术家、2：专辑、3：曲名 、4: 播放列表
     * @param queryFlag 具体查询的条 ( 按 歌手 或 专辑查询 )
     */
    @Override
    public void startMusicServiceFlag(int position, int sortFlag, int dataFlag, String queryFlag) {
        mCurrentPosition = position;
        Intent intent = new Intent(this, MusicPlayService.class);
        intent.putExtra("sortFlag", sortFlag);
        intent.putExtra("dataFlag", dataFlag);
        intent.putExtra("queryFlag", queryFlag);
        intent.putExtra("position", mCurrentPosition);
        mConnection = new AudioServiceConnection();
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        startServiceIntent(intent);
    }

    private void startServiceIntent(Intent intent) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intent);
//        } else {
//        }
        startService(intent);
    }

    /**
     * PagerAdapter回调
     */
    @Override
    public void onOpenMusicPlayDialogFag() {
        readyMusic();
    }

    private void openMusicPlayDialogFag() {
        mCompositeDisposable.add(RxView.clicks(mBinding.smartisanControlBar)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> readyMusic()));
    }

    private void readyMusic() {
        if (audioBinder != null) {
            if (mMusicConfig) {
                startPlayActivity();
            } else {
                ToastUtil.showNoMusic(MusicActivity.this);
            }
        } else {
            SnakbarUtil.favoriteSuccessView(mBinding.smartisanControlBar, "请先播放音乐!");
        }
    }

    /**
     * @param musicItem 当前播放的歌曲信息，用于更新进度和动画状态,需要用的界面复写这个方法
     */
    @Override
    protected void updateCurrentPlayInfo(MusicBean musicItem) {
        // 将MusicConfig设置为ture
        SpUtil.setMusicConfig(MusicActivity.this);
        mMusicConfig = true;
        // 更新歌曲的信息
        MusicActivity.this.setMusicInfo(musicItem);
        // 设置歌曲最大进度
        setDuration();
        // 更新播放按钮状态
        MusicActivity.this.updatePlayBtnStatus();
        // 初始化动画
        mBinding.smartisanControlBar.initAnimation();
        //更新歌曲的进度
        upDataPlayProgress();
        if (isShowQqBar) {
            mBinding.qqControlBar.setPagerData(audioBinder.getMusicList());
            mBinding.qqControlBar.setPagerCurrentItem(audioBinder.getPosition());
            setQqPagerLyric();
        }
    }

    /**
     * 切换音乐控制面板的样式
     */
    private void switchMusicControlBar() {
        if (audioBinder != null && audioBinder.isPlaying()) {
            if (isShowQqBar) {
                mBinding.qqControlBar.setVisibility(View.INVISIBLE);
                mBinding.smartisanControlBar.setVisibility(View.VISIBLE);
                disposableQqLyric();
            } else {
                if (audioBinder != null) {
                    List<MusicBean> musicList = audioBinder.getMusicList();
                    mBinding.qqControlBar.updaPagerData(musicList, audioBinder.getPosition());
                }
                mBinding.qqControlBar.setVisibility(View.VISIBLE);
                mBinding.smartisanControlBar.setVisibility(View.INVISIBLE);

                //TODO 这里做更新歌词的操作
                setQqPagerLyric();
            }
            isShowQqBar = !isShowQqBar;
        } else {
            SnakbarUtil.firstPlayMusic(mBinding.smartisanControlBar);
        }

    }

    /**
     * QQbar时时更新歌词
     */
    private void setQqPagerLyric() {
        List<MusicLyricBean> lyricList = LyricsUtil.getLyricList(mCurrentMusicBean);
        disposableQqLyric();
        if (mQqLyricsDisposable == null) {
            if (lyricList.size() > 1 && lyricsPlayPosition < lyricList.size()) {
                mQqLyricsDisposable = Observable.interval(1600, TimeUnit.MICROSECONDS)
//                    .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(musicBeanList -> {
                            //通过集合，播放过的歌词就从集合中删除
                            MusicLyricBean lyrBean = lyricList.get(lyricsPlayPosition == lyricList.size() || lyricsPlayPosition > lyricList.size() ? lyricList.size() - 1 : lyricsPlayPosition);
                            String lyrics = lyrBean.getContent();
                            int progress = audioBinder.getProgress();
                            int startTime = lyrBean.getStartTime();
                            List<MusicBean> musicList = audioBinder.getMusicList();
                            if (musicList != null && progress > startTime) {
                                LogUtil.d(TAG, "歌词List的长度    ==  " + lyricList.size());
                                if (mCurrentPosition < musicList.size()) {
                                    mQqBarBean = musicList.get(mCurrentPosition);
                                    mQqBarBean.setCurrentLyrics(lyrics);
                                    musicList.set(mCurrentPosition, mQqBarBean);
                                }
                                LogUtil.d(TAG, "当前的位置 ===  " + mCurrentPosition);
                                LogUtil.d(TAG, "当前的进度 ===  " + progress);
                                LogUtil.d(TAG, "当前的时间和歌词 ===  " + startTime + " ==  " + lyrics);
                                mBinding.qqControlBar.updaPagerData(musicList, mCurrentPosition);
                                lyricsPlayPosition++;
                            }
                        });

            } else {
                LogUtil.d(TAG, "========MusicActivity =====没有发现歌词 ");
            }
        } else {
            LogUtil.d(TAG, "=============没有时间和歌词 ");
            mBinding.qqControlBar.setPagerData(audioBinder.getMusicList());
        }

    }

    private void setDuration() {
        int duration = audioBinder.getDuration();
        mBinding.smartisanControlBar.setMaxProgress(duration);
        mBinding.qqControlBar.setMaxProgress(duration);
    }


    /**
     * 设置歌曲名和歌手名
     *
     * @param musicItem g
     */
    private void setMusicInfo(MusicBean musicItem) {
        mCurrentMusicBean = musicItem;
        checkCurrentSongIsFavorite(mCurrentMusicBean, mBinding.qqControlBar, mBinding.smartisanControlBar);
        // 更新音乐标题
        musicItem = TitleArtistUtil.getMusicBean(musicItem);
        mBinding.smartisanControlBar.setSongName(musicItem.getTitle());
        // 更新歌手名称
        mBinding.smartisanControlBar.setSingerName(mCurrentMusicBean.getArtist());
        // 设置专辑
        mBinding.smartisanControlBar.setAlbulmUrl(FileUtil.getAlbumUrl(mCurrentMusicBean, 1));
    }

    private void updateQqBar() {
        if (isShowQqBar) {
            mBinding.qqControlBar.updaPagerData(audioBinder.getMusicList(), audioBinder.getPosition());
            setQqPagerLyric();
        }
    }

    @Override
    protected void updateCurrentPlayProgress() {
        if (audioBinder != null) {

            if (audioBinder.isPlaying()) {
                mBinding.smartisanControlBar.setSongProgress(audioBinder.getProgress());
                mBinding.qqControlBar.setProgress(audioBinder.getProgress());
            }
        }
    }

    private void updatePlayBtnStatus() {
        //根据当前播放状态设置图片
        if (audioBinder != null) {
            mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder.isPlaying());
            mBinding.qqControlBar.updatePlayButtonState(audioBinder.isPlaying());
        } else {
            SnakbarUtil.firstPlayMusic(mBinding.smartisanControlBar);
        }
    }


    public static MusicPlayService.AudioBinder getAudioBinder() {
        return audioBinder;
    }


    private static class AudioServiceConnection
            implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            audioBinder = (MusicPlayService.AudioBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            audioBinder = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBinding.smartisanControlBar.animatorOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (audioBinder != null) {
            setMusicInfo(audioBinder.getMusicBean());
            mBinding.smartisanControlBar.animatorOnResume(audioBinder.isPlaying());
            checkCurrentSongIsFavorite(mCurrentMusicBean, mBinding.qqControlBar, mBinding.smartisanControlBar);
            updatePlayBtnStatus();
            updateCurrentPlayProgress();
            setDuration();
            updateQqBar();
        }
        openMusicPlayDialogFag();
    }

    @Override
    protected void moreMenu(MoreMenuStatus moreMenuStatus) {
        super.moreMenu(moreMenuStatus);
        MusicBean musicBean = moreMenuStatus.getMusicBean();
        switch (moreMenuStatus.getPosition()) {
            case Constant.NUMBER_ZERO:
                startPlayListActivity(musicBean.getTitle());
                break;
            case Constant.NUMBER_ONE:
                SnakbarUtil.keepGoing(mBinding.smartisanControlBar);
                break;
            case Constant.NUMBER_TWO:
                if (audioBinder != null) {
                    if (audioBinder.getPosition() == moreMenuStatus.getMusicPosition()) {
                        audioBinder.updateFavorite();
                        checkCurrentSongIsFavorite(musicBean, mBinding.qqControlBar, mBinding.smartisanControlBar);
                    } else {
                        audioBinder.updateFavorite(moreMenuStatus.getMusicBean());
                    }
                } else {
                    SnakbarUtil.firstPlayMusic(mBinding.smartisanControlBar);
                }

                break;
            case Constant.NUMBER_THREE:
                SnakbarUtil.keepGoing(mBinding.smartisanControlBar);
                break;
            case Constant.NUMBER_FOUR:
                deleteSong(moreMenuStatus);
                break;
            default:
                break;
        }
    }

    /**
     * 删除歌曲，如果删除当前正在播放的歌曲，先进行 《下一曲》 操作, 删除后通知
     * SongCategoryFragment刷新列表。
     *
     * @param moreMenuStatus m
     */
    private void deleteSong(MoreMenuStatus moreMenuStatus) {
        MusicBean musicBean = moreMenuStatus.getMusicBean();
        if (audioBinder != null) {
            if (mCurrentMusicBean.getTitle().equals(musicBean.getTitle())) {
                audioBinder.playNext();
            }
            String songUrl = musicBean.getSongUrl();
            // 先从本地数据库删除歌曲，再彻底删除歌曲文件。
            mMusicDao.delete(musicBean);
            FileUtil.deleteFile(new File(songUrl));
            mBus.post(Constant.DELETE_SONG, moreMenuStatus.getPosition());
        } else {
            SnakbarUtil.favoriteSuccessView(mBinding.smartisanControlBar, "请先播放音乐!");
        }

    }


    /**
     * AboutFragment 界面恢复收藏歌曲后调用
     */
    @Override
    public void checkCurrentFavorite() {
        MusicBean musicBean = mMusicDao.queryBuilder().where(MusicBeanDao.Properties.Id.eq(mCurrentMusicBean.getId())).build().unique();
        checkCurrentSongIsFavorite(musicBean, mBinding.qqControlBar, mBinding.smartisanControlBar);
    }

    @Override
    public void switchControlBar() {
        //TODO
        switchMusicControlBar();

    }


    @Override
    public void onBackPressed() {
        if (!HandleBackUtil.INSTANCE.handleBackPress(this)) {
            super.onBackPressed();
        } else {
            Log.d(TAG, "自己处理");
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handleAftermath();
        unbindAudioService();

    }

    public void unbindAudioService() {
        if (mConnection != null) {
            unbindService(mConnection);
            mConnection = null;
        }
//        stopService(new Intent(this, AudioPlayService.class));
    }

    private void handleAftermath() {
        mBinding.smartisanControlBar.animatorStop();
        if (audioBinder != null) {
            mPlayState = audioBinder.isPlaying() ? Constant.NUMBER_TWO : Constant.NUMBER_ONE;
            SpUtil.setMusicPlayState(this, mPlayState);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        switch (requestCode) {
            case Constant.CODE_GALLERY_REQUEST:
                mContentUri = intent.getData();
                startActivityForResult(ImageUitl.cropRawPhotoIntent(mContentUri), Constant.CODE_RESULT_REQUEST);
                break;
            case Constant.CODE_CAMERA_REQUEST:
                if (FileUtil.hasSdcard()) {
                    mContentUri = FileUtil.getImageContentUri(this, ImageUitl.getTempFile());
                    startActivityForResult(ImageUitl.cropRawPhotoIntent(mContentUri), Constant.CODE_RESULT_REQUEST);
                } else {
                    ToastUtil.show(this, "没发现SD卡!");
                }
                break;
            case Constant.CODE_RESULT_REQUEST:
                mBus.post(Constant.HEADER_PIC_URI, mContentUri);
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void resumeRequests() {
        if (!isDestroyed()) {
            Glide.with(this).resumeRequests();
        }
    }

    @Override
    public void pauseRequests() {
        if (!isDestroyed()) {
            Glide.with(this).pauseRequests();
        }
    }
}