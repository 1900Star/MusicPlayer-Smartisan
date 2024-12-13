package com.yibao.music.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yibao.music.MusicApplication
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.ErrorBean
import com.yibao.music.model.Message
import com.yibao.music.model.greendao.MusicBeanDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author  luoshipeng
 * createDate：2020/6/20 0020 10:24
 * className   BaseViewModel
 * Des：TODO
 */
open class BaseViewModel : ViewModel() {

    protected val mTag = " ==== " + this::class.java.simpleName + "  "

    val resultModel = SingleLiveEvent<Message>()

    protected var job: Job? = null
    override fun onCleared() {
        super.onCleared()
        if (job != null)
            job!!.cancel()
    }


    /**
     * 失败发送
     */
    val errorLiveData = SingleLiveEvent<ErrorBean>()

    fun postError(errorMessage: String) {

        errorLiveData.postValue(ErrorBean(100, errorMessage))
    }


    fun ok(code: Int) {
        resultModel.postValue(Message(code, "完成"))
    }

    fun fail(msg: String) {
        resultModel.postValue(Message(100, msg))
    }

    protected fun musicDao(): MusicBeanDao {
        return MusicApplication.getInstance().musicDao
    }
}