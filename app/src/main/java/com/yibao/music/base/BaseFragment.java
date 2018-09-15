package com.yibao.music.base;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.yibao.music.MusicApplication;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.model.DetailsFlagBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.model.greendao.MusicInfoDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RandomUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SharePrefrencesUtil;

import java.util.HashMap;
import java.util.List;

import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

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
    protected MusicBeanDao mMusicBeanDao;
    protected MusicInfoDao mMusicInfoDao;
    protected boolean isShowDetailsView = false;
    protected CompositeDisposable mDisposable;

    protected String mClassName;
    protected FragmentManager mFragmentManager;
    protected Context mContext;
    protected Unbinder unbinder;
    protected List<MusicBean> mSongList;

    protected BaseFragment() {
        mMusicBeanDao = MusicApplication.getIntstance().getMusicDao();
        mMusicInfoDao = MusicApplication.getIntstance().getMusicInfoDao();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tag = this.getClass().getSimpleName();
        mActivity = getActivity();
        mContext = getActivity();
        mDisposable = new CompositeDisposable();
        mBus = MusicApplication.getIntstance().bus();
        mClassName = getClass().getSimpleName();
        mFragmentManager = mActivity.getFragmentManager();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongList = mMusicBeanDao.queryBuilder().list();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable.clear();
            mDisposable = null;

        }
    }
}
