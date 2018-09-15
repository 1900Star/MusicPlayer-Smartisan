package com.yibao.music.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.yibao.music.R;
import com.yibao.music.base.BaseMusicFragment;
import com.yibao.music.fragment.dialogfrag.RelaxDialogFragment;
import com.yibao.music.fragment.dialogfrag.PreviewBigPicDialogFragment;
import com.yibao.music.model.MusicBean;
import com.yibao.music.model.greendao.MusicBeanDao;
import com.yibao.music.util.FileUtil;
import com.yibao.music.util.LogUtil;
import com.yibao.music.util.ReadFavoriteFileUtil;
import com.yibao.music.util.ToastUtil;
import com.yibao.music.view.CircleImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.folder
 * @文件名: AboutMusicFragment
 * @author: Stran
 * @Email: www.strangermy@outlook.com / www.strangermy98@gmail.com
 * @创建时间: 2018/2/9 20:51
 * @描述： {TODO}
 */

public class AboutMusicFragment extends BaseMusicFragment {


    @BindView(R.id.about_header_iv)
    CircleImageView mAboutHeaderIv;

    @BindView(R.id.tv_backups_favorite)
    TextView mTvBackupsFavorite;
    @BindView(R.id.tv_recover_favorite)
    TextView mTvRecoverFavorite;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.about_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initListener();
        return view;
    }

    private void initListener() {
        mDisposable.add(RxView.clicks(mAboutHeaderIv)
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(o -> PreviewBigPicDialogFragment.newInstance("")
                        .show(mFragmentManager, "album")));
        mDisposable.add(RxView.clicks(mTvBackupsFavorite)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> backupsFavoriteList()));
        mDisposable.add(RxView.clicks(mTvRecoverFavorite)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(o -> recoverFavoriteList()));
        mAboutHeaderIv.setOnLongClickListener(view -> {
            RelaxDialogFragment.newInstance().show(mFragmentManager, "girlsDialog");
            return true;
        });
    }

    private void recoverFavoriteList() {
        if (FileUtil.getFavoriteFile()) {
            HashMap<String, String> songInfoMap = new HashMap<>();
            Set<String> stringSet = ReadFavoriteFileUtil.stringToSet();
            for (String s : stringSet) {
                String songName = s.substring(0, s.lastIndexOf("T"));
                String favoriteTime = s.substring(s.lastIndexOf("T") + 1);
                songInfoMap.put(songName, favoriteTime);
            }
            mDisposable.add(Observable.just(mSongList)
                    .flatMap((Function<List<MusicBean>, ObservableSource<MusicBean>>) Observable::fromIterable).map(musicBean -> {
                        //将歌名截取出来进行比较
                        String favoriteTime = songInfoMap.get(musicBean.getTitle());
                        if (favoriteTime != null) {
                            musicBean.setTime(favoriteTime);
                            musicBean.setIsFavorite(true);
                            mMusicBeanDao.update(musicBean);
                        }
                        return musicBean;
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(musicBean -> {
//                                LogUtil.d(musicBean.getTitle());
                    }));
        } else {
            ToastUtil.showNotFoundFavoriteFile(mActivity);
        }
    }

    private void backupsFavoriteList() {
        List<MusicBean> list = mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsFavorite.eq(true)).build().list();
        mDisposable.add(Observable.just(list)
                .flatMap((Function<List<MusicBean>, ObservableSource<MusicBean>>) Observable::fromIterable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(musicBean -> {
                    String songInfo = musicBean.getTitle() + "T" + musicBean.getAddTime();
                    ReadFavoriteFileUtil.writeFile(songInfo);
                }));
        LogUtil.d("lsp", "收藏列表备份完成");
        ToastUtil.showFavoriteListBackupsDown(mActivity);
    }

    public static AboutMusicFragment newInstance() {

        return new AboutMusicFragment();
    }

}
