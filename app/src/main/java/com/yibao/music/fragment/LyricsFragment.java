package com.yibao.music.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseLazyFragment;
import com.yibao.music.util.LogUtil;

import butterknife.BindView;

/**
 * @author luoshipeng
 * createDate：2019/12/26 0026 17:17
 * className   LyricsFragment
 * Des：TODO
 */
public class LyricsFragment extends BaseLazyFragment {
    @BindView(R.id.tv_lyrics_page)
    TextView mTvLyricsPage;
    private int mPosition;
    private String mLyrics;

    @Override
    protected void initView(View view) {
        mLyrics = getArguments().getString("lyrics");
        mPosition = getArguments().getInt("position");
        setLyrics();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.lyrics_fragment;
    }

    private void setLyrics() {
        LogUtil.d(TAG, "显示歌词 " + mPosition);
        String replace = mLyrics.replace("\\n", "\n\n");
        mTvLyricsPage.setText(replace);
    }

    public static LyricsFragment newInstance(int position, String lyrics) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putString("lyrics", lyrics);
        LyricsFragment fragment = new LyricsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean getIsOpenDetail() {
        return false;
    }


}
