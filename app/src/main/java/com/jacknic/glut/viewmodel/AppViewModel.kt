package com.jacknic.glut.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.jacknic.glut.BuildConfig
import com.jacknic.glut.data.model.Version
import com.jacknic.glut.data.repository.remote.AppRepository
import com.jacknic.glut.util.Preferences
import com.jacknic.glut.util.cancelToastOnMain
import com.jacknic.glut.util.request
import com.jacknic.glut.util.toastOnMain
import kotlinx.coroutines.Job

/**
 * App 相关
 *
 * @author Jacknic
 */
class AppViewModel(val app: Application) : AndroidViewModel(app) {

    val version = MutableLiveData<Version>()
    private val prefer = Preferences.getInstance()
    private var appRepo = AppRepository()
    private var checkUpdateJob: Job? = null

    /**
     * 执行版本更新检测
     */
    fun checkUpdate(isShowToast: Boolean = false) {
        if (checkUpdateJob?.isActive == true) {
            return
        }
        checkUpdateJob = request(
            action = { appRepo.checkUpdate() },
            success = {
                if (it.data.versionCode > BuildConfig.VERSION_CODE) {
                    cancelToastOnMain()
                    version.postValue(it.data)
                } else {
                    val message = "已是最新版本 v${BuildConfig.VERSION_NAME}"
                    showToast(isShowToast, message)
                    clearCache()
                }
            },
            error = { showToast(isShowToast, it.exception.message.orEmpty()) }
        )
    }

    private fun clearCache() {
        app.cacheDir.deleteRecursively()
        prefer.downloadSize = 0
        prefer.downloadUrl = ""
    }

    private suspend fun showToast(isShowToast: Boolean, message: String) {
        if (isShowToast) {
            app.toastOnMain(message)
        }
    }
}