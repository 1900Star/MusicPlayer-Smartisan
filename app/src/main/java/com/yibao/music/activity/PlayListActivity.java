package com.yibao.music.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.yibao.music.R;
import com.yibao.music.base.BaseActivity;
import com.yibao.music.base.listener.OnFinishActivityListener;
import com.yibao.music.fragment.PlayListFragment;
import com.yibao.music.util.Constant;
import com.yibao.music.util.SpUtils;

import java.util.ArrayList;

/**
 * @author lsp
 *
 * 点击Item更多菜单，将歌曲添加到播放列表。
 */
public class PlayListActivity extends BaseActivity implements OnFinishActivityListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        initView();
        initData();
    }

    private void initData() {

        mSps.putValues(new SpUtils.ContentValue(Constant.ADD_TO_PLAY_LIST_FLAG,Constant.NUMBER_ONE));

        String songName = getIntent().getStringExtra(Constant.SONG_NAME);
        ArrayList<String> arrayList = getIntent().getStringArrayListExtra(Constant.ADD_TO_LIST);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        PlayListFragment playListFragment = PlayListFragment.newInstance(songName, arrayList, true);
        transaction.replace(R.id.fl_content, playListFragment);
        transaction.commit();
    }


    private void initView() {
        TextView tvCancel = findViewById(R.id.tv_play_list_cancel);
        tvCancel.setOnClickListener(v -> finish());


    }

    @Override
    public void finish() {
        super.finish();
        mSps.putValues(new SpUtils.ContentValue(Constant.ADD_TO_PLAY_LIST_FLAG,Constant.NUMBER_ZERO));
        overridePendingTransition(0, R.anim.dialog_push_out);
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
