package com.yibao.music.fragment

import android.view.View
import com.yibao.music.R
import com.yibao.music.adapter.SongViewPagerAdapter
import com.yibao.music.base.bindings.BaseMusicFragmentDev
import com.yibao.music.databinding.SongFragmentBinding
import com.yibao.music.util.ColorUtil
import com.yibao.music.util.Constant
import com.yibao.music.view.music.MusicToolBar.OnToolbarClickListener

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/4 21:45
 * @描述： {歌曲TAB}
 */
class SongFragment : BaseMusicFragmentDev<SongFragmentBinding>(), View.OnClickListener {

    private var isSelectStatus = false
    private var mPageType = 1
    override fun initView() {
        mBinding.musicBar.setTvEditVisibility(false)
        initData()
        initListener()
    }


    override fun onResume() {
        super.onResume()

        mBinding.musicBar.setToolbarTitle(getString(R.string.music_song))

    }


    override fun initData() {

        val pagerAdapter = SongViewPagerAdapter(requireActivity())
        mBinding.viewPager2Song.offscreenPageLimit = 4
        mBinding.viewPager2Song.adapter = pagerAdapter
        mBinding.viewPager2Song.isUserInputEnabled = false
//        mBinding.viewPager2Song.registerOnPageChangeCallback(object : OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                switchListCategory(position)
//            }
//        })
    }

    private fun initListener() {
        mBinding.musicBar.setClickListener(object : OnToolbarClickListener {
            override fun clickEdit() {
                mBus.post(Constant.SONG_FAG_EDIT, Constant.NUMBER_ONE)
                mBinding.musicBar.setTvEditText(if (isSelectStatus) R.string.tv_edit else R.string.complete)
                isSelectStatus = !isSelectStatus
            }

            override fun switchMusicControlBar() {
                switchControlBar()
            }

            override fun clickDelete() {
                mBus.post(Constant.SONG_FAG_EDIT, Constant.NUMBER_TWO)
            }
        })
        mBinding.songCategory.tvMusicCategorySongname.setOnClickListener(this)
        mBinding.songCategory.tvMusicCategoryScore.setOnClickListener(this)
        mBinding.songCategory.tvMusicCategoryFrequency.setOnClickListener(this)
        mBinding.songCategory.tvMusicCategoryAddtime.setOnClickListener(this)
        mBinding.songCategory.ivRandomPlay.setOnClickListener(this)


    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_random_play -> randomPlayMusic(mPageType)
            R.id.tv_music_category_songname -> switchListCategory(0)
            R.id.tv_music_category_score -> switchListCategory(1)
            R.id.tv_music_category_frequency -> switchListCategory(2)
            R.id.tv_music_category_addtime -> switchListCategory(3)

        }
    }

    private fun switchListCategory(pageType: Int) {

        mBinding.viewPager2Song.setCurrentItem(pageType, false)
        when (pageType) {
            0 -> {
                setAllCategoryNotNormal(Constant.NUMBER_ONE)

                mBinding.songCategory.tvMusicCategorySongname.setTextColor(ColorUtil.wihtle)
                mBinding.songCategory.tvMusicCategorySongname.setBackgroundResource(R.drawable.btn_category_start_down_selector)
            }
            1 -> {

                setAllCategoryNotNormal(Constant.NUMBER_TWO)
                mBinding.songCategory.tvMusicCategoryScore.setTextColor(ColorUtil.wihtle)
                mBinding.songCategory.tvMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_middle_down_selector)
            }
            2 -> {
                setAllCategoryNotNormal(Constant.NUMBER_THREE)
                mBinding.songCategory.tvMusicCategoryFrequency.setTextColor(ColorUtil.wihtle)
                mBinding.songCategory.tvMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_middle_down_selector)
            }
            3 -> {
                setAllCategoryNotNormal(Constant.NUMBER_FOUR)
                mBinding.songCategory.tvMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_end_down_selector)
                mBinding.songCategory.tvMusicCategoryAddtime.setTextColor(ColorUtil.wihtle)
            }

        }
    }

    private fun setAllCategoryNotNormal(pageType: Int) {
        mPageType = pageType
        mBinding.songCategory.tvMusicCategorySongname.setTextColor(ColorUtil.textName)
        mBinding.songCategory.tvMusicCategorySongname.setBackgroundResource(R.drawable.btn_category_start_selector)
        mBinding.songCategory.tvMusicCategoryScore.setTextColor(ColorUtil.textName)
        mBinding.songCategory.tvMusicCategoryScore.setBackgroundResource(R.drawable.btn_category_middle_selector)
        mBinding.songCategory.tvMusicCategoryFrequency.setTextColor(ColorUtil.textName)
        mBinding.songCategory.tvMusicCategoryFrequency.setBackgroundResource(R.drawable.btn_category_middle_selector)
        mBinding.songCategory.tvMusicCategoryAddtime.setTextColor(ColorUtil.textName)
        mBinding.songCategory.tvMusicCategoryAddtime.setBackgroundResource(R.drawable.btn_category_end_selector)


    }

    companion object {
        fun newInstance(): SongFragment {
            return SongFragment()
        }
    }


}