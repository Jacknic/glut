package com.jacknic.glut.util

import androidx.annotation.WorkerThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jacknic.glut.data.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * 带返回结果协程操作
 **/
@WorkerThread
fun <T : Any> ViewModel.request(
    action: suspend () -> Result<T>,
    success: suspend (Result.Success<T>) -> Unit,
    error: suspend (Result.Error) -> Unit = {},
    after: suspend () -> Unit = {}
): Job {
    return workerAction {
        try {
            action().let {
                when (it) {
                    is Result.Error -> error(it)
                    is Result.Success -> success(it)
                }
            }
        } catch (e: Exception) {
            error(Result.Error(e))
        } finally {
            after()
        }
    }
}

/**
 * IO 线程执行协程
 */
@WorkerThread
fun ViewModel.workerAction(action: suspend () -> Unit) =
    viewModelScope.launch(Dispatchers.Default) {
        action.invoke()
    }
