package com.yibao.music.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.base.listener.UpdataTitleListener;
import com.yibao.music.model.DetailsFlagBean;
import com.yibao.music.model.EditBean;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RandomUtil;
import com.yibao.music.util.SpUtil;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
    private Disposable mEditDisposable;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailsViewMap = new HashMap<>(6);
        initDetailsFlag();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mEditDisposable == null) {
            mEditDisposable = mBus.toObserverable(EditBean.class)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(editBean -> changeEditStatus(editBean.getCurrentIndex()));
        }
    }

    protected abstract void changeEditStatus(int currentIndex);

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
        SpUtil.setDetailsFlag(mActivity, Constants.NUMBER_ZOER);
    }

    /**
     * @param tvEdit 编辑按钮、返回列表按钮  显示Text
     */
    protected void changeTvEditText(String tvEdit) {
        if (mContext instanceof UpdataTitleListener) {
            ((UpdataTitleListener) mContext).changeTvEdit(tvEdit);
        }
    }

    /**
     * @param tvEdit 编辑按钮、返回列表按钮  显示Text
     */
    protected void changeToolBarTitle(String tvEdit, boolean isShowDetail) {
        if (mContext instanceof UpdataTitleListener) {
            ((UpdataTitleListener) mContext).updataTitle(tvEdit, isShowDetail);
        }
    }

    protected void randomPlayMusic() {
        int position = RandomUtil.getRandomPostion(mSongList.size());
        if (getActivity() instanceof OnMusicItemClickListener) {
            ((OnMusicItemClickListener) getActivity()).startMusicService(position);
        }
    }
    protected void putFragToMap(int detailFlag,String fragmentName) {
        SpUtil.setDetailsFlag(mActivity, detailFlag);
        if (!mDetailsViewMap.containsKey(fragmentName)) {
            mDetailsViewMap.put(fragmentName, this);
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
        if (mEditDisposable != null) {
            mEditDisposable.dispose();
            mEditDisposable = null;
        }
    }
}
