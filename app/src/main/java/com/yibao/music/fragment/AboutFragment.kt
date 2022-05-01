package com.yibao.music.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.yibao.music.R
import com.yibao.music.base.bindings.BaseLazyFragmentDev
import com.yibao.music.base.listener.OnUpdataTitleListener
import com.yibao.music.databinding.AboutFragmentBinding
import com.yibao.music.fragment.dialogfrag.CrashSheetDialog
import com.yibao.music.fragment.dialogfrag.RelaxDialogFragment
import com.yibao.music.fragment.dialogfrag.ScannerConfigDialog
import com.yibao.music.fragment.dialogfrag.TakePhotoBottomSheetDialog
import com.yibao.music.model.MusicBean
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.util.*
import com.yibao.music.view.music.MusicToolBar.OnToolbarClickListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.TimeUnit

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.folder
 * @文件名: AboutFragment
 * @author: Stran
 * @创建时间: 2018/2/9 20:51
 * @描述： {TODO}
 */
class AboutFragment : BaseLazyFragmentDev<AboutFragmentBinding>() {

    private var mCurrentPosition: Long = 0
    override fun initView() {
        mBinding.appbarAbout.musicToolbarList.setToolbarTitle(getString(R.string.about))
        mBinding.appbarAbout.musicToolbarList.setTvEditVisibility(false)
        initData()
        initListener()
    }

    override fun initData() {
        val file = File(Constants.MUSIC_LYRICS_ROOT)
        if (file.exists()) {
            mBinding.tvDeleteErrorLyric.visibility = View.VISIBLE
        }
        val headerFile = FileUtil.getHeaderFile()
        if (FileUtil.getHeaderFile().exists()) {
            setHeaderView(Uri.fromFile(headerFile))
        }
    }

    private fun initListener() {
        mBinding.tvScannerMedia.setOnClickListener { scannerMedia() }
        mBinding.tvShare.setOnClickListener { shareMe() }
        mCompositeDisposable.add(RxView.clicks(mBinding.aboutHeaderIv)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe {
                TakePhotoBottomSheetDialog.newInstance().getBottomDialog(mActivity)
            })
        mCompositeDisposable.add(mBus.toObservableType(Constants.HEADER_PIC_URI, Any::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { o: Any? -> setHeaderView(o as Uri?) })
        mCompositeDisposable.add(RxView.clicks(mBinding.tvBackupsFavorite)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe { backupsFavoriteList() })
        mCompositeDisposable.add(RxView.clicks(mBinding.tvRecoverFavorite)
            .throttleFirst(3, TimeUnit.SECONDS)
            .subscribe { recoverFavoriteList() })
        mCompositeDisposable.add(RxView.clicks(mBinding.tvDeleteErrorLyric)
            .throttleFirst(2, TimeUnit.SECONDS)
            .subscribe { clearErrorLyric() })
        mCompositeDisposable.add(RxView.clicks(mBinding.tvCrashLog)
            .throttleFirst(2, TimeUnit.SECONDS)
            .subscribe { CrashSheetDialog.newInstance().getBottomDialog(mActivity) })
        mBinding.aboutHeaderIv.setOnLongClickListener {
            RelaxDialogFragment.newInstance().show(childFragmentManager, "girlsDialog")
            true
        }
        mBinding.appbarAbout.musicToolbarList.setClickListener(object : OnToolbarClickListener {
            override fun clickEdit() {}
            override fun switchMusicControlBar() {
                switchControlBar()
            }

            override fun clickDelete() {}
        })
    }

    private fun scannerMedia() {
        ScannerConfigDialog.newInstance(false).show(childFragmentManager, "config_scanner")
    }

    private fun shareMe() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_info))
        shareIntent.type = "text/plain"
        startActivity(shareIntent)
    }

    private fun recoverFavoriteList() {
        val musicList = mMusicBeanDao.queryBuilder().list()
        if (FileUtil.getFavoriteFile()) {
            val songInfoMap = HashMap<String, String>(16)
            val stringSet = ReadFavoriteFileUtil.stringToSet()
            for (s in stringSet) {
                val songName = s.substring(0, s.lastIndexOf("T"))
                val favoriteTime = s.substring(s.lastIndexOf("T") + 1)
                songInfoMap[songName] = favoriteTime
            }
            mCompositeDisposable.add(
                Observable.fromIterable(musicList).map { musicBean: MusicBean ->
                    //将歌名截取出来进行比较
                    val favoriteTime = songInfoMap[musicBean.title]
                    if (favoriteTime != null) {
                        musicBean.time = favoriteTime
                        musicBean.setIsFavorite(true)
                        mMusicBeanDao.update(musicBean)
                    }
                    mCurrentPosition++
                }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { currentPostion: Long ->
                        if (currentPostion == (musicList.size - 1).toLong()) {
                            if (mActivity is OnUpdataTitleListener) {
                                (mActivity as OnUpdataTitleListener).checkCurrentFavorite()
                            }
                        }
                    })
        } else {
            ToastUtil.showNotFoundFavoriteFile(mActivity)
        }
    }

    private fun backupsFavoriteList() {
        val list =
            mMusicBeanDao.queryBuilder().where(MusicBeanDao.Properties.IsFavorite.eq(true)).build()
                .list()
        mCompositeDisposable.add(
            Observable.fromIterable(list)
                .map { musicBean: MusicBean ->
                    val songInfo = musicBean.title + "T" + musicBean.addTime
                    ReadFavoriteFileUtil.writeFile(songInfo)
                    songInfo
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { favoriteName: String ->
                    LogUtil.d(
                        mTag,
                        " 更新本地收藏文件==========   $favoriteName"
                    )
                })
        ToastUtil.showFavoriteListBackupsDown(mActivity)
    }

    private fun setHeaderView(uri: Uri?) {
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeStream(mActivity.contentResolver.openInputStream(uri!!))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        mBinding.aboutHeaderIv.setImageBitmap(bitmap)
    }

    private fun clearErrorLyric() {
//        val handler = Handler()
//        ThreadPoolProxyFactory.newInstance().execute {
//            LyricsUtil.clearLyricList()
//            handler.post { ToastUtil.show(mActivity, "错误歌词已删除") }
//        }
                ToastUtil.show(mActivity, "开发中")
    }

    override val isOpenDetail: Boolean
        get() = false

    companion object {
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }
}