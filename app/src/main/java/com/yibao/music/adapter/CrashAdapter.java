package com.yibao.music.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.yibao.music.R;

import java.io.File;

/**
 * @ Author: Luoshipeng
 * @ Name:   CrashAdapter
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2019/1/21/ 0:04
 * @ Des:    崩溃日志列表
 */
public class CrashAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SectionIndexer {
    private File[] mArray;

    public CrashAdapter(File[] array) {
        mArray = array;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.crash_item, viewGroup, false);
        return new CrashHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder rvHolder, int i) {
        if (rvHolder instanceof CrashHolder) {
            CrashHolder crashHolder = (CrashHolder) rvHolder;
            final File crashFile = mArray[i];
            crashHolder.mTvItem.setText(crashFile.getName());
            crashHolder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.click(crashFile);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mArray.length;
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

    class CrashHolder extends RecyclerView.ViewHolder {

        private final TextView mTvItem;

        CrashHolder(@NonNull View itemView) {
            super(itemView);
            mTvItem = itemView.findViewById(R.id.tv_crash_name);
        }
    }

    private OnCrashItemClickListener mListener;

    public void setItemClickListener(OnCrashItemClickListener listener) {
        mListener = listener;
    }

    public interface OnCrashItemClickListener {
        void click(File crashFile);
    }
}
