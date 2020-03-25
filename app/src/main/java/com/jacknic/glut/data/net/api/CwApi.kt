package com.jacknic.glut.data.net.api

import com.google.gson.JsonElement
import com.jacknic.glut.data.net.FinanceResult
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * 财务接口信息
 *
 * @author Jacknic
 */

interface CwApi {

    /**
     * 通用数据请求接口
     *
     * @param formMap 请求参数
     */
    @FormUrlEncoded
    @POST("interface/index")
    suspend fun index(@FieldMap formMap: Map<String, String>): Response<FinanceResult<JsonElement>>

    /**
     * 财务用户登录
     *
     * @param sid 学号
     * @param passWord 密码
     * @param verifyCode 验证码
     */
    @FormUrlEncoded
    @POST("interface/login")
    suspend fun login(
        @Field("sid") sid: String,
        @Field("passWord") passWord: String,
        @Field("verifyCode") verifyCode: String
    ): Response<FinanceResult<JsonElement>>

    /**
     * 获取验证码
     */
    @GET("interface/getVerifyCode")
    suspend fun captcha(): Response<ResponseBody>


}