package com.yibao.music.fragment.dialogfrag;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.adapter.MoreMemuAdapter;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.BottomSheetCallback;
import com.yibao.music.model.MoreMenuStatus;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MenuListUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SpUtil;

/**
 * Des：${TODO}
 * Time:2017/8/22 14:11
 *
 * @author Stran
 */
public class MoreMenuBottomDialog {
    private static MusicBeanDao musicDao;
    private LinearLayout mBottomListContent;
    private BottomSheetBehavior<View> mBehavior;
    private static MusicBean mMusicBean;
    private TextView mBottomCancel;
    private MoreMemuAdapter mMemuAdapter;
    private static int mMusicPosition;
    private RatingBar mRatingBar;
    private static boolean mIsNeedScore;
    private static boolean mIsNeedSetTime;

    public static MoreMenuBottomDialog newInstance(MusicBean musicBean, int musicPosition, boolean isNeedScore, boolean isNeedSetTime) {
        mMusicBean = musicBean;
        mMusicPosition = musicPosition;
        mIsNeedScore = isNeedScore;
        mIsNeedSetTime = isNeedSetTime;
        musicDao = MusicApplication.getIntstance().getMusicDao();
        return new MoreMenuBottomDialog();
    }

    public void getBottomDialog(Context context) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.bottom_more_menu_dialog, null);

        init(dialog, view);
        initData();
        initListener(context, dialog);
        dialog.show();
    }

    private void initListener(Context context, BottomSheetDialog dialog) {
        mBottomCancel.setOnClickListener(v -> dialog.dismiss());
        mMemuAdapter.setClickListener(((musicPosition, position, musicBean) -> {
            if (position == Constants.NUMBER_ZERO) {
                SpUtil.setAddTodPlayListFlag(context, Constants.NUMBER_ONE);
            }
            RxBus.getInstance().post(new MoreMenuStatus(mMusicPosition, position, musicBean));
            dialog.dismiss();
        }));
        mRatingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            mMusicBean.setSongScore((int) rating);
            musicDao.update(mMusicBean);
        });
        mBehavior.setBottomSheetCallback(new BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void init(BottomSheetDialog dialog, View view) {
        mRatingBar = view.findViewById(R.id.rating_bar);
        mBottomListContent = view.findViewById(R.id.bottom_list_content);
        mBottomCancel = view.findViewById(R.id.bottom_sheet_cancel);
        TextView tvTitle = view.findViewById(R.id.bottom_title);
        mRatingBar.setVisibility(mIsNeedScore ? View.VISIBLE : View.GONE);
        tvTitle.setVisibility(mIsNeedScore ? View.GONE : View.VISIBLE);
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
        mRatingBar.setRating(mMusicBean.getSongScore());
        mMemuAdapter = new MoreMemuAdapter(MenuListUtil.getMenuData(mMusicBean.isFavorite(), mIsNeedSetTime), mMusicBean, mMusicPosition);
        RecyclerView recyclerView = RecyclerFactory.createRecyclerView(4, mMemuAdapter);
        mBottomListContent.addView(recyclerView);
    }

}


