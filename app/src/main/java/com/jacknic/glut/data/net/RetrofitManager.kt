package com.jacknic.glut.data.net

import com.jacknic.glut.BuildConfig
import com.jacknic.glut.data.util.URL_GLUT_CW
import com.jacknic.glut.data.util.URL_GLUT_JW
import com.jacknic.glut.data.util.gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit 管理
 *
 * @author Jacknic
 */
class RetrofitManager {

    private val okHttpClient by lazy {
        OkHttpClient.Builder().apply {
            cookieJar(AndroidCookieJar())
            hostnameVerifier { _, _ -> true }
            retryOnConnectionFailure(false)
            followRedirects(false)
            if (BuildConfig.DEBUG) {
                val log = HttpLoggingInterceptor()
                log.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(log)
            }
        }.build()
    }

    private val glutJw by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(URL_GLUT_JW)
            .addConverterFactory(StringConverterFactory())
            .addConverterFactory(DomConverterFactory())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private val glutCw by lazy {
        glutJw.newBuilder().baseUrl(URL_GLUT_CW)
            .build()
    }


    companion object {

        @Volatile
        private var instance: RetrofitManager? = null

        private fun getInstance(): RetrofitManager {
            return instance ?: synchronized(this) {
                instance ?: RetrofitManager().also { instance = it }
            }
        }

        /**
         * 教务站点
         */
        fun <T> jwCreate(clazz: Class<T>): T {
            return getInstance().glutJw.create(clazz)
        }

        /**
         * 财务站点
         */
        fun <T> cwCreate(clazz: Class<T>): T = getInstance().glutCw.create(clazz)
    }
}