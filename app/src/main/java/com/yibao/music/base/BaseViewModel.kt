package com.yibao.music.base

import androidx.lifecycle.ViewModel
import com.yibao.music.base.listener.OnNetworkDataListener
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.ErrorBean
import com.yibao.music.util.LogUtil
import kotlinx.coroutines.Job
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * @author  luoshipeng
 * createDate：2020/6/20 0020 10:24
 * className   BaseViewModel
 * Des：TODO
 */
open class BaseViewModel : ViewModel() {

    protected val mTag = " ==== " + this::class.java.simpleName + "  "
    private val mTagUrl = " ==URL== " + this::class.java.simpleName + "  "

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

    fun postErrorValue(errorCode: Int, errorMessage: String, errorUrl: String) {

        errorLiveData.postValue(ErrorBean(errorCode, errorMessage, errorUrl))
    }

    private fun getClient(): OkHttpClient {
        var okHttpClient: OkHttpClient? = null
        if (okHttpClient == null) {
            synchronized(BaseViewModel::class.java) {
                if (okHttpClient == null) {
                    okHttpClient =
                        OkHttpClient.Builder()
                            .pingInterval(10, TimeUnit.SECONDS)
                            .connectTimeout(3, TimeUnit.SECONDS)
                            .writeTimeout(3, TimeUnit.SECONDS)
                            .readTimeout(3, TimeUnit.SECONDS)
                            .build()
                }

            }
        }

        return okHttpClient!!
    }

    fun postValue() {

    }

}