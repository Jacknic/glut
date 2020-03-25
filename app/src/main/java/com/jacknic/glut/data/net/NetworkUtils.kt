package com.jacknic.glut.data.net

import com.google.gson.JsonParseException
import com.jacknic.glut.data.util.Result
import com.orhanobut.logger.Logger
import org.json.JSONException
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.text.ParseException
import javax.net.ssl.SSLHandshakeException

/**
 * API 安全调用
 */
suspend fun <T : Any> safeApiCall(call: suspend () -> Result<T>): Result<T> {
    return try {
        call()
    } catch (e: Exception) {
        Logger.e(e, "网络请求错误")
        var msg = "网络错误，请检查网络！"
        when (e) {
            is HttpException -> msg = "请求响应错误！"
            is ConnectException -> msg = "连接服务器失败！"
            is SocketTimeoutException -> msg = "连接超时，请稍后重试！"
            is SSLHandshakeException -> msg = "安全握手连接错误！"
            is JSONException, is JsonParseException, is ParseException -> msg = "数据解析错误！"
        }
        Result.Error(IOException(msg, e))
    }
}

/**
 * 结果类型转换
 */
suspend fun <I : Any, O : Any> convertResult(
    action: suspend () -> Result<I>,
    handle: suspend (Result.Success<I>) -> Result<O>
): Result<O> {
    return when (val result = action()) {
        is Result.Error -> result
        is Result.Success -> handle(result)
    }
}

/**
 * 结果类型装换拓展
 **/
suspend fun <I : Any, O : Any> Result<I>.convert(
    handle: suspend (Result.Success<I>) -> Result<O>
) = convertResult({ this }, handle)