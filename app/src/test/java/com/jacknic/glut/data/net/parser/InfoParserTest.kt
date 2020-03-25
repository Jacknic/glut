package com.jacknic.glut.data.net.parser

import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * 学生信息解析单元测试
 *
 * @author Jacknic
 */
class InfoParserTest {

    private lateinit var parser: InfoParser

    @Before
    fun setUp() {
        val html = File("./src/test/res/info.html").readText()
        val document = Jsoup.parse(html)
        parser = InfoParser(document)
    }

    @Test
    fun getStudent() {
        Assert.assertNotNull(parser.student)
        val student = parser.student!!
        Assert.assertEquals("尼古拉斯赵四", student.name)
    }
}