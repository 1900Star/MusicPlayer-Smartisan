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

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.lyrics_fragment);
        mTvLyricsPage = getViewById(R.id.tv_lyrics_page);
        mPosition = getArguments().getInt("position");
        if (mPosition == 0) {
            mTvLyricsPage.setText("Hello World 0");
        }
    }

    @Override
    protected void onLazyLoadData() {
        super.onLazyLoadData();
        LogUtil.d(TAG, "开始加载歌词");
        String str = "Hello World " + mPosition;
        mTvLyricsPage.setText(str);
    }

    public static LyricsFragment newInstance(int position) {
        Bundle args = new Bundle();
        args.putInt("position", position);
        LyricsFragment fragment = new LyricsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
