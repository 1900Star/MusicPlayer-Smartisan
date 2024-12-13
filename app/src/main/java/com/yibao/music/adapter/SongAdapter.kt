package com.yibao.music.adapter

import android.app.Activity
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yibao.music.R
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.listener.OnMusicItemClickListener
import com.yibao.music.databinding.ItemMusicListBinding
import com.yibao.music.model.MusicBean
import com.yibao.music.util.Constant
import com.yibao.music.util.FileUtil
import com.yibao.music.util.ImageUitl
import com.yibao.music.util.StringUtil

///**
// * @param context               c
// * @param list                  lx
// * @param sparseBooleanArray    s
// * @param isShowStickyView      控制列表的StickyView是否显示，0 显示 ，1 ：不显示
// * parm isArtistList     用来控制音乐列表和艺术家列表的显示
// * @param scoreAndFrequencyFlag s 显示评分和播放次数 0 都不显示 ，1显示评分 ，2 显示播放次数
// * @param pageType              1 ABC 、2 评分 、3 播放次数 、 4 添加时间
// */
class SongAdapter(
    private val mContext: Activity,
    list: List<MusicBean>,
    private var mSparseBooleanArray: SparseBooleanArray,
    private val mIsShowStickyView: Boolean,
    private val mScroeAndFrequnecyFlag: Int,
    private val mPageType: Int
) : BaseBindingAdapter<MusicBean>(list as MutableList<MusicBean>) {


    override fun getLastItemDes(): String {
        return " 首歌"
    }

    override fun bindView(holder: RecyclerView.ViewHolder, bean: MusicBean) {
        if (holder is SongListViewHolder) {

            val position = holder.getAdapterPosition()
            if (mScroeAndFrequnecyFlag == Constant.NUMBER_ONE) {
                holder.mBinding.menuRatingBar.visibility = View.VISIBLE
                holder.mBinding.menuRatingBar.rating = bean.songScore.toFloat()
            } else if (mScroeAndFrequnecyFlag == Constant.NUMBER_TWO) {
                holder.mBinding.tvFrequency.visibility = View.VISIBLE
                holder.mBinding.tvFrequency.text = bean.playFrequency.toString()
            }
            // 显示单选框
            holder.mBinding.checkboxItem.visibility =
                if (isSelectStatus) View.VISIBLE else View.GONE
            // 显示更多按钮，单选框显示时，moreMenu不显示。
            holder.mBinding.ivSongItemMenu.visibility =
                if (isSelectStatus) View.INVISIBLE else View.VISIBLE
            holder.mBinding.checkboxItem.isChecked = mSparseBooleanArray[position]
            ImageUitl.customLoadPic(
                mContext,
                FileUtil.getAlbumUrl(bean, 1),
                R.drawable.noalbumcover_220,
                holder.mBinding.songAlbum
            )
            holder.mBinding.songArtistName.text = StringUtil.getArtist(bean)
            holder.mBinding.songName.text = StringUtil.getTitle(bean)
            if (mIsShowStickyView) {
                val firstTv = bean.firstChar
                holder.mBinding.itemStickyView.text = firstTv
                if (position == 0) {
                    holder.mBinding.itemStickyView.visibility = View.VISIBLE
                } else if (firstTv == mList[position - 1].firstChar) {
                    holder.mBinding.itemStickyView.visibility = View.GONE
                } else {
                    holder.mBinding.itemStickyView.visibility = View.VISIBLE
                }
            } else {
                holder.mBinding.itemStickyView.visibility = View.GONE
            }
            // 设置播放标识小喇叭,只在歌曲名列表显示。
            if (mPageType == 1) {
                val b = isItemSelected(holder.adapterPosition)
                val visit = if (b) View.VISIBLE else View.GONE
                holder.mBinding.songItemPlayFlag.visibility = visit
            }
            holder.mBinding.ivSongItemMenu.setOnClickListener {
                this@SongAdapter.openItemMenu(
                    bean,
                    position
                )
            }
            holder.mBinding.checkboxItem.setOnClickListener {
                checkBoxClick(
                    bean,
                    position,
                    holder.mBinding.checkboxItem.isChecked
                )
            }
            //  Item点击监听
            holder.mBinding.root.setOnClickListener {
                if (mPageType == 1) {
                    setSelectedPosition(holder.adapterPosition)
                }
                if (isSelectStatus) {
                    checkBoxClick(
                        bean,
                        position,
                        !holder.mBinding.checkboxItem.isChecked
                    )
                } else {
                    if (mContext is OnMusicItemClickListener) {
                        (mContext as OnMusicItemClickListener).startMusicService(
                            position,
                            mPageType
                        )
                    }
                }
            }

            holder.mBinding.root.setOnLongClickListener {
                updateCbState()
                if (mCbListener != null) {
                    mCbListener?.showCb(isSelectStatus)
                }
                false
            }
        }
    }


    override fun getFirstChar(i: Int): String? {
        return mList[i].firstChar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemMusicListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongListViewHolder(binding)
    }


    fun updateCbState() {
        isSelectStatus = !isSelectStatus
        notifyDataSetChanged()
    }


    internal class SongListViewHolder(var mBinding: ItemMusicListBinding) : RecyclerView.ViewHolder(
        mBinding.root
    )

    private var mCbListener: OnShowCbListener? = null

    fun setCbShowListener(listener: OnShowCbListener) {
        mCbListener = listener

    }

    interface OnShowCbListener {
        fun showCb(showCb: Boolean)
    }

}
