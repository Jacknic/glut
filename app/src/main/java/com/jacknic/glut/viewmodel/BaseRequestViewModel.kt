package com.jacknic.glut.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jacknic.glut.data.repository.remote.CwcRepository
import com.jacknic.glut.data.repository.remote.JwcRepository
import com.jacknic.glut.util.Preferences
import com.jacknic.glut.util.request
import com.jacknic.glut.util.toastOnMain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

/**
 * 数据获取验证基类
 *
 * @author Jacknic
 */
abstract class BaseRequestViewModel : ViewModel() {

    protected val jwcRepo by lazy { JwcRepository() }
    protected val cwcRepo by lazy { CwcRepository() }
    protected val prefer = Preferences.getInstance()
    val needLogin = MutableLiveData<Boolean>()
    val loadState = MutableLiveData<Pair<Boolean, String?>>()
    private var checkJwJob: Job? = null

    protected fun checkJwRequest(request: () -> Unit) {
        if (checkJwJob?.isActive == true) {
            return
        }
        emitState(true)
        checkJwJob = request(
            { jwcRepo.loginStatus() },
            {
                if (it.data) {
                    withContext(Dispatchers.Default) { request() }
                } else {
                    prefer.app.toastOnMain("获取失败，请登录后重试！")
                    needLogin.postValue(true)
                }
            },
            { prefer.app.toastOnMain(it.exception.message) },
            { emitState(false) }
        )
    }

    fun emitState(loading: Boolean = true, msg: String? = null) {
        loadState.postValue(loading to msg)
    }
}