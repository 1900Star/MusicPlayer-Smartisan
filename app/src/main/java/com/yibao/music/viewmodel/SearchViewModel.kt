package com.yibao.music.viewmodel

import com.yibao.music.base.BaseViewModel
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.AlbumInfo
import com.yibao.music.model.SearchCategoryBean

class SearchViewModel : BaseViewModel() {

    val searchViewModel = SingleLiveEvent<SearchCategoryBean>()
    fun postAlbum(bean: SearchCategoryBean) {

        searchViewModel.postValue(bean)
    }
}