package com.yibao.music.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.activity.SplashActivity;
import com.yibao.music.base.BaseLazyFragment;
import com.yibao.music.base.listener.OnUpdataTitleListener;
import com.yibao.music.fragment.dialogfrag.CrashSheetDialog;
import com.yibao.music.fragment.dialogfrag.RelaxDialogFragment;
import com.yibao.music.fragment.dialogfrag.ScannerConfigDialog;
import com.yibao.music.fragment.dialogfrag.TakePhotoBottomSheetDialog;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.Constants;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.LyricsUtil;
import com.yibao.music.util.ReadFavoriteFileUtil;
import com.yibao.music.util.ThreadPoolProxyFactory;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.view.CircleImageView;
import com.yibao.music.view.music.MusicToolBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.folder
 * @文件名: AboutFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.strangermy98@gmail.com
 * @创建时间: 2018/2/9 20:51
 * @描述： {TODO}
 */

public class AboutFragment extends BaseLazyFragment {

    @BindView(R.id.music_toolbar_list)
    MusicToolBar mMusicToolBar;
    @BindView(R.id.about_header_iv)
    CircleImageView mAboutHeaderIv;

    @BindView(R.id.tv_backups_favorite)
    TextView mTvBackupsFavorite;
    @BindView(R.id.tv_recover_favorite)
    TextView mTvRecoverFavorite;
    @BindView(R.id.tv_share)
    TextView mTvShare;
    @BindView(R.id.tv_scanner_media)
    TextView mtScannerMedia;
    @BindView(R.id.tv_crash_log)
    TextView mTvCrashLog;
    @BindView(R.id.tv_delete_error_lyric)
    TextView mTvDeleteErrorLyric;
    private long mCurrentPosition;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.about_fragment);
        mMusicToolBar.setToolbarTitle(getString(R.string.about));
        mMusicToolBar.setTvEditVisibility(false);
        initListener();
    }

    @Override
    protected void onLazyLoadData() {
        super.onLazyLoadData();
        initData();
    }

    private void initData() {
        File file = new File(Constants.MUSIC_LYRICS_ROOT);
        if (file.exists()) {
            mTvDeleteErrorLyric.setVisibility(View.VISIBLE);
        }
        File headerFile = FileUtil.getHeaderFile();
        if (FileUtil.getHeaderFile().exists()) {
            setHeaderView(Uri.fromFile(headerFile));
        }
    }

    private void initListener() {
        mtScannerMedia.setOnClickListener(v -> scannerMedia());
        mTvShare.setOnClickListener(v -> shareMe());
        mCompositeDisposable.add(RxView.clicks(mAboutHeaderIv)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> TakePhotoBottomSheetDialog.newInstance().getBottomDialog(mActivity)));
        mCompositeDisposable.add(mBus.toObservableType(Constants.HEADER_PIC_URI, Object.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> setHeaderView((Uri) o)));
        mCompositeDisposable.add(RxView.clicks(mTvBackupsFavorite)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> backupsFavoriteList()));
        mCompositeDisposable.add(RxView.clicks(mTvRecoverFavorite)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> recoverFavoriteList()));
        mCompositeDisposable.add(RxView.clicks(mTvDeleteErrorLyric)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> clearErrorLyric()));
        mCompositeDisposable.add(RxView.clicks(mTvCrashLog)
                .throttleFirst(2, TimeUnit.SECONDS)
                .subscribe(o -> CrashSheetDialog.newInstance().getBottomDialog(mActivity)));
        mAboutHeaderIv.setOnLongClickListener(view -> {
            RelaxDialogFragment.newInstance().show(mFragmentManager, "girlsDialog");
            return true;
        });
        mMusicToolBar.setClickListener(new MusicToolBar.OnToolbarClickListener() {
            @Override
            public void clickEdit() {
            }

            @Override
            public void switchMusicControlBar() {
                switchControlBar();
            }

            @Override
            public void clickDelete() {

            }
        });
    }


    private void scannerMedia() {
        ScannerConfigDialog.newInstance(false).show(getChildFragmentManager(), "config_scanner");
    }

    private void shareMe() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_info));
        shareIntent.setType("text/plain");
        startActivity(shareIntent);
    }

    private void recoverFavoriteList() {
        List<MusicBean> musicList = mMusicBeanDao.queryBuilder().list();
        if (FileUtil.getFavoriteFile()) {
            HashMap<String, String> songInfoMap = new HashMap<>(16);
            Set<String> stringSet = ReadFavoriteFileUtil.stringToSet();
            for (String s : stringSet) {
                String songName = s.substring(0, s.lastIndexOf("T"));
                String favoriteTime = s.substring(s.lastIndexOf("T") + 1);
                songInfoMap.put(songName, favoriteTime);
            }
            mCompositeDisposable.add(Observable.fromIterable(musicList).map(musicBean -> {
                //将歌名截取出来进行比较
                String favoriteTime = songInfoMap.get(musicBean.getTitle());
                if (favoriteTime != null) {
                    musicBean.setTime(favoriteTime);
                    musicBean.setIsFavorite(true);
                    mMusicBeanDao.update(musicBean);
                }
                return mCurrentPosition++;
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(currentPostion -> {
                        if (currentPostion == musicList.size() - 1) {
                            if (mActivity instanceof OnUpdataTitleListener) {
                                ((OnUpdataTitleListener) mActivity).checkCurrentFavorite();
                            }
                        }
                    }));

        } else {
            ToastUtil.showNotFoundFavoriteFile(mActivity);
        }
    }

    private void backupsFavoriteList() {
        List<MusicBean> list = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsFavorite.eq(true)).build().list();
        mCompositeDisposable.add(Observable.fromIterable(list)
                .map(musicBean -> {
                    String songInfo = musicBean.getTitle() + "T" + musicBean.getAddTime();
                    ReadFavoriteFileUtil.writeFile(songInfo);
                    return songInfo;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favoriteName -> LogUtil.d(TAG, " 更新本地收藏文件==========   " + favoriteName)));
        ToastUtil.showFavoriteListBackupsDown(mActivity);
    }

    private void setHeaderView(Uri uri) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(mActivity.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mAboutHeaderIv.setImageBitmap(bitmap);
    }

    private void clearErrorLyric() {
        Handler handler = new Handler();
        ThreadPoolProxyFactory.newInstance().execute(() -> {
            LyricsUtil.clearLyricList();
            handler.post(() -> ToastUtil.show(mActivity, "错误歌词已删除"));

        });
    }

    public static AboutFragment newInstance() {

        return new AboutFragment();
    }


    @Override
    protected boolean getIsOpenDetail() {
        return false;
    }
}
