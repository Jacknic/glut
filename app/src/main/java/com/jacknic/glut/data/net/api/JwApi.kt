package com.jacknic.glut.data.net.api

import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import retrofit2.Response
import retrofit2.http.*

/**
 * 教务数据请求
 *
 * @author Jacknic
 */
interface JwApi {

    /**
     * 登录验证码
     **/
    @Streaming
    @GET("getCaptcha.do")
    suspend fun captcha(): Response<ResponseBody>

    /**
     * 登录验证码正确性检查
     **/
    @GET("checkCaptcha.do")
    suspend fun checkCaptcha(@Query("captchaCode") code: String): Response<String>

    /**
     * 登录表单验证
     **/
    @POST("j_acegi_security_check")
    @FormUrlEncoded
    suspend fun login(@Field("j_username") sid: String,
                      @Field("j_password") pwd: String,
                      @Field("j_captcha") captcha: String
    ): Response<Void>

    /**
     * 登录状态检查
     **/
    @GET("showHeader.do")
    suspend fun loginStatus(): Response<Void>

    /**
     * 学生头像
     */
    @Streaming
    @GET("manager/studentinfo/showStudentImage.jsp")
    suspend fun studentImage(): Response<ResponseBody>

    /**
     * 学业进度
     */
    @GET("manager/score/studentStudyProcess.do")
    suspend fun studyProcess(): Response<ResponseBody>

    /**
     * 考试安排
     */
    @GET("manager/examstu/studentQueryAllExam.do?pagingNumberPerVLID=1000")
    suspend fun allExam(): Response<ResponseBody>

    /**
     * 学籍信息
     */
    @GET("student/studentinfo/studentInfoModifyIndex.do?frombase=0&wantTag=0&moduleId=2060")
    suspend fun studentInfo(): Response<Document>

    /**
     * 当前学期课表
     * @param queryMap year=>当前年份-1980，term=>1春/2秋；空则默认获取当前课表
     */
    @GET("student/currcourse/currcourse.jsdo")
    suspend fun courses(@QueryMap queryMap: Map<String, String?> = emptyMap()): Response<Document>

    /**
     * 获取全部成绩列表
     **/
    @GET("manager/score/studentOwnScore.do?year=&term=&para=0&sortColumn=&Submit=%E6%9F%A5%E8%AF%A2")
    suspend fun score(): Response<ResponseBody>

    /**
     * 获取当前运行周数
     **/
    @GET("calendarinfo/viewCalendarInfo.do")
    suspend fun currWeek(): Response<Document>
}