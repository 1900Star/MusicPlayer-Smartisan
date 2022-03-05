package com.yibao.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yibao.music.base.bindings.BaseBindingAdapter;
import com.yibao.music.databinding.MoreMenuItemBinding;
import com.yibao.music.model.MoreMenuBean;
import com.yibao.music.model.MusicBean;

import java.util.List;

/**
 * Des：${快速列表的Adapter}
 * Time:2017/8/22 14:31
 *
 * @author Stran
 */
public class MoreMenuAdapter
        extends BaseBindingAdapter<MoreMenuBean> {
    private OnMenuItemClickListener mListener;
    private static MusicBean mMusicBean;
    private static int mMusicPosition;

    public MoreMenuAdapter(List<MoreMenuBean> list, MusicBean musicBean, int musicPosition) {
        super(list);
        mMusicBean = musicBean;
        mMusicPosition = musicPosition;
    }

    @Override
    protected String getLastItemDes() {
        return " 首歌";
    }

    @Override
    public void bindView(RecyclerView.ViewHolder holder, MoreMenuBean menuBean) {
        if (holder instanceof MoreMenuHolder) {
            MoreMenuHolder menuHolder = (MoreMenuHolder) holder;
            int position = menuHolder.getAdapterPosition();
            menuHolder.mBinding.ivMoreMenu.setImageResource(menuBean.getPicId());
            menuHolder.mBinding.tvMoreMenuName.setText(menuBean.getNameId());
            menuHolder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.itemClick(mMusicPosition, position, mMusicBean);
                }
            });
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MoreMenuItemBinding binding = MoreMenuItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MoreMenuHolder(binding);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    static class MoreMenuHolder
            extends RecyclerView.ViewHolder {
        MoreMenuItemBinding mBinding;

        MoreMenuHolder(MoreMenuItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }


    public void setClickListener(OnMenuItemClickListener listener) {
        mListener = listener;
    }

    public interface OnMenuItemClickListener {
        /**
         * menu点击
         *
         * @param musicPosition 音乐位置
         * @param position      菜单位置
         * @param musicBean     bean
         */
        void itemClick(int musicPosition, int position, MusicBean musicBean);
    }

}
