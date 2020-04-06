package com.yibao.music.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.base.BasePlayActivity;
import com.yibao.music.base.listener.MyAnimatorUpdateListener;
import com.yibao.music.fragment.dialogfrag.CountdownBottomSheetDialog;
import com.yibao.music.fragment.dialogfrag.FavoriteBottomSheetDialog;
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog;
import com.yibao.music.fragment.dialogfrag.PreviewBigPicDialogFragment;
import com.yibao.music.model.MoreMenuStatus;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicLyricBean;
import com.yibao.music.network.QqMusicRemote;
import com.yibao.music.util.AnimationUtil;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.LyricsUtil;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.view.CircleImageView;
import com.yibao.music.view.music.LyricsView;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.activity
 * @文件名: PlayActivity
 * @author: lsp
 * @创建时间: 2018/2/17 20:39
 * @描述： {TODO}
 */

public class PlayActivity extends BasePlayActivity {
    @BindView(R.id.rv_titlebar)
    RelativeLayout mTitleBar;
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
    @BindView(R.id.album_cover)
    ImageView mAlbumCover;
    @BindView(R.id.rotate_rl)
    RelativeLayout mRotateRl;
    @BindView(R.id.tv_lyrics)
    LyricsView mLyricsView;
    @BindView(R.id.iv_lyrics_switch)
    ImageView mIvLyricsSwitch;

    @BindView(R.id.ll_sun_and_delete)
    LinearLayout mLlSunAndDelele;
    @BindView(R.id.iv_secreen_sun_switch)
    ImageView mIvSecreenSunSwitch;
    @BindView(R.id.iv_select_lyric)
    ImageView mIvSelectLyric;
    @BindView(R.id.iv_delete_lyric)
    ImageView mIvDeleteLyric;
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
    private MusicBean mCurrenMusicInfo;
    boolean isShowLyrics = false;
    private ObjectAnimator mAnimator;
    private MyAnimatorUpdateListener mAnimatorListener;
    private Disposable mCloseLyrDisposable;
    private List<MusicLyricBean> mLyricList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);
        mBind = ButterKnife.bind(this);
        init();
        initSongInfo();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrenMusicInfo != null && audioBinder != null) {
            checkCurrentIsFavorite(mMusicDao.load(mCurrenMusicInfo.getId()).isFavorite());
            updateCurrentPlayInfo(audioBinder.getMusicBean());
        }
        rxViewClick();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isShowLyrics) {
            showLyrics();
        }
        disPosableLyricsView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boolean allSwitch = mAnimator != null && mAnimatorListener != null;
        if (allSwitch) {
            mAnimatorListener.pause();
            mAnimator.cancel();
        }
    }


    private void initListener() {
        mSbProgress.setOnSeekBarChangeListener(new SeekBarListener());
        mSbVolume.setOnSeekBarChangeListener(new SeekBarListener());
        mPlayingSongAlbum.setOnLongClickListener(view -> {
            PreviewBigPicDialogFragment.newInstance(FileUtil.getAlbumUrl(mCurrenMusicInfo, 1))
                    .show(getSupportFragmentManager(), "album");
            return true;
        });
    }


    private void initSongInfo() {
        mCurrenMusicInfo = audioBinder != null ? audioBinder.getMusicBean() : getIntent().getParcelableExtra("currentBean");
        if (mCurrenMusicInfo != null) {
            setTitleAndArtist(mCurrenMusicInfo);
            setAlbum(FileUtil.getAlbumUrl(mCurrenMusicInfo, 1));
        }
    }

    public void checkCurrentIsFavorite(boolean cureentMusicIsFavorite) {
//        mIvFavoriteMusic.setImageResource(cureentMusicIsFavorite ? R.drawable.favorite_yes : R.drawable.music_qqbar_favorite_normal_selector);
        mIvFavoriteMusic.setImageResource(cureentMusicIsFavorite ? R.drawable.btn_favorite_red_selector : R.drawable.btn_favorite_gray_selector);
    }

    private void init() {
        if (audioBinder != null) {
            if (audioBinder.isPlaying()) {
                initAnimation();
                updatePlayBtnStatus();
            }
            setSongDuration();
        }
        //设置播放模式图片
        int mode = SpUtil.getMusicMode(this);
        updatePlayModeImage(mode, mMusicPlayerMode);
        //音量设置
        mSbVolume.setMax(mMaxVolume);
        updateMusicVolume(mVolume);
    }

    private void updateMusicVolume(int volume) {
        mSbVolume.setProgress(volume);
        // 更新音量值  flag 0 默认不显示系统控制栏  1 显示系统音量控制
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    @Override
    protected void moreMenu(MoreMenuStatus moreMenuStatus) {
        super.moreMenu(moreMenuStatus);
        switch (moreMenuStatus.getPosition()) {
            case Constants.NUMBER_ZERO:
                startPlayListActivity(mCurrenMusicInfo.getTitle());
                break;
            case Constants.NUMBER_ONE:
                SnakbarUtil.keepGoing(mAlbumCover);
                break;
            case Constants.NUMBER_TWO:
                if (audioBinder != null) {
                    if (audioBinder.getPosition() == moreMenuStatus.getMusicPosition()) {
                        audioBinder.updataFavorite();
                        checkCurrentIsFavorite(getFavoriteState(mCurrenMusicInfo));
                    }
                } else {
                    SnakbarUtil.firstPlayMusic(mPlayingSongAlbum);
                }

                break;
            case Constants.NUMBER_THREE:
                showLyrics();
                break;
            case Constants.NUMBER_FOUR:
                CountdownBottomSheetDialog.newInstance().getBottomDialog(this);
                break;
            case Constants.NUMBER_FIVE:
                audioBinder.playNext();
                String songUrl = mCurrenMusicInfo.getSongUrl();
                // 先从本地数据库删除歌曲，再彻底删除歌曲文件。
                mMusicDao.delete(mCurrenMusicInfo);
                FileUtil.deleteFile(new File(songUrl));
                break;
            default:
                break;
        }
    }

    @Override
    protected void updateCurrentPlayInfo(MusicBean musicBean) {
        mCurrenMusicInfo = musicBean;
        checkCurrentIsFavorite(mCurrenMusicInfo.isFavorite());
        initAnimation();
        setTitleAndArtist(musicBean);
        setAlbum(FileUtil.getAlbumUrl(musicBean, 1));
        setSongDuration();
        updatePlayBtnStatus();
        // 设置当前歌词
        mLyricList = LyricsUtil.getLyricList(musicBean);
        mLyricsView.setLrcFile(mLyricList, mLyricList.size() > 1 ? Constants.MUSIC_LYRIC_OK : Constants.PURE_MUSIC);
        if (isShowLyrics) {
            startRollPlayLyrics(mLyricsView);
            closeLyricsView();
            mLlSunAndDelele.setVisibility(mLyricList.size() > 2 ? View.VISIBLE : View.GONE);

        }
    }

    private void setTitleAndArtist(MusicBean bean) {
        mPlaySongName.setText(StringUtil.getSongName(bean.getTitle()));
        mPlayArtistName.setText(StringUtil.getArtist(bean.getArtist()));
    }

    /**
     * Rxbus接收歌曲时时的进度 和 时间，并更新UI
     */
    @Override
    protected void updateCurrentPlayProgress() {
        updataMusicProgress(audioBinder.getProgress());
    }

    protected void updataMusicProgress(int progress) {
        // 时间进度
        mStartTime.setText(StringUtil.parseDuration(progress));
        // 时时播放进度
        mSbProgress.setProgress(progress);
        // 歌曲总时长递减
        mEndTime.setText(StringUtil.parseDuration(mDuration - progress));
    }

    private void setSongDuration() {
        // 获取并记录总时长
        mDuration = audioBinder.getDuration();
        // 设置进度条的总进度
        mSbProgress.setMax(mDuration);
        // 设置歌曲总时长
        mEndTime.setText(StringUtil.parseDuration(mDuration));
    }


    private void disPosableLyricsView() {
        if (mCloseLyrDisposable != null) {
            mCloseLyrDisposable.dispose();
            mCloseLyrDisposable = null;
        }
//        clearDisposableLyric();
    }

    private void setAlbum(String url) {
        try {
            ImageUitl.loadPic(this, url, mPlayingSongAlbum, R.drawable.playing_cover_lp, isSuccess -> {
                if (isSuccess) {
                    showAlbum(true);
                } else {
                    QqMusicRemote.getSongImg(PlayActivity.this, mCurrenMusicInfo.getTitle(), url1 -> {
                        if (url1 == null) {
                            showAlbum(false);
                        } else {
                            Glide.with(PlayActivity.this).load(url1).placeholder(R.drawable.playing_cover_lp).error(R.drawable.playing_cover_lp).into(mPlayingSongAlbum);
                            showAlbum(true);
                        }
                    });


                }
            });
        } catch (Exception e) {
            LogUtil.d(TAG, e.getMessage());
        }


    }

    private void showAlbum(boolean b) {
        mPlayingSongAlbum.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        mAlbumCover.setVisibility(b ? View.GONE : View.VISIBLE);
    }

    private void switchPlayState(boolean isPlaying) {
        // 更新播放状态按钮
        updatePlayBtnStatus();
        playBtnState(isPlaying);
    }

    private void playBtnState(boolean isPlaying) {
        if (isPlaying) {
            // 当前播放  暂停
            audioBinder.pause();
            mAnimator.pause();
            if (isShowLyrics && mDisposableLyrics != null) {
                clearDisposableLyric();
            }
        } else {
            // 当前暂停  播放
            audioBinder.start();
            initAnimation();
            if (isShowLyrics) {
                startRollPlayLyrics(mLyricsView);
            }
        }
    }

    //根据当前播放状态设置图片

    private void updatePlayBtnStatus() {
        mMusicPlay.setImageResource(audioBinder.isPlaying() ? R.drawable.btn_playing_pause_selector : R.drawable.btn_playing_play_selector);
    }


    private void initAnimation() {
        mRotateRl.setBackgroundColor(ColorUtil.transparentColor);
        if (mAnimator == null || mAnimatorListener == null) {
            mAnimator = AnimationUtil.getRotation(mRotateRl);
            mAnimatorListener = new MyAnimatorUpdateListener(mAnimator);
            mAnimator.start();
            mMusicPlay.setImageResource(R.drawable.btn_playing_pause);
        }
        if (audioBinder != null && audioBinder.isPlaying()) {
            mAnimator.resume();
        } else {
            mAnimator.pause();
        }

    }

    @Override
    protected void refreshBtnAndNotify(int playStatus) {
        switch (playStatus) {
            case 0:
                switchPlayState(!audioBinder.isPlaying());
                break;
            case 1:
                checkCurrentIsFavorite(audioBinder.getMusicBean().isFavorite());
                break;
            case 2:
                mAnimator.pause();
                updatePlayBtnStatus();
                break;
            default:
                break;
        }
    }


    @OnClick({R.id.titlebar_down, R.id.rv_titlebar,
            R.id.playing_song_album, R.id.album_cover, R.id.rotate_rl, R.id.tv_lyrics,
            R.id.iv_lyrics_switch, R.id.iv_delete_lyric, R.id.iv_select_lyric,
            R.id.iv_secreen_sun_switch, R.id.music_player_mode, R.id.music_player_pre,
            R.id.music_play, R.id.music_player_next, R.id.iv_favorite_music})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_down:
                finish();
                break;
            case R.id.rv_titlebar:
                startSearchActivity(mCurrenMusicInfo);
                break;
            case R.id.rotate_rl:
                // 按下音乐停止播放  动画停止 ，抬起恢复
//                switchPlayState();
                break;
            case R.id.playing_song_album:
            case R.id.album_cover:
            case R.id.tv_lyrics:
                showLyrics();
                break;
            case R.id.iv_lyrics_switch:
                MoreMenuBottomDialog.newInstance(mCurrenMusicInfo, audioBinder.getPosition(), true, true).getBottomDialog(this);
                break;
            case R.id.iv_delete_lyric:
                LogUtil.d(TAG, "============ 删除当前歌词");
                showLyrics();
                LyricsUtil.deleteCurrentLyric(mCurrenMusicInfo.getTitle(), mCurrenMusicInfo.getArtist());
                break;
            case R.id.iv_select_lyric:
                Intent intent = new Intent(this, SearchLyricsActivity.class);
                intent.putExtra(Constants.SONG_NAME, StringUtil.getSongName(mCurrenMusicInfo.getTitle()));
                intent.putExtra(Constants.SONG_ARTIST, StringUtil.getArtist(mCurrenMusicInfo.getArtist()));
                startActivityForResult(intent, Constants.SELECT_LYRICS);
                overridePendingTransition(R.anim.dialog_push_in, 0);
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
                playBtnState(audioBinder.isPlaying());
                updatePlayBtnStatus();
                break;
            case R.id.music_player_next:
                mAnimator.pause();
                audioBinder.playNext();
                break;
            case R.id.iv_favorite_music:
                boolean favoriteState = getFavoriteState(mCurrenMusicInfo);
                audioBinder.updataFavorite();
                checkCurrentIsFavorite(!favoriteState);
                break;
            default:
                break;
        }
    }


    @Override
    protected void updateLyricsView(boolean lyricsOk, String downMsg) {
        if (lyricsOk) {
            mLyricList = LyricsUtil.getLyricList(mCurrenMusicInfo);
        }
        mLyricsView.setLrcFile(lyricsOk ? mLyricList : null, downMsg);
        closeLyricsView();

    }

    /**
     * 显示歌词 和 屏幕常亮图标显示
     */
    private void showLyrics() {
        if (isShowLyrics) {
            clearDisposableLyric();
            disPosableLyricsView();
        } else {
            boolean lyricIsExists = LyricsUtil.checkLyricFile(StringUtil.getSongName(mCurrenMusicInfo.getTitle()), StringUtil.getArtist(mCurrenMusicInfo.getArtist()));
            if (lyricIsExists) {
                mLyricList = LyricsUtil.getLyricList(mCurrenMusicInfo);
                mLyricsView.setLrcFile(mLyricList, mLyricList.size() > 1 ? Constants.MUSIC_LYRIC_OK : Constants.PURE_MUSIC);
                // 开始滚动歌词
                if (audioBinder.isPlaying()) {
                    startRollPlayLyrics(mLyricsView);
                }
                closeLyricsView();
            } else {
                mLyricsView.setLrcFile(null, Constants.NO_LYRICS);
            }
        }
        mLyricsView.setVisibility(isShowLyrics ? View.GONE : View.VISIBLE);
        mLlSunAndDelele.setVisibility(isShowLyrics ? View.GONE : mLyricList.size() > 2 ? View.VISIBLE : View.GONE);
        mIvLyricsSwitch.setBackgroundResource(isShowLyrics ? R.drawable.music_lrc_close : R.drawable.music_lrc_open);
        AnimationDrawable animation = (AnimationDrawable) mIvLyricsSwitch.getBackground();
        animation.start();
        isShowLyrics = !isShowLyrics;
    }


    private void rxViewClick() {
        mCompositeDisposable.add(RxView.clicks(mTitlebarPlayList)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> FavoriteBottomSheetDialog.newInstance(mCurrenMusicInfo.getTitle())
                        .getBottomDialog(this)));
    }

    @Override
    protected void updateMusicBarAndVolumeBar(SeekBar seekBar, int progress, boolean b) {
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
    public void updateVolumeProgress(int currVolume) {
        mSbVolume.setProgress(currVolume);
    }

    /**
     * 清空收藏列表中所有音乐后的回调，
     */
    @Override
    public void updataFavoriteStatus() {
        checkCurrentIsFavorite(getFavoriteState(mCurrenMusicInfo));
    }

    /**
     * size 小于2表示没有歌词，5秒后自动关闭歌词画面。
     */
    public void closeLyricsView() {
        disPosableLyricsView();
        if (mLyricList.size() < Constants.NUMBER_TWO) {
            if (mCloseLyrDisposable == null) {
                mCloseLyrDisposable = Observable.timer(5, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aLong -> PlayActivity.this.showLyrics());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.SELECT_LYRICS) {
            if (data != null) {
                String songMid = data.getStringExtra(Constants.SONGMID);
                if (songMid != null) {
                    LyricsUtil.deleteCurrentLyric(mCurrenMusicInfo.getTitle(), mCurrenMusicInfo.getArtist());
                    QqMusicRemote.getOnlineLyrics(songMid, mCurrenMusicInfo.getTitle(), mCurrenMusicInfo.getArtist());
                    showLyrics();
                }
            }
        }
    }
}
