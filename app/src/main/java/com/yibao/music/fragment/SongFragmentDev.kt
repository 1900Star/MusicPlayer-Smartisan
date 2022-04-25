package com.yibao.music.fragment

import com.yibao.music.base.BaseMusicFragment
import butterknife.BindView
import com.yibao.music.R
import com.yibao.music.view.music.MusicToolBar
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import butterknife.ButterKnife
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import com.yibao.music.adapter.SongViewPagerAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.yibao.music.view.music.MusicToolBar.OnToolbarClickListener
import butterknife.OnClick
import com.yibao.music.base.BaseMusicFragmentDev
import com.yibao.music.databinding.SongFragmentBinding
import com.yibao.music.util.SpUtil
import com.yibao.music.fragment.SongFragmentDev
import com.yibao.music.util.ColorUtil
import com.yibao.music.util.Constants

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 21:45
 * @描述： {歌曲TAB}
 */
class SongFragmentDev : BaseMusicFragmentDev<SongFragmentBinding>() {

    private var curentIndex = 0
    private var isSelecteStatus = false

    override fun initView() {
        initData()
        initListener()
    }


    override fun onResume() {
        super.onResume()

        mBinding.musicBar.musicToolbarList.setToolbarTitle(getString(R.string.music_song))
        initRxBusData()
        switchListCategory(curentIndex)
    }

    private fun initRxBusData() {
        disposeToolbar()
        if (mEditDisposable == null) {
            mEditDisposable = mBus.toObservableType(Constants.FRAGMENT_SONG, Any::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe { editBean: Any? ->
                    mBinding.musicBar.musicToolbarList.setTvEditText(R.string.tv_edit)
                    mBinding.musicBar.musicToolbarList.setTvDeleteVisibility(View.GONE)
                    isSelecteStatus = false
                }
        }
    }

    override fun initData() {
        switchListCategory(0)
        val pagerAdapter = SongViewPagerAdapter(requireActivity())
        mBinding.viewPager2Song.offscreenPageLimit = 4
        mBinding.viewPager2Song.adapter = pagerAdapter
        mBinding.viewPager2Song.isUserInputEnabled = false
        mBinding.viewPager2Song.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                switchListCategory(position)
            }
        })
    }

    private fun initListener() {
        mBinding.musicBar.musicToolbarList.setClickListener(object : OnToolbarClickListener {
            override fun clickEdit() {
                interceptBackEvent(if (isSelecteStatus) Constants.NUMBER_ELEVEN else Constants.NUMBER_ZERO)
                mBus.post(Constants.SONG_FAG_EDIT, Constants.NUMBER_ONE)
                mBinding.musicBar.musicToolbarList.setTvEditText(if (isSelecteStatus) R.string.tv_edit else R.string.complete)
                mBinding.musicBar.musicToolbarList.setTvDeleteVisibility(if (isSelecteStatus) View.GONE else View.VISIBLE)
                isSelecteStatus = !isSelecteStatus
            }

            override fun switchMusicControlBar() {
                switchControlBar()
            }

            override fun clickDelete() {
                mBus.post(Constants.SONG_FAG_EDIT, Constants.NUMBER_TWO)
            }
        })
    }

    @OnClick
    fun onClick(v: View) {
        when (v.id) {
            R.id.iv_music_category_paly -> randomPlayMusic()
            R.id.tv_music_category_songname -> switchListCategory(0)
            R.id.tv_music_category_score -> switchListCategory(1)
            R.id.tv_music_category_frequency -> switchListCategory(2)
            R.id.tv_music_category_addtime -> switchListCategory(3)
            else -> {}
        }
    }

    private fun switchListCategory(flag: Int) {
        curentIndex = flag
        mBinding.viewPager2Song.setCurrentItem(flag, false)
        when (flag) {
            0 -> {
                setAllCategoryNotNormal(Constants.NUMBER_ONE)

                mBinding.songCategory.tvMusicCategorySongname.setTextColor(ColorUtil.wihtle)
                mBinding.songCategory.tvMusicCategorySongname.setBackgroundResource(R.drawable.btn_category_songname_down_selector)
            }
            1 -> {
                setAllCategoryNotNormal(Constants.NUMBER_TWO)
                mBinding.songCategory.tvMusicCategoryScore.setTextColor(ColorUtil.wihtle)
                mBinding.songCategory.tvMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_score_down_selector)
            }
            2 -> {
                setAllCategoryNotNormal(Constants.NUMBER_THREE)
                mBinding.songCategory.tvMusicCategoryFrequency.setTextColor(ColorUtil.wihtle)
                mBinding.songCategory.tvMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_score_down_selector)
            }
            3 -> {
                setAllCategoryNotNormal(Constants.NUMBER_FOUR)
                mBinding.songCategory.tvMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_views_down_selector)
                mBinding.songCategory.tvMusicCategoryAddtime.setTextColor(ColorUtil.wihtle)
            }
            else -> {}
        }
    }

    private fun setAllCategoryNotNormal(playListFlag: Int) {
        mBinding.songCategory.tvMusicCategorySongname.setTextColor(ColorUtil.textName)
        mBinding.songCategory.tvMusicCategorySongname.setBackgroundResource(R.drawable.btn_category_songname_selector)
        mBinding.songCategory.tvMusicCategoryScore.setTextColor(ColorUtil.textName)
        mBinding.songCategory.tvMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_score_selector)
        mBinding.songCategory.tvMusicCategoryFrequency.setTextColor(ColorUtil.textName)
        mBinding.songCategory.tvMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_score_selector)
        mBinding.songCategory.tvMusicCategoryAddtime.setTextColor(ColorUtil.textName)
        mBinding.songCategory.tvMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_views_selector)
        SpUtil.setSortFlag(requireContext(), playListFlag)
    }

    companion object {
        fun newInstance(): SongFragmentDev {
            return SongFragmentDev()
        }
    }

    override val isOpenDetail: Boolean
        get() = isOpenDetail


}