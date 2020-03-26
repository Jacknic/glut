package com.jacknic.glut.viewmodel

import com.jacknic.glut.data.util.Result
import com.jacknic.glut.util.workerAction

/**
 * 网页浏览器
 *
 * @author Jacknic
 */
class WebBrowserViewModel : BaseRequestViewModel() {

    /**
     * 检查教务登录状态
     */
    fun checkJw() = checkJwRequest { }

    /**
     * 检查财务登录状态
     */
    fun checkCw() = workerAction {
        val infoResult = cwcRepo.fetchInfo()
        if (infoResult is Result.Error &&
            infoResult.exception is IllegalAccessException
        ) {
            needLogin.postValue(true)
        }
    }
}