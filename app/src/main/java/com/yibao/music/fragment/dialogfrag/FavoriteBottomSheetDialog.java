package com.yibao.music.fragment.dialogfrag;

import android.content.Context;
import android.content.Intent;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.PlayListBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.service.MusicPlayService;
import com.yibao.music.util.Constants;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SpUtil;
import com.yibao.music.util.StringUtil;

import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Des：${TODO}
 * Time:2017/8/22 14:11
 *
 * @author Stran
 */
public class FavoriteBottomSheetDialog
        implements View.OnClickListener {
    private LinearLayout mBottomListContent;
    private TextView mBottomListColection;
    private TextView mBottomListClear;
    private TextView mBottomListTitleSize;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private BottomSheetBehavior<View> mBehavior;
    private List<MusicBean> mList;
    private CompositeDisposable mCompositeDisposable;
    private RxBus
            mBus = RxBus.getInstance();
    private BottomSheetAdapter mAdapter;
    private static String mSongTitle;
    private MusicBeanDao mMusicDao;

    public static FavoriteBottomSheetDialog newInstance(String songTitle) {
        mSongTitle = songTitle;
        return new FavoriteBottomSheetDialog();
    }

    public void getBottomDialog(Context context) {
        this.mContext = context;
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context)
                .inflate(R.layout.bottom_sheet_list_dialog, null);
        mCompositeDisposable = new CompositeDisposable();
        mMusicDao = MusicApplication.getIntstance().getMusicDao();
        initView(view);
        initListener();
        rxData();
        initData(dialog, view);
        dialog.show();
    }

    private void initData(BottomSheetDialog dialog, View view) {
        dialog.setContentView(view);
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        dialog.setCanceledOnTouchOutside(true);
        dialog.setOnCancelListener(dialog12 -> FavoriteBottomSheetDialog.this.clearDisposable());
    }

    private void clearDisposable() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }


    private void rxData() {
        mCompositeDisposable.add(MusicListUtil.getFavoriteList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicBeanList -> {
                    mList = musicBeanList;
                    String sheetTitle = StringUtil.getBottomSheetTitle(musicBeanList.size());
                    mBottomListTitleSize.setText(sheetTitle);
                    mAdapter = new BottomSheetAdapter(musicBeanList);
                    mRecyclerView = RecyclerFactory.creatRecyclerView(Constants.NUMBER_ONE, mAdapter);
                    mBottomListContent.addView(mRecyclerView);
                }));
        //    接收BottomSheetAdapter发过来的当前点击Item的Position
        mCompositeDisposable.add(mBus.toObservableType(Constants.FAVORITE_POSITION, Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> FavoriteBottomSheetDialog.this.playMusic((Integer) o)));
        mCompositeDisposable.add(mBus.toObserverable(AddAndDeleteListBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(bean -> {
                    if (bean.getOperationType() == Constants.NUMBER_THRRE) {
                        // 清空收藏列表
                        clearAllFavoriteMusic();
                    } else if (bean.getOperationType() == Constants.NUMBER_FIEV) {
                        // 侧滑删除收藏歌曲
                        mAdapter.notifyDataSetChanged();
                        mList.remove(bean.getPosition());
                        setTitle(mList);
                        checkCurrentFavorite(bean.getSongTitle());
                        if (mList.size() == Constants.NUMBER_ZERO) {
                            mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }
                }));
    }

    private void setTitle(List<MusicBean> musicBeanList) {
        String sheetTitle = StringUtil.getBottomSheetTitle(musicBeanList.size());
        mBottomListTitleSize.setText(sheetTitle);
    }

    private void initListener() {
        mBottomListColection.setOnClickListener(this);
        mBottomListClear.setOnClickListener(this);
        mBottomListTitleSize.setOnClickListener(this);
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
                    // playstatus 在这里暂时用来做删除播放列表和收藏列表的标识，在DeletePlayListDialog中使用，2 为播放列表PlayActivity界面 ，3 为收藏列表FavoriteBottomDialog界面。
                    PlayListBean bean = new PlayListBean("收藏的所有", (long) Constants.NUMBER_THRRE);
                    DeletePlayListDialog.newInstance(bean, Constants.NUMBER_THRRE).show(((AppCompatActivity) mContext).getSupportFragmentManager(), "favoriteList");
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
        mCompositeDisposable.add(Observable.fromIterable(mList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicBean -> {
                    musicBean.setIsFavorite(false);
                    mMusicDao.update(musicBean);
                    checkCurrentFavorite(musicBean.getTitle());
                }));
    }

    private void checkCurrentFavorite(String songTitle) {
        if (mSongTitle.equals(songTitle)) {
            if (mContext instanceof OnCheckFavoriteListener) {
                ((OnCheckFavoriteListener) mContext).updataFavoriteStatus();
            }
        }
    }

    private void backTop() {
        BottomSheetAdapter adapter = (BottomSheetAdapter) mRecyclerView.getAdapter();
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        if (adapter != null && manager != null) {
            int positionForSection = adapter.getPositionForSection(0);
            manager.scrollToPositionWithOffset(positionForSection, 0);
        }
    }

    private void playMusic(int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, MusicPlayService.class);
        intent.putExtra("sortFlag", Constants.NUMBER_EIGHT);
        intent.putExtra("position", position);
        LogUtil.d("===========      " + position);
        mContext.startService(intent);
        SpUtil.setSortFlag(mContext, Constants.NUMBER_EIGHT);
    }


    private void initView(View view) {
        mBottomListContent = view.findViewById(R.id.bottom_favorite_root);
        mBottomListColection = view.findViewById(R.id.bottom_sheet_bar_play);
        mBottomListClear = view.findViewById(R.id.bottom_sheet_bar_clear);
        mBottomListTitleSize = view.findViewById(R.id.bottom_list_title_size);
    }

}


