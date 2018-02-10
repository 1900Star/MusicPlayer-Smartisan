package com.yibao.music.album;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yibao.music.R;
import com.yibao.music.util.LogUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int position = getIntent().getIntExtra("position", 0);
        LogUtil.d("==================Main====   "+position);
    }
}
