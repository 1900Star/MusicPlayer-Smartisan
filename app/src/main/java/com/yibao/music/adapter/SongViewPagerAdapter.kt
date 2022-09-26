package com.yibao.music.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yibao.music.fragment.SongCategoryFragment
import com.yibao.music.viewmodel.SongViewModel

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
class SongViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return SongCategoryFragment.newInstance(position)
    }

    override fun getItemCount(): Int {
        return 4
    }
}