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
public abstract class BaseMusicFragment extends BaseFragment {
    public static HashMap<String, BaseFragment> mDetailsViewMap;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailsViewMap = new HashMap<>(3);

        initDetailsFlag();
    }

    // 根据detailFlag处理具体详情页面的返回事件
    private void initDetailsFlag() {
        mDisposable.add(mBus.toObserverable(DetailsFlagBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(detailsFlagBean -> {
                    int detailFlag = detailsFlagBean.getDetailFlag();
                    handleDetailsBack(detailFlag);
                })
        );


    }

    // 有详情页面的子类重写这个方法，让自己处理返回事件的，只要这个方法一调用，按返回键就会将详情页面隐藏。
    protected void handleDetailsBack(int detailFlag) {
        // 详情页面关闭后，将标记置为0，将返回事件交给Activity处理，这样就能正常返回。
        SharePrefrencesUtil.setDetailsFlag(mActivity, Constants.NUMBER_ZOER);
    }


    protected void randomPlayMusic() {
        int position = RandomUtil.getRandomPostion(mSongList.size());
        if (getActivity() instanceof OnMusicItemClickListener) {
            ((OnMusicItemClickListener) getActivity()).startMusicService(position);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDetailsViewMap != null) {
            mDetailsViewMap.clear();
            mDetailsViewMap = null;
        }
    }
}
