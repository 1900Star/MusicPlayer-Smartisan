package com.yibao.music.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.baidu.mobstat.StatService;
import com.squareup.leakcanary.RefWatcher;
import com.yibao.music.MusicApplication;
import com.yibao.music.model.greendao.AlbumInfoDao;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.greendao.PlayListBeanDao;
import com.yibao.music.model.greendao.SearchHistoryBeanDao;
import com.yibao.music.util.RxBus;

import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Stran
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.base
 * @文件名: BaseMusicFragment
 * @创建时间: 2018/1/1 17:36
 * @描述： TODO
 */
public abstract class BaseFragment extends Fragment {
    protected AppCompatActivity mActivity;
    protected RxBus mBus;
    protected final MusicBeanDao mMusicBeanDao;
    protected final SearchHistoryBeanDao mSearchDao;
    protected final PlayListBeanDao mPlayListDao;
    protected CompositeDisposable mCompositeDisposable;
    protected Context mContext;
    protected Unbinder unbinder;
    protected final AlbumInfoDao mAlbumDao;
    protected FragmentManager mFragmentManager;

    protected BaseFragment() {
        mMusicBeanDao = MusicApplication.getIntstance().getMusicDao();
        mPlayListDao = MusicApplication.getIntstance().getPlayListDao();
        mAlbumDao = MusicApplication.getIntstance().getAlbumDao();
        mSearchDao = MusicApplication.getIntstance().getSearchDao();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) getActivity();
        mContext = getActivity();
        mCompositeDisposable = new CompositeDisposable();
        mBus = RxBus.getInstance();
        mFragmentManager = mActivity.getSupportFragmentManager();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        StatService.start(mActivity.getApplicationContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
            mCompositeDisposable = null;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MusicApplication.getRefWatcher(mActivity);
        refWatcher.watch(this);
    }
}
