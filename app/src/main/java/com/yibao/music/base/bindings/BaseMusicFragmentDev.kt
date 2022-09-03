package com.yibao.music.base.bindings

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.yibao.music.base.listener.OnMusicItemClickListener
import com.yibao.music.base.listener.OnUpdateTitleListener
import com.yibao.music.util.RandomUtil
import com.yibao.music.util.SpUtil

/**
 * @author Stran
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.base
 * @文件名: BaseMusicFragment
 * @创建时间: 2018/1/1 17:36
 * @描述： TODO
 */
abstract class BaseMusicFragmentDev<T : ViewBinding> : BaseBindingFragment<T>() {
    private var mAddToPlayListFlag = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAddToPlayListFlag = SpUtil.getAddToPlayListFdlag(mContext)
    }

    protected fun switchControlBar() {
        if (requireActivity() is OnUpdateTitleListener) {
            (requireActivity() as OnUpdateTitleListener).switchControlBar()
        }
    }

    protected open fun deleteItem(musicPosition: Int) {}


    protected fun randomPlayMusic() {
        val randomSize = mMusicBeanDao.queryBuilder().list().size
        val position = RandomUtil.getRandomPostion(randomSize.coerceAtLeast(0))
        if (activity is OnMusicItemClickListener) {
            (activity as OnMusicItemClickListener?)!!.startMusicService(position)
        }
    }

}