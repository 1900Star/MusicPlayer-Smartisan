package com.yibao.music.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.base.listener.OnFinishActivityListener;
import com.yibao.music.fragment.PlayListFragment;
import com.yibao.music.util.Constants;
import com.yibao.music.util.SpUtil;

public class PlayListActivity extends AppCompatActivity implements OnFinishActivityListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        initView();
        initData();
    }

    private void initData() {
        String songName = getIntent().getStringExtra(Constants.SONG_NAME);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        PlayListFragment playListFragment = PlayListFragment.newInstance(Constants.NUMBER_ONE, songName);
        transaction.replace(R.id.fl_content, playListFragment);
        transaction.commit();
    }


    private void initView() {
        TextView tvPlayistCancel = findViewById(R.id.tv_playist_cancel);
        tvPlayistCancel.setOnClickListener(v -> finish());


    }

    @Override
    public void finish() {
        super.finish();
        SpUtil.setAddToPlayListFlag(this, Constants.NUMBER_ZOER);
        overridePendingTransition(0, R.anim.dialog_push_out);
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
