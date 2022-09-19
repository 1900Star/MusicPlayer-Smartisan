package com.yibao.music.fragment

import android.view.View
import com.yibao.music.R
import com.yibao.music.adapter.ArtistAdapter
import com.yibao.music.adapter.DetailsViewAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseMusicFragmentDev
import com.yibao.music.databinding.ArtisanListFragmentBinding
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog
import com.yibao.music.model.ArtistInfo
import com.yibao.music.model.MusicBean
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.util.Constant
import com.yibao.music.util.MusicListUtil
import com.yibao.music.view.music.MusicToolBar.OnToolbarClickListener

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: ArtistFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 23:49
 * @描述： {TODO}
 */
class ArtistFragment : BaseMusicFragmentDev<ArtisanListFragmentBinding>() {
    private lateinit var mAdapter: ArtistAdapter
    private var isShowDetailsView = false
    private var mDetailsAdapter: DetailsViewAdapter? = null
    private lateinit var mDetailList: ArrayList<MusicBean>
    override fun initView() {
        mBinding.musicBar.musicToolbarList.setToolbarTitle(getString(R.string.music_artisan))
    }

    override fun onResume() {
        super.onResume()
        initListener()
    }

    private fun initListener() {
        mBinding.musicBar.musicToolbarList.setClickListener(object : OnToolbarClickListener {
            override fun clickEdit() {
                if (isShowDetailsView) {
                    showDetail(null)
                }
            }

            override fun switchMusicControlBar() {
                switchControlBar()
            }

            override fun clickDelete() {}
        })
    }

    override fun initData() {
        val musicBeans = mMusicBeanDao.queryBuilder().list()
        val artistList = MusicListUtil.getArtistList(musicBeans)
        mAdapter = ArtistAdapter(artistList)
        mBinding.artistMusicView.setAdapter(activity, Constant.NUMBER_TWO, true, mAdapter)
        mAdapter.setItemListener(object : BaseBindingAdapter.OnItemListener<ArtistInfo> {
            override fun showDetailsView(bean: ArtistInfo, position: Int) {
                showDetail(bean)
            }
        })
    }

    private fun showDetail(artistInfo: ArtistInfo?) {
        if (isShowDetailsView) {
            mBinding.musicBar.musicToolbarList.setToolbarTitle(getString(R.string.music_artisan))
            mBinding.detailsView.visibility = View.GONE
            mBinding.musicBar.musicToolbarList.setTvEditVisibility(false)
            mDetailList.clear()
        } else {
            if (artistInfo != null) {
                mDetailList = MusicListUtil.sortMusicAbc(
                    mMusicBeanDao.queryBuilder()
                        .where(MusicBeanDao.Properties.Artist.eq(artistInfo.artist)).build().list()
                ) as ArrayList<MusicBean>
                // DetailsView播放音乐需要的参数
                mBinding.detailsView.setDataFlag(
                    childFragmentManager,
                    mDetailList.size,
                    artistInfo.artist,
                    Constant.NUMBER_ONE
                )
                mDetailsAdapter =
                    DetailsViewAdapter(
                        requireActivity(),
                        mDetailList,
                        Constant.NUMBER_SIX,
                        artistInfo.artist
                    )
                mBinding.detailsView.setAdapter(Constant.NUMBER_ONE, artistInfo, mDetailsAdapter)
                mDetailsAdapter!!.setOnItemMenuListener(object :
                    BaseBindingAdapter.OnOpenItemMoreMenuListener {
                    override fun openClickMoreMenu(position: Int, musicBean: MusicBean) {
                        MoreMenuBottomDialog.newInstance(
                            musicBean,
                            position,
                            false,
                            false
                        ).getBottomDialog(requireActivity())

                    }
                })
                mBinding.detailsView.setSuspension()

                mBinding.musicBar.musicToolbarList.setTvEditText(R.string.music_artisan)
                mBinding.musicBar.musicToolbarList.setTvEditVisibility(true)
                mBinding.musicBar.musicToolbarList.setToolbarTitle(artistInfo.albumName)
            }
            mBinding.detailsView.visibility = View.VISIBLE
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


    companion object {
        fun newInstance(): ArtistFragment {
            return ArtistFragment()
        }
    }

    override fun onBackPressed(): Boolean {
        return if (isShowDetailsView && isVisible && isResumed) {
            showDetail(null)
            true
        } else {
            false
        }
    }

}