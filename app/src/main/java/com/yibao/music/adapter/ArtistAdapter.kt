package com.yibao.music.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.databinding.ArtistItemBinding
import com.yibao.music.model.ArtistInfo
import com.yibao.music.util.HanziToPinyins

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.artist
 * @文件名: ArtistAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/7 17:18
 * @描述： {TODO}
 */
class ArtistAdapter(list: List<ArtistInfo>) :
    BaseBindingAdapter<ArtistInfo>(list as MutableList<ArtistInfo>) {
    override fun getLastItemDes(): String {
        return " 位艺术家"
    }


    override fun bindView(holder: RecyclerView.ViewHolder, bean: ArtistInfo) {
        if (holder is ArtisHolder) {
            val position = holder.getAdapterPosition()
            val songCount = bean.songCount.toString() + " 首歌曲"
            val albumCount = bean.albumCount.toString() + " 张专辑"
            holder.mBinding.artistItemName.text = bean.artist
            holder.mBinding.artistItemAlbumCount.text = albumCount
            holder.mBinding.artistItemSongCount.text = songCount
            val firstChar = HanziToPinyins.stringToPinyinSpecial(bean.artist).toString() + ""

            holder.mBinding.artistItemStickyView.text = firstChar
            if (position == 0) {
                holder.mBinding.artistItemStickyView.visibility = View.VISIBLE
            } else if (firstChar == HanziToPinyins
                    .stringToPinyinSpecial(mList[position - 1].artist).toString() + ""
            ) {
                holder.mBinding.artistItemStickyView.visibility = View.GONE
            } else {
                holder.mBinding.artistItemStickyView.visibility = View.VISIBLE
            }


            holder.mBinding.artistItemContent.setOnClickListener {
                openDetails(
                    bean,
                    position
                )
            }
        }
    }


    override fun getFirstChar(i: Int): String? {
        return mList[i].firstChar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if (viewType == typeItem) {
            val binding =
                ArtistItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ArtisHolder(binding)
        } else {
            return moreHolder(parent)
        }


    }


    internal class ArtisHolder(var mBinding: ArtistItemBinding) : RecyclerView.ViewHolder(
        mBinding.root
    )
}

