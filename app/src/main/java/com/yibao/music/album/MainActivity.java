package com.yibao.music.album;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import flow.Flow;

/**
 * @author Stran
 */
public class MainActivity extends AppCompatActivity {
    @Override protected void attachBaseContext(Context baseContext) {
        baseContext = Flow.configure(baseContext, this).install();
        super.attachBaseContext(baseContext);
    }

    @Override public void onBackPressed() {
        if (!Flow.get(this).goBack()) {
            super.onBackPressed();
        }
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        setContentView(R.layout.activity_main);
//        int position = getIntent().getIntExtra("position", 0);
//        LogUtil.d("==================Main====   " + position);
//    }
}
