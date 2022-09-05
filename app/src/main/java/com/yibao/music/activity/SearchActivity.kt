package com.yibao.music.activity

import com.yibao.music.base.BaseTransitionActivity
import com.yibao.music.base.listener.OnMusicItemClickListener
import com.yibao.music.base.listener.OnFlowLayoutClickListener
import com.yibao.music.model.MusicBean
import com.yibao.music.service.MusicPlayService.AudioBinder
import io.reactivex.disposables.Disposable
import android.os.Bundle
import com.yibao.music.activity.MusicActivity
import com.yibao.music.adapter.SearchPagerAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.jakewharton.rxbinding2.view.RxView
import com.yibao.music.model.SearchCategoryBean
import com.yibao.music.R
import com.yibao.music.base.listener.TextChangedListener
import android.text.Editable
import com.yibao.music.view.music.SmartisanControlBar.OnSmartisanControlBarListener
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.yibao.music.base.bindings.BaseBindingActivity
import com.yibao.music.databinding.ActivitySearchBinding
import com.yibao.music.service.MusicPlayService
import com.yibao.music.model.MusicLyricBean
import com.yibao.music.util.*
import com.yibao.music.viewmodel.AlbumViewModel
import com.yibao.music.viewmodel.SearchViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * @author lsp
 */
class SearchActivity : BaseBindingActivity<ActivitySearchBinding>(), OnMusicItemClickListener,
    OnFlowLayoutClickListener, View.OnClickListener {

    private var mMusicBean: MusicBean? = null
    private var audioBinder: AudioBinder? = null
    private var lyricsFlag = 0
    private val mInputMethodManager: InputMethodManager? = null
    private var mDisposableSoftKeyboard: Disposable? = null
    private var mSearchCondition: String = ""
    private var currentCategoryPosition = 0

    override fun initData() {
        // pageType 0 toolbar上的搜索，1 播放界面点击歌曲名，直接以歌手搜索。
        val pageType = intent.getIntExtra(Constant.PAGE_TYPE, 0)
        LogUtil.d(TAG, "搜索标识    $pageType")
        audioBinder = MusicActivity.getAudioBinder()
        mBinding.smartisanControlBar.setPbColorAndPreBtnGone()
        val pagerAdapter: SearchPagerAdapter
        if (pageType > Constant.NUMBER_ZERO) {
            mMusicBean = intent.getParcelableExtra("musicBean")
            mBinding.editSearch.setText(mMusicBean!!.artist)
            mBinding.editSearch.setSelection(mMusicBean!!.artist.length)
            mBinding.searchCategoryRoot.root.visibility = View.VISIBLE
            // ViewPager
            pagerAdapter = SearchPagerAdapter(this, mMusicBean!!.artist, mViewModel)
            switchListCategory(3)
            setMusicInfo(mMusicBean)
            mBinding.ivEditClear.visibility = View.VISIBLE
        } else {
            pagerAdapter = SearchPagerAdapter(this, "", mViewModel)
            // 主动弹出键盘
//            mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            mDisposableSoftKeyboard = Observable.timer(50, TimeUnit.MILLISECONDS)
//                    .subscribe(aLong -> SoftKeybordUtil.showAndHintSoftInput(mInputMethodManager, 2, InputMethodManager.SHOW_FORCED));
        }
        mBinding.vpSearch.adapter = pagerAdapter
        mBinding.vpSearch.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                switchListCategory(position)
            }
        })
    }

    override fun initView() {

    }

    private val mViewModel: SearchViewModel by lazy { gets(SearchViewModel::class.java) }
    override fun initListener() {
        mBinding.editSearch.addTextChangedListener(object : TextChangedListener() {
            override fun afterTextChanged(s: Editable) {
                mSearchCondition = s.toString().trim()
                // 将输入内容发送给 SearchFragment 进行搜索
                if (mSearchCondition.isNotEmpty()) {
                    LogUtil.d(TAG, mSearchCondition)
                    mViewModel.postAlbum(SearchCategoryBean(1, mSearchCondition))
                }
                mBinding.ivEditClear.visibility =
                    if (mSearchCondition.isEmpty()) View.GONE else View.VISIBLE

                mBinding.searchCategoryRoot.root.visibility =
                    if (mSearchCondition.isEmpty()) View.GONE else View.VISIBLE

            }
        })


        mBinding.smartisanControlBar.setClickListener { clickFlag: Int ->
            when (clickFlag) {
                Constant.NUMBER_ONE -> {
                    audioBinder!!.updateFavorite()
                    checkCurrentSongIsFavorite(mMusicBean, null, mBinding.smartisanControlBar)
                }
                Constant.NUMBER_TWO -> audioBinder!!.playPre()
                Constant.NUMBER_THREE -> switchPlayState()
                Constant.NUMBER_FOUR -> audioBinder!!.playNext()
                else -> {}
            }
        }
        mBinding.tvSearchCancel.setOnClickListener(this)
        mBinding.ivEditClear.setOnClickListener(this)
        mBinding.searchCategoryRoot.tvSearchAll.setOnClickListener(this)
        mBinding.searchCategoryRoot.tvSearchSong.setOnClickListener(this)
        mBinding.searchCategoryRoot.tvSearchArtist.setOnClickListener(this)
        mBinding.searchCategoryRoot.tvSearchAlbum.setOnClickListener(this)


    }

    override fun updateLyricsView(lyricsOK: Boolean, downMsg: String) {
        if (lyricsOK) {
            updateLyric()
        }
    }


    override fun onResume() {
        super.onResume()
        if (audioBinder != null) {
            mMusicBean = audioBinder!!.musicBean
            setMusicInfo(mMusicBean)
            checkCurrentSongIsFavorite(mMusicBean, null, mBinding.smartisanControlBar)
            mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder!!.isPlaying)
            mBinding.smartisanControlBar.animatorOnResume(audioBinder!!.isPlaying)
            updateLyric()
            setDuration()
        }
        mCompositeDisposable.add(RxView.clicks(mBinding.smartisanControlBar)
            .throttleFirst(1, TimeUnit.SECONDS)
            .subscribe { o: Any? -> startPlayActivity() })
    }

    override fun updateCurrentPlayInfo(musicItem: MusicBean) {
        mMusicBean = musicItem
        setMusicInfo(musicItem)
        mBinding.smartisanControlBar.initAnimation()
        if (audioBinder != null) {
            setDuration()
            checkCurrentSongIsFavorite(mMusicBean, null, mBinding.smartisanControlBar)
            mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder!!.isPlaying)
            updateLyric()
        }
    }

    override fun updateCurrentPlayProgress() {
        if (audioBinder != null) {
            mBinding.smartisanControlBar.setSongProgress(audioBinder!!.progress)
        }
    }

    private fun setDuration() {
        val duration = audioBinder!!.duration
        mBinding.smartisanControlBar.setMaxProgress(duration)
    }

    private fun setMusicInfo(musicItem: MusicBean?) {
        var musicItem = musicItem
        if (musicItem != null) {
            mBinding.smartisanControlBar.visibility = View.VISIBLE
            musicItem = TitleArtistUtil.getMusicBean(musicItem)
            mBinding.smartisanControlBar.setSongName(musicItem.title)
            mBinding.smartisanControlBar.setSingerName(musicItem.artist)
            mBinding.smartisanControlBar.setAlbulmUrl(FileUtil.getAlbumUrl(musicItem, 1))
        }
        if (audioBinder != null) {
            mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder!!.isPlaying)
            mBinding.smartisanControlBar.initAnimation()
        } else {
            mBinding.smartisanControlBar.visibility = View.GONE
        }
    }

    private fun switchListCategory(position: Int) {
        currentCategoryPosition = position
        mBinding.vpSearch.setCurrentItem(position, false)
        if (mSearchCondition.isNotEmpty()) {
            mViewModel.postAlbum(SearchCategoryBean(position, mSearchCondition))
        }
        when (position) {
            0 -> {
                setAllCategoryNotNormal()
                mBinding.searchCategoryRoot.tvSearchAll.setTextColor(ColorUtil.wihtle)
                mBinding.searchCategoryRoot.tvSearchAll.setBackgroundResource(R.drawable.btn_category_songname_down_selector)
            }
            1 -> {
                setAllCategoryNotNormal()
                mBinding.searchCategoryRoot.tvSearchSong.setTextColor(ColorUtil.wihtle)
                mBinding.searchCategoryRoot.tvSearchSong.setBackgroundResource(R.drawable.btn_category_score_down_selector)
            }
            2 -> {
                setAllCategoryNotNormal()
                mBinding.searchCategoryRoot.tvSearchAlbum.setTextColor(ColorUtil.wihtle)
                mBinding.searchCategoryRoot.tvSearchAlbum.setBackgroundResource(R.drawable.btn_category_score_down_selector)
            }
            3 -> {
                setAllCategoryNotNormal()
                mBinding.searchCategoryRoot.tvSearchArtist.setBackgroundResource(R.drawable.btn_category_views_down_selector)
                mBinding.searchCategoryRoot.tvSearchArtist.setTextColor(ColorUtil.wihtle)
            }
            else -> {}
        }
    }

    private fun setAllCategoryNotNormal() {
        mBinding.searchCategoryRoot.tvSearchAll.setTextColor(ColorUtil.textName)
        mBinding.searchCategoryRoot.tvSearchAll.setBackgroundResource(R.drawable.btn_category_songname_selector)
        mBinding.searchCategoryRoot.tvSearchSong.setTextColor(ColorUtil.textName)
        mBinding.searchCategoryRoot.tvSearchSong.setBackgroundResource(R.drawable.btn_category_score_selector)
        mBinding.searchCategoryRoot.tvSearchAlbum.setTextColor(ColorUtil.textName)
        mBinding.searchCategoryRoot.tvSearchAlbum.setBackgroundResource(R.drawable.btn_category_score_selector)
        mBinding.searchCategoryRoot.tvSearchArtist.setTextColor(ColorUtil.textName)
        mBinding.searchCategoryRoot.tvSearchArtist.setBackgroundResource(R.drawable.btn_category_views_selector)
    }

    override fun refreshBtnAndNotify(playStatus: Int) {
        when (playStatus) {
            0 -> {
                mBinding.smartisanControlBar.animatorOnResume(audioBinder!!.isPlaying)
                mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder!!.isPlaying)
            }
            1 -> checkCurrentSongIsFavorite(mMusicBean, null, mBinding.smartisanControlBar)
            2 -> {
                mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder!!.isPlaying)
                mBinding.smartisanControlBar.animatorOnPause()
            }
            else -> {}
        }
    }

    private val dataFlag = when {

        currentCategoryPosition == 0 || currentCategoryPosition == 1 -> Constant.NUMBER_THREE
        currentCategoryPosition == 2 -> 2
        currentCategoryPosition == 3 -> 1
        else -> 4
    }

    private fun switchPlayState() {
        if (audioBinder!!.isPlaying) {
            audioBinder!!.pause()
        } else {
            audioBinder!!.start()
        }
        mBinding.smartisanControlBar.animatorOnResume(audioBinder!!.isPlaying)
        mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder!!.isPlaying)
    }


    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.tv_search_cancel -> {
                SoftKeybordUtil.showAndHintSoftInput(
                    mInputMethodManager,
                    1,
                    InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
                finish()
            }
            R.id.iv_edit_clear -> {
                mBinding.editSearch.text = null
                findViewById<View>(R.id.search_category_root).visibility = View.GONE
                mBus.post(SearchCategoryBean(Constant.NUMBER_NINE, null))
            }
            R.id.tv_search_all -> {
                switchListCategory(0)
            }
            R.id.tv_search_song -> {
                switchListCategory(1)
            }
            R.id.tv_search_album -> {
                switchListCategory(2)
            }
            R.id.tv_search_artist -> {
                switchListCategory(3)
            }
        }
    }

    override fun startMusicServiceFlag(
        position: Int,
        sortFlag: Int,
        dataFlag: Int,
        queryFlag: String
    ) {
        val intent = Intent(this, MusicPlayService::class.java)
        intent.putExtra("sortFlag", sortFlag)
        intent.putExtra("dataFlag", dataFlag)
        intent.putExtra("queryFlag", queryFlag)
        intent.putExtra("position", position)
        startService(intent)
    }

    override fun startMusicService(position: Int) {}
    override fun onOpenMusicPlayDialogFag() {}
    override fun onPause() {
        super.onPause()
        mBinding.smartisanControlBar.animatorOnPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mDisposableSoftKeyboard != null) {
            mDisposableSoftKeyboard!!.dispose()
            mDisposableSoftKeyboard = null
        }
        if (audioBinder != null) {
            audioBinder = null
        }
    }

    private fun updateLyric() {
        val lyricList = LyricsUtil.getLyricList(mMusicBean)
        disposableQqLyric()
        if (mQqLyricsDisposable == null) {
            mQqLyricsDisposable = Observable.interval(0, 2800, TimeUnit.MICROSECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (lyricList.size > 1 && lyricsFlag < lyricList.size) {
                        //通过集合，播放过的歌词就从集合中删除
                        val lyrBean = lyricList[lyricsFlag]
                        val content = lyrBean.content
                        val progress = audioBinder!!.progress
                        val startTime = lyrBean.startTime
                        if (progress > startTime) {
                            mBinding.smartisanControlBar.setSingerName(content)
                            lyricsFlag++
                        }
                    }
                }
        }
    }

    override fun click(songName: String) {
        mBinding.editSearch.setText(songName)
        mBinding.editSearch.setSelection(songName.length)
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.dialog_push_out)
    }
}