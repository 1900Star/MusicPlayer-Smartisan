package com.yibao.music.fragment

import android.view.View
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.yibao.music.R
import com.yibao.music.adapter.AlbumViewPagerAdapter
import com.yibao.music.adapter.DetailsViewAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseLazyFragmentDev
import com.yibao.music.databinding.AlbumFragmentBinding
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog
import com.yibao.music.model.AlbumInfo
import com.yibao.music.model.MusicBean
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.util.ColorUtil
import com.yibao.music.util.Constants
import com.yibao.music.view.music.MusicToolBar.OnToolbarClickListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.album
 * @文件名: AlbumFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/8 20:01
 * @描述： {TODO}
 */
class AlbumFragment : BaseLazyFragmentDev<AlbumFragmentBinding>(), View.OnClickListener {
    private var mDetailsAdapter: DetailsViewAdapter? = null
    private var isShowDetailsView = false
    private var mDetailViewFlag = true
    private var mDetailList = ArrayList<MusicBean>()
    private var detailsViewTitle = ""
    override fun initView() {


    }

    override fun initData() {
        val pagerAdapter = AlbumViewPagerAdapter(requireActivity())
        mBinding.viewPager2Album.adapter = pagerAdapter
        mBinding.viewPager2Album.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                switchCategory(position)
            }
        })
        initRxbusData()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        mBinding.musicBar.musicToolbarList.setToolbarTitle(
            if (isShowDetailsView) detailsViewTitle else getString(
                R.string.music_album
            )
        )
    }

    override fun initRxBusData() {
        disposeToolbar()
        if (mEditDisposable == null) {
            mEditDisposable = mBus.toObservableType(Constants.FRAGMENT_ALBUM, Any::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { editBean: Any? ->
                    mBinding.musicBar.musicToolbarList.setTvEditText(R.string.tv_edit)
                    mBinding.musicBar.musicToolbarList.setTvDeleteVisibility(View.GONE)
                    isShowDetailsView = false
                }
        }
    }

    private fun initListener() {
        mBinding.musicBar.musicToolbarList.setClickListener(object : OnToolbarClickListener {
            override fun clickEdit() {
                if (mDetailViewFlag) {
                    mBus.post(Constants.ALBUM_FAG_EDIT, Constants.NUMBER_THREE)
                }
                mBinding.musicBar.musicToolbarList.setTvDeleteVisibility(if (isShowDetailsView) View.GONE else View.VISIBLE)
                mBinding.musicBar.musicToolbarList.setTvEditText(if (!isShowDetailsView) R.string.tv_edit else R.string.complete)
                showDetailsView(null)
            }

            override fun switchMusicControlBar() {
                switchControlBar()
            }

            override fun clickDelete() {
                mBus.post(Constants.ALBUM_FAG_EDIT, Constants.NUMBER_FOUR)
            }
        })
        mBinding.albumCategory.ivAlbumCategoryRandomPlay.setOnClickListener(this)
        mBinding.albumCategory.albumCategoryTileLl.setOnClickListener(this)
        mBinding.albumCategory.albumCategoryListLl.setOnClickListener(this)
        mBinding.albumCategory.ivAlbumCategoryPlay.setOnClickListener(this)

    }

    private fun initRxbusData() {
        mCompositeDisposable.add(mBus.toObserverable(AlbumInfo::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { albumInfo: AlbumInfo? -> showDetailsView(albumInfo) })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_album_category_random_play, R.id.iv_album_category_play -> randomPlayMusic()
            R.id.album_category_list_ll -> switchCategory(Constants.NUMBER_ZERO)
            R.id.album_category_tile_ll -> switchCategory(Constants.NUMBER_ONE)
            else -> {}
        }
    }

    private fun showDetailsView(albumInfo: AlbumInfo?) {
        if (isShowDetailsView) {
            mBinding.detailsView.visibility = View.GONE
            mBinding.musicBar.musicToolbarList.setToolbarTitle(getString(R.string.music_album))
            mDetailViewFlag = true
        } else {
            if (albumInfo != null) {
                mDetailViewFlag = false
                mBinding.detailsView.visibility = View.VISIBLE
                mDetailList = mMusicBeanDao.queryBuilder()
                    .where(MusicBeanDao.Properties.Album.eq(albumInfo.albumName)).build()
                    .list() as ArrayList<MusicBean>
                // DetailsView播放音乐需要的参数
                mBinding.detailsView.setDataFlag(
                    childFragmentManager,
                    mDetailList.size,
                    albumInfo.albumName,
                    Constants.NUMBER_TWO
                )
                mDetailsAdapter = DetailsViewAdapter(mContext, mDetailList, Constants.NUMBER_TWO)
                mBinding.detailsView.setAdapter(Constants.NUMBER_TWO, albumInfo, mDetailsAdapter)
                mDetailsAdapter!!.setOnItemMenuListener(object :
                    BaseBindingAdapter.OnOpenItemMoreMenuListener {
                    override fun openClickMoreMenu(position: Int, musicBean: MusicBean) {
                        MoreMenuBottomDialog.newInstance(
                            musicBean,
                            position,
                            false,
                            false
                        ).getBottomDialog(mActivity)
                    }
                })
                interceptBackEvent(Constants.NUMBER_TWELVE)
                detailsViewTitle = albumInfo.albumName
                mBinding.musicBar.musicToolbarList.setToolbarTitle(detailsViewTitle)
            }
        }
        mBinding.musicBar.musicToolbarList.setTvEditText(if (isShowDetailsView) R.string.tv_edit else R.string.back)
        isShowDetailsView = !isShowDetailsView
    }

    override fun deleteItem(musicPosition: Int) {
        super.deleteItem(musicPosition)
        if (mDetailsAdapter != null) {
            mDetailList.removeAt(musicPosition)
            mDetailsAdapter!!.setData(mDetailList)
        }
    }

    override fun handleDetailsBack(detailFlag: Int) {
        if (detailFlag == Constants.NUMBER_TWELVE) {
            showDetailsView(null)
        }
    }

    private fun switchCategory(showType: Int) {
        mBinding.viewPager2Album.setCurrentItem(showType, false)
        if (showType == Constants.NUMBER_ZERO) {
            mBinding.albumCategory.albumCategoryListLl.setBackgroundResource(R.drawable.btn_category_songname_down_selector)
            mBinding.albumCategory.ivAlbumCategoryList.setImageResource(R.drawable.album_category_list_down_selector)
            mBinding.albumCategory.tvAlbumCategoryList.setTextColor(ColorUtil.wihtle)
            mBinding.albumCategory.albumCategoryTileLl.setBackgroundResource(R.drawable.btn_category_views_selector)
            mBinding.albumCategory.ivAlbumCategoryTile.setImageResource(R.drawable.album_category_tile_selector)
            mBinding.albumCategory.tvAlbumCategoryTile.setTextColor(ColorUtil.textName)
        } else if (showType == Constants.NUMBER_ONE) {
            mBinding.albumCategory.albumCategoryTileLl.setBackgroundResource(R.drawable.btn_category_views_down_selector)
            mBinding.albumCategory.ivAlbumCategoryTile.setImageResource(R.drawable.album_category_tile_down_selector)
            mBinding.albumCategory.tvAlbumCategoryTile.setTextColor(ColorUtil.wihtle)
            mBinding.albumCategory.albumCategoryListLl.setBackgroundResource(R.drawable.btn_category_songname_selector)
            mBinding.albumCategory.ivAlbumCategoryList.setImageResource(R.drawable.album_category_list_selector)
            mBinding.albumCategory.tvAlbumCategoryList.setTextColor(ColorUtil.textName)
        }
    }

    companion object {
        fun newInstance(): AlbumFragment {
            return AlbumFragment()
        }
    }

    override val isOpenDetail: Boolean
        get() = isShowDetailsView

}