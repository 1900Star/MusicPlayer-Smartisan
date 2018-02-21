package com.yibao.music.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yibao.music.MyApplication;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.RxBus;

import io.reactivex.disposables.CompositeDisposable;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.base
 * @文件名: BaseActivity
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/20 13:07
 * @描述： {TODO}
 */

public class BaseActivity extends AppCompatActivity {

    public RxBus mBus;
    public MusicBeanDao mMusicDao;
    public CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBus = MyApplication.getIntstance()
                .bus();
        mMusicDao = MyApplication.getIntstance().getMusicDao();
        mCompositeDisposable = new CompositeDisposable();

    }
}
