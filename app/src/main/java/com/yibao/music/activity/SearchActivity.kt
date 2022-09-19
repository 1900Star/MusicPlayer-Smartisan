package com.yibao.music.activity

import android.content.Intent
import android.text.Editable
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.jakewharton.rxbinding2.view.RxView
import com.yibao.music.R
import com.yibao.music.adapter.DetailsViewAdapter
import com.yibao.music.base.bindings.BaseBindingActivity
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.listener.OnFlowLayoutClickListener
import com.yibao.music.base.listener.OnMusicItemClickListener
import com.yibao.music.base.listener.TextChangedListener
import com.yibao.music.databinding.ActivitySearchBinding
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog
import com.yibao.music.model.MusicBean
import com.yibao.music.service.MusicPlayService
import com.yibao.music.service.MusicPlayService.AudioBinder
import com.yibao.music.util.*
import com.yibao.music.viewmodel.SearchViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

/**
 * @author lsp
 */
class SearchActivity : BaseBindingActivity<ActivitySearchBinding>(), OnMusicItemClickListener,
    OnFlowLayoutClickListener, View.OnClickListener {
    private val mViewModel: SearchViewModel by lazy { gets(SearchViewModel::class.java) }
    private var mMusicBean: MusicBean? = null
    private var audioBinder: AudioBinder? = null
    private var lyricsFlag = 0
    private var mInputMethodManager: InputMethodManager? = null
    private var mAdapter: DetailsViewAdapter? = null

    // 默认为2 ，按歌曲名搜索。
    private var mPosition = 2
    override fun initView() {
        initRecyclerView(mBinding.recyclerSearch)
    }

    override fun initData() {
        // pageType 0 toolbar上的搜索，1 播放界面点击歌曲名，直接以歌手搜索。
        val pageType = intent.getIntExtra(Constant.PAGE_TYPE, 0)
        audioBinder = MusicActivity.getAudioBinder()
        mBinding.smartisanControlBar.setPbColorAndPreBtnGone()
        // 从PlayActivity过来的
        if (pageType > Constant.NUMBER_ZERO) {
            mMusicBean = intent.getParcelableExtra(Constant.MUSIC_BEAN)
            mBinding.editSearch.setText(mMusicBean!!.artist)
            switchListCategory(4)

        } else {
            // 加载历史记录
            mViewModel.getHistory()

            // 从Toolbar上过来的，主动弹出键盘
//            mInputMethodManager =
//                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            Timer().schedule(timerTask {
//                SoftKeybordUtil.showAndHintSoftInput(
//                    mInputMethodManager,
//                    2,
//                    InputMethodManager.SHOW_FORCED
//                )
//            }, 100)

        }
    }

    override fun onResume() {
        super.onResume()
        // 搜索结果
        mViewModel.searchViewModel.observe(this) { musicList ->
            setData(musicList)
        }
        mViewModel.historyViewModel.observe(this) { historyList ->
            mBinding.flowlayout.setData(historyList)
            mBinding.flowlayout.setItemClickListener { _, bean ->
                mPosition = 2
                mBinding.editSearch.setText(bean.searchContent)
                mBinding.editSearch.setSelection(bean.searchContent.length)
            }
        }
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
            .throttleFirst(1, TimeUnit.SECONDS).subscribe { startPlayActivity() })


    }

    private fun setData(musicList: List<MusicBean>) {

        if (musicList.isEmpty()) {
            mBinding.tvNoSearchResult.visibility = View.VISIBLE
            mBinding.recyclerSearch.visibility = View.GONE
        } else {
            mBinding.tvNoSearchResult.visibility = View.GONE
            mBinding.recyclerSearch.visibility = View.VISIBLE
            // 列表数据
            mAdapter = DetailsViewAdapter(this, musicList, Constant.NUMBER_TEN, "")
            mBinding.recyclerSearch.adapter = mAdapter
            mAdapter!!.setOnItemMenuListener(object :
                BaseBindingAdapter.OnOpenItemMoreMenuListener {
                override fun openClickMoreMenu(position: Int, musicBean: MusicBean) {
                    // 关闭键盘
                    SoftKeybordUtil.showAndHintSoftInput(
                        mInputMethodManager, 1, InputMethodManager.SHOW_FORCED
                    )
                    MoreMenuBottomDialog.newInstance(
                        musicBean, position, false, false
                    ).getBottomDialog(this@SearchActivity)

                }
            })

        }

    }

    override fun initListener() {
        mBinding.tvSearchCancel.setOnClickListener(this)
        mBinding.ivEditClear.setOnClickListener(this)
        mBinding.searchCategoryRoot.tvSearchAll.setOnClickListener(this)
        mBinding.searchCategoryRoot.tvSearchSong.setOnClickListener(this)
        mBinding.searchCategoryRoot.tvSearchArtist.setOnClickListener(this)
        mBinding.searchCategoryRoot.tvSearchAlbum.setOnClickListener(this)
        mBinding.ivClearHistory.setOnClickListener(this)
        mBinding.editSearch.addTextChangedListener(object : TextChangedListener() {
            override fun afterTextChanged(s: Editable) {
                searchMusic(s.toString().trim(), mPosition)
            }
        })
        // 底部控制Bar按钮监听
        mBinding.smartisanControlBar.setClickListener { clickFlag: Int ->
            LogUtil.d(TAG, clickFlag.toString())
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
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.iv_edit_clear -> {
                mBinding.editSearch.setText("")
                mAdapter?.clear()
            }
            R.id.iv_clear_history -> {
                LogUtil.d(TAG, "历史点击")
                // 清空历史记录
            }

            R.id.tv_search_all -> {
                switchListCategory(1)
            }
            R.id.tv_search_song -> {
                switchListCategory(2)
            }
            R.id.tv_search_album -> {
                switchListCategory(3)
            }
            R.id.tv_search_artist -> {
                switchListCategory(4)
            }
            R.id.tv_search_cancel -> {
                SoftKeybordUtil.showAndHintSoftInput(
                    mInputMethodManager, 1, InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
                finish()
            }
        }
    }

    /**
     * @param searchKey 搜索关键字key 、
     * @param position 搜索类别： 1全部 、 2歌曲 、 3专辑 、 4 艺术家
     */
    private fun searchMusic(searchKey: String, position: Int) {
        mBinding.ivEditClear.visibility = if (searchKey.isEmpty()) View.GONE else View.VISIBLE
        mBinding.tvNoSearchResult.visibility = if (searchKey.isEmpty()) View.GONE else View.VISIBLE
        mBinding.flowlayout.visibility = if (searchKey.isEmpty()) View.VISIBLE else View.GONE

        mBinding.searchCategoryRoot.root.visibility =
            if (searchKey.isEmpty()) View.GONE else View.VISIBLE
        mBinding.groupSearch.visibility = if (searchKey.isEmpty()) View.VISIBLE else View.GONE
        if (searchKey.isNotEmpty()) {
            mViewModel.searchMusic(searchKey, position)
        } else {
            // 获取历史记录
            mViewModel.getHistory()
        }
    }

    override fun updateLyricsView(lyricsOK: Boolean, downMsg: String) {
        if (lyricsOK) {
            updateLyric()
        }
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
        mPosition = position
        val searchKey = mBinding.editSearch.text.trim().toString()
        mViewModel.searchMusic(searchKey, position)
        when (position) {
            1 -> {
                setAllCategoryNotNormal()
                mBinding.searchCategoryRoot.tvSearchAll.setTextColor(ColorUtil.wihtle)
                mBinding.searchCategoryRoot.tvSearchAll.setBackgroundResource(R.drawable.btn_category_end_down_selector)
            }
            2 -> {
                setAllCategoryNotNormal()
                mBinding.searchCategoryRoot.tvSearchSong.setTextColor(ColorUtil.wihtle)
                mBinding.searchCategoryRoot.tvSearchSong.setBackgroundResource(R.drawable.btn_category_start_down_selector)
            }
            3 -> {
                setAllCategoryNotNormal()
                mBinding.searchCategoryRoot.tvSearchAlbum.setTextColor(ColorUtil.wihtle)
                mBinding.searchCategoryRoot.tvSearchAlbum.setBackgroundResource(R.drawable.btn_category_middle_down_selector)
            }
            4 -> {
                setAllCategoryNotNormal()
                mBinding.searchCategoryRoot.tvSearchArtist.setBackgroundResource(R.drawable.btn_category_middle_down_selector)
                mBinding.searchCategoryRoot.tvSearchArtist.setTextColor(ColorUtil.wihtle)
            }
            else -> {}
        }
    }

    private fun setAllCategoryNotNormal() {
        mBinding.searchCategoryRoot.tvSearchSong.setTextColor(ColorUtil.textName)
        mBinding.searchCategoryRoot.tvSearchSong.setBackgroundResource(R.drawable.btn_category_start_selector)
        mBinding.searchCategoryRoot.tvSearchAlbum.setTextColor(ColorUtil.textName)
        mBinding.searchCategoryRoot.tvSearchAlbum.setBackgroundResource(R.drawable.btn_category_middle_selector)
        mBinding.searchCategoryRoot.tvSearchArtist.setTextColor(ColorUtil.textName)
        mBinding.searchCategoryRoot.tvSearchArtist.setBackgroundResource(R.drawable.btn_category_middle_selector)
        mBinding.searchCategoryRoot.tvSearchAll.setTextColor(ColorUtil.textName)
        mBinding.searchCategoryRoot.tvSearchAll.setBackgroundResource(R.drawable.btn_category_end_selector)
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


    private fun switchPlayState() {
        if (audioBinder!!.isPlaying) {
            audioBinder!!.pause()
        } else {
            audioBinder!!.start()
        }
        mBinding.smartisanControlBar.animatorOnResume(audioBinder!!.isPlaying)
        mBinding.smartisanControlBar.updatePlayBtnStatus(audioBinder!!.isPlaying)
    }


    override fun startMusicServiceFlag(
        position: Int, pageType: Int, conditon: String
    ) {
        val condition = mBinding.editSearch.text.toString().trim()
        val intent = Intent(this, MusicPlayService::class.java)
        intent.putExtra(Constant.PAGE_TYPE, Constant.NUMBER_TEN)
        intent.putExtra(Constant.CONDITION, condition)
        intent.putExtra(Constant.POSITION, position)
        startService(intent)
    }

    override fun startMusicService(position: Int, mPageType: Int) {}
    override fun onOpenMusicPlayDialogFag() {}
    override fun onPause() {
        super.onPause()
        mBinding.smartisanControlBar.animatorOnPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (audioBinder != null) {
            audioBinder = null
        }
    }

    private fun updateLyric() {
        val lyricList = LyricsUtil.getLyricList(mMusicBean)
        disposableQqLyric()
        if (mQqLyricsDisposable == null) {
            mQqLyricsDisposable =
                Observable.interval(0, 2800, TimeUnit.MICROSECONDS).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe {
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