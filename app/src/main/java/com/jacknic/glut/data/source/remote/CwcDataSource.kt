package com.jacknic.glut.data.source.remote

import com.google.gson.reflect.TypeToken
import com.jacknic.glut.data.model.Financial
import com.jacknic.glut.data.net.api.CwApi
import com.jacknic.glut.data.net.byteStream
import com.jacknic.glut.data.net.financeResult
import com.jacknic.glut.data.util.Result
import com.jacknic.glut.data.util.gson

/**
 * 财务处数据源
 *
 * @author Jacknic
 */
class CwcDataSource(private val api: CwApi) {

    /**
     * 请求基本参数
     *
     * [methodName] 请求接口方法
     * [stuId] 类型 1需登录验证，0无需登录验证
     */
    private fun methodParam(methodName: String, stuId: String = "1") =
        mutableMapOf<String, String>().apply {
            this["method"] = methodName
            this["stuid"] = stuId
        }

    suspend fun requestInfo() = api.index(methodParam("getinfo"))
        .financeResult({
            if (it.isJsonObject) {
                val financial = gson.fromJson<Financial>(it, object : TypeToken<Financial>() {}.type)
                Result.Success(financial)
            } else null
        }, "获取财务信息失败！")

    /**
     * 余额查询无需登录
     *
     * [sid] 学号
     */
    suspend fun requestYuE(sid: String): Result<String> {
        val formMap = methodParam("getecardinfo", "0")
        formMap["carno"] = sid
        return api.index(formMap).financeResult({
            var balance = "获取失败"
            if (it.isJsonObject) {
                it.asJsonObject.get("Balance")?.apply { balance = this.asString }
            }
            Result.Success(balance)
        }, "获取余额信息失败！")
    }

    suspend fun requestLogin(sid: String, pwd: String, captcha: String) =
        api.login(sid, pwd, captcha)
            .financeResult({ Result.Success(true) }, "登录失败，请检查输入信息！")

    suspend fun requestCaptcha() = api.captcha().byteStream("获取验证码失败！")
}