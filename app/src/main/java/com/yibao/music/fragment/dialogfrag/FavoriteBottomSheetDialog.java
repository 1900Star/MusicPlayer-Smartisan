package com.yibao.music.fragment.dialogfrag;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yibao.music.MusicApplication;
import com.yibao.music.R;
import com.yibao.music.adapter.BottomSheetAdapter;
import com.yibao.music.base.factory.RecyclerFactory;
import com.yibao.music.base.listener.OnCheckFavoriteListener;
import com.yibao.music.model.AddAndDeleteListBean;
import com.yibao.music.model.BottomSheetStatus;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.MusicInfo;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.service.AudioPlayService;
import com.yibao.music.service.AudioServiceConnection;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author：Sid
 * Des：${TODO}
 * Time:2017/8/22 14:11
 *
 * @author Stran
 */
public class FavoriteBottomSheetDialog
        implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private LinearLayout mBottomListContent;
    private TextView mBottomListColection;
    private TextView mBottomListClear;
    private TextView mBottomListTitleSize;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private BottomSheetBehavior<View> mBehavior;
    private List<MusicBean> mList = new ArrayList<>();
    private CompositeDisposable
            mDisposable = new CompositeDisposable();
    private RxBus
            mBus = RxBus.getInstance();
    private ViewPager mBottomViewPager;
    // ViewPager使用
    private List<List<MusicBean>> mListList = new ArrayList<>();
    private BottomSheetAdapter mAdapter;

    public static FavoriteBottomSheetDialog newInstance() {

        return new FavoriteBottomSheetDialog();
    }

    public void getBottomDialog(Context context) {
        this.mContext = context;

//        this.mList = musicDao.queryBuilder().where(MusicBeanDao.Properties.IsFavorite.eq(true)).build().list();
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_list_dialog, null);
        initView(view);
        initListener();
        rxData();
        initData(dialog, view);
        dialog.show();
    }

    private void initData(BottomSheetDialog dialog, View view) {
//        mListList.add(MusicListUtil.sortMusicAddTime(mList, Constants.NUMBER_TWO));
        // ViewPager 显示多个列表
//        BottomPagerAdapter bottomPagerAdapter = new BottomPagerAdapter(mContext, mListList);
//        mBottomViewPager.setAdapter(bottomPagerAdapter);
//        mRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());
        dialog.setContentView(view);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        dialog.setCanceledOnTouchOutside(true);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
    }

    //    接收BottomSheetAdapter发过来的当前点击Item的Position

    private void rxData() {
        mDisposable.add(MusicListUtil.getFavoriteList().observeOn(AndroidSchedulers.mainThread()).subscribe(musicBeanList -> {
            mList.addAll(musicBeanList);
            String sheetTitle = StringUtil.getBottomSheetTitle(musicBeanList.size());
            mBottomListTitleSize.setText(sheetTitle);
            List<MusicBean> beanList = MusicListUtil.sortMusicAddTime(musicBeanList, Constants.NUMBER_TWO);
            mAdapter = new BottomSheetAdapter(beanList);
            mRecyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, mAdapter);
            mBottomListContent.addView(mRecyclerView);
        }));

        mDisposable.add(mBus.toObserverable(BottomSheetStatus.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bean -> FavoriteBottomSheetDialog.this.playMusic(bean.getPosition())));
        // 清空收藏列表
        mDisposable.add(mBus.toObserverable(AddAndDeleteListBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(bean -> {
                    if (bean.getOperationType() == Constants.NUMBER_THRRE) {
                        clearAllFavoriteMusic();
                        if (mContext != null) {
                            mContext = null;
                        }
                    }
                }));

    }

    private void initListener() {
        mBottomListColection.setOnClickListener(this);
        mBottomListClear.setOnClickListener(this);
        mBottomListTitleSize.setOnClickListener(this);
        mBottomViewPager.addOnPageChangeListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bottom_sheet_bar_play:
                if (mList != null && mList.size() > 0) {
                    Random random = new Random();
                    int position = random.nextInt(mList.size());
                    playMusic(position);
                } else {
                    SnakbarUtil.noFavoriteMusic(mBottomListClear);
                }
                break;
            case R.id.bottom_list_title_size:
                backTop();
                break;
            case R.id.bottom_sheet_bar_clear:
                if (mList != null && mList.size() > 0) {
                    // playstatus 在这里暂时用来做删除播放列表和收藏列表的标识，2 为播放列表PlayActivity界面 ，3 为收藏列表MusicBottomDialog界面。
                    PlayListBean bean = new PlayListBean("收藏的所有", (long) Constants.NUMBER_THRRE);
                    DeletePlayListDialog.newInstance(bean, Constants.NUMBER_THRRE).show(((Activity) mContext).getFragmentManager(), "favoriteList");
                } else {
                    SnakbarUtil.noFavoriteMusic(mBottomListClear);
                }
                break;
            default:
                break;
        }
    }


    private void clearAllFavoriteMusic() {
        mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        mDisposable.add(Observable.fromIterable(mList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicBean -> {
                    musicBean.setIsFavorite(false);
                    MusicApplication.getIntstance().getMusicDao()
                            .update(musicBean);

                    if (mContext instanceof OnCheckFavoriteListener) {
                        ((OnCheckFavoriteListener) mContext).updataFavoriteStatus();
                    }
                }));
    }

    private void backTop() {
        BottomSheetAdapter adapter = (BottomSheetAdapter) mRecyclerView.getAdapter();
        int positionForSection = adapter.getPositionForSection(0);
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        manager.scrollToPositionWithOffset(positionForSection, 0);
    }

    private void playMusic(int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, AudioPlayService.class);
        intent.putExtra("sortFlag", Constants.NUMBER_EIGHT);
        intent.putExtra("position", position);
        AudioServiceConnection connection = new AudioServiceConnection();
        mContext.bindService(intent, connection, Service.BIND_AUTO_CREATE);
        mContext.startService(intent);
        SpUtil.setMusicDataListFlag(mContext, Constants.NUMBER_EIGHT);
    }


    private void initView(View view) {
        mBottomListContent = view.findViewById(R.id.bottom_list_content);
        mBottomListColection = view.findViewById(R.id.bottom_sheet_bar_play);
        mBottomListClear = view.findViewById(R.id.bottom_sheet_bar_clear);
        mBottomListTitleSize = view.findViewById(R.id.bottom_list_title_size);
        mBottomViewPager = view.findViewById(R.id.bottom_vp);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

        LogUtil.d("==========退回客户   " + position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}


