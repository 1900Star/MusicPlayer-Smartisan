package com.yibao.music.base.factory;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.LinearLayout;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.base.BaseRvAdapter;
import com.yibao.music.util.Constants;
import com.yibao.music.view.SwipeItemLayout;

import java.util.Objects;

/**
 * @项目名： BigGirl
 * @包名： com.yibao.biggirl.factory
 * @文件名: RecyclerFactory
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.stranger98@gmail.com
 * @创建时间: 2017/5/9 15:02
 * @描述： {TODO}
 */

public class RecyclerFactory {
    private static final int RECYCLERVIEW_NORMAL = 1;
    private static final int RECYCLERVIEW_STAGGERED = 2;

    public static RecyclerView creatRecyclerView(int type,
                                                 RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        RecyclerView recyclerView = new RecyclerView(MusicApplication.getIntstance());

        if (type == RECYCLERVIEW_NORMAL) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.MATCH_PARENT);

            LinearLayoutManager manager = new LinearLayoutManager(MusicApplication.getIntstance());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setVerticalScrollBarEnabled(true);
            recyclerView.setLayoutManager(manager);
            recyclerView.setLayoutParams(params);
            DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
            divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.shape_item_decoration)));
            recyclerView.addItemDecoration(divider);

        } else {

//            StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(
//                    RecyclerView.LayoutParams.MATCH_PARENT,
//                    RecyclerView.LayoutParams.MATCH_PARENT);
//            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(type,
//                    StaggeredGridLayoutManager.VERTICAL);
//            manager.setOrientation(StaggeredGridLayoutManager.VERTICAL);
//            recyclerView.setLayoutManager(manager);
//            recyclerView.setLayoutParams(params);

            GridLayoutManager manager = new GridLayoutManager(recyclerView.getContext(), type);
            manager.setOrientation(GridLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(manager);
        }
        recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(recyclerView.getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return recyclerView;
    }

    public static void backTop(RecyclerView recyclerView, int type) {
        BaseRvAdapter adapter = (BaseRvAdapter) recyclerView.getAdapter();
        int position = adapter.getPositionForSection(0);
        if (type == RECYCLERVIEW_NORMAL) {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            manager.scrollToPositionWithOffset(position, 0);
        } else if (type == RECYCLERVIEW_STAGGERED) {
            StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(type,
                    StaggeredGridLayoutManager.VERTICAL);

            manager.scrollToPositionWithOffset(position, 0);
        }

    }
}
