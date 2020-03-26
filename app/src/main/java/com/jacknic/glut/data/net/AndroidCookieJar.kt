package com.jacknic.glut.data.net

import android.webkit.CookieManager
import com.jacknic.glut.data.util.URL_GLUT_CW
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

/**
 * Cookie同步存储
 *
 * @author Jacknic
 */
class AndroidCookieJar : CookieJar {

    private val cookieManager = CookieManager.getInstance()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val cookieValue = StringBuilder()
        cookies.joinTo(cookieValue, ";", transform = { "${it.name()}=${it.value()}" })
        //Logger.d("存储：${url}->${cookieValue}")
        // 简单处理财务 Cookie path 问题
        var urlValue = url.toString()
        if (urlValue.startsWith(URL_GLUT_CW)) {
            urlValue = url.host()
        }
        cookieManager.setCookie(urlValue, cookieValue.toString())
        cookieManager.flush()
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val cookieStringValue = cookieManager.getCookie(url.toString()).orEmpty()
        val valueList = cookieStringValue.split(";")
        //Logger.d("读取：${url}->${cookieStringValue}")
        return valueList.mapNotNull { Cookie.parse(url, it) }
    }
}