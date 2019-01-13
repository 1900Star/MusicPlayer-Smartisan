package com.yibao.music.fragment.dialogfrag;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.model.CountdownBean;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.service.CountdownService;
import com.yibao.music.util.Constants;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.ServiceUtil;
import com.yibao.music.util.StringUtil;
import com.yibao.music.view.WheelView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Des：${TODO}
 * Time:2017/8/22 14:11
 *
 * @author Stran
 */
public class NewScannerMusicSheetDialog {
    private Context mContext;
    private BottomSheetBehavior<View> mBehavior;
    private static int mNewMusicSize;

    public static NewScannerMusicSheetDialog newInstance(int newMusicSize) {
        mNewMusicSize = newMusicSize;
        return new NewScannerMusicSheetDialog();
    }

    public void getBottomDialog(Context context) {
        this.mContext = context;
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.countdown_dialog_fragment, null);
        initView(dialog, view);
        initData();
        dialog.show();
    }

    private void initData() {
        MusicBeanDao musicDao = MusicApplication.getIntstance().getMusicDao();
        List<MusicBean> musicBeanList = MusicListUtil.sortMusicList(musicDao.queryBuilder().list(), Constants.NUMBER_ONE);

    }

    private void initView(BottomSheetDialog dialog, View view) {
        dialog.setContentView(view);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        dialog.setCanceledOnTouchOutside(true);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        view.findViewById(R.id.tv_new_music_close).setOnClickListener(v -> mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
    }


}


