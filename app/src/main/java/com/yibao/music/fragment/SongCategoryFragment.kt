package com.yibao.music.fragment

import android.os.Bundle
import android.util.SparseBooleanArray
import com.yibao.music.adapter.SongAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseLazyFragmentDev
import com.yibao.music.databinding.CategoryFragmentBinding

import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog
import com.yibao.music.model.MusicBean
import com.yibao.music.util.Constants
import com.yibao.music.util.LogUtil
import com.yibao.music.util.MusicListUtil
import com.yibao.music.util.SnakbarUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongFragment
 * @author: lsp
 * @创建时间: 2018/2/4 21:45
 * @描述： {显示音乐分类列表}
 */
class SongCategoryFragment : BaseLazyFragmentDev<CategoryFragmentBinding>() {
    private lateinit var mSongAdapter: SongAdapter
    private var mPosition = 0
    private val mSparseBooleanArray = SparseBooleanArray()
    private val mSelectList: MutableList<MusicBean> = ArrayList()
    private var mDeleteSongDisposable: Disposable? = null
    override fun initView() {
        val arguments = arguments
        if (arguments != null) {
            mPosition = arguments.getInt(MUSIC_POSITION)
        }
        val musicBeanList = mMusicBeanDao.queryBuilder().list()
        when (mPosition) {
            0 -> {
                val abcList = MusicListUtil.sortMusicAbc(musicBeanList)
                setNotAllSelected(abcList)
                mSongAdapter = SongAdapter(
                    mActivity,
                    abcList,
                    mSparseBooleanArray,
                    Constants.NUMBER_ZERO,
                    Constants.NUMBER_ZERO
                )
            }
            1 -> {
                val scoreList = MusicListUtil.sortMusicList(musicBeanList, Constants.SORT_SCORE)
                setNotAllSelected(scoreList)
                mSongAdapter = SongAdapter(
                    mActivity,
                    scoreList,
                    mSparseBooleanArray,
                    Constants.NUMBER_ONE,
                    Constants.NUMBER_ONE
                )
            }
            2 -> {
                val playFrequencyList =
                    MusicListUtil.sortMusicList(musicBeanList, Constants.SORT_FREQUENCY)
                setNotAllSelected(playFrequencyList)
                mSongAdapter = SongAdapter(
                    mActivity,
                    playFrequencyList,
                    mSparseBooleanArray,
                    Constants.NUMBER_ONE,
                    Constants.NUMBER_TWO
                )
            }
            3 -> {
                val addTimeList =
                    MusicListUtil.sortMusicList(musicBeanList, Constants.SORT_DOWN_TIME)
                setNotAllSelected(addTimeList)
                mSongAdapter = SongAdapter(
                    mActivity,
                    addTimeList,
                    mSparseBooleanArray,
                    Constants.NUMBER_ONE,
                    Constants.NUMBER_ZERO
                )
            }
        }
        mBinding.musicView.setAdapter(mActivity, Constants.NUMBER_ONE, true, mSongAdapter)
        initListener()
    }

    override fun initData() {}
    private fun initListener() {
        mSongAdapter.setOnItemMenuListener(object : BaseBindingAdapter.OnOpenItemMoreMenuListener {
            override fun openClickMoreMenu(position: Int, musicBean: MusicBean) {
                MoreMenuBottomDialog.newInstance(
                    musicBean,
                    position,
                    false,
                    false
                ).getBottomDialog(this@SongCategoryFragment.activity)
            }
        })
        mSongAdapter.setItemListener(object : BaseBindingAdapter.OnItemListener<MusicBean> {
            override fun showDetailsView(bean: MusicBean, position: Int) {
                    mSparseBooleanArray.put(position, true)
                    updateSelected(bean)
                    mSongAdapter.notifyDataSetChanged()

            }
        })
        mSongAdapter.setCheckBoxClickListener(object :
            BaseBindingAdapter.OnCheckBoxClickListener<MusicBean> {
            override fun checkboxChange(t: MusicBean, isChecked: Boolean, position: Int) {
                LogUtil.d(mTag, t.title + " == " + isChecked)
                mSparseBooleanArray.put(position, isChecked)
                updateSelected(t)
                mSongAdapter.notifyDataSetChanged()
            }
        })
    }




    override fun deleteItem(musicPosition: Int) {
        super.deleteItem(musicPosition)
        mSongAdapter.notifyItemRemoved(musicPosition)
    }

    private fun updateSelected(bean: MusicBean) {
        if (mSelectList.contains(bean)) {
            mSelectList.remove(bean)
        } else {
            mSelectList.add(bean)
        }
    }

    private fun setNotAllSelected(listBeanList: List<MusicBean>) {
        for (i in listBeanList.indices) {
            mSparseBooleanArray.put(i, false)
        }
    }



    private fun deleteListItem() {
        LogUtil.d(mTag, "Size " + mSelectList.size)
        if (mSelectList.size > Constants.NUMBER_ZERO) {
            for (musicBean in mSelectList) {
                LogUtil.d(mTag, musicBean.title)
                //                FileUtil.deleteFile(new File(musicBean.getSongUrl()));
//                mMusicBeanDao.delete(musicBean);
            }
            mSongAdapter.setItemSelectStatus(false)
            mSongAdapter.setNewData(songList)
            //            getMBus().post(Constants.FRAGMENT_SONG, Constants.NUMBER_ZERO);
        } else {
            SnakbarUtil.favoriteSuccessView(mBinding.musicView, "没有选中条目")
        }
    }

    private val songList: List<MusicBean>
        get() {
            val musicBeans = mMusicBeanDao.queryBuilder().list()
            return MusicListUtil.sortMusicAbc(musicBeans)
        }


    override fun onPause() {
        super.onPause()
        if (mDeleteSongDisposable != null) {
            mDeleteSongDisposable!!.dispose()
            mDeleteSongDisposable = null
        }
    }

    companion object {
        private const val MUSIC_POSITION = "position"

        @JvmStatic
        fun newInstance(position: Int): SongCategoryFragment {
            val args = Bundle()
            val fragment = SongCategoryFragment()
            args.putInt(MUSIC_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }
}