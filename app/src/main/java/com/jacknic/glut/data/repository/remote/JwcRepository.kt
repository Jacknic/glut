package com.jacknic.glut.data.repository.remote

import android.webkit.CookieManager
import androidx.annotation.MainThread
import com.google.gson.reflect.TypeToken
import com.jacknic.glut.R
import com.jacknic.glut.data.db.CourseDatabase
import com.jacknic.glut.data.net.RetrofitManager
import com.jacknic.glut.data.net.api.JwApi
import com.jacknic.glut.data.net.convert
import com.jacknic.glut.data.net.convertResult
import com.jacknic.glut.data.net.safeApiCall
import com.jacknic.glut.data.source.remote.JwcDataSource
import com.jacknic.glut.data.util.*
import com.jacknic.glut.util.Preferences
import java.io.File
import java.io.InputStream

/**
 * 教务处登录
 *
 * @author Jacknic
 */
class JwcRepository {

    private val prefer = Preferences.getInstance()
    private val jwApi = RetrofitManager.jwCreate(JwApi::class.java)
    private val dataSource = JwcDataSource(jwApi)
    private val database = CourseDatabase.getInstance(prefer.app)
    private val courseInfoDao = database.getCourseInfoDao()
    private val courseDao = database.getCourseDao()

    /**
     * 用户登录
     **/
    suspend fun login(sid: String, pwd: String, captcha: String) = safeApiCall {
        dataSource.requestLogin(sid, pwd, captcha).apply {
            if (this is Result.Success) {
                /// 登录成功记录状态
                prefer.sid = sid
                prefer.pwd = pwd
                prefer.logged = true
                prefer.student = data
            }
        }
    }

    /**
     * 用户登出
     */
    @MainThread
    fun logout(done: () -> Unit) {
        prefer.logged = false
        prefer.student = null
        CookieManager.getInstance().removeAllCookies { }
        database.clearAllTables()
        prefer.app.filesDir.deleteRecursively()
        done()
    }

    /**
     * 验证码加载
     **/
    suspend fun fetchCaptcha() = safeApiCall {
        convertResult({ dataSource.requestCaptcha() }, { decodeBitmap(it.data) })
    }


    /**
     * 验证码检验
     **/
    suspend fun checkCaptcha(code: String) = safeApiCall { dataSource.requestCheckCaptcha(code) }

    private val courseArrangeMap by lazy {
        val reader = prefer.app.resources.openRawResource(R.raw.course_arrange).reader()
        val typeToken = object : TypeToken<Map<String, String>>() {}
        gson.fromJson<Map<String, String>>(reader, typeToken.type)
    }

    /**
     * 获取课程列表
     *
     * @param year 学年
     * @param term 学期
     * @param keep 是否设为当前学期
     **/
    suspend fun fetchCourses(year: Int? = null, term: Int? = null, keep: Boolean = false) =
        safeApiCall {
            dataSource.requestCourses(year, term, courseArrangeMap).apply {
                if (this is Result.Success) {
                    val parser = data
                    courseDao.saveAll(parser.getCourseList())
                    courseInfoDao.saveAll(parser.getCourseInfoList())
                    if (keep) {
                        prefer.schoolYear = parser.schoolYear
                        prefer.term = parser.term
                    }
                }
            }
        }

    /**
     * 保存为文件
     */
    private fun saveFile(result: InputStream, fileName: String): Result<Boolean> {
        val file = File(prefer.app.filesDir, fileName)
        result.copyTo(file.outputStream())
        result.close()
        return Result.Success(true)
    }

    /**
     * 获取学生头像
     **/
    suspend fun fetchStudentImage() = safeApiCall {
        dataSource.requestStudentImage().convert { saveFile(it.data, FILE_NAME_HEAD_IMAGE) }
    }

    /**
     * 获取学业进度
     **/
    suspend fun fetchStudyProcess() = safeApiCall {
        dataSource.requestStudyProcess().convert { saveFile(it.data, FILE_NAME_STUDY_PROCESS) }
    }

    /**
     * 获取所有考试安排
     **/
    suspend fun fetchAllExam() = safeApiCall {
        dataSource.requestAllExam().convert { saveFile(it.data, FILE_NAME_ALL_EXAM) }
    }

    /**
     * 获取所有成绩
     **/
    suspend fun fetchAllGrade() = safeApiCall {
        dataSource.requestAllGrade().convert { saveFile(it.data, FILE_NAME_GRADE) }
    }

    /**
     * 检查登录状态
     **/
    suspend fun loginStatus() = safeApiCall { dataSource.loginStatus() }

    /**
     * 当前运行周
     **/
    suspend fun currWeek() = safeApiCall {
        val response = dataSource.currWeek()
        response.body()?.apply {
            val eleCurrWeek = select(".curweek strong").firstOrNull()
            val currWeek = eleCurrWeek?.text()?.toIntOrNull() ?: 1
            prefer.markWeek = currWeek
            var endWeek = select(".week table tbody tr td").size
            if (endWeek <= 0) endWeek = 30
            prefer.endWeek = endWeek
        }
        Result.Success(true)
    }
}