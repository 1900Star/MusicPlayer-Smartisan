package com.yibao.music.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.model.BottomSheetStatus;
import com.yibao.music.model.MoreMenuBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicCountBean;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.StringUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author：Sid
 * Des：${快速列表的Adapter}
 * Time:2017/8/22 14:31
 *
 * @author Stran
 */
public class MoreMemuAdapter
        extends BaseRvAdapter<MoreMenuBean> {
    private OnMenuItemClickListener mListener;
    private static MusicBean mMusicBean;

    public MoreMemuAdapter(List<MoreMenuBean> list, MusicBean musicBean) {
        super(list);
        mMusicBean = musicBean;

    }

    @Override
    protected String getLastItemDes() {
        return " 首歌";
    }

    @Override
    protected void bindView(RecyclerView.ViewHolder holder, MoreMenuBean menuBean) {
        if (holder instanceof MoreMenuHolder) {
            MoreMenuHolder menuHolder = (MoreMenuHolder) holder;
            int position = menuHolder.getAdapterPosition();
            menuHolder.mIvMoreMenu.setImageResource(menuBean.getPicId());
            menuHolder.mTvMoreMenu.setText(menuBean.getNameId());
            menuHolder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.itemClick(position, mMusicBean);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    @Override
    protected RecyclerView.ViewHolder getViewHolder(View view) {
        return new MoreMenuHolder(view);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.more_menu_item;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return new Object[0];
    }

    @Override
    public int getPositionForSection(int i) {
        return 0;
    }

    @Override
    public int getSectionForPosition(int i) {
        return 0;
    }

    static class MoreMenuHolder
            extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_more_menu_name)
        TextView mTvMoreMenu;
        @BindView(R.id.iv_more_menu)
        ImageView mIvMoreMenu;

        MoreMenuHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    public void setClickLiseter(OnMenuItemClickListener listener) {
        mListener = listener;
    }

    public interface OnMenuItemClickListener {
        void itemClick(int position, MusicBean musicBean);
    }

}
