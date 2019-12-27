package com.yibao.music.fragment;

import android.os.Bundle;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.BaseLazyFragment;
import com.yibao.music.util.LogUtil;

/**
 * @author luoshipeng
 * createDate：2019/12/26 0026 17:17
 * className   LyricsFragment
 * Des：TODO
 */
public class LyricsFragment extends BaseLazyFragment {
    private TextView mTvLyricsPage;
    private int mPosition;
    private String mLyrics;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.lyrics_fragment);
        mTvLyricsPage = getViewById(R.id.tv_lyrics_page);
        mLyrics = getArguments().getString("lyrics");
        mPosition = getArguments().getInt("position");
        setLyrics();
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
