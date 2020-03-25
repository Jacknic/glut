package com.jacknic.glut.data.source.remote

import com.jacknic.glut.data.model.Student
import com.jacknic.glut.data.net.api.JwApi
import com.jacknic.glut.data.net.byteStream
import com.jacknic.glut.data.net.handle
import com.jacknic.glut.data.net.parser.CoursesParser
import com.jacknic.glut.data.net.parser.InfoParser
import com.jacknic.glut.data.util.Result

/**
 * 教务处数据源
 *
 * @author Jacknic
 */
class JwcDataSource(private val api: JwApi) {

    suspend fun requestCaptcha() = api.captcha().byteStream("获取验证码失败！")

    suspend fun requestCheckCaptcha(code: String) = api.checkCaptcha(code)
        .handle({ Result.Success("true".equals(it, true)) }, "检查验证码失败！")

    suspend fun requestLogin(sid: String, pwd: String, captcha: String): Result<Student> {
        val response = api.login(sid, pwd, captcha)
        val location = response.headers().get("Location")
        val loginFail = location?.contains("login_error", true) ?: false
        if (!loginFail) {
            return requestStudentInfo()
        }
        val errorMsg = "登录失败，请检查输入信息！"
        return Result.Error(IllegalAccessException(errorMsg))
    }

    private suspend fun requestStudentInfo() = api.studentInfo()
        .handle({ dom ->
            InfoParser(dom).student?.let { Result.Success(it) }
        }, "获取用户信息失败！")

    suspend fun requestCourses(
        year: Int? = null, term: Int? = null,
        courseArrangeMap: Map<String, String>
    ): Result<CoursesParser> {
        val queryMap = mutableMapOf<String, String>()
        year?.let { queryMap["year"] = it.minus(1980).toString() }
        term?.let { queryMap["term"] = it.toString() }
        return api.courses(queryMap).handle({
            Result.Success(CoursesParser(it, courseArrangeMap))
        }, "获取课表失败！")
    }

    suspend fun requestStudentImage() = api.studentImage().byteStream("获取用户头像失败！")

    suspend fun requestStudyProcess() = api.studyProcess().byteStream("获取学业进度失败！")

    suspend fun requestAllExam() = api.allExam().byteStream("获取考试安排失败！")

    suspend fun requestAllGrade() = api.score().byteStream("获取成绩失败！")

    suspend fun loginStatus() = api.loginStatus().let { Result.Success(it.code() < 300) }

    suspend fun currWeek() = api.currWeek()
}