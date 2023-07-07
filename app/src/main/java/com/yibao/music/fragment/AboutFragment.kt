package com.yibao.music.fragment

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.storage.StorageManager
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.yibao.music.R
import com.yibao.music.base.bindings.BaseMusicFragmentDev
import com.yibao.music.base.listener.OnScanConfigListener
import com.yibao.music.base.listener.OnUpdateTitleListener
import com.yibao.music.databinding.AboutFragmentBinding
import com.yibao.music.fragment.dialogfrag.*
import com.yibao.music.model.MusicBean
import com.yibao.music.model.greendao.MusicBeanDao
import com.yibao.music.util.*
import com.yibao.music.view.music.MusicToolBar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.io.FileNotFoundException
import java.util.Locale
import java.util.Locale.LanguageRange

/**
 * @项目名： ArtisanMusic
 * @包名： com.yibao.music.folder
 * @文件名: AboutFragment
 * @author: Stran
 * @创建时间: 2018/2/9 20:51
 * @描述： {TODO}
 */
class AboutFragment : BaseMusicFragmentDev<AboutFragmentBinding>(), OnScanConfigListener {
    override fun initView() {
        mBinding.musicBar.setToolbarTitle(getString(R.string.about))
        initData()
        initListener()
    }

    override fun initData() {
        val file = File(Constant.MUSIC_LYRICS_ROOT)
        if (file.exists()) {
            mBinding.tvDeleteErrorLyric.visibility = View.VISIBLE
        }
        val headerFile = FileUtil.getHeaderFile()
        if (FileUtil.getHeaderFile().exists()) {
            setHeaderView(Uri.fromFile(headerFile))
        }
    }

    private fun initListener() {
        // 手动扫描
        mBinding.tvScannerMedia.setOnClickListener {
            ScannerConfigDialog.newInstance(false, this)
                .show(childFragmentManager, "config_scanner")
        }
        // 分享
        mBinding.tvShare.setOnClickListener { shareMe() }
        // 头像 、拍照
        mBinding.aboutHeaderIv.setOnClickListener {
            takePhoto()
        }
        // 设置头像
        mCompositeDisposable.add(mBus.toObservableType(Constant.HEADER_PIC_URI, Uri::class.java)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { o: Uri -> setHeaderView(o) })

        mBinding.tvBackupsFavorite.setOnClickListener {
            backupsFavoriteList()
        }

        mBinding.tvRecoverFavorite.setOnClickListener {
            recoverFavoriteList()
        }
        mBinding.tvDeleteErrorLyric.setOnClickListener {
            clearErrorLyric()
        }

        mBinding.tvCrashLog.setOnClickListener {
            CrashSheetDialog.newInstance().getBottomDialog(mActivity)
        }

        mBinding.aboutHeaderIv.setOnLongClickListener {
            RelaxDialogFragment.newInstance().show(childFragmentManager, "girlsDialog")
            true
        }
        mBinding.musicBar.setClickListener(object : MusicToolBar.OnToolbarClickListener {
            override fun clickEdit() {

            }

            override fun switchMusicControlBar() {
                switchControlBar()
            }

            override fun clickDelete() {

            }
        })
        mBinding.tvSwitchLanguage.setOnClickListener {
            LogUtil.d(mTag, "切换语言")
            showLanguage()
        }
    }

    private fun showLanguage() {

        val arr = arrayOf(getString(R.string.zh), getString(R.string.us))
        val dialog =
            AlertDialog.Builder(requireActivity()).setItems(arr) { _: DialogInterface?, i: Int ->
                when (i) {
                    0 -> switchLanguage("zh")
                    1 -> switchLanguage("en")
                }
            }.create()
        dialog.show()
    }


    private fun switchLanguage(language: String) {
        mSp.putValues(SpUtils.ContentValue(Constant.LANGUAGE, language))
        requireActivity().recreate()
    }

    private fun takePhoto() {
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        LogUtil.d(mTag, "相机权限获取结果   $granted")
        if (granted) {
            TakePhotoBottomSheetDialog.newInstance().getBottomDialog(mActivity)
        } else {
            PermissionsDialog.newInstance(getString(R.string.camera_permission))
                .show(childFragmentManager, "permissions")
        }
    }

    private fun shareMe() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mActivity.title)
        shareIntent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.app_info))
        shareIntent.type = "text/plain"
        startActivity(shareIntent)
    }

    private var mCurrentPosition = 0
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
            mCompositeDisposable.add(Observable.fromIterable(musicList)
                .map { musicBean: MusicBean ->
                    //将歌名截取出来进行比较
                    val favoriteTime = songInfoMap[musicBean.title]
                    if (favoriteTime != null) {
                        musicBean.time = favoriteTime
                        musicBean.setIsFavorite(true)
                        mMusicBeanDao.update(musicBean)
                    }
                    mCurrentPosition++
                }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe { currentPosition: Int ->
                    if (currentPosition == musicList.size - 1) {
                        if (mActivity is OnUpdateTitleListener) {
                            (mActivity as OnUpdateTitleListener).checkCurrentFavorite()
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
        mCompositeDisposable.add(Observable.fromIterable(list).map { musicBean: MusicBean ->
            val songInfo = musicBean.title + "T" + musicBean.addTime
            ReadFavoriteFileUtil.writeFile(songInfo)
            songInfo
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe { favoriteName: String ->
                LogUtil.d(
                    mTag, " 更新本地收藏文件==========   $favoriteName"
                )
            })
        ToastUtil.showFavoriteListBackupsDown(mActivity)
    }

    private fun setHeaderView(uri: Uri) {
        var bitmap: Bitmap? = null
        try {
            bitmap = BitmapFactory.decodeStream(mActivity.contentResolver.openInputStream(uri))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        mBinding.aboutHeaderIv.setImageBitmap(bitmap)
    }

    private fun clearErrorLyric() {
        val handler = Handler(Looper.getMainLooper())
        ThreadPoolProxyFactory.newInstance().execute {
            LyricsUtil.clearLyricList()
            handler.post { ToastUtil.show(mActivity, "错误歌词已删除") }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(): AboutFragment {
            return AboutFragment()
        }
    }

    override fun scanMusic(isAutoScan: Boolean) {
        LogUtil.d(mTag, "关于界面扫描   $isAutoScan")


    }
}