package com.jacknic.glut.data.net

import androidx.annotation.Keep

/**
 * 财务JSON请求结果
 *
 * @author Jacknic
 */
@Keep
data class FinanceResult<T : Any>(
    val state: Int,
    val success: Boolean,
    val data: T?
)