package com.jacknic.glut.viewmodel

import androidx.lifecycle.MutableLiveData
import com.jacknic.glut.data.model.Financial
import com.jacknic.glut.data.util.Result
import com.jacknic.glut.util.request
import com.jacknic.glut.util.toastOnMain
import com.jacknic.glut.util.workerAction
import kotlinx.coroutines.Job

/**
 * 财务信息
 *
 * @author Jacknic
 */
class CwcInfoViewModel : BaseRequestViewModel() {

    private var fetchBalanceJob: Job? = null
    private var fetchInfoJob: Job? = null

    val info = MutableLiveData<Financial?>()
        get() {
            if (field.value == null) {
                field.postValue(cwcRepo.localInfo())
            }
            return field
        }

    val balance = MutableLiveData<String>()
        get() {
            if (field.value == null) {
                fetchBalance()
            }
            return field
        }

     fun fetchInfo() {
        if (fetchInfoJob?.isActive == true) {
            return
        }
        fetchInfoJob = request(
            action = { cwcRepo.fetchInfo() },
            success = {
                prefer.app.toastOnMain("刷新成功")
                info.postValue(it.data)
            },
            error = { handleError(it) },
            after = { emitState(false) }
        )
    }

    private suspend fun handleError(it: Result.Error) {
        if (it.exception is IllegalAccessException) {
            needLogin.postValue(true)
        } else {
            prefer.app.toastOnMain(it.exception.message)
        }
    }

    fun refreshInfo() {
        workerAction {
            fetchInfo()
            fetchBalance()
        }
    }

    fun fetchBalance() {
        if (fetchBalanceJob?.isActive == true) {
            return
        }
        fetchBalanceJob = request(
            action = { cwcRepo.fetchYuE(prefer.cwSid) },
            success = { balance.postValue(it.data) },
            error = {
                val message = "获取失败"
                balance.postValue(message)
            }
        )
    }

}