package com.yibao.music.fragment.dialogfrag;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.R;
import com.yibao.music.adapter.MoreMemuAdapter;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.model.MoreMenuStatus;
import com.yibao.music.model.MusicBean;
import com.yibao.music.util.MenuListUtil;
import com.yibao.music.util.RxBus;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/8/22 14:11
 *
 * @author Stran
 */
public class MoreMenuBottomDialog {
    private LinearLayout mBottomListContent;
    private TextView mBottomTitle;
    private Context mContext;
    private BottomSheetBehavior<View> mBehavior;
    private static MusicBean mMusicBean;
    private TextView mBottomCancel;
    private MoreMemuAdapter mMemuAdapter;

    public static MoreMenuBottomDialog newInstance(MusicBean musicBean) {
        mMusicBean = musicBean;
        return new MoreMenuBottomDialog();
    }

    public void getBottomDialog(Context context) {
        this.mContext = context;

        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.bottom_more_menu_dialog, null);

        init(dialog, view);
        initData();
        initListener();
        dialog.show();
    }

    private void initListener() {
        mBottomCancel.setOnClickListener(v -> mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
        mMemuAdapter.setClickLiseter((position, musicBean) -> {
            RxBus.getInstance().post(new MoreMenuStatus(position, musicBean));
            mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }

    private void init(BottomSheetDialog dialog, View view) {
        mBottomListContent = view.findViewById(R.id.bottom_list_content);
        mBottomCancel = view.findViewById(R.id.bottom_sheet_cancel);
        mBottomTitle = view.findViewById(R.id.bottom_title);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        dialog.setCanceledOnTouchOutside(true);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
    }


    private void initData() {
        mMemuAdapter = new MoreMemuAdapter(MenuListUtil.getMenuData(mMusicBean.isFavorite()), mMusicBean);
        RecyclerView recyclerView = RecyclerFactory.creatRecyclerView(4, mMemuAdapter);
        mBottomListContent.addView(recyclerView);
    }

}


