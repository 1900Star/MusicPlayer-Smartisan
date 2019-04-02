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
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SpUtil;

import java.util.ArrayList;

/**
 * @author Luoshipeng
 */
public class PlayListActivity extends AppCompatActivity implements OnFinishActivityListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        initView();
        initData();
    }

    private void initData() {
        SpUtil.setAddTodPlayListFlag(this, Constants.NUMBER_ONE);
        String songName = getIntent().getStringExtra(Constants.SONG_NAME);
        ArrayList<String> arrayList = getIntent().getStringArrayListExtra(Constants.ADD_TO_LIST);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        PlayListFragment playListFragment = PlayListFragment.newInstance(songName, arrayList, true);
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
        SpUtil.setAddTodPlayListFlag(this, Constants.NUMBER_ZERO);
        overridePendingTransition(0, R.anim.dialog_push_out);
    }

    @Override
    public void finishActivity() {
        finish();
    }
}
