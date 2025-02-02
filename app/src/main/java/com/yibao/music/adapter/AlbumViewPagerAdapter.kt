package com.yibao.music.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yibao.music.fragment.AlbumCategoryFragment
import com.yibao.music.viewmodel.AlbumViewModel

/**
 * 作者：Stran on 2017/3/23 03:31
 * 描述：${TODO}
 * 邮箱：strangermy@outlook.com
 *
 * @author Stran
 */
class AlbumViewPagerAdapter(
    fragment: Fragment, private val albumViewModel: AlbumViewModel
) :
    FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return AlbumCategoryFragment.newInstance(position, albumViewModel)
    }

    override fun getItemCount(): Int {
        return 2
    }
}