package com.yibao.music.fragment

import android.view.View
import com.yibao.music.R
import com.yibao.music.adapter.ArtistAdapter
import com.yibao.music.adapter.DetailsViewAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseLazyFragmentDev
import com.yibao.music.databinding.ArtisanListFragmentBinding
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog
import com.yibao.music.model.ArtistInfo
import com.yibao.music.model.MusicBean
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.util.Constants
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
class ArtistFragment : BaseLazyFragmentDev<ArtisanListFragmentBinding>() {
    private lateinit var mAdapter: ArtistAdapter
    private var isShowDetailsView = false
    private var mDetailsAdapter: DetailsViewAdapter? = null
    private lateinit var mDetailList: ArrayList<MusicBean>
    private var mTempTitle: String? = null
    override fun initView() {
        mBinding.musicBar.musicToolbarList.setToolbarTitle(
            if (isShowDetailsView) mTempTitle else getString(
                R.string.music_artisan
            )
        )
        mBinding.musicBar.musicToolbarList.setTvEditVisibility(isShowDetailsView)

    }

    override fun onResume() {
        super.onResume()
        initListener()
    }

    private fun initListener() {


        mAdapter.setItemListener(object : BaseBindingAdapter.OnItemListener<ArtistInfo> {
            override fun showDetailsView(bean: ArtistInfo, position: Int, isEditStatus: Boolean) {
                openDetailsView(bean)

            }

        })


        mBinding.musicBar.musicToolbarList.setClickListener(object : OnToolbarClickListener {
            override fun clickEdit() {
                if (isShowDetailsView) {
                    openDetailsView(null)
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
        mBinding.artistMusicView.setAdapter(activity, Constants.NUMBER_TWO, true, mAdapter)
    }

    private fun openDetailsView(artistInfo: ArtistInfo?) {
        if (!isShowDetailsView) {
            if (artistInfo != null) {
                mTempTitle = artistInfo.albumName
                mDetailList = mMusicBeanDao.queryBuilder()
                    .where(MusicBeanDao.Properties.Artist.eq(artistInfo.artist)).build()
                    .list() as ArrayList<MusicBean>
                // DetailsView播放音乐需要的参数
                mBinding.detailsView.setDataFlag(
                    childFragmentManager,
                    mDetailList.size,
                    artistInfo.artist,
                    Constants.NUMBER_ONE
                )
                mDetailsAdapter = DetailsViewAdapter(requireActivity(), mDetailList, Constants.NUMBER_ONE)
                mBinding.detailsView.setAdapter(Constants.NUMBER_ONE, artistInfo, mDetailsAdapter)
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
                interceptBackEvent(Constants.NUMBER_NINE)

                mBinding.musicBar.musicToolbarList.setTvEditText(R.string.music_artisan)
            }
        }
        mBinding.detailsView.visibility = if (isShowDetailsView) View.GONE else View.VISIBLE
        mBinding.musicBar.musicToolbarList.setToolbarTitle(if (isShowDetailsView) getString(R.string.music_artisan) else mTempTitle)
        isShowDetailsView = !isShowDetailsView
        mBinding.musicBar.musicToolbarList.setTvEditVisibility(isShowDetailsView)
    }

    override fun deleteItem(musicPosition: Int) {
        super.deleteItem(musicPosition)
        if (mDetailsAdapter != null) {
            mDetailList.removeAt(musicPosition)
            mDetailsAdapter!!.setData(mDetailList!!)
        }
    }

    override fun handleDetailsBack(detailFlag: Int) {
        if (detailFlag == Constants.NUMBER_NINE) {
            openDetailsView(null)
        }
    }


    companion object {
        fun newInstance(): ArtistFragment {
            return ArtistFragment()
        }
    }

    override val isOpenDetail: Boolean
        get() = isShowDetailsView


}