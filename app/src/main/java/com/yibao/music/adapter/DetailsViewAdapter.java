package com.yibao.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.yibao.music.MusicApplication;
import com.yibao.music.base.bindings.BaseBindingAdapter;
import com.yibao.music.base.listener.OnMusicItemClickListener;
import com.yibao.music.databinding.ItemDetailsAdapterBinding;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.SearchHistoryBean;
import com.yibao.music.model.greendao.SearchHistoryBeanDao;
import com.yibao.music.util.Constant;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.SpUtils;
import com.yibao.music.util.StringUtil;

import java.util.List;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.playlist
 * @文件名: PlayListAdapter
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2018/2/9 16:22
 * @描述： { 1 ArtistFragment 、 2 AlbumFragment、 3 SearchActivity 、4 PlayListDetailView 会使用这个Adapter，dataFlag ( 1 、2、3、4)作为使用页面的标识}
 */

public class DetailsViewAdapter extends BaseBindingAdapter<MusicBean> {
    private Context mContext;
    /**
     * 用来区分搜索的标识：1 Artist 、 2 Album 、  3 SongName(目前只按歌名搜索) 、   4 PlayList (播放列表)
     */
    private int mDataFlag;

    public DetailsViewAdapter(Context context, List<MusicBean> list, int dataFlag) {
        super(list);
        this.mContext = context;
        this.mDataFlag = dataFlag;
    }

    public void setDataFlag(int flag) {
        this.mDataFlag = flag;
    }

    @Override
    protected String getLastItemDes() {
        return " 首歌";
    }

    @Override
    public void bindView(@NonNull RecyclerView.ViewHolder holder, MusicBean info) {
        if (holder instanceof DetailsHolder) {
            DetailsHolder detailsHolder = (DetailsHolder) holder;
            int adapterPosition = detailsHolder.getAdapterPosition();
            detailsHolder.mBinding.tvDetailsSongName.setText(info.getTitle());
//            LogUtil.d(getMTAG(), " artist info     " + info.getTitle() + " == " + info.getDuration());
            int duration = (int) info.getDuration();
            detailsHolder.mBinding.tvSongDuration.setText(StringUtil.parseDuration(duration));
            if (mDataFlag == Constant.NUMBER_FOUR) {
                // 播放列表的详情列表有侧滑删除
                detailsHolder.mBinding.deleteItemDetail.setOnClickListener(v -> {
                    LogUtil.d(getMTAG(), "播放列表的详情列表有侧滑删除");
//                    info.setPlayListFlag(Constants.PLAY_LIST_BACK_FLAG);
//                    MusicApplication.getInstance().getMusicDao().update(info);
//                    RxBus.getInstance().post(new AddAndDeleteListBean(Constants.NUMBER_SIX, adapterPosition, info.getTitle()));
                });
            }
            detailsHolder.mBinding.ivDetailsMenu.setOnClickListener(v -> openItemMenu(info, adapterPosition));

            detailsHolder.mBinding.detailItemView.setOnClickListener(view -> {
                if (mContext instanceof OnMusicItemClickListener) {


                    getMSp().putValues(new SpUtils.ContentValue(Constant.MUSIC_DATA_FLAG,Constant.NUMBER_TEN));
                    LogUtil.d(getMTAG(), info.toString());
                    // 保存搜索记录
                    if (mDataFlag == Constant.NUMBER_THREE) {
                        insertSearchBean(info.getTitle());
                    }
                    LogUtil.d(getMTAG(), info.toString());
                    ((OnMusicItemClickListener) mContext).startMusicServiceFlag(adapterPosition, Constant.NUMBER_TEN, mDataFlag, getQueryFlag(info));
                }

            });

        }

    }

    /**
     * 保存搜索历史记录，并播放过的歌曲
     *
     * @param queryConditions 搜索的歌名
     */
    private void insertSearchBean(String queryConditions) {
        LogUtil.d(getMTAG(), queryConditions);
        SearchHistoryBeanDao searchDao = MusicApplication.getInstance().getSearchDao();
        List<SearchHistoryBean> historyList = searchDao.queryBuilder().where(SearchHistoryBeanDao.Properties.SearchContent.eq(queryConditions)).build().list();
        // 没有保存过，直接插入一条数据。
        if (historyList.size() < 1) {
            searchDao.insert(new SearchHistoryBean(queryConditions, Long.toString(System.currentTimeMillis())));
        } else {
            // 保存过，更新保存时间。
            SearchHistoryBean searchHistoryBean = historyList.get(0);
            searchHistoryBean.setSearchTime(Long.toString(System.currentTimeMillis()));
            searchDao.update(searchHistoryBean);
        }
    }

    @Nullable
    private String getQueryFlag(MusicBean info) {
        String queryFlag = null;
        if (mDataFlag == Constant.NUMBER_ONE) {
            queryFlag = info.getArtist();
        } else if (mDataFlag == Constant.NUMBER_TWO) {
            queryFlag = info.getAlbum();
        } else if (mDataFlag == Constant.NUMBER_THREE) {
            queryFlag = info.getTitle();
        } else if (mDataFlag == Constant.NUMBER_FOUR) {
            queryFlag = info.getPlayListFlag();
        }
        return queryFlag;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailsAdapterBinding binding = ItemDetailsAdapterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new DetailsHolder(binding);
    }


    static class DetailsHolder extends RecyclerView.ViewHolder {
        ItemDetailsAdapterBinding mBinding;

        DetailsHolder(ItemDetailsAdapterBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
