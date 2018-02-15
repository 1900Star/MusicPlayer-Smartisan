package com.yibao.music.base;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.yibao.music.R;
import com.yibao.music.fragment.ControlBarFragment;

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base
 * @文件名: BaseActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/12 15:57
 * @描述： {TODO}
 */

public class BaseActivity extends AppCompatActivity {
    private ControlBarFragment playBarFragment = (ControlBarFragment) getFragmentManager().findFragmentById(R.id.controlbar_fragment_root);

    private int aA;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addFragment();
    }


    private void addFragment() {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (playBarFragment == null) {
            aA++;
            System.out.println("=========================       " + aA);
            playBarFragment = ControlBarFragment.newInstance();
            transaction.add(R.id.music_controlbar, playBarFragment, "a");
            transaction.commit();
        } else {
            aA++;
            System.out.println("--------------------------       " + aA);
            transaction.show(playBarFragment).commit();
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
}
