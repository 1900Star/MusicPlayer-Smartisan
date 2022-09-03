package com.yibao.music.viewmodel

import com.yibao.music.base.BaseViewModel
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.AlbumInfo

class AlbumViewModel : BaseViewModel() {

    val albumViewModel = SingleLiveEvent<AlbumInfo>()
    fun postAlbum(albumInfo: AlbumInfo) {

        albumViewModel.postValue(albumInfo)
    }
}