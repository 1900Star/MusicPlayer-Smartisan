package com.yibao.music.activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.base.BasePlayActivity;
import com.yibao.music.base.listener.MyAnimatorUpdateListener;
import com.yibao.music.base.listener.OnCheckFavoriteListener;
import com.yibao.music.fragment.dialogfrag.MusicBottomSheetDialog;
import com.yibao.music.fragment.dialogfrag.TopBigPicDialogFragment;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicStatusBean;
import com.yibao.music.model.song.MusicFavoriteBean;
import com.yibao.music.util.AnimationUtil;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SharePrefrencesUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.view.CircleImageView;
import com.yibao.music.view.music.LyricsView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.activity
 * @文件名: PlayActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/17 20:39
 * @描述： {TODO}
 */

public class PlayActivity extends BasePlayActivity {
    @BindView(R.id.titlebar_down)
    ImageView mTitlebarDown;
    @BindView(R.id.play_song_name)
    TextView mPlaySongName;
    @BindView(R.id.play_artist_name)
    TextView mPlayArtistName;
    @BindView(R.id.titlebar_play_list)
    ImageView mTitlebarPlayList;
    @BindView(R.id.start_time)
    TextView mStartTime;
    @BindView(R.id.sb_progress)
    SeekBar mSbProgress;
    @BindView(R.id.end_time)
    TextView mEndTime;
    @BindView(R.id.playing_song_album)
    CircleImageView mPlayingSongAlbum;
    @BindView(R.id.rotate_rl)
    RelativeLayout mRotateRl;
    @BindView(R.id.tv_lyrics)
    LyricsView mTvLyrics;
    @BindView(R.id.iv_lyrics_switch)
    ImageView mIvLyricsSwitch;
    @BindView(R.id.iv_secreen_sun_switch)
    ImageView mIvSecreenSunSwitch;
    @BindView(R.id.music_player_mode)
    ImageView mMusicPlayerMode;
    @BindView(R.id.music_player_pre)
    ImageView mMusicPlayerPre;
    @BindView(R.id.music_play)
    ImageView mMusicPlay;
    @BindView(R.id.music_player_next)
    ImageView mMusicPlayerNext;
    @BindView(R.id.iv_favorite_music)
    ImageView mIvFavoriteMusic;
    @BindView(R.id.sb_volume)
    SeekBar mSbVolume;

    private int mDuration;
    private String mAlbumUrl;
    private MusicBean mCurrenMusicInfo;
    boolean isShowLyrics = false;
    private Unbinder mBind;
    private ObjectAnimator mAnimator;
    private MyAnimatorUpdateListener mAnimatorListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);
        mBind = ButterKnife.bind(this);
        initSongInfo();
        initData();
        initListener();

    }

    @Override
    protected void refreshAllPlayBtn(MusicStatusBean musicStatusBean) {
        refreshBtnAndAnim(musicStatusBean);
    }

    @Override
    protected void updataCurrentTitle(MusicBean info) {
        perpareMusic(info);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        mSbProgress.setOnSeekBarChangeListener(new SeekBarListener());
        mSbVolume.setOnSeekBarChangeListener(new SeekBarListener());
        rxViewClick();
        mPlayingSongAlbum.setOnLongClickListener(view -> {
            TopBigPicDialogFragment.newInstance(mAlbumUrl)
                    .show(getFragmentManager(), "album");
            return true;
        });
//        mRotateRl.setOnTouchListener((v, event) -> {
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_DOWN:
//                    if (audioBinder.isPlaying()) {
//                        switchPlayState();
//                    }
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    break;
//                case MotionEvent.ACTION_UP:
//                    switchPlayState();
//                    break;
//                default:
//                    break;
//
//            }
//            return true;
//        });

    }


    private void initSongInfo() {
        Bundle bundle = getIntent().getBundleExtra("bundle");
        mCurrenMusicInfo = bundle.getParcelable("info");
        if (mCurrenMusicInfo != null) {
            mPlaySongName.setText(mCurrenMusicInfo.getTitle());
            mPlayArtistName.setText(mCurrenMusicInfo.getArtist());
            mTvLyrics.setLrcFile(mCurrenMusicInfo.getTitle(), mCurrenMusicInfo.getArtist());
            mAlbumUrl = StringUtil.getAlbulm(mCurrenMusicInfo.getAlbumId())
                    .toString();
            setAlbulm(mAlbumUrl);
        }
    }

    public void checkCurrentIsFavorite(boolean cureentMusicIsFavorite) {
        if (cureentMusicIsFavorite) {
            mIvFavoriteMusic.setImageResource(R.mipmap.favorite_yes);
        } else {
            mIvFavoriteMusic.setImageResource(R.drawable.music_qqbar_favorite_selector);
        }
    }

    private void initData() {
        if (audioBinder.isPlaying()) {
            initAnimation();
            updatePlayBtnStatus();
            setSongDuration();

        }
        setSongDuration();
        //设置播放模式图片
        int mode = SharePrefrencesUtil.getMusicMode(this);
        updatePlayModeImage(mode, mMusicPlayerMode);
        //音量设置
        mSbVolume.setMax(mMaxVolume);
        updateMusicVolume(mVolume);
    }

    private void updateMusicVolume(int volume) {
        mSbVolume.setProgress(volume);
        //更新音量值  flag 0 默认不显示系统控制栏  1 显示系统音量控制
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);


    }


    /**
     * Rxbus接收歌曲时时的进度 和 时间，并更新UI
     */
    @Override
    protected void updataCurrentPlayProgress(int progress) {
        updataMusicProgress(progress);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isShowLyrics) {
            showLyrics();
        }
        if (mDisposableLyrics != null) {
            mDisposableLyrics.dispose();
        }
    }

    protected void updataMusicProgress(int progress) {
//        // 时间进度
        mStartTime.setText(StringUtil.parseDuration(progress));
        // 时时播放进度
        mSbProgress.setProgress(progress);
        // 歌曲总时长递减
        mEndTime.setText(StringUtil.parseDuration(mDuration - progress));
    }

    private void setSongDuration() {
        //获取并记录总时长
        mDuration = audioBinder.getDuration();
        //设置进度条的总进度
        mSbProgress.setMax(mDuration);
        //  设置歌曲总时长
        mEndTime.setText(StringUtil.parseDuration(mDuration));
    }

    private void refreshBtnAndAnim(MusicStatusBean bean) {

        switch (bean.getType()) {
            case 0:
                if (bean.isPlay()) {
                    audioBinder.pause();
                    mAnimator.pause();
                } else {
                    audioBinder.start();
                    mAnimator.resume();
                }
                updatePlayBtnStatus();
                break;
            case 2:
                finish();
                break;
            default:
                break;
        }
    }


    /**
     * //设置歌曲名和歌手名
     *
     * @param info k
     */
    private void perpareMusic(MusicBean info) {

        mCurrenMusicInfo = info;
        checkCurrentIsFavorite(mCurrenMusicInfo.isFavorite());
        initAnimation();
        mPlaySongName.setText(info.getTitle());
        mPlayArtistName.setText(info.getArtist());
        mAlbumUrl = StringUtil.getAlbulm(info.getAlbumId())
                .toString();
        setAlbulm(mAlbumUrl);
        setSongDuration();
        updatePlayBtnStatus();
//        初始化歌词
        mTvLyrics.setLrcFile(info.getTitle(), info.getArtist());

    }

    private void setAlbulm(String url) {
        Glide.with(this)
                .load(url)
                .asBitmap()
                .into(mPlayingSongAlbum);
    }

    private void switchPlayState() {
        if (audioBinder.isPlaying()) {
            //当前播放  暂停
            audioBinder.pause();
            mAnimator.pause();
            MusicApplication.getIntstance()
                    .bus()
                    .post(new MusicStatusBean(0, true));
            if (isShowLyrics) {
                mDisposableLyrics.dispose();
            }
        } else {
            //当前暂停  播放
            audioBinder.start();
            initAnimation();
            MusicApplication.getIntstance()
                    .bus()
                    .post(new MusicStatusBean(0, false));
            if (isShowLyrics) {
                startRollPlayLyrics(mTvLyrics);
            }
        }


        //更新播放状态按钮
        updatePlayBtnStatus();


    }

    //根据当前播放状态设置图片

    private void updatePlayBtnStatus() {
        if (audioBinder.isPlaying()) {
            //正在播放    设置为暂停
            mMusicPlay.setImageResource(R.drawable.btn_playing_pause_selector);
        } else {
            mMusicPlay.setImageResource(R.drawable.btn_playing_play_selector);
        }
    }


    private void initAnimation() {
        mRotateRl.setBackgroundColor(ColorUtil.transparentColor);
        if (mAnimator == null || mAnimatorListener == null) {
            mAnimator = AnimationUtil.getRotation(mRotateRl);
            mAnimatorListener = new MyAnimatorUpdateListener(mAnimator);
            mAnimator.start();
            mMusicPlay.setImageResource(R.drawable.btn_playing_pause);
        }
        mAnimator.resume();

    }

    @OnClick({R.id.titlebar_down,
            R.id.playing_song_album, R.id.rotate_rl, R.id.tv_lyrics, R.id.iv_lyrics_switch, R.id.iv_secreen_sun_switch, R.id.music_player_mode, R.id.music_player_pre, R.id.music_play, R.id.music_player_next, R.id.iv_favorite_music})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_down:
                finish();
                break;
            case R.id.playing_song_album:
                showLyrics();
                break;
            case R.id.rotate_rl:
                // 按下音乐停止播放  动画停止 ，抬起恢复
//                switchPlayState();
                break;
            case R.id.tv_lyrics:
                showLyrics();
                break;
            case R.id.iv_lyrics_switch:
                showLyrics();
                break;
            case R.id.iv_secreen_sun_switch:
                screenAlwaysOnSwitch(mIvSecreenSunSwitch);
                break;
            case R.id.music_player_mode:
                switchPlayMode(mMusicPlayerMode);
                break;
            case R.id.music_player_pre:
                mAnimator.pause();
                audioBinder.playPre();
                break;
            case R.id.music_play:
                switchPlayState();
                break;
            case R.id.music_player_next:
                mAnimator.pause();
                audioBinder.playNext();
                break;
            case R.id.iv_favorite_music:
                favoriteMusic();
                break;
            default:
                break;
        }
    }

    private void favoriteMusic() {
        if (mCurrenMusicInfo.isFavorite()) {
            mCurrenMusicInfo.setIsFavorite(false);
            mMusicDao.update(mCurrenMusicInfo);
            mIvFavoriteMusic.setImageResource(R.drawable.music_qqbar_favorite_selector);

        } else {
            String time = StringUtil.getCurrentTime();
            mCurrenMusicInfo.setTime(time);
            mCurrenMusicInfo.setIsFavorite(true);
            mMusicDao.update(mCurrenMusicInfo);
            mIvFavoriteMusic.setImageResource(R.mipmap.favorite_yes);

        }
    }


    /**
     * 显示歌词 和 屏幕常亮图标显示
     */
    private void showLyrics() {

        if (isShowLyrics) {
            mIvLyricsSwitch.setBackgroundResource(R.drawable.music_lrc_close);
            AnimationDrawable animation = (AnimationDrawable) mIvLyricsSwitch.getBackground();
            animation.start();
            mIvSecreenSunSwitch.setVisibility(View.INVISIBLE);
            mTvLyrics.setVisibility(View.GONE);
            mDisposableLyrics.dispose();
        } else {
            mIvLyricsSwitch.setBackgroundResource(R.drawable.music_lrc_open);
            AnimationDrawable animation = (AnimationDrawable) mIvLyricsSwitch.getBackground();
            animation.start();
            mIvSecreenSunSwitch.setVisibility(View.VISIBLE);
            // 开始滚动歌词
            if (audioBinder.isPlaying()) {
                startRollPlayLyrics(mTvLyrics);
            }

            mTvLyrics.setVisibility(View.VISIBLE);
        }
        isShowLyrics = !isShowLyrics;
    }


    @SuppressLint("CheckResult")
    private void rxViewClick() {
        RxView.clicks(mTitlebarPlayList)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> MusicBottomSheetDialog.newInstance()
                        .getBottomDialog(this));
    }

    @Override
    protected void updataMusicBarAndVolumeBar(SeekBar seekBar, int progress, boolean b) {
        switch (seekBar.getId()) {
            case R.id.sb_progress:
                if (!b) {
                    return;
                }
                //拖动音乐进度条播放
                audioBinder.seekTo(progress);
                //更新音乐进度数值
                updataMusicProgress(progress);
                break;
            //更新音量  SeekBar
            case R.id.sb_volume:
                updateMusicVolume(progress);
                break;
            default:
                break;
        }
    }


    /**
     * 广播监听系统音量，同时更新VolumeSeekBar
     *
     * @param currVolume c
     */
    @Override
    public void updataVolumeProgresse(int currVolume) {
        mSbVolume.setProgress(currVolume);
    }

    @Override
    public void updataFavoriteStatus() {
        boolean updataFavorite = mMusicDao.load(mCurrenMusicInfo.getId()).isFavorite();
        checkCurrentIsFavorite(updataFavorite);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkCurrentIsFavorite(mCurrenMusicInfo.isFavorite());
    }

    @Override
    protected void headsetPullOut() {
        super.headsetPullOut();
        if (audioBinder != null && audioBinder.isPlaying()) {
            switchPlayState();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boolean allSwitch = mAnimator != null && mAnimatorListener != null;
        if (allSwitch) {
            mAnimatorListener.pause();
            mAnimator.cancel();
        }
        mBind.unbind();

    }
}
