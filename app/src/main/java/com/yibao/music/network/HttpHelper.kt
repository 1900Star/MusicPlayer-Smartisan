package com.yibao.music.network

import com.yibao.music.base.BaseViewModel
import com.yibao.music.base.listener.OnNetworkDataListener
import com.yibao.music.livedata.SingleLiveEvent
import com.yibao.music.model.ErrorBean
import com.yibao.music.util.Api
import com.yibao.music.util.Constant
import com.yibao.music.util.LogUtil
import okhttp3.*
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

open class HttpHelper {
    protected val mTag = " ==== " + this::class.java.simpleName + "  "

    /**
     *
     * 返回一个 Request Builder  统一添加 Token 、CustomerId、AppType
     * @param urlPath urlPath
     */

    private val accept =
        "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9"
    private val encoding = "gzip, deflate"
    private val cacheControl = "max-age=0"
    private val proxyConnection = "keep-alive"
    private val upgradeInsecureRequests = "1"
    private val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36"


     fun getRequestBuilder(url: String): Request {


        return Request.Builder().url(url).addHeader("User-Agent",userAgent).build()
    }


    /**
     * GET 带参数的求体
     */
    fun getParamsRequest(urlPath: String, urlParams: HashMap<Any, Any>): Request {
        val url = urlPath + getParams(urlParams)
        return getRequestBuilder(url)
    }


    /**
     * Get请求 参数拼接
     */
    fun getParams(params: HashMap<Any, Any>): String {
        if (params.size > 0) {
            val stringBuffer = StringBuffer("?")
            params.entries.forEachIndexed { index, entry ->
                if (params[entry.key] != null) {
                    if (params.size == 1) {
                        stringBuffer.append(entry.key)
                        stringBuffer.append("=")
                        stringBuffer.append(entry.value)
                    } else if (index == 0) {
                        stringBuffer.append(entry.key)
                        stringBuffer.append("=")
                        stringBuffer.append(entry.value)
                    } else {
                        stringBuffer.append("&")
                        stringBuffer.append(entry.key)
                        stringBuffer.append("=")
                        stringBuffer.append(entry.value)
                    }
                }
            }
            return stringBuffer.toString()
        } else {
            return ""
        }
    }


    /**
     *
     * @param request 构建的请求体
     * @param listener listener
     */
    open fun requestData(request: Request, listener: OnNetworkDataListener) {
        // 打印请求信息
        printRequest(request)
        // 发起请求
        val call = getClient().newCall(request)
        val requestUrl = request.url.toString()
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                LogUtil.d(mTag, "onFailure: authenticator  ==== " + e.message)
                postErrorValue(500, e.message.toString(), requestUrl)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                val successful = response.isSuccessful
                val code = response.code
                if (code == 200 && successful) {
                    val json = response.body!!.string()
                    listener.responseData(json)
//                    LogUtil.d(mTag, json)

                } else {
                    val failDesc = response.toString()
                    LogUtil.d(mTag, "isFailed  请求失败: ==== $failDesc")
                    postErrorValue(code, "获取失败", requestUrl)
                }
            }
        })
    }

    private val UTF8 = Charset.forName("UTF-8")
    private fun printRequest(request: Request) {
        val requestBody = request.body
        var body: String? = null
        requestBody?.let {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            var charset: Charset? = UTF8
            val contentType = requestBody.contentType()
            contentType?.let {
                charset = contentType.charset(UTF8)
            }
            body = buffer.readString(charset!!)
        }
        LogUtil.d(
            mTag, "response info \n${request.url}\n${request.headers} body: $body"
        )
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
                    okHttpClient = OkHttpClient.Builder().addInterceptor(object :Interceptor{
                        override fun intercept(chain: Interceptor.Chain): Response {
                        val request = chain.request()
                                .newBuilder()
                                .removeHeader("User-Agent")//移除旧的
                                //WebSettings.getDefaultUserAgent(mContext) 是获取原来的User-Agent
                                .addHeader("User-Agent",userAgent )
                            .build();
                            return chain.proceed(request);

                        }
                    }).pingInterval(10, TimeUnit.SECONDS)
                        .connectTimeout(3, TimeUnit.SECONDS).writeTimeout(3, TimeUnit.SECONDS)
                        .readTimeout(3, TimeUnit.SECONDS).build()
                }

            }
        }

        return okHttpClient!!
    }

    fun postValue() {

    }


}