package com.yibao.music.view.music;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.yibao.music.R;
import com.yibao.music.util.ColorUtil;
import com.yibao.music.util.Constants;

/**
 * Author：Sid
 * Des：${将音乐列表(RecyclerView) ,StickyView, SlideBar导航栏 封装到一个Viwe里面，方便多个页面使用}
 * Time:2017/9/10 00:43
 *
 * @author Stran
 */

public class MusicView
        extends RelativeLayout {

    private RecyclerView mRecyclerView;
    private int defultHeight;
    private int mCurrentPosition = 0;
    private MusicSlidBar mSlidebar;

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
        //自定义组合控件将第三个参数设置为true  解析之后直接添加到当前view中
        LayoutInflater.from(getContext())
                .inflate(R.layout.music_view, this, true);
        mRecyclerView = findViewById(R.id.rv);
        mSlidebar = findViewById(R.id.music_slidbar);


    }


    /**
     * //设置列表的适配器
     *
     * @param context
     * @param adapterType 1 : SongListAdapter  、 2  ：ArtistAdapter  、
     *                    3  ： AlbumAdapter  普通视图  、 4  ： AlbumAdapter  平铺视图 GridView 3列
     * @param adapter
     */
    public void setAdapter(Context context, int adapterType, boolean isShowSlideBar, RecyclerView.Adapter adapter) {
        mSlidebar.setAdapterType(adapterType);
        if (adapterType == Constants.NUMBER_FOUR) {
            mRecyclerView.setBackgroundColor(ColorUtil.wihtle);
            GridLayoutManager manager = new GridLayoutManager(context, Constants.NUMBER_THRRE);
            manager.setOrientation(GridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(manager);
            mRecyclerView.setLayoutManager(manager);
        } else {
            mRecyclerView.setBackgroundColor(ColorUtil.rvBg);
            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(manager);
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (isShowSlideBar) {
            mSlidebar.setBarVisibility(Constants.NUMBER_ZOER);
        } else {
            mSlidebar.setBarVisibility(Constants.NUMBER_FOUR);

        }
    }


}







