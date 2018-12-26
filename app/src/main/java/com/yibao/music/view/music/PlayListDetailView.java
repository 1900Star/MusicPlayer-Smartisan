package com.yibao.music.view.music;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.util.Constants;
import com.yibao.music.util.RandomUtil;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SpUtil;

/**
 * @ Author: Luoshipeng
 * @ Name:   PlayListDetailView
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/12/7/ 11:59
 * @ Des:    TODO
 */
public class PlayListDetailView extends LinearLayout implements View.OnClickListener {

    private LinearLayout mLlContent;
    private String mQueryFlag;
    private int mListSize;

    public PlayListDetailView(Context context) {
        super(context);
        initView();
    }


    public PlayListDetailView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public void setQureyFlag(String queryFlag, int listSize) {
        this.mQueryFlag = queryFlag;
        this.mListSize = listSize;
    }

    public void setAdapter(RecyclerView recyclerView) {
        mLlContent.addView(recyclerView);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }



    private void initView() {
        LayoutInflater.from(getContext())
                .inflate(R.layout.play_list_detail, this, true);
        mLlContent = findViewById(R.id.ll_content);
        TextView tvRandomPlay = findViewById(R.id.tv_random_play);
        TextView tvDeletePlayList = findViewById(R.id.tv_delete_play_list);
        TextView tvEditPlayList = findViewById(R.id.tv_edit_play_list);
        tvRandomPlay.setOnClickListener(this);
        tvDeletePlayList.setOnClickListener(this);
        tvEditPlayList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_random_play:
                if (mListSize > 0) {
                    startMusic(RandomUtil.getRandomPostion(mListSize));
                }
                break;
            case R.id.tv_delete_play_list:
                SnakbarUtil.keepGoing(mLlContent);
                break;
            case R.id.tv_edit_play_list:
                SnakbarUtil.keepGoing(mLlContent);
                break;
            default:
                break;
        }
    }

    private void startMusic(int startPosition) {
        if (getContext() instanceof OnMusicItemClickListener) {
            SpUtil.setMusicDataListFlag(getContext(), Constants.NUMBER_TEN);
            ((OnMusicItemClickListener) getContext()).startMusicServiceFlag(startPosition, Constants.NUMBER_FOUR, mQueryFlag);
        }
    }
}
