package com.yibao.music.base;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.OnUpdataTitleListener;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RandomUtil;
import com.yibao.music.util.SpUtil;

import java.util.List;

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
    private String mClassName;
    private int mAddToPlayListFdlag;
    protected List<MusicBean> mSongList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDetailsFlag();
        mSongList = mMusicBeanDao.queryBuilder().list();
        mAddToPlayListFdlag = SpUtil.getAddToPlayListFdlag(mContext);
    }

    /**
     * 详情页面是否打开
     *
     * @return b
     */
    protected abstract boolean getIsOpenDetail();

    protected void switchControlBar() {
        if (mActivity instanceof OnUpdataTitleListener) {
            ((OnUpdataTitleListener) mActivity).switchControlBar();
        }
    }

    protected void deleteItem(int musicPosition) {
    }

    /**
     * 根据detailFlag处理具体详情页面的返回事件
     */
    private void initDetailsFlag() {
        mCompositeDisposable.add(mBus.toObservableType(Constants.HANDLE_BACK, Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> BaseMusicFragment.this.handleDetailsBack((Integer) o))
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
        int randomSize = mMusicBeanDao.queryBuilder().list().size();
        int position = RandomUtil.getRandomPostion(randomSize > 0 ? randomSize : 0);
        if (getActivity() instanceof OnMusicItemClickListener) {
            ((OnMusicItemClickListener) getActivity()).startMusicService(position);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mClassName = getClass().getSimpleName();
        if (mAddToPlayListFdlag != Constants.NUMBER_ONE) {
            interceptBackEvent(isVisibleToUser && getIsOpenDetail() ? getPageFlag() : Constants.NUMBER_ZERO);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (SpUtil.getAddToPlayListFdlag(mContext) != Constants.NUMBER_ONE) {
            interceptBackEvent(getUserVisibleHint() && getIsOpenDetail() ? getPageFlag() : Constants.NUMBER_ZERO);
        }
        disposeToolbar();
        if (mMenuDisposable != null) {
            mMenuDisposable.dispose();
            mMenuDisposable = null;
        }
    }

    private int getPageFlag() {
        int pageFlag = Constants.NUMBER_ZERO;
        if (mClassName != null) {
            switch (mClassName) {
                case Constants.FRAGMENT_PLAYLIST:
                    pageFlag = Constants.NUMBER_EIGHT;
                    break;
                case Constants.FRAGMENT_ARTIST:
                    pageFlag = Constants.NUMBER_NINE;
                    break;
                case Constants.FRAGMENT_SONG:
                    pageFlag = Constants.NUMBER_ELEVEN;
                    break;
                case Constants.FRAGMENT_SONG_CATEGORY:
                    pageFlag = Constants.NUMBER_ELEVEN;
                    break;
                case Constants.FRAGMENT_ALBUM:
                    pageFlag = Constants.NUMBER_TWELVE;
                    break;
                case Constants.FRAGMENT_ALBUM_CATEGORY:
                    pageFlag = Constants.NUMBER_TWELVE;
                    break;
                default:
                    break;
            }
        }
        return pageFlag;

    }

    protected void disposeToolbar() {
        if (mEditDisposable != null) {
            mEditDisposable.dispose();
            mEditDisposable = null;
        }
    }
}
