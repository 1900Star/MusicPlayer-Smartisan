package com.yibao.music.adapter

import androidx.fragment.app.Fragment
import com.yibao.music.fragment.SearchFragment.Companion.newInstance
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yibao.music.fragment.SearchFragment
import com.yibao.music.viewmodel.SearchViewModel

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
class SearchPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val artist: String,
    private val viewModel: SearchViewModel
) :
    FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return newInstance(position, artist, viewModel)
    }

    override fun getItemCount(): Int {
        return 4
    }
}