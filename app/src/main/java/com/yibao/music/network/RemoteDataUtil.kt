package com.yibao.music.network

import com.yibao.music.base.listener.OnNetworkDataListener
import com.yibao.music.util.Api

 class RemoteDataUtil {



    fun getArtistImg(artist: String, listener: OnNetworkDataListener) {
        val bodyParams = HashMap<Any, Any>()
        bodyParams["s"] = artist
        bodyParams["type"] = 100
        val params = HttpHelper().getParams(bodyParams)
        val paramsRequest = HttpHelper().getRequestBuilder(Api.SINGER_PIC_BASE_URL+params)


        HttpHelper().requestData(paramsRequest, listener)


    }


}