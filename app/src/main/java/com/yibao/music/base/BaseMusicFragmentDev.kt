package com.yibao.music.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.yibao.music.base.bindings.BaseBindingFragment
import com.yibao.music.base.listener.OnMusicItemClickListener
import com.yibao.music.base.listener.OnUpdataTitleListener
import com.yibao.music.util.Constants
import com.yibao.music.util.RandomUtil
import com.yibao.music.util.SpUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @author Stran
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.base
 * @文件名: BaseMusicFragment
 * @创建时间: 2018/1/1 17:36
 * @描述： TODO
 */
abstract class BaseMusicFragmentDev<T : ViewBinding> : BaseBindingFragment<T>() {
    protected var mEditDisposable: Disposable? = null
    private var mMenuDisposable: Disposable? = null
    private lateinit var mClassName: String
    private var mAddToPlayListFlag = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDetailsFlag()
        mAddToPlayListFlag = SpUtil.getAddToPlayListFdlag(mContext)
    }

    /**
     * 详情页面是否打开
     *
     * @return b
     */
    protected abstract val isOpenDetail: Boolean
    protected fun switchControlBar() {
        if (mActivity is OnUpdataTitleListener) {
            (mActivity as OnUpdataTitleListener).switchControlBar()
        }
    }

    protected fun deleteItem(musicPosition: Int) {}

    /**
     * 根据detailFlag处理具体详情页面的返回事件
     */
    private fun initDetailsFlag() {
        mCompositeDisposable.add(mBus.toObservableType(Constants.HANDLE_BACK, Any::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { o: Any -> handleDetailsBack(o as Int) }
        )
    }

    /**
     * 有详情页面的子类重写这个方法，让自己处理返回事件的，只要这个方法一调用，按返回键就会将详情页面隐藏。
     *
     * @param detailFlag 页面标识
     */
    protected fun handleDetailsBack(detailFlag: Int) {}

    /**
     * 详情页面打开时，拦截Activity的onBackPressed()的返回事件。
     *
     * @param handleFlag 页面标识
     */
    protected fun interceptBackEvent(handleFlag: Int) {
        if (mActivity is OnUpdataTitleListener) {
            (mActivity as OnUpdataTitleListener).handleBack(handleFlag)
        }
    }

    protected fun randomPlayMusic() {
        val randomSize = mMusicBeanDao.queryBuilder().list().size
        val position = RandomUtil.getRandomPostion(Math.max(randomSize, 0))
        if (activity is OnMusicItemClickListener) {
            (activity as OnMusicItemClickListener?)!!.startMusicService(position)
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        mClassName = javaClass.simpleName
        if (mAddToPlayListFlag != Constants.NUMBER_ONE) {
            interceptBackEvent(if (isVisibleToUser && isOpenDetail) pageFlag() else Constants.NUMBER_ZERO)
        }
    }

    override fun onPause() {
        super.onPause()
        if (SpUtil.getAddToPlayListFdlag(mContext) != Constants.NUMBER_ONE) {
            interceptBackEvent(if (userVisibleHint && isOpenDetail) pageFlag() else Constants.NUMBER_ZERO)
        }
        disposeToolbar()
        if (mMenuDisposable != null) {
            mMenuDisposable!!.dispose()
            mMenuDisposable = null
        }
    }

    private fun pageFlag(): Int {
        val pageFlag: Int = when (mClassName) {
            Constants.FRAGMENT_PLAYLIST -> Constants.NUMBER_EIGHT
            Constants.FRAGMENT_ARTIST -> Constants.NUMBER_NINE
            Constants.FRAGMENT_SONG -> Constants.NUMBER_ELEVEN
            Constants.FRAGMENT_SONG_CATEGORY -> Constants.NUMBER_ELEVEN
            Constants.FRAGMENT_ALBUM -> Constants.NUMBER_TWELVE
            Constants.FRAGMENT_ALBUM_CATEGORY -> Constants.NUMBER_TWELVE
            else -> {
                Constants.NUMBER_ZERO
            }
        }
        return pageFlag
    }

    protected fun disposeToolbar() {
        if (mEditDisposable != null) {
            mEditDisposable!!.dispose()
            mEditDisposable = null
        }
    }
}