package com.jacknic.glut.data.repository.remote

import com.jacknic.glut.data.model.Financial
import com.jacknic.glut.data.net.RetrofitManager
import com.jacknic.glut.data.net.api.CwApi
import com.jacknic.glut.data.net.safeApiCall
import com.jacknic.glut.data.source.remote.CwcDataSource
import com.jacknic.glut.data.util.*
import com.jacknic.glut.util.Preferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * 财务处数据访问
 *
 * @author Jacknic
 */
class CwcRepository {

    private val prefer = Preferences.getInstance()
    private val api = RetrofitManager.cwCreate(CwApi::class.java)
    private val dataSource = CwcDataSource(api)

    /**
     * 用户登录验证
     **/
    suspend fun login(sid: String, pwd: String, captcha: String) =
        safeApiCall { dataSource.requestLogin(sid, pwd, captcha) }

    /**
     * 验证码加载
     **/
    suspend fun fetchCaptcha() = safeApiCall {
        when (val result = dataSource.requestCaptcha()) {
            is Result.Error -> result
            is Result.Success -> decodeBitmap(result.data, "验证码解析错误！")
        }
    }

    /**
     * 获取用户信息
     */
    suspend fun fetchInfo() = safeApiCall {
        dataSource.requestInfo().apply {
            if (this is Result.Success) {
                withContext(Dispatchers.IO) {
                    val writer = File(prefer.app.filesDir, FILE_NAME_FINANCE).bufferedWriter()
                    val json = toJSON(data)
                    writer.write(json)
                    writer.flush()
                    writer.close()
                }
            }
        }
    }

    /**
     * 获取余额
     */
    suspend fun fetchYuE(sid: String) = safeApiCall { dataSource.requestYuE(sid) }

    /**
     * 加载本地缓存财务信息
     */
    fun localInfo(): Financial? {
        val financeFile = File(prefer.app.filesDir, FILE_NAME_FINANCE)
        if (financeFile.isFile) {
            return fromJSON(financeFile.readText())
        }
        return null
    }

}