package com.jacknic.glut.data.net

import android.webkit.CookieManager
import com.orhanobut.logger.Logger
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
        //Log.d(javaClass.name, "存储：$url,$cookies")
        cookies.forEachIndexed { index, cookie ->
            Logger.d("${cookie.name()} -> ${cookie.value()}")
            cookieValue.apply {
                append(cookie.name())
                append("=")
                append(cookie.value())
                if (index != cookies.lastIndex) {
                    append(";")
                }
            }
        }
        cookieManager.setCookie(url.host(), cookieValue.toString())
        cookieManager.flush()
    }

    override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
        val cookeList = mutableListOf<Cookie>()
        val cookieValue = cookieManager.getCookie(url.host()).orEmpty()
        val valueList = cookieValue.split(";")
        // Logger.d("读取到的：${cookieValue}")
        valueList.forEach {
            val cookie = Cookie.parse(url, it)
            cookie?.apply {
                cookeList.add(this)
            }
        }
        //Log.d(javaClass.name, "读取：$url,$cookeList")
        return cookeList
    }
}