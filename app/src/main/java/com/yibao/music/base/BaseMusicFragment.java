package com.yibao.music.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.OnUpdataTitleListener;
import com.yibao.music.model.DetailsFlagBean;
import com.yibao.music.util.RandomUtil;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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
    protected Disposable mEditDisposable;
    private Disposable mMenuDisposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDetailsFlag();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    protected void switchControlBar() {
        if (mActivity instanceof OnUpdataTitleListener) {
            ((OnUpdataTitleListener) mActivity).switchControlBar();
        }
    }

    protected void deleteItem(int musicPosition) {
    }


    // 根据detailFlag处理具体详情页面的返回事件
    private void initDetailsFlag() {
        mCompositeDisposable.add(mBus.toObserverable(DetailsFlagBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(detailsFlagBean -> handleDetailsBack(detailsFlagBean.getDetailFlag()))
        );

    }

    /**
     * 有详情页面的子类重写这个方法，让自己处理返回事件的，只要这个方法一调用，按返回键就会将详情页面隐藏。
     *
     * @param detailFlag 页面标识
     */
    protected void handleDetailsBack(int detailFlag) {
    }

    /**
     * 详情页面打开时，拦截Activity的onBackPressed()的返回事件。
     *
     * @param handleFlag 页面标识
     */
    protected void interceptBackEvent(int handleFlag) {
        if (mActivity instanceof OnUpdataTitleListener) {
            ((OnUpdataTitleListener) mActivity).handleBack(handleFlag);
        }
    }

    protected void randomPlayMusic() {
        int randomSize = mSongList.size();
        int position = RandomUtil.getRandomPostion(randomSize > 0 ? randomSize : 0);
        if (getActivity() instanceof OnMusicItemClickListener) {
            ((OnMusicItemClickListener) getActivity()).startMusicService(position);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        disposeToolbar();
        if (mMenuDisposable != null) {
            mMenuDisposable.dispose();
            mMenuDisposable = null;
        }
    }

    protected void disposeToolbar() {
        if (mEditDisposable != null) {
            mEditDisposable.dispose();
            mEditDisposable = null;
        }
    }
}
