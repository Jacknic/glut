package com.jacknic.glut.viewmodel

import android.app.Application
import androidx.lifecycle.MediatorLiveData
import com.jacknic.glut.data.repository.remote.CwcRepository
import com.jacknic.glut.util.request
import com.jacknic.glut.util.toastOnMain
import kotlinx.coroutines.Job

/**
 * 财务处登录
 *
 * @author Jacknic
 */
class CwcLoginViewModel(app: Application) : BaseLoginViewModel(app) {

    private val cwcRepo = CwcRepository()
    private var loadCaptchaJob: Job? = null
    private var loginJob: Job? = null
    val user = MediatorLiveData<Boolean>()

    init {
        sid.value = prefer.cwSid
        pwd.value = prefer.cwPwd
        loadCaptcha()
    }

    override fun loadCaptcha() {
        if (loadCaptchaJob?.isActive == true) {
            return
        }
        loadCaptchaJob = request(
            { cwcRepo.fetchCaptcha() },
            { postCaptcha(it.data) },
            { app.toastOnMain(it.exception.message) }
        )
    }

    override fun login() {
        if (loginJob?.isActive == true || invalidInput()) {
            return
        }
        val sidValue = sid.value.orEmpty()
        val pwdValue = pwd.value.orEmpty()
        val captchaValue = captcha.value.orEmpty()
        prefer.cwSid = sidValue
        prefer.cwPwd = pwdValue
        loadingStatus(true, "正在登录")
        loginJob = request(
            { cwcRepo.login(sidValue, pwdValue, captchaValue) },
            { user.postValue(it.data) },
            { app.toastOnMain(it.exception.message) },
            { loadingStatus(false) }
        )
    }
}