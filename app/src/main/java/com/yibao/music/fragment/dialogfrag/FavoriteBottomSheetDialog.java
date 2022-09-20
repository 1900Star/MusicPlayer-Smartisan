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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
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
import com.yibao.music.databinding.FavoriteDialogBinding;
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

import java.util.List;
import java.util.Objects;
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


    private Context mContext;

    private BottomSheetBehavior<View> mBehavior;
    private List<MusicBean> mList;
    private CompositeDisposable mCompositeDisposable;
    private final RxBus
            mBus = RxBus.getInstance();
    private BottomSheetAdapter mAdapter;
    private static String mSongTitle;
    private MusicBeanDao mMusicDao;
    private FavoriteDialogBinding mBinding;

    public static FavoriteBottomSheetDialog newInstance(String songTitle) {
        mSongTitle = songTitle;
        return new FavoriteBottomSheetDialog();
    }

    public void getBottomDialog(Context context) {
        this.mContext = context;
        BottomSheetDialog dialog = new BottomSheetDialog(context);
       mBinding = FavoriteDialogBinding
                .inflate(LayoutInflater.from(context),null,false);
        mCompositeDisposable = new CompositeDisposable();
        mMusicDao = MusicApplication.getInstance().getMusicDao();
        initListener();
        rxData();
        initData(dialog);
        dialog.show();
    }

    private void initData(BottomSheetDialog dialog) {
        dialog.setContentView(mBinding.getRoot());
        dialog.setCancelable(true);
        Window window = dialog.getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mBehavior = BottomSheetBehavior.from((View) mBinding.getRoot().getParent());
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
                  mBinding.recyclerFavorite.setAdapter(mAdapter);
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
        mBinding.tvSize.setText(String.valueOf(favoriteSize));
    }

    private void initListener() {
        LinearLayoutManager manager = new LinearLayoutManager(MusicApplication.getInstance());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.recyclerFavorite.setVerticalScrollBarEnabled(true);
        mBinding.recyclerFavorite.setLayoutManager(manager);
        DividerItemDecoration divider = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        divider.setDrawable(Objects.requireNonNull(ContextCompat.getDrawable(mContext, R.drawable.shape_item_decoration)));
        mBinding.recyclerFavorite.addItemDecoration(divider);

        mBinding.tvPlayAll.setOnClickListener(this);
        mBinding.tvFavorite.setOnClickListener(this);
        mBinding.tvClear.setOnClickListener(this);
        mBinding.tvSize.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_play_all) {
            if (mList != null && mList.size() > 0) {
                Random random = new Random();
                int position = random.nextInt(mList.size());
                playMusic(position);
            } else {
                SnakbarUtil.noFavoriteMusic(mBinding.tvFavorite);
            }
        } else if (id == R.id.tv_size || id == R.id.tv_favorite) {
            backTop();
        } else if (id == R.id.tv_clear) {
            if (mList != null && mList.size() > 0) {
                // playstatus 在这里暂时用来做删除播放列表和收藏列表的标识，在DeletePlayListDialog中使用，2 为播放列表PlayActivity界面 ，3 为收藏列表FavoriteBottomDialog界面。
                PlayListBean bean = new PlayListBean("收藏的所有", (long) Constant.NUMBER_THREE);
                DeletePlayListDialog.newInstance(bean, Constant.NUMBER_THREE, this).show(((AppCompatActivity) mContext).getSupportFragmentManager(), "favoriteList");
            } else {
                SnakbarUtil.noFavoriteMusic(mBinding.tvFavorite);
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
        BottomSheetAdapter adapter = (BottomSheetAdapter) mBinding.recyclerFavorite.getAdapter();
        LinearLayoutManager manager = (LinearLayoutManager) mBinding.recyclerFavorite.getLayoutManager();
        if (adapter != null && manager != null) {
            int positionForSection = adapter.getPositionForSection(0);
            manager.scrollToPositionWithOffset(positionForSection, 0);
        }
    }

    private void playMusic(int position) {
        Intent intent = new Intent();
        intent.setClass(mContext, MusicPlayService.class);
        intent.putExtra(Constant.PAGE_TYPE, Constant.NUMBER_EIGHT);
        intent.putExtra(Constant.POSITION, position);
        LogUtil.d(TAG, "===========      " + position);
        mContext.startService(intent);

    }



    @Override
    public void onRefresh() {

    }
}


