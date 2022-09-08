package com.yibao.music.fragment.dialogfrag;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import com.yibao.music.util.Constant;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.MusicListUtil;
import com.yibao.music.util.RxBus;
import com.yibao.music.util.SnakbarUtil;
import com.yibao.music.util.SpUtils;

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
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private String TAG = " ==== " + FavoriteBottomSheetDialog.class.getSimpleName() + "  ";
    private LinearLayout mBottomListContent;
    private TextView mBottomListColection;
    private TextView mBottomListClear;
    private TextView mBottomListTitleSize;
    private TextView mBottomListTitle;
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
        mMusicDao = MusicApplication.getInstance().getMusicDao();
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
                    setTitle(musicBeanList.size());
                    mAdapter = new BottomSheetAdapter(musicBeanList);
                    mRecyclerView = RecyclerFactory.createRecyclerView(Constant.NUMBER_ONE, mAdapter);
                    mBottomListContent.addView(mRecyclerView);
                }));
        //    接收BottomSheetAdapter发过来的当前点击Item的Position
        mCompositeDisposable.add(mBus.toObservableType(Constant.FAVORITE_POSITION, Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> FavoriteBottomSheetDialog.this.playMusic((Integer) o)));
        mCompositeDisposable.add(mBus.toObserverable(AddAndDeleteListBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(bean -> {
                    if (bean.getOperationType() == Constant.NUMBER_THREE) {
                        // 清空收藏列表
                        clearAllFavoriteMusic();
                    } else if (bean.getOperationType() == Constant.NUMBER_FIVE) {
                        // 侧滑删除收藏歌曲
                        mAdapter.notifyDataSetChanged();
                        mList.remove(bean.getPosition());
                        setTitle(mList.size());
                        checkCurrentFavorite(bean.getSongTitle());
                        if (mList.size() == Constant.NUMBER_ZERO) {
                            mBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        }
                    }
                }));
    }

    private void setTitle(int favoriteSize) {
        mBottomListTitleSize.setText(String.valueOf(favoriteSize));
    }

    private void initListener() {
        mBottomListColection.setOnClickListener(this);
        mBottomListClear.setOnClickListener(this);
        mBottomListTitleSize.setOnClickListener(this);
        mBottomListTitle.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.bottom_sheet_bar_play) {
            if (mList != null && mList.size() > 0) {
                Random random = new Random();
                int position = random.nextInt(mList.size());
                playMusic(position);
            } else {
                SnakbarUtil.noFavoriteMusic(mBottomListClear);
            }
        } else if (id == R.id.tv_bottom_favorite || id == R.id.bottom_list_title_size) {
            backTop();
        } else if (id == R.id.bottom_sheet_bar_clear) {
            if (mList != null && mList.size() > 0) {
                // playstatus 在这里暂时用来做删除播放列表和收藏列表的标识，在DeletePlayListDialog中使用，2 为播放列表PlayActivity界面 ，3 为收藏列表FavoriteBottomDialog界面。
                PlayListBean bean = new PlayListBean("收藏的所有", (long) Constant.NUMBER_THREE);
                DeletePlayListDialog.newInstance(bean, Constant.NUMBER_THREE, this).show(((AppCompatActivity) mContext).getSupportFragmentManager(), "favoriteList");
            } else {
                SnakbarUtil.noFavoriteMusic(mBottomListClear);
            }
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
        intent.putExtra("sortFlag", Constant.NUMBER_EIGHT);
        intent.putExtra("position", position);
        LogUtil.d(TAG, "===========      " + position);
        mContext.startService(intent);
        SpUtils sp = new SpUtils(mContext.getApplicationContext(), Constant.MUSIC_CONFIG);
        sp.putValues(new SpUtils.ContentValue(Constant.MUSIC_DATA_FLAG,Constant.NUMBER_EIGHT));

    }


    private void initView(View view) {
        mBottomListContent = view.findViewById(R.id.bottom_favorite_root);
        mBottomListColection = view.findViewById(R.id.bottom_sheet_bar_play);
        mBottomListClear = view.findViewById(R.id.bottom_sheet_bar_clear);
        mBottomListTitleSize = view.findViewById(R.id.bottom_list_title_size);
        mBottomListTitle = view.findViewById(R.id.tv_bottom_favorite);
    }

    @Override
    public void onRefresh() {

    }
}


