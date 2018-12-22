package com.yibao.music.base;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.baidu.mobstat.StatService;
import com.yibao.music.MusicApplication;
import com.yibao.music.aidl.MusicBean;
import com.yibao.music.model.greendao.AlbumInfoDao;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.greendao.PlayListBeanDao;
import com.yibao.music.util.RxBus;

import java.util.List;

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
    protected String tag;
    protected Activity mActivity;
    protected RxBus mBus;
    protected final MusicBeanDao mMusicBeanDao;
    protected final PlayListBeanDao mPlayListDao;
    protected CompositeDisposable mCompositeDisposable;
    protected String mClassName;
    protected FragmentManager mFragmentManager;
    protected Context mContext;
    protected Unbinder unbinder;
    protected List<MusicBean> mSongList;
    protected final AlbumInfoDao mAlbumDao;

    protected BaseFragment() {
        mMusicBeanDao = MusicApplication.getIntstance().getMusicDao();
        mPlayListDao = MusicApplication.getIntstance().getPlayListDao();
        mAlbumDao = MusicApplication.getIntstance().getAlbumDao();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tag = this.getClass().getSimpleName();
        mActivity = getActivity();
        mContext = getActivity();
        mCompositeDisposable = new CompositeDisposable();
        mBus =RxBus.getInstance();
        mClassName = getClass().getSimpleName();
        mFragmentManager = mActivity.getFragmentManager();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatService.start(mActivity);
        mSongList = mMusicBeanDao.queryBuilder().list();
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
//        if (mActivity != null) {
//            mActivity = null;
//        }
//        if (mContext != null) {
//            mContext = null;
//        }
    }
}
