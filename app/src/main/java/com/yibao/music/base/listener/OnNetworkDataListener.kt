package com.yibao.music.base.listener

/**
 * @author  luoshipeng
 * createDate：2022/7/18 0018 11:05
 * className   OnNetworkDataListener
 * Des：TODO
 */
interface OnNetworkDataListener {
    /**
     * 接口请求成功后返回的数据,就是后台返回的json中 data 对应的数据 。
     */
    fun responseData(data: String)
}