package com.jacknic.glut.data.source.remote

import com.jacknic.glut.data.net.api.AppApi
import com.jacknic.glut.data.net.handle
import com.jacknic.glut.data.util.Result

/**
 * App 数据源
 *
 * @author Jacknic
 */
class AppDataSource(private val api: AppApi) {

    suspend fun requestCheckUpdate() = api.checkVersion()
        .handle({ Result.Success(it) }, "获取版本信息失败")
}