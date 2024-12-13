package com.yibao.music.fragment

import android.content.Intent
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.View
import com.yibao.music.R
import com.yibao.music.activity.PlayListActivity
import com.yibao.music.adapter.SongAdapter
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseMusicFragmentDev
import com.yibao.music.databinding.CategoryFragmentBinding
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog
import com.yibao.music.model.MusicBean
import com.yibao.music.util.Constant
import com.yibao.music.util.LogUtil
import com.yibao.music.util.ToastUtil
import com.yibao.music.viewmodel.SongViewModel

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artisanlist
 * @文件名: SongFragment
 * @author: lsp
 * @创建时间: 2018/2/4 21:45
 * @描述： {显示音乐分类列表}
 */
class SongCategoryFragment : BaseMusicFragmentDev<CategoryFragmentBinding>(), View.OnClickListener {

    private val mViewModel: SongViewModel by lazy { gets(SongViewModel::class.java) }
    private var mStateArray = SparseBooleanArray()
    private val mSelectList = ArrayList<MusicBean>()


    private var mList = ArrayList<MusicBean>()
    override fun initView() {
        mBinding.ivSongAddToList.setOnClickListener(this)
        mBinding.ivSongAddToPlay.setOnClickListener(this)
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        val position = requireArguments().getInt(Constant.POSITION)
        mViewModel.getMusicList(position)
        mViewModel.listModel.observe(this) { musicList ->
            if (musicList.isNotEmpty()) {
                mList.clear()
                mList.addAll(musicList)
                initAdapter(musicList, position)

            }

        }

    }

    private fun initAdapter(musicList: List<MusicBean>, position: Int) {
        when (position) {
            0 -> {
                val adapter = SongAdapter(
                    mActivity, musicList, mStateArray, true, 0, 1
                )
                setData(adapter)
            }

            1 -> {

                val adapter = SongAdapter(
                    mActivity, musicList, mStateArray, false, 1, 2
                )


                setData(adapter)
            }

            2 -> {
                val adapter = SongAdapter(
                    mActivity, musicList, mStateArray, false, 2, 3
                )

                setData(adapter)
            }

            3 -> {
                val adapter = SongAdapter(
                    mActivity, musicList, mStateArray, false, 0, 4
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
                    isNeedScore = false,
                    isNeedSetTime = false
                ).getBottomDialog(requireActivity())
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

        adapter.setItemLongClickListener(object :
            BaseBindingAdapter.ItemLongClickListener<MusicBean> {
            override fun longClickItem(musicInfo: MusicBean, currentPosition: Int) {
                LogUtil.d(mTag, "长按了")
            }
        })

        adapter.setCbShowListener(object : SongAdapter.OnShowCbListener {
            override fun showCb(showCb: Boolean) {
                LogUtil.d(mTag, "IS SHOW CB  $showCb")
                isShowCb = showCb
                if (!showCb) {
                    mSelectList.clear()
                    setNotAllSelected(mList)
                }
                updateAddToListBtn(showCb)
            }
        })
    }

    private var isShowCb = false
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
        LogUtil.d(mTag, "选中长度：  ${mSelectList.size}")
    }

    private fun setNotAllSelected(listBeanList: List<MusicBean>) {
        for (i in listBeanList.indices) {
            mStateArray.put(i, false)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.iv_song_add_to_list -> {
                LogUtil.d(mTag, "添加长度：  ${mSelectList.size}")
                startPlayListActivity()
            }

            R.id.iv_song_add_to_play -> {
                LogUtil.d(mTag, "添加长度：  ${mSelectList.size}")
            }
        }

    }

    private fun startPlayListActivity() {
        if (mSelectList.isEmpty()) {
            ToastUtil.show(requireActivity(), "请选择要添加的歌曲")
        } else {
            val arrayList = ArrayList<String>()
            for (musicBean in mSelectList) {
                arrayList.add(musicBean.title)
            }
            val intent = Intent(context, PlayListActivity::class.java)
            intent.putStringArrayListExtra(Constant.ADD_TO_LIST, arrayList)
            startActivity(intent)
        }
    }

    private fun updateAddToListBtn(showCb: Boolean) {
        mBinding.groupSongAdd.visibility = if (showCb) View.VISIBLE else View.GONE
    }

    /**
     * 列表进入选中状态时，需要处理返回事件。
     */
    override fun onBackPressed(): Boolean {
        updateAddToListBtn(!isShowCb)
        if (isShowCb) {
            // 选中状态，按返回键取消选中状态
            mBinding.groupSongAdd
            // 隐藏添加到插入列表按钮。
            mBinding.musicView.updateCbState()
            isShowCb = false
            return true
        } else {
            return false
        }

    }
}