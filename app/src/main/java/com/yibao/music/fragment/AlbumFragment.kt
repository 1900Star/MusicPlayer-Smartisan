package com.yibao.music.fragment

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.yibao.music.R
import com.yibao.music.adapter.AlbumViewPagerAdapter
import com.yibao.music.adapter.DetailsViewAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseMusicFragmentDev
import com.yibao.music.databinding.AlbumFragmentBinding
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog
import com.yibao.music.model.AlbumInfo
import com.yibao.music.model.MusicBean
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.util.ColorUtil
import com.yibao.music.util.Constant
import com.yibao.music.util.MusicListUtil
import com.yibao.music.view.music.MusicToolBar.OnToolbarClickListener
import com.yibao.music.viewmodel.AlbumViewModel

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.album
 * @文件名: AlbumFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/8 20:01
 * @描述： {TODO}
 */
class AlbumFragment : BaseMusicFragmentDev<AlbumFragmentBinding>(), View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener {
    private val mViewModel: AlbumViewModel by lazy { gets(AlbumViewModel::class.java) }
    private var mDetailsAdapter: DetailsViewAdapter? = null
    private var isShowDetailsView = false
    private var mDetailList = ArrayList<MusicBean>()
    override fun initView() {
        mBinding.musicBar.setToolbarTitle(getString(R.string.music_album))
        mBinding.musicBar.isShowAlbumWall(visibility = true)
    }


    override fun initData() {
        val pagerAdapter = AlbumViewPagerAdapter(this, mViewModel)
        mBinding.viewPager2Album.adapter = pagerAdapter
        mBinding.viewPager2Album.isUserInputEnabled = false
        mBinding.viewPager2Album.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                switchCategory(position)
            }
        })

        initListener()
    }

    override fun onResume() {
        super.onResume()
        //    接收AlbumAdapter发过来的当前点击Item的Position
        mViewModel.albumViewModel.observe(this) { bean ->
            showDetailsView(bean)
        }

    }


    private fun initListener() {

        mBinding.albumCategory.ivAlbumCategoryRandomPlay.setOnClickListener(this)
        mBinding.albumCategory.albumCategoryTileLl.setOnClickListener(this)
        mBinding.albumCategory.albumCategoryListLl.setOnClickListener(this)
        mBinding.albumCategory.ivAlbumCategoryPlay.setOnClickListener(this)

        mBinding.musicBar.setClickListener(object : OnToolbarClickListener {
            override fun clickEdit() {
                if (isShowDetailsView) {
                    showDetailsView(null)
                }
            }

            override fun switchMusicControlBar() {
                switchControlBar()
            }

            override fun clickDelete() {}
        })
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_album_category_random_play, R.id.iv_album_category_play -> randomPlayMusic(6)
            R.id.album_category_list_ll -> switchCategory(Constant.NUMBER_ZERO)
            R.id.album_category_tile_ll -> switchCategory(Constant.NUMBER_ONE)
            else -> {}
        }
    }

    private fun showDetailsView(albumInfo: AlbumInfo?) {
        if (isShowDetailsView) {
            mBinding.detailsView.visibility = View.GONE
            mBinding.musicBar.setToolbarTitle(getString(R.string.music_album))
            mBinding.musicBar.setTvEditVisibility(false)
        } else {
            if (albumInfo != null) {
                mBinding.detailsView.visibility = View.VISIBLE
                mBinding.musicBar.setTvEditVisibility(true)
                mBinding.musicBar.setToolbarTitle(albumInfo.albumName)
                mBinding.musicBar.setTvEditText(R.string.music_album)

                mDetailList = MusicListUtil.sortByAbc(
                    mMusicBeanDao.queryBuilder()
                        .where(MusicBeanDao.Properties.Album.eq(albumInfo.albumName)).build()
                        .list()
                ) as ArrayList<MusicBean>
                // DetailsView播放音乐需要的参数
                mBinding.detailsView.setDataFlag(
                    childFragmentManager,
                    mDetailList.size,
                    albumInfo.albumName,
                    Constant.NUMBER_SEVEN
                )
                mDetailsAdapter = DetailsViewAdapter(
                    mContext,
                    mDetailList,
                    Constant.NUMBER_SEVEN,
                    albumInfo.albumName
                )
                mBinding.detailsView.setAdapter(Constant.NUMBER_TWO, albumInfo, mDetailsAdapter)
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
            }
        }
        isShowDetailsView = !isShowDetailsView

    }

    override fun deleteItem(musicPosition: Int) {
        super.deleteItem(musicPosition)
        if (mDetailsAdapter != null) {
            mDetailList.removeAt(musicPosition)
            mDetailsAdapter!!.setData(mDetailList)
        }
    }


    private fun switchCategory(showType: Int) {
        mBinding.viewPager2Album.setCurrentItem(showType, false)
        if (showType == Constant.NUMBER_ZERO) {
            mBinding.albumCategory.albumCategoryListLl.setBackgroundResource(R.drawable.btn_category_start_down_selector)
            mBinding.albumCategory.ivAlbumCategoryList.setImageResource(R.drawable.album_category_list_down_selector)
            mBinding.albumCategory.tvAlbumCategoryList.setTextColor(ColorUtil.wihtle)
            mBinding.albumCategory.albumCategoryTileLl.setBackgroundResource(R.drawable.btn_category_end_selector)
            mBinding.albumCategory.ivAlbumCategoryTile.setImageResource(R.drawable.album_category_tile_selector)
            mBinding.albumCategory.tvAlbumCategoryTile.setTextColor(ColorUtil.textName)
        } else if (showType == Constant.NUMBER_ONE) {
            mBinding.albumCategory.albumCategoryTileLl.setBackgroundResource(R.drawable.btn_category_end_down_selector)
            mBinding.albumCategory.ivAlbumCategoryTile.setImageResource(R.drawable.album_category_tile_down_selector)
            mBinding.albumCategory.tvAlbumCategoryTile.setTextColor(ColorUtil.wihtle)
            mBinding.albumCategory.albumCategoryListLl.setBackgroundResource(R.drawable.btn_category_start_selector)
            mBinding.albumCategory.ivAlbumCategoryList.setImageResource(R.drawable.album_category_list_selector)
            mBinding.albumCategory.tvAlbumCategoryList.setTextColor(ColorUtil.textName)
        }
    }

    companion object {
        fun newInstance(): AlbumFragment {
            return AlbumFragment()
        }
    }


    override fun onBackPressed(): Boolean {
        return if (isShowDetailsView && isVisible && isResumed) {
            showDetailsView(null)
            true
        } else {
            false
        }
    }

    override fun onRefresh() {
        showDetailsView(null)

    }
}