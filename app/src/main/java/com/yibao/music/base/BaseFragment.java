package com.yibao.music.base;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yibao.music.MyApplication;
import com.yibao.music.model.AlbumInfo;
import com.yibao.music.model.ArtistInfo;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RxBus;

import java.util.ArrayList;
import java.util.Collections;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mSongList == null) {
            mSongList = MyApplication.mSongDataList;
            Collections.sort(mSongList);

        }
        if (musicBeans == null) {
            musicBeans = MyApplication.mMusicDataList;
        }
        if (mAlbumList == null) {
            mAlbumList = MusicListUtil.getAlbumList(MyApplication.mMusicDataList);
        }
        if (mArtistList == null) {
            mArtistList = MusicListUtil.getArtistList(MyApplication.mMusicDataList);

        }

    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        tag = this.getClass().getSimpleName();
        mActivity = getActivity();
        disposables = new CompositeDisposable();
        mBus = MyApplication.getIntstance().bus();


    }


}
