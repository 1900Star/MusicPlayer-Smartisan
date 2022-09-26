package com.yibao.music.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yibao.music.MusicApplication
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.listener.OnMusicItemClickListener
import com.yibao.music.databinding.ItemDetailsAdapterBinding
import com.yibao.music.model.MusicBean
import com.yibao.music.model.SearchHistoryBean
import com.yibao.music.model.greendao.SearchHistoryBeanDao
import com.yibao.music.util.Constant
import com.yibao.music.util.LogUtil
import com.yibao.music.util.SpUtils.ContentValue
import com.yibao.music.util.StringUtil

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.playlist
 * @文件名: PlayListAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 16:22
 * @描述： { 1 ArtistFragment 、 2 AlbumFragment、 3 SearchActivity 、4 PlayListDetailView 会使用这个Adapter，dataFlag ( 1 、2、3、4)作为使用页面的标识}
 */
class DetailsViewAdapter(
    private val mContext: Context,
    list: List<MusicBean>,
    private val mPageType: Int,
    private val mCondition: String
) : BaseBindingAdapter<MusicBean> (list as MutableList<MusicBean>){


    override fun getLastItemDes(): String {
        return " 首歌"
    }

    override fun bindView(holder: RecyclerView.ViewHolder, bean: MusicBean) {
        if (holder is DetailsHolder) {

            val adapterPosition = holder.adapterPosition
            holder.mBinding.tvDetailsSongName.text = bean.title
            //            LogUtil.d(getMTAG(), " artist info     " + info.getTitle() + " == " + info.getDuration());
            val duration = bean.duration.toInt()
            holder.mBinding.tvSongDuration.text = StringUtil.parseDuration(duration)
            if (mPageType == Constant.NUMBER_FOUR) {
                // 播放列表的详情列表有侧滑删除
                holder.mBinding.deleteItemDetail.setOnClickListener {
                    LogUtil.d(
                        mTAG,
                        "播放列表的详情列表有侧滑删除"
                    )
                }
            }
            holder.mBinding.ivDetailsMenu.setOnClickListener {
                openItemMenu(
                    bean,
                    adapterPosition
                )
            }
            holder.mBinding.detailItemView.setOnClickListener {
                if (mContext is OnMusicItemClickListener) {
                    mSp.putValues(ContentValue(Constant.MUSIC_DATA_FLAG, Constant.NUMBER_TEN))

                    // 保存搜索记录 11、12、13、14
                    if (mPageType in 11..14) {
                        insertSearchBean(bean.title)
                    }
                    LogUtil.d(mTAG, "详情界面点击了    $mPageType")
                    (mContext as OnMusicItemClickListener).startMusicServiceFlag(
                        adapterPosition,
                        mPageType,
                        mCondition
                    )
                }
            }
        }
    }

    /**
     * 保存搜索历史记录，并播放过的歌曲
     *
     * @param queryConditions 搜索的歌名
     */
    private fun insertSearchBean(queryConditions: String) {
        LogUtil.d(mTAG, queryConditions)
        val searchDao = MusicApplication.getInstance().searchDao
        val historyList = searchDao.queryBuilder()
            .where(SearchHistoryBeanDao.Properties.SearchContent.eq(queryConditions)).build().list()
        // 没有保存过，直接插入一条数据。
        if (historyList.size < 1) {
            searchDao.insert(
                SearchHistoryBean(
                    queryConditions,
                    System.currentTimeMillis().toString()
                )
            )
        } else {
            // 保存过，更新保存时间。
            val searchHistoryBean = historyList[0]
            searchHistoryBean.searchTime = System.currentTimeMillis().toString()
            searchDao.update(searchHistoryBean)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =
            ItemDetailsAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailsHolder(binding)
    }

    internal class DetailsHolder(var mBinding: ItemDetailsAdapterBinding) : RecyclerView.ViewHolder(
        mBinding.root
    )
}