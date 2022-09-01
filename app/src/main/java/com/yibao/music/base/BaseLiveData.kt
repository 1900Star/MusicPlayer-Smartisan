package com.yibao.music.base

import androidx.lifecycle.MutableLiveData

/**
 * @author  luoshipeng
 * createDate：2020/7/22 0022 10:06
 * className   BaseLiveData
 * Des：TODO
 */
class BaseLiveData<T> : MutableLiveData<T>() {
    override fun postValue(value: T) {
        super.postValue(value)
    }

    fun postErrorValue(value: T) {
        super.postValue(value)
    }
}