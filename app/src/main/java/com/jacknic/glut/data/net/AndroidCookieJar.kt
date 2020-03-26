package com.jacknic.glut.data.net

import android.webkit.CookieManager
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Android 设备 Cookie存储
 *<p>
 * 用于 Cookie 同步
 * </p
 * @author Jacknic
 */
class AndroidCookieJar : CookieJar {

    private val cookieManager = CookieManager.getInstance()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookieValue = StringBuilder()
        cookies.joinTo(cookieValue, ";", transform = { "${it.name()}=${it.value()}" })
        cookieManager.setCookie(url.toString(), cookieValue.toString())
        cookieManager.flush()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieStringValue = cookieManager.getCookie(url.toString()).orEmpty()
        val valueList = cookieStringValue.split(";")
        return valueList.mapNotNull { Cookie.parse(url, it) }
    }
}