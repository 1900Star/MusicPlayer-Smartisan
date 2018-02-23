package com.yibao.music.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.yibao.music.MyApplication;
import com.yibao.music.base.listener.FragBackPressedListener;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RxBus;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

/**
 * @author Stran
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.base
 * @文件名: BaseFragment
 * @创建时间: 2018/1/1 17:36
 * @描述： TODO
 */
public abstract class BaseFragment extends Fragment {
    protected String tag;
    protected Activity mActivity;
    protected static ArrayList<MusicBean> musicBeans;
    protected static ArrayList<AlbumInfo> mAlbumList;
    protected static ArrayList<ArtistInfo> mArtistList;
    protected static RxBus mBus;
    protected static CompositeDisposable disposables;
    public ArrayList<MusicBean> mSongList;
    private boolean mHandledPress = false;
    private FragBackPressedListener mBackPressedListener;
    private MusicBeanDao mMusicBeanDao;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tag = this.getClass().getSimpleName();
        mMusicBeanDao = MyApplication.getIntstance().getMusicDao();
        mActivity = getActivity();
        disposables = new CompositeDisposable();
        mBus = MyApplication.getIntstance().bus();
        if (!(getActivity() instanceof FragBackPressedListener)) {
            throw new ClassCastException("Hosting Activity must implement BackPressedListener");
        } else {

            this.mBackPressedListener = (FragBackPressedListener) getActivity();
        }

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mSongList == null) {
            mSongList = (ArrayList<MusicBean>) mMusicBeanDao.queryBuilder().list();

        }
        if (musicBeans == null) {
            musicBeans = (ArrayList<MusicBean>) mMusicBeanDao.queryBuilder().list();
        }
        if (mAlbumList == null) {
            mAlbumList = MusicListUtil.getAlbumList((ArrayList<MusicBean>) mMusicBeanDao.queryBuilder().list());
        }
        if (mArtistList == null) {
            mArtistList = MusicListUtil.getArtistList((ArrayList<MusicBean>) mMusicBeanDao.queryBuilder().list());

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        mBackPressedListener.putFragment(this);
    }

    public boolean onBackPressed() {

        if (!mHandledPress) {
            Toast.makeText(getActivity(), "捕捉到了回退事件哦！", Toast.LENGTH_LONG).show();
            getFragmentManager().beginTransaction().hide(this).commit();
            mHandledPress = true;
            return true;

        }
        return false;
    }
}
