package com.yibao.music.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yibao.music.MusicApplication
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.databinding.ItemPlayListBinding
import com.yibao.music.model.AddAndDeleteListBean
import com.yibao.music.model.PlayListBean
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.util.Constant
import com.yibao.music.util.MusicDaoUtil
import com.yibao.music.util.RxBus

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.playlist
 * @文件名: PlayListAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 16:22
 * @描述： {TODO}
 */
class PlayListAdapter(list: List<PlayListBean>) :
    BaseBindingAdapter<PlayListBean>(list as MutableList<PlayListBean>) {


    override fun bindView(holder: RecyclerView.ViewHolder, bean: PlayListBean) {
        if (holder is PlayViewHolder) {

            holder.mBinding.tvPlayListName.text = bean.title
            val musicBeans = MusicApplication.getInstance().musicDao.queryBuilder()
                .where(MusicBeanDao.Properties.PlayListFlag.eq(bean.title)).build().list()
            val count = musicBeans.size.toString() + " 首歌曲"
            holder.mBinding.tvPlayListCount.text = count
            val adapterPosition = holder.adapterPosition

            holder.mBinding.rlPlayListItem.setOnClickListener {
                this@PlayListAdapter.openDetails(bean, adapterPosition)
            }
            holder.mBinding.playListItemSlide.setOnClickListener {
                mList.removeAt(adapterPosition)
                MusicDaoUtil.setMusicListFlag(bean)
                RxBus.getInstance().post(AddAndDeleteListBean(Constant.NUMBER_TWO))
            }

            holder.mBinding.ivItemEdit.setOnClickListener {
                editItemTitle(
                    adapterPosition
                )
            }
            holder.mBinding.rlPlayListItem.setOnLongClickListener {
                setLongClick(bean, adapterPosition)
                true
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == typeItem) {
            val binding =
                ItemPlayListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return PlayViewHolder(binding)
        } else {
            return moreHolder(parent)
        }
    }


    internal class PlayViewHolder(var mBinding: ItemPlayListBinding) : RecyclerView.ViewHolder(
        mBinding.root
    )

    override fun getLastItemDes(): String {
        return " 个播放列表"
    }
}
