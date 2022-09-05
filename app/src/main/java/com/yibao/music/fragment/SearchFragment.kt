package com.yibao.music.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.yibao.music.MusicApplication
import com.yibao.music.R
import com.yibao.music.activity.PlayListActivity
import com.yibao.music.adapter.DetailsViewAdapter
import com.yibao.music.base.BaseObserver
import com.yibao.music.base.bindings.BaseBindingAdapter
import com.yibao.music.base.bindings.BaseBindingFragment
import com.yibao.music.base.factory.RecyclerFactory
import com.yibao.music.base.listener.OnFlowLayoutClickListener
import com.yibao.music.databinding.SearchFragmentBinding
import com.yibao.music.fragment.dialogfrag.MoreMenuBottomDialog
import com.yibao.music.model.MoreMenuStatus
import com.yibao.music.model.MusicBean
import com.yibao.music.model.SearchCategoryBean
import com.yibao.music.model.SearchHistoryBean
import com.yibao.music.model.greendao.SearchHistoryBeanDao
import com.yibao.music.util.Constant
import com.yibao.music.util.LogUtil
import com.yibao.music.util.MusicDaoUtil
import com.yibao.music.util.SnakbarUtil
import com.yibao.music.viewmodel.AlbumViewModel
import com.yibao.music.viewmodel.SearchViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * @author: Lsp
 * @ Name:    SearchFragment
 * @ GitHub:  https://github.com/1900Star
 * @ Time:    2019/3/16/ 16:20
 * @ Des:     TODO
 */
class SearchFragment : BaseBindingFragment<SearchFragmentBinding>() {
    private lateinit var mSearchDetailAdapter: DetailsViewAdapter
    private var mDisposableSearch: Disposable? = null
    private var mDisposableMoreMenu: Disposable? = null
    private var mIsFirst = false
    private var mPosition = 0
    private lateinit var mSearchDao: SearchHistoryBeanDao
    private val musicList = ArrayList<MusicBean>()

    override fun initView() {
        initData()
        initListener()
        initRecyclerView(mBinding.recyclerSearch)
    }

    override fun initData() {
        mSearchDao = MusicApplication.getInstance().searchDao
        // 搜索记录
        val searchList = mSearchDao.queryBuilder().list()
        if (searchList != null && searchList.size > 0) {
            mBinding.groupSearch.visibility = View.VISIBLE
            searchList.sortBy { it.searchTime }
            mBinding.flowlayout.setData(searchList)
        }
        // 列表数据
        mSearchDetailAdapter = DetailsViewAdapter(mContext, musicList, Constant.NUMBER_THREE)
//        mBinding.recyclerSearch.adapter = mSearchDetailAdapter
        if (arguments != null) {
            mPosition = requireArguments().getInt(Constant.POSITION)
            val searchArtist = requireArguments().getString("searchArtist").toString()
            setFlagAndSearch(searchArtist, 1)
        }
    }


    override fun onResume() {
        super.onResume()
//        rxBusData()

        mViewModel.searchViewModel.observe(this) { bean ->
            if (bean.searchCondition.isNotEmpty()) {
                mPosition = bean.categoryFlag
                searchMusic(bean.searchCondition)
            }
//            searchSong(bean)
        }
    }

    private fun rxBusData() {

        if (mDisposableMoreMenu == null) {
            mDisposableMoreMenu = mBus.toObserverable(MoreMenuStatus::class.java)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { moreMenuStatus: MoreMenuStatus -> moreMenu(moreMenuStatus) }
        }
    }

    private fun initListener() {

        mSearchDetailAdapter.setOnItemMenuListener(object :
            BaseBindingAdapter.OnOpenItemMoreMenuListener {
            override fun openClickMoreMenu(position: Int, musicBean: MusicBean) {
                MoreMenuBottomDialog.newInstance(
                    musicBean,
                    position,
                    false,
                    false
                ).getBottomDialog(requireActivity())

            }
        })

        mBinding.ivSearchClear.setOnClickListener {
            mSearchDao.deleteAll()
            mBinding.flowlayout.removeAllViews()
            mBinding.groupSearch.visibility = View.INVISIBLE
            mBinding.recyclerSearch.visibility = View.INVISIBLE
        }
        mBinding.flowlayout.setItemClickListener { position: Int, bean: SearchHistoryBean ->
            initSearch(
                bean.searchContent
            )
        }
    }

    /**
     * 点击搜索记录
     *
     * @param searchContent 搜索内容
     */
    private fun initSearch(searchContent: String) {
        // 填入输入框
        if (mContext is OnFlowLayoutClickListener) {
            (mContext as OnFlowLayoutClickListener).click(searchContent)
        }
        searchMusic(searchContent)
    }

    private fun clearAdapter() {
        mSearchDetailAdapter.clear()
    }

    /**
     * 搜索音乐
     *
     * @param searchKey 搜索关键字
     */
    private fun searchMusic(searchKey: String) {
        LogUtil.d(mTag, "====key#####################################======= $searchKey")

        if (searchKey.length > 0) {
            MusicDaoUtil.getSearchResult(mMusicBeanDao, searchKey, mPosition)
                .observeOn(AndroidSchedulers.mainThread()).subscribe(object :
                    BaseObserver<List<MusicBean>>() {
                    override fun onNext(list: List<MusicBean>) {
                        list.forEach {
                            when (mPosition) {
                                0 -> {

                                }
                                1 -> {

                                }
                                2 -> {

                                }
                                3 -> {

                                }
                            }
                            LogUtil.d(mTag, it.toString())
                        }
                        musicList.clear()
                        musicList.addAll(list)
                        mSearchDetailAdapter.setNewData(list)
                        mBinding.groupSearch.visibility = View.GONE
                        mBinding.tvNoSearchResult.visibility = View.GONE
                        mBinding.recyclerSearch.visibility = View.VISIBLE

                    }

                    override fun onError(e: Throwable) {
                        if (mIsFirst) {
                            mBinding.groupSearch.visibility = View.GONE
                            mBinding.recyclerSearch.visibility = View.GONE
                            mBinding.tvNoSearchResult.visibility = View.VISIBLE
                        }

                    }
                })
        }


    }

    private fun historyViewVisibility() {
        val historyList = mSearchDao.queryBuilder().list()
        if (historyList != null && historyList.size > 0) {
            mBinding.groupSearch.visibility = View.VISIBLE
            mBinding.tvNoSearchResult.visibility = View.GONE
            with(historyList) { sort() }
            mBinding.flowlayout.clearView(historyList)
        } else {
            mBinding.groupSearch.visibility = View.GONE
        }
    }


    private fun searchSong(categoryBean: SearchCategoryBean) {
        LogUtil.d(mTag, categoryBean.searchCondition)
        val categoryFlag = categoryBean.categoryFlag
        val condition = categoryBean.searchCondition
        if (condition != null) {
            mIsFirst = true
        }
        when (categoryFlag) {
            0 -> setFlagAndSearch(condition, Constant.NUMBER_THREE)
            1 -> setFlagAndSearch(condition, Constant.NUMBER_THREE)
            2 -> setFlagAndSearch(condition, Constant.NUMBER_TWO)
            3 -> setFlagAndSearch(condition, Constant.NUMBER_ONE)
            4 -> setFlagAndSearch(condition, Constant.NUMBER_FOUR)
            9 -> {
                clearAdapter()
                mBinding.recyclerSearch.visibility = View.INVISIBLE
                historyViewVisibility()
            }
            10 -> {
                historyViewVisibility()
                clearAdapter()
                mBinding.recyclerSearch.visibility = View.INVISIBLE
                mBinding.tvNoSearchResult.visibility = View.GONE
            }
            else -> {}
        }
    }

    private fun setFlagAndSearch(condition: String, numberTwo: Int) {
        mSearchDetailAdapter.setDataFlag(numberTwo)
        searchMusic(condition)
    }

    private fun moreMenu(moreMenuStatus: MoreMenuStatus) {
        val musicBean = moreMenuStatus.musicBean
        when (moreMenuStatus.position) {
            Constant.NUMBER_ZERO -> {
                val intent = Intent(mContext, PlayListActivity::class.java)
                intent.putExtra(Constant.SONG_NAME, musicBean.title)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.dialog_push_in, 0)
            }
            Constant.NUMBER_ONE -> SnakbarUtil.keepGoing(mBinding.ivSearchClear)
            Constant.NUMBER_TWO -> {}
            Constant.NUMBER_THREE -> SnakbarUtil.keepGoing(mBinding.ivSearchClear)
            Constant.NUMBER_FOUR ->                 // 刷新搜索列表, 后续完成
                mMusicBeanDao.delete(moreMenuStatus.musicBean)
            else -> {}
        }
    }

    override fun onPause() {
        super.onPause()
        if (mDisposableSearch != null) {
            mDisposableSearch!!.dispose()
            mDisposableSearch = null
        }
        if (mDisposableMoreMenu != null) {
            mDisposableMoreMenu!!.dispose()
            mDisposableMoreMenu = null
        }
    }

    companion object {
        lateinit var mViewModel: SearchViewModel
        fun newInstance(
            position: Int,
            artist: String,
            viewModel: SearchViewModel
        ): SearchFragment {
            mViewModel = viewModel
            val args = Bundle()
            val fragment = SearchFragment()
            args.putInt(Constant.POSITION, position)
            args.putString("searchArtist", artist)
            fragment.arguments = args
            return fragment
        }
    }


}