package com.jacknic.glut.data.net.api

import com.jacknic.glut.data.util.URL_APP_UPDATE
import com.jacknic.glut.data.util.URL_GLUT_JW
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 应用API测试
 *
 * @author Jacknic
 */


class AppApiTest {

    private lateinit var api: AppApi

    @Before
    fun setup() {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(URL_GLUT_JW)
            .build()
        api = retrofit.create(AppApi::class.java)
    }

    /**
     * 版本信息检测
     */
    //@Test
    fun testVersion() = runBlocking {
        val response = api.checkVersion(URL_APP_UPDATE)
        val body = response.body()
        Assert.assertNotNull(body)
        Assert.assertNotNull(body?.versionName)
    }
}