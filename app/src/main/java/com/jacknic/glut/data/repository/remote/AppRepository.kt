package com.jacknic.glut.data.repository.remote

import com.jacknic.glut.data.net.RetrofitManager
import com.jacknic.glut.data.net.api.AppApi
import com.jacknic.glut.data.net.safeApiCall
import com.jacknic.glut.data.source.remote.AppDataSource

/**
 * App 功能相关
 *
 * @author Jacknic
 */
class AppRepository {

    private val appApi = RetrofitManager.cwCreate(AppApi::class.java)
    private val dataSource = AppDataSource(appApi)

    /**
     * 检测新版本
     **/
    suspend fun checkUpdate() = safeApiCall { dataSource.requestCheckUpdate() }

}