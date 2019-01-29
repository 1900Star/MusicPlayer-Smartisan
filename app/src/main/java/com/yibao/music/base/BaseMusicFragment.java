package com.yibao.music.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.OnUpdataTitleListener;
import com.yibao.music.model.DetailsFlagBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RandomUtil;
import com.yibao.music.util.SpUtil;

import java.util.HashMap;

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
    public static HashMap<String, BaseFragment> mDetailsViewMap;
    // 页面列表的选择状态打开时，添加到Map里面
    public static HashMap<String, BaseFragment> mItemStatusMap;
    protected Disposable mEditDisposable;
    private Disposable mMenuDisposable;
    protected boolean mIsLoadedData = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailsViewMap = new HashMap<>(5);
        mItemStatusMap = new HashMap<>(5);
        initDetailsFlag();
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.d("========== ClassName  " + mClassName);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            viewStatusProcessing(isVisibleToUser);
        }
    }

    /**
     * 处理对用户是否可见
     *
     * @param isVisibleToUser v
     */
    private void viewStatusProcessing(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            // 对用户可见
            if (!mIsLoadedData) {
                mIsLoadedData = true;
                onLazyLoadData();
            }
            onVisibleToUser();
        } else {
            // 对用户不可见
            onInvisibleToUser();
        }
    }

    /**
     * 懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法
     */
    protected void onLazyLoadData() {
    }

    /**
     * 对用户可见时触发该方法。如果只想在对用户可见时才加载数据，在子类中重写该方法
     */
    protected void onVisibleToUser() {
    }

    /**
     * 对用户不可见时触发该方法
     */
    protected void onInvisibleToUser() {
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
                .observeOn(AndroidSchedulers.mainThread()).subscribe(detailsFlagBean -> {
                    int detailFlag = detailsFlagBean.getDetailFlag();
                    handleDetailsBack(detailFlag);
                })
        );

    }

    // 有详情页面的子类重写这个方法，让自己处理返回事件的，只要这个方法一调用，按返回键就会将详情页面隐藏。
    protected void handleDetailsBack(int detailFlag) {
        // 详情页面关闭后，将标记置为0，将返回事件交给Activity处理，这样就能正常返回。
        SpUtil.setDetailsFlag(mActivity, Constants.NUMBER_ZERO);
    }

    protected void randomPlayMusic() {
        int randomSize = mSongList.size();
        int position = RandomUtil.getRandomPostion(randomSize > 0 ? randomSize : 0);
        if (getActivity() instanceof OnMusicItemClickListener) {
            ((OnMusicItemClickListener) getActivity()).startMusicService(position);
        }
    }

    protected void putFragToMap(int detailFlag, String fragmentName) {
        SpUtil.setDetailsFlag(mActivity, detailFlag);
        if (mDetailsViewMap != null) {
            if (!mDetailsViewMap.containsKey(fragmentName)) {
                mDetailsViewMap.put(fragmentName, this);
            }
        }
    }

    protected void putFragToItemStatusMap(int detailFlag, String fragmentName) {
        SpUtil.setDetailsFlag(mActivity, detailFlag);
        if (mItemStatusMap != null) {
            if (!mItemStatusMap.containsKey(fragmentName)) {
                mItemStatusMap.put(fragmentName, this);
            }
        }
    }

    protected void putFragToMap(String fragmentName) {
        if (mDetailsViewMap != null) {
            if (!mDetailsViewMap.containsKey(fragmentName)) {
                mDetailsViewMap.put(fragmentName, this);
            }
        }
    }

    protected void removeFrag(String fragmentName) {
        if (mDetailsViewMap != null) {
            mDetailsViewMap.remove(fragmentName);
        }
    }

    protected void removeFragItemStatus(String fragmentName) {
        if (mItemStatusMap != null) {
            mItemStatusMap.remove(fragmentName);
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
