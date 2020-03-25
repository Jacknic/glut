package com.jacknic.glut.data.net.parser

import com.google.gson.reflect.TypeToken
import org.jsoup.Jsoup
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.File

/**
 * 课表解析单元测试
 *
 * @author Jacknic
 */
class CourseParserTest {

    private lateinit var courseSource: CoursesParser

    @Before
    fun setUp() {
        val reader = File("./src/main/res/raw/course_arrange.json").bufferedReader()
        val typeToken = object : TypeToken<Map<String, String>>() {}
        val arrangeMap: Map<String, String> = com.jacknic.glut.data.util.gson.fromJson(reader, typeToken.type)
        val html = File("./src/test/res/courses.html").readText()
        val document = Jsoup.parse(html)
        courseSource = CoursesParser(document, arrangeMap)
    }

    @Test
    fun getCourseList() {
        val courseList = courseSource.getCourseList()
        Assert.assertEquals(29, courseList.size)
    }

    @Test
    fun getCourseInfoList() {
        val courseInfoList = courseSource.getCourseInfoList()
        Assert.assertEquals(7, courseInfoList.size)
    }

    @Test
    fun getSchoolYear() {
        val schoolYear = courseSource.schoolYear
        courseSource.schoolYear = 23
        Assert.assertEquals(2019, schoolYear)
    }

    @Test
    fun getSemester() {
        val semester = courseSource.term
        Assert.assertEquals(2, semester)
    }
}