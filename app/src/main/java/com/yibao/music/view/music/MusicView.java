package com.yibao.music.view.music;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yibao.biggirl.R;
import com.yibao.music.music.musiclist.MusicListAdapter;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/9/10 00:43
 */


public class MusicView
        extends RelativeLayout {

    private RecyclerView mRecyclerView;
    private int defultHeight;
    private int mCurrentPosition = 0;

    public MusicView(Context context) {
        super(context);
        initView();
    }

    public MusicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public MusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    private void initView() {
        //第三个参数设置为true  直接解析之后添加到当前view中
        LayoutInflater.from(getContext())
                .inflate(R.layout.music_view, this, true);
        mRecyclerView = findViewById(R.id.rv);
        TextView stickyheader = findViewById(R.id.music_rv_sticky_view);


    }



    /**
     *  //设置列表的适配器
     * @param context
     * @param adapter
     */
    public void setAdapter(Context context, MusicListAdapter adapter) {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
        //        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        //            @Override
        //            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        //                super.onScrollStateChanged(recyclerView, newState);
        //                //拿到悬浮条的高度
        //                defultHeight = mStickyheader.getHeight();
        //            }
        //
        //            @Override
        //            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        //                super.onScrolled(recyclerView, dx, dy);
        //                View view = manager.findViewByPosition(mCurrentPosition + 1);
        //                if (view != null) {
        //                    if (view.getTop() <= defultHeight) {
        //                        mStickyheader.setY(-(defultHeight - view.getTop()));
        //                    } else {
        //                        mStickyheader.setY(0);
        //                    }
        //                }
        //
        //                if (mCurrentPosition != manager.findFirstVisibleItemPosition()) {
        //                    mCurrentPosition = manager.findFirstVisibleItemPosition();
        //                    mStickyheader.setY(0);
        //
        //                    //                    updateSuspensionBar(mCurrentPosition);
        //                }
        //            }
        //        });

    }


}







