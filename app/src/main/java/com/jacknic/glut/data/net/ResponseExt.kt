package com.jacknic.glut.data.net

import com.jacknic.glut.data.util.Result
import okhttp3.ResponseBody
import retrofit2.Response

// Response 拓展工具类

/**
 * 响应结果处理
 **/
fun <I : Any, O : Any> Response<I>.handle(
    process: (data: I) -> Result<O>?,
    errorMsg: String
): Result<O> {
    val result = body()?.let { process(it) }
    return result ?: Result.Error(IllegalAccessException(errorMsg))
}

/**
 * 转换为输入流
 */
fun Response<ResponseBody>.byteStream(errorMsg: String) =
    handle({ Result.Success(it.byteStream()) }, errorMsg)

/**
 * 财务数据请求判断
 */
fun <I : Any, O : Any> Response<FinanceResult<I>>.financeResult(
    process: (data: I) -> Result<O>?,
    errorMsg: String
) = handle({ if (it.success && it.data != null) process(it.data) else null }, errorMsg)
