package com.yibao.music.base.bindings

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.yibao.music.base.listener.OnMusicItemClickListener
import com.yibao.music.base.listener.OnUpdateTitleListener
import com.yibao.music.util.Constant
import com.yibao.music.util.LogUtil
import com.yibao.music.util.RandomUtil

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

        mAddToPlayListFlag = mSp.getInt(Constant.ADD_TO_PLAY_LIST_FLAG)
    }

    protected fun switchControlBar() {
        if (requireActivity() is OnUpdateTitleListener) {
            (requireActivity() as OnUpdateTitleListener).switchControlBar()
        }
    }

    protected open fun deleteItem(musicPosition: Int) {}


    protected fun randomPlayMusic(pageType: Int) {
        val randomSize = mMusicBeanDao.queryBuilder().list().size
        LogUtil.d(mTag, "随机播放   $pageType")
        val position = RandomUtil.getRandomPosition(randomSize)
        if (requireActivity() is OnMusicItemClickListener) {
            (requireActivity() as OnMusicItemClickListener?)!!.startMusicService(position, pageType)
        }
    }

}