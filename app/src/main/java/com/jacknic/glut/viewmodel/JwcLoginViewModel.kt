package com.jacknic.glut.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.jacknic.glut.data.model.Student
import com.jacknic.glut.data.repository.remote.JwcRepository
import com.jacknic.glut.util.request
import com.jacknic.glut.util.toastOnMain
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*

/**
 * 教务登录
 *
 * @author Jacknic
 */
class JwcLoginViewModel(app: Application) : BaseLoginViewModel(app) {

    private var loginJob: Job? = null
    private val jwcRepo = JwcRepository()

    val captchaPass = MutableLiveData<Boolean>()
    val student = MutableLiveData<Student>()

    init {
        sid.value = prefer.sid
        pwd.value = prefer.pwd
        captcha.observeForever {
            if (it.length >= 4) {
                checkCaptcha()
            } else if (captchaPass.value == true) {
                captchaPass.postValue(false)
            }
        }
        loadCaptcha()
    }

    private var loadCaptchaJob: Job? = null
    private var checkCaptchaJob: Job? = null

    /**
     * 加载验证码
     **/
    override fun loadCaptcha() {
        if (loadCaptchaJob?.isActive == true) {
            return
        }
        loadCaptchaJob = request(
            { jwcRepo.fetchCaptcha() },
            { postCaptcha(it.data) },
            { app.toastOnMain(it.exception.message) }
        )
    }

    /**
     * 检验验证码
     */
    private fun checkCaptcha() {
        if (checkCaptchaJob?.isActive == true) {
            checkCaptchaJob?.cancel("取消上次请求")
        }
        checkCaptchaJob = request(
            { jwcRepo.checkCaptcha(captcha.value.orEmpty()) },
            { captchaPass.postValue(it.data) },
            { captchaPass.postValue(false) }
        )
    }

    /**
     * 用户登录
     **/
    override fun login() {
        if (loginJob?.isActive == true) {
            return
        }
        val sidValue = sid.value.orEmpty()
        val pwdValue = pwd.value.orEmpty()
        val captchaValue = captcha.value.orEmpty()
        val invalidInput = invalidInput()
        if (invalidInput) {
            return
        }
        prefer.sid = sidValue
        prefer.pwd = pwdValue
        val isLogged = prefer.logged
        loginJob = request(
            action = {
                loadingStatus(msg = "正在登录...")
                jwcRepo.login(sidValue, pwdValue, captchaValue)
            },
            success = {
                loadingStatus(msg = "登录成功")
                withContext(Dispatchers.IO) {
                    jwcRepo.fetchStudentImage()
                    if (!isLogged) loadInfo()
                }
                student.postValue(it.data)
                Logger.d("-------------------登录结束-------------------")
            },
            error = {
                loadCaptcha()
                app.toastOnMain(it.exception.message)
            },
            after = { loadingStatus(false) }
        )
    }

    private suspend fun loadInfo() {
        coroutineScope {
            val currWeek = async {
                loadingStatus(msg = "当前运行周")
                jwcRepo.currWeek()
            }
            val allExam = async {
                loadingStatus(msg = "获取考试安排")
                jwcRepo.fetchAllExam()
            }
            val studyProcess = async {
                loadingStatus(msg = "获取学业进度")
                jwcRepo.fetchStudyProcess()
            }
            val courses = async {
                loadingStatus(msg = "获取当前课表")
                jwcRepo.fetchCourses(keep = true)
            }
            listOf(currWeek, allExam, studyProcess, courses).awaitAll()
            Logger.d("完成获取信息")
        }
    }
}