package com.jacknic.glut.data.net.api

import com.jacknic.glut.data.model.Version
import com.jacknic.glut.data.util.URL_APP_UPDATE
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * APP 相关接口
 *
 * @author Jacknic
 */
interface AppApi {

    /**
     * 检测新版本
     **/
    @GET
    suspend fun checkVersion(@Url url: String = URL_APP_UPDATE): Response<Version>

    /**
     * 文件下载
     */
    @GET
    @Streaming
    fun download(@Url url: String, @Header("Range") range: String): Call<ResponseBody>

}