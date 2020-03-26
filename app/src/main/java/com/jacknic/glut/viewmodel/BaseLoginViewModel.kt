package com.jacknic.glut.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.jacknic.glut.util.Preferences
import com.jacknic.glut.util.toast

/**
 * 用户登录基类
 *
 * @author Jacknic
 */
abstract class BaseLoginViewModel(val app: Application) : AndroidViewModel(app) {

    protected val prefer = Preferences.getInstance()
    val sid = MutableLiveData<String>()
    val pwd = MutableLiveData<String>()
    val captcha = MutableLiveData<String>()
    val captchaBitmap = MutableLiveData<Bitmap>()
    val loadState = MutableLiveData<Pair<Boolean, String?>>()

    /**
     * 加载验证码
     **/
    abstract fun loadCaptcha()

    /**
     * 用户登录
     **/
    abstract fun login()

    fun invalidInput(): Boolean {
        val sidValue: String = sid.value.orEmpty()
        val pwdValue: String = pwd.value.orEmpty()
        val captchaValue: String = captcha.value.orEmpty()
        if (sidValue.isEmpty()) {
            app.toast("用户名不能为空！")
            return true
        }
        if (pwdValue.isEmpty()) {
            app.toast("密码不能为空！")
            return true
        }
        if (captchaValue.isEmpty()) {
            app.toast("验证码不能为空！")
            return true
        }
        return false
    }

    fun postCaptcha(data: Bitmap) {
        captchaBitmap.value?.recycle()
        captchaBitmap.postValue(data)
    }

    fun loadingStatus(isLoading: Boolean = true, msg: String? = null) {
        loadState.postValue(isLoading to msg)
    }
}