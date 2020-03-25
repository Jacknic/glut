package com.jacknic.glut.data.net.parser

import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * 考试安排单元测试
 *
 * @author Jacknic
 */
class ExamsParserTest {

    private lateinit var parser: ExamsParser

    @Before
    fun setUp() {
        val html = File("./src/test/res/exam.html").readText()
        val document = Jsoup.parse(html)
        parser = ExamsParser(document)
    }

    @Test
    fun getExamList() {
        val examList = parser.getExamList()
        Assert.assertEquals(36, examList.size)
    }
}