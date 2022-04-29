package com.yibao.music.fragment

import android.os.Bundle
import com.yibao.music.adapter.AlbumAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseLazyFragmentDev
import com.yibao.music.databinding.CategoryFragmentBinding
import com.yibao.music.model.AlbumInfo
import com.yibao.music.model.greendao.AlbumInfoDao
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.util.Constants
import com.yibao.music.util.LogUtil
import com.yibao.music.util.MusicListUtil
import com.yibao.music.util.SnakbarUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * @author Luoshipeng
 * @ author: Luoshipeng
 * @ Name:   AlbumCategoryFragment
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/9/11/ 23:47
 * @ Des:    TODO
 */
class AlbumCategoryFragment : BaseLazyFragmentDev<CategoryFragmentBinding>() {

    private var mPosition = 0
    private lateinit var mAlbumList: List<AlbumInfo>
    private var isItemSelectStatus = true
    private lateinit var mAlbumAdapter: AlbumAdapter
    private var mSelectCount = 0


    override fun initView() {
        val arguments = arguments
        if (arguments != null) {
            mPosition = arguments.getInt("position")
        }
        val musicBeanList = mMusicBeanDao.queryBuilder().list()
        mAlbumList = MusicListUtil.getAlbumList(musicBeanList)
        initData()
    }

    override fun initData() {
        mAlbumAdapter = AlbumAdapter(mActivity, mAlbumList, mPosition)

        mBinding.musciView.setAdapter(
            mContext,
            if (mPosition == 0) 3 else 4,
            mPosition == 0,
            mAlbumAdapter
        )

        mAlbumAdapter.setItemListener(object : BaseBindingAdapter.OnItemListener<AlbumInfo> {
            override fun showDetailsView(bean: AlbumInfo, position: Int, isEditStatus: Boolean) {
                LogUtil.d(mTag, bean.toString())
                if (isEditStatus) {
                    mSelectCount = if (bean.isSelected) mSelectCount-- else mSelectCount++
                    bean.isSelected = !bean.isSelected
                    LogUtil.d(mTag, "=========== album list 选中  $mSelectCount")
                    mAlbumAdapter.notifyDataSetChanged()
                } else {
                    mBus.post(bean)
                }
            }
        })
    }


    override fun initRxBusData() {
        disposeToolbar()
        if (mEditDisposable == null) {
            mEditDisposable = mBus.toObservableType(Constants.ALBUM_FAG_EDIT, Any::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { o: Any -> changeEditStatus(o as Int) }
        }
    }

    private fun changeEditStatus(currentIndex: Int) {
        if (currentIndex == Constants.NUMBER_THREE) {
            closeEditStatus()
        } else if (currentIndex == Constants.NUMBER_FOUR) {
            // 删除已选择的条目
            val musicBeanList =
                mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsSelected.eq(true))
                    .build().list()
            if (musicBeanList.size > Constants.NUMBER_ZERO) {
                LogUtil.d(mTag, "======== Size    " + musicBeanList.size)
                for (musicBean in musicBeanList) {
                    mMusicBeanDao.delete(musicBean)
                }
                mAlbumAdapter.setItemSelectStatus(false)
                mAlbumAdapter.setNewData(albumList)
                interceptBackEvent(Constants.NUMBER_TEN)
            } else {
                SnakbarUtil.favoriteSuccessView(mBinding.musciView, "没有选中条目")
            }
        }
    }

    private fun closeEditStatus() {
        mAlbumAdapter.setItemSelectStatus(isItemSelectStatus)
        isItemSelectStatus = !isItemSelectStatus
        interceptBackEvent(Constants.NUMBER_TEN)
        if (!isItemSelectStatus && mSelectCount > 0) {
            cancelAllSelected()
        }
    }

    override fun handleDetailsBack(detailFlag: Int) {
        if (detailFlag == Constants.NUMBER_TEN) {
            mAlbumAdapter.setItemSelectStatus(false)
            mBus.post(Constants.FRAGMENT_ALBUM, Constants.NUMBER_ZERO)
            isItemSelectStatus = true
        }
    }

    /**
     * 取消所有已选
     */
    private fun cancelAllSelected() {
        val albumInfoList =
            mAlbumDao.queryBuilder().where(AlbumInfoDao.Properties.MSelected.eq(true)).build()
                .list()
        Collections.sort(albumInfoList)
        for (albumInfo in albumInfoList) {
            mAlbumDao.delete(albumInfo)
        }
        mSelectCount = 0
        mAlbumAdapter.setNewData(albumList)
    }


    private val albumList: List<AlbumInfo>
        get() {
            return mAlbumDao.queryBuilder().list()
        }


    companion object {
        @JvmStatic
        fun newInstance(position: Int): AlbumCategoryFragment {
            val args = Bundle()
            val fragment = AlbumCategoryFragment()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }

    override val isOpenDetail: Boolean
        get() = !isItemSelectStatus
}