package com.yibao.music.view.music;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yibao.music.R;
import com.yibao.music.base.listener.MyAnimatorUpdateListener;
import com.yibao.music.util.AnimationUtil;
import com.yibao.music.util.Constant;
import com.yibao.music.util.ImageUitl;
import com.yibao.music.util.StringUtil;
import com.yibao.music.view.CircleImageView;
import com.yibao.music.view.ProgressBtn;

/**
 * @ Author: Luoshipeng
 * @ Name:   SmartisanControlBar
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/8/11/ 1:57
 * @ Des:
 */
public class SmartisanControlBar extends RelativeLayout implements View.OnClickListener {
    TextView mSongName;
    TextView mSingerName;
    ImageView mButtonFavorite;
    ImageView mButtonPre;
    ImageView mButtonNext;
    ImageView mButtonPlay;
    ProgressBtn mProgressBtn;
    CircleImageView mSongAlbulm;
    RelativeLayout mSmartisanMusicBar;


    private ObjectAnimator mAnimator;
    private MyAnimatorUpdateListener mAnimatorListener;


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.music_floating_favorite) {
            controlBarClick(Constant.NUMBER_ONE);
        } else if (id == R.id.music_floating_pre) {
            controlBarClick(Constant.NUMBER_TWO);
        } else if (id == R.id.music_floating_play) {
            controlBarClick(Constant.NUMBER_THREE);
        } else if (id == R.id.music_floating_next) {
            controlBarClick(Constant.NUMBER_FOUR);
        }
    }

    //**************按钮状态********************
    public void setPlayButtonState(int resourceId) {
        mButtonPlay.setImageResource(resourceId);
    }

    public void setFavoriteButtonState(boolean isFavorite) {
        mButtonFavorite.setImageResource(isFavorite ? R.drawable.btn_favorite_red_selector : R.drawable.btn_favorite_gray_selector);
    }

    public void updatePlayBtnStatus(boolean isPlaying) {
        mButtonPlay.setImageResource(isPlaying ? R.drawable.btn_playing_pause_selector : R.drawable.btn_playing_play_selector);
    }

    public void setMaxProgress(int maxProgress) {
        mProgressBtn.setMax(maxProgress);
    }

    public void setSongProgress(int songProgress) {
        mProgressBtn.setProgress(songProgress);
    }

    // 搜索界面调用
    public void setPbColorAndPreBtnGone() {
//        mProgressBtn.setPainColor(ColorUtil.textName);
        mButtonPre.setVisibility(GONE);
    }

    //**************动画********************

    public void initAnimation() {
        if (mAnimator == null || mAnimatorListener == null) {
            mAnimator = AnimationUtil.getRotation(mSongAlbulm);
            mAnimatorListener = new MyAnimatorUpdateListener(mAnimator);
            mAnimator.start();
        }
        mAnimator.resume();
    }

    public void animatorOnResume(boolean isPlaying) {
        if (mAnimator != null && isPlaying) {
            mAnimator.resume();
        } else if (mAnimator != null) {
            mAnimator.pause();
        }
    }

    public void animatorOnPause() {
        if (mAnimator != null) {
            mAnimator.pause();
        }
    }

    public void animatorStop() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator = null;
        }
        if (mAnimatorListener != null) {
            mAnimatorListener.pause();
            mAnimatorListener = null;
        }
    }


    //**************歌曲信息********************

    public void setSongName(String songName) {
        mSongName.setText(songName);
    }

    public void setSingerName(String singerName) {
        mSingerName.setText(StringUtil.getArtist(singerName));

    }

    public void setAlbulmUrl(String albulmUrl) {
        ImageUitl.loadPlaceholder(getContext(), albulmUrl, mSongAlbulm);
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.music_tabbar_smartisan, this, true);
        mSongName = findViewById(R.id.music_float_song_name);
        mSingerName = findViewById(R.id.music_float_singer_name);
        mButtonFavorite = findViewById(R.id.music_floating_favorite);
        mButtonPre = findViewById(R.id.music_floating_pre);
        mButtonNext = findViewById(R.id.music_floating_next);
        mButtonPlay = findViewById(R.id.music_floating_play);
        mSongAlbulm = findViewById(R.id.music_float_block_albulm);
        mSmartisanMusicBar = findViewById(R.id.smartisan_music_bar);
        mProgressBtn = findViewById(R.id.smartisan_bar_progress);
        initListener();

    }


    private void initListener() {
        mButtonFavorite.setOnClickListener(this);
        mButtonPre.setOnClickListener(this);
        mButtonNext.setOnClickListener(this);
        mButtonPlay.setOnClickListener(this);
        mSingerName.setSelected(true);
        mSingerName.setSelected(true);
    }

    public SmartisanControlBar(Context context) {
        super(context);
        initView();
    }

    public SmartisanControlBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void controlBarClick(int clickFlag) {
        if (mControlBarListener != null) {
            mControlBarListener.click(clickFlag);
        }
    }

    //**************歌曲点击监听********************
    private OnSmartisanControlBarListener mControlBarListener;

    public void setClickListener(OnSmartisanControlBarListener controlBarListener) {
        mControlBarListener = controlBarListener;
    }

    public interface OnSmartisanControlBarListener {
        void click(int clickFlag);
    }

}
