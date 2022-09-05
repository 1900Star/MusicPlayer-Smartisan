package com.yibao.music.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.base.BasePlayActivity;
import com.yibao.music.base.listener.MyAnimatorUpdateListener;
import com.yibao.music.databinding.PlayActivityBinding;
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
import com.yibao.music.util.Constant;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.LyricsUtil;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.StringUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

public class PlayActivity extends BasePlayActivity<PlayActivityBinding> implements View.OnClickListener {

    private int mDuration;
    private MusicBean mCurrentMusicInfo;
    boolean isShowLyrics = false;
    private ObjectAnimator mAnimator;
    private MyAnimatorUpdateListener mAnimatorListener;
    private Disposable mCloseLyrDisposable;
    private List<MusicLyricBean> mLyricList;

    @Override
    public void initView() {
        init();
        initSongInfo();
        initListener();

    }

    @Override
    public void initData() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mCurrentMusicInfo != null && audioBinder != null) {
            checkCurrentIsFavorite(mMusicDao.load(mCurrentMusicInfo.getId()).isFavorite());
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


    private void initSongInfo() {
        mCurrentMusicInfo = audioBinder != null ? audioBinder.getMusicBean() : getIntent().getParcelableExtra("currentBean");
        if (mCurrentMusicInfo != null) {
            setTitleAndArtist(mCurrentMusicInfo);
            setAlbum(FileUtil.getAlbumUrl(mCurrentMusicInfo, 1));
        }
    }

    public void checkCurrentIsFavorite(boolean cureentMusicIsFavorite) {
//        mBinding.ivFavoriteMusic.setImageResource(cureentMusicIsFavorite ? R.drawable.favorite_yes : R.drawable.music_qqbar_favorite_normal_selector);
        mBinding.ivFavoriteMusic.setImageResource(cureentMusicIsFavorite ? R.drawable.btn_favorite_red_selector : R.drawable.btn_favorite_gray_selector);
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
        updatePlayModeImage(mode, mBinding.musicPlayerMode);
        //音量设置
        mBinding.sbVolume.setMax(mMaxVolume);
        updateMusicVolume(mVolume);
    }

    private void updateMusicVolume(int volume) {
        mBinding.sbVolume.setProgress(volume);
        // 更新音量值  flag 0 默认不显示系统控制栏  1 显示系统音量控制
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
    }

    @Override
    protected void moreMenu(MoreMenuStatus moreMenuStatus) {
        super.moreMenu(moreMenuStatus);
        switch (moreMenuStatus.getPosition()) {
            case Constant.NUMBER_ZERO:
                startPlayListActivity(mCurrentMusicInfo.getTitle());
                break;
            case Constant.NUMBER_ONE:
                SnakbarUtil.keepGoing(mBinding.albumCover);
                break;
            case Constant.NUMBER_TWO:
                if (audioBinder != null) {
                    if (audioBinder.getPosition() == moreMenuStatus.getMusicPosition()) {
                        audioBinder.updateFavorite();
                        checkCurrentIsFavorite(getFavoriteState(mCurrentMusicInfo));
                    }
                } else {
                    SnakbarUtil.firstPlayMusic(mBinding.playingSongAlbum);
                }

                break;
            case Constant.NUMBER_THREE:
                showLyrics();
                break;
            case Constant.NUMBER_FOUR:
                CountdownBottomSheetDialog.newInstance().getBottomDialog(this);
                break;
            case Constant.NUMBER_FIVE:
                audioBinder.playNext();
                String songUrl = mCurrentMusicInfo.getSongUrl();
                // 先从本地数据库删除歌曲，再彻底删除歌曲文件。
                mMusicDao.delete(mCurrentMusicInfo);
                FileUtil.deleteFile(new File(songUrl));
                break;
            default:
                break;
        }
    }

    @Override
    protected void updateCurrentPlayInfo(MusicBean musicBean) {
        mCurrentMusicInfo = musicBean;
        checkCurrentIsFavorite(mCurrentMusicInfo.isFavorite());
        initAnimation();
        setTitleAndArtist(musicBean);
        setAlbum(FileUtil.getAlbumUrl(musicBean, 1));
        setSongDuration();
        updatePlayBtnStatus();
        // 设置当前歌词
        mLyricList = LyricsUtil.getLyricList(musicBean);
        mBinding.tvLyrics.setLrcFile(mLyricList, mLyricList.size() > 1 ? Constant.MUSIC_LYRIC_OK : Constant.PURE_MUSIC);
        if (isShowLyrics) {
            startRollPlayLyrics(mBinding.tvLyrics);
            closeLyricsView();
            mBinding.llSunAndDelete.setVisibility(mLyricList.size() > 2 ? View.VISIBLE : View.GONE);

        }
    }

    private void setTitleAndArtist(MusicBean bean) {
        mBinding.playSongName.setText(StringUtil.getSongName(bean.getTitle()));
        mBinding.playArtistName.setText(StringUtil.getArtist(bean.getArtist()));
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
        mBinding.startTime.setText(StringUtil.parseDuration(progress));
        // 时时播放进度
        mBinding.sbProgress.setProgress(progress);
        // 歌曲总时长递减
        mBinding.endTime.setText(StringUtil.parseDuration(mDuration - progress));
    }

    private void setSongDuration() {
        // 获取并记录总时长
        mDuration = audioBinder.getDuration();
        // 设置进度条的总进度
        mBinding.sbProgress.setMax(mDuration);
        // 设置歌曲总时长
        mBinding.endTime.setText(StringUtil.parseDuration(mDuration));
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
            ImageUitl.loadPic(this, url, mBinding.playingSongAlbum, R.drawable.playing_cover_lp, isSuccess -> {
                if (isSuccess) {
                    showAlbum(true);
                } else {
                    QqMusicRemote.getSongImg(PlayActivity.this, mCurrentMusicInfo.getTitle(), url1 -> {
                        if (url1 == null) {
                            showAlbum(false);
                        } else {
                            Glide.with(PlayActivity.this).load(url1).placeholder(R.drawable.playing_cover_lp).error(R.drawable.playing_cover_lp).into(mBinding.playingSongAlbum);
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
        mBinding.playingSongAlbum.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
        mBinding.albumCover.setVisibility(b ? View.GONE : View.VISIBLE);
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
                startRollPlayLyrics(mBinding.tvLyrics);
            }
        }
    }

    //根据当前播放状态设置图片

    private void updatePlayBtnStatus() {
        mBinding.musicPlay.setImageResource(audioBinder.isPlaying() ? R.drawable.btn_playing_pause_selector : R.drawable.btn_playing_play_selector);
    }


    private void initAnimation() {
        mBinding.rotateRl.setBackgroundColor(ColorUtil.transparentColor);
        if (mAnimator == null || mAnimatorListener == null) {
            mAnimator = AnimationUtil.getRotation(mBinding.rotateRl);
            mAnimatorListener = new MyAnimatorUpdateListener(mAnimator);
            mAnimator.start();
            mBinding.musicPlay.setImageResource(R.drawable.btn_playing_pause);
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


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_down) {
            finish();
        } else if (id == R.id.rv_titlebar) {
            startSearchActivity(mCurrentMusicInfo);
        } else if (id == R.id.rotate_rl) {// 按下音乐停止播放  动画停止 ，抬起恢复
//                switchPlayState();
        } else if (id == R.id.playing_song_album || id == R.id.album_cover || id == R.id.tv_lyrics) {
            showLyrics();
        } else if (id == R.id.iv_lyrics_switch) {
            MoreMenuBottomDialog.newInstance(mCurrentMusicInfo, audioBinder.getPosition(), true, true).getBottomDialog(this);
        } else if (id == R.id.iv_delete_lyric) {
            LogUtil.d(TAG, "============ 删除当前歌词");
            showLyrics();
            LyricsUtil.deleteCurrentLyric(mCurrentMusicInfo.getTitle(), mCurrentMusicInfo.getArtist());
        } else if (id == R.id.iv_select_lyric) {
            Intent intent = new Intent(this, SearchLyricsActivity.class);
            intent.putExtra(Constant.SONG_NAME, StringUtil.getSongName(mCurrentMusicInfo.getTitle()));
            intent.putExtra(Constant.SONG_ARTIST, StringUtil.getArtist(mCurrentMusicInfo.getArtist()));
            startActivityForResult(intent, Constant.SELECT_LYRICS);
            overridePendingTransition(R.anim.dialog_push_in, 0);
        } else if (id == R.id.iv_secreen_sun_switch) {
            screenAlwaysOnSwitch(mBinding.ivSecreenSunSwitch);
        } else if (id == R.id.music_player_mode) {
            switchPlayMode(mBinding.musicPlayerMode);
        } else if (id == R.id.music_player_pre) {
            mAnimator.pause();
            audioBinder.playPre();
        } else if (id == R.id.music_play) {
            playBtnState(audioBinder.isPlaying());
            updatePlayBtnStatus();
        } else if (id == R.id.music_player_next) {
            mAnimator.pause();
            audioBinder.playNext();
        } else if (id == R.id.iv_favorite_music) {
            boolean favoriteState = getFavoriteState(mCurrentMusicInfo);
            audioBinder.updateFavorite();
            checkCurrentIsFavorite(!favoriteState);
        }
    }


    @Override
    protected void updateLyricsView(boolean lyricsOk, String downMsg) {
        if (lyricsOk) {
            mLyricList = LyricsUtil.getLyricList(mCurrentMusicInfo);
        }
        mBinding.tvLyrics.setLrcFile(lyricsOk ? mLyricList : null, downMsg);
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
            boolean lyricIsExists = LyricsUtil.checkLyricFile(StringUtil.getSongName(mCurrentMusicInfo.getTitle()), StringUtil.getArtist(mCurrentMusicInfo.getArtist()));
            if (lyricIsExists) {
                mLyricList = LyricsUtil.getLyricList(mCurrentMusicInfo);
                mBinding.tvLyrics.setLrcFile(mLyricList, mLyricList.size() > 1 ? Constant.MUSIC_LYRIC_OK : Constant.PURE_MUSIC);
                // 开始滚动歌词
                if (audioBinder.isPlaying()) {
                    startRollPlayLyrics(mBinding.tvLyrics);
                }
                closeLyricsView();
            } else {
                mBinding.tvLyrics.setLrcFile(null, Constant.NO_LYRICS);
            }
        }
        mBinding.tvLyrics.setVisibility(isShowLyrics ? View.GONE : View.VISIBLE);
        mBinding.llSunAndDelete.setVisibility(isShowLyrics ? View.GONE : mLyricList.size() > 2 ? View.VISIBLE : View.GONE);
        mBinding.ivSecreenSunSwitch.setBackgroundResource(isShowLyrics ? R.drawable.music_lrc_close : R.drawable.music_lrc_open);
        AnimationDrawable animation = (AnimationDrawable) mBinding.ivSecreenSunSwitch.getBackground();
        animation.start();
        isShowLyrics = !isShowLyrics;
    }


    private void rxViewClick() {
        mCompositeDisposable.add(RxView.clicks(mBinding.titlebarPlayList)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> FavoriteBottomSheetDialog.newInstance(mCurrentMusicInfo.getTitle())
                        .getBottomDialog(this)));
    }

    @Override
    protected void updateMusicBarAndVolumeBar(SeekBar seekBar, int progress, boolean b) {
        int id = seekBar.getId();
        if (id == R.id.sb_progress) {
            if (!b) {
                return;
            }
            //拖动音乐进度条播放
            audioBinder.seekTo(progress);
            //更新音乐进度数值
            updataMusicProgress(progress);
            //更新音量  SeekBar
        } else if (id == R.id.sb_volume) {
            updateMusicVolume(progress);
        }
    }


    /**
     * 广播监听系统音量，同时更新VolumeSeekBar
     *
     * @param currVolume c
     */
    @Override
    public void updateVolumeProgress(int currVolume) {
        mBinding.sbVolume.setProgress(currVolume);
    }

    /**
     * 清空收藏列表中所有音乐后的回调，
     */
    @Override
    public void updataFavoriteStatus() {
        checkCurrentIsFavorite(getFavoriteState(mCurrentMusicInfo));
    }

    /**
     * size 小于2表示没有歌词，5秒后自动关闭歌词画面。
     */
    public void closeLyricsView() {
        disPosableLyricsView();
        if (mLyricList.size() < Constant.NUMBER_TWO) {
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
        if (requestCode == Constant.SELECT_LYRICS) {
            if (data != null) {
                String songMid = data.getStringExtra(Constant.SONGMID);
                if (songMid != null) {
                    LyricsUtil.deleteCurrentLyric(mCurrentMusicInfo.getTitle(), mCurrentMusicInfo.getArtist());
                    QqMusicRemote.getOnlineLyrics(songMid, mCurrentMusicInfo.getTitle(), mCurrentMusicInfo.getArtist());
                    showLyrics();
                }
            }
        }
    }
    @Override
    public void initListener() {

        mBinding.sbProgress.setOnSeekBarChangeListener(new SeekBarListener());
        mBinding.sbVolume.setOnSeekBarChangeListener(new SeekBarListener());
        mBinding.playingSongAlbum.setOnLongClickListener(view -> {
            PreviewBigPicDialogFragment.newInstance(FileUtil.getAlbumUrl(mCurrentMusicInfo, 1))
                    .show(getSupportFragmentManager(), "album");
            return true;
        });
        mBinding.titlebarDown.setOnClickListener(this);
        mBinding.rvTitlebar.setOnClickListener(this);
        mBinding.rotateRl.setOnClickListener(this);
        mBinding.playingSongAlbum.setOnClickListener(this);
        mBinding.ivLyricsSwitch.setOnClickListener(this);
        mBinding.ivDeleteLyric.setOnClickListener(this);
        mBinding.ivSelectLyric.setOnClickListener(this);
        mBinding.ivSecreenSunSwitch.setOnClickListener(this);
        mBinding.musicPlayerMode.setOnClickListener(this);
        mBinding.musicPlayerPre.setOnClickListener(this);
        mBinding.musicPlayerNext.setOnClickListener(this);
        mBinding.musicPlay.setOnClickListener(this);
        mBinding.ivFavoriteMusic.setOnClickListener(this);

    }

}
