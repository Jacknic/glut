package com.jacknic.glut.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.math.ln
import kotlin.math.pow

/**
 * 从字节流解码图片
 */
fun decodeBitmap(data: InputStream, errorMsg: String = "验证码解析错误！"): Result<Bitmap> {
    val bitmap: Bitmap? = BitmapFactory.decodeStream(data)
    return if (bitmap != null) {
        Result.Success(bitmap)
    } else {
        Result.Error(IOException(errorMsg))
    }
}

/**
 * 获取格式化存储单位
 */
fun getFmtBytes(bytes: Long, kSize: Boolean): String? {
    val unit = if (kSize) 1000 else 1024
    if (bytes < unit) {
        return "${bytes}B"
    }
    var exp = (ln(bytes.toDouble()) / ln(unit.toDouble()) + 0.5f).toInt()
    var size = bytes / unit.toDouble().pow(exp.toDouble())
    if (size < 1) {
        size *= 1024
        exp -= 1
    }
    val pre = "KMGTPE"[exp - 1]
    return java.lang.String.format(Locale.getDefault(), "%.1f%s", size, pre)
}