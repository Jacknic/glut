package com.jacknic.glut.data.net.api


import com.jacknic.glut.data.util.URL_GLUT_JW
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okio.sink
import org.junit.Assert
import org.junit.Before
import retrofit2.Retrofit
import java.io.File

/**
 * 教务API测试
 *
 * @author Jacknic
 */
class JwApiTest {

    private lateinit var api: JwApi

    @Before
    fun setup() {
        val retrofit = Retrofit.Builder()
            .baseUrl(URL_GLUT_JW)
            .build()
        api = retrofit.create(JwApi::class.java)
    }

    /**
     * 验证码下载测试
     */
    // @Test
    fun testCaptcha() = runBlocking {
        val response = api.captcha()
        val source = response.body()?.source()
        val file = File("build/p.png")
        withContext(Dispatchers.IO) {
            val sink = file.sink()
            source?.readAll(sink)
            Assert.assertTrue(file.exists())
            Assert.assertTrue(file.length() > 0)
        }
    }
}