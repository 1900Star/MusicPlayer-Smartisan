package com.yibao.music.fragment

import android.os.Bundle
import android.util.SparseBooleanArray
import com.yibao.music.adapter.SongAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseMusicFragmentDev
import com.yibao.music.databinding.CategoryFragmentBinding
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog
import com.yibao.music.model.MusicBean
import com.yibao.music.util.Constant
import com.yibao.music.util.LogUtil
import com.yibao.music.viewmodel.SongViewModel
import io.reactivex.disposables.Disposable

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongFragment
 * @author: lsp
 * @创建时间: 2018/2/4 21:45
 * @描述： {显示音乐分类列表}
 */
class SongCategoryFragment : BaseMusicFragmentDev<CategoryFragmentBinding>() {

    private val mViewModel: SongViewModel by lazy { gets(SongViewModel::class.java) }
    private val mStateArray = SparseBooleanArray()
    private val mSelectList: MutableList<MusicBean> = ArrayList()
    private var mDeleteSongDisposable: Disposable? = null


    override fun initView() {

    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        val position = requireArguments().getInt(Constant.POSITION)
        mViewModel.getMusicList(position)
        mViewModel.listModel.observe(this) { musicList ->
            if (musicList.isNotEmpty()) {
                initAdapter(musicList, position)

            }

        }

    }

    private fun initAdapter(musicList: List<MusicBean>, position: Int) {
        when (position) {
            0 -> {
                val adapter = SongAdapter(
                    mActivity, musicList, mStateArray, true, 0,1
                )
                setData(adapter)
            }
            1 -> {

                val adapter = SongAdapter(
                    mActivity, musicList, mStateArray, false, 1,2
                )


                setData(adapter)
            }
            2 -> {
                val adapter = SongAdapter(
                    mActivity, musicList, mStateArray, false, 2,3
                )

                setData(adapter)
            }
            3 -> {
                val adapter = SongAdapter(
                    mActivity, musicList, mStateArray, false, 0,4
                )

                setData(adapter)
            }
        }


    }


    private fun setData(adapter: SongAdapter) {
        mBinding.musicView.setAdapter(requireActivity(), Constant.NUMBER_ONE, true, adapter)
        adapter.setOnItemMenuListener(object : BaseBindingAdapter.OnOpenItemMoreMenuListener {
            override fun openClickMoreMenu(position: Int, musicBean: MusicBean) {
                MoreMenuBottomDialog.newInstance(
                    musicBean,
                    position,
                    false,
                    false
                ).getBottomDialog(this@SongCategoryFragment.activity)
            }
        })
        adapter.setItemListener(object : BaseBindingAdapter.OnItemListener<MusicBean> {
            override fun showDetailsView(bean: MusicBean, position: Int) {
                mStateArray.put(position, true)
                updateSelected(bean)
                adapter.notifyDataSetChanged()

            }
        })
        adapter.setCheckBoxClickListener(object :
            BaseBindingAdapter.OnCheckBoxClickListener<MusicBean> {
            override fun checkboxChange(t: MusicBean, isChecked: Boolean, position: Int) {
                LogUtil.d(mTag, t.title + " == " + isChecked)
                mStateArray.put(position, isChecked)
                updateSelected(t)
                adapter.notifyDataSetChanged()
            }
        })
    }


    override fun deleteItem(musicPosition: Int) {
        super.deleteItem(musicPosition)
//        mSongAdapter.notifyItemRemoved(musicPosition)
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
            mStateArray.put(i, false)
        }
    }




    override fun onPause() {
        super.onPause()
        if (mDeleteSongDisposable != null) {
            mDeleteSongDisposable!!.dispose()
            mDeleteSongDisposable = null
        }
    }

    companion object {


        @JvmStatic
        fun newInstance(position: Int): SongCategoryFragment {

            val args = Bundle()
            val fragment = SongCategoryFragment()
            args.putInt(Constant.POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }
}