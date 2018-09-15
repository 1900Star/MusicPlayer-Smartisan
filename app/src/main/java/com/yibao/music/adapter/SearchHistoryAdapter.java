package com.yibao.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.SearchHistoryBean;

import java.util.List;

/**
 * @ Author: Luoshipeng
 * @ Name:   MyGridViewAdapter
 * @ Email:  strangermy98@gmail.com
 * @ Time:   2018/6/9/ 15:13
 * @ Des:    //TODO
 */
public class SearchHistoryAdapter extends BaseAdapter {
    private Context mContext;
    private List<SearchHistoryBean> mList;

    public SearchHistoryAdapter(Context context, List<SearchHistoryBean> list) {
        mContext = context;
        mList = list;
    }


    @Override
    public int getCount() {
        return mList != null && mList.size() > 0 ? mList.size() : 0;

    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.search_history_item, null);
            holder = new Holder();
            holder.mTextView = view.findViewById(R.id.grid_tv);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        holder.mTextView.setText(mList.get(position).getSearchContent());
        return view;
    }

    public void updata(List<SearchHistoryBean> list) {
        if (mList != null) {
            mList.clear();
        }
        mList = list;
        notifyDataSetChanged();
    }

    private class Holder {

        TextView mTextView;

        public TextView getTextView() {
            return mTextView;
        }

        public void setTextView(TextView textView) {
            this.mTextView = textView;
        }


    }


}



