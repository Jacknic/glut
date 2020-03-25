package com.jacknic.glut.data.net.parser

import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.data.db.entity.CourseInfo
import org.jsoup.nodes.Document
import java.util.*

/**
 * 课表解析
 *
 * @author Jacknic
 */
class CoursesParser(
    private val document: Document,
    private val courseArrangeMap: Map<String, String>
) {

    private val patternWeek = "(\\d+)(-)?(\\d+)?(\\D)?".toPattern()

    private val courseList = mutableListOf<Course>()
    private val courseInfoList = mutableListOf<CourseInfo>()

    /**
     * 获取当前页面选中的学年
     *
     * @return 学年
     */
    var schoolYear = Calendar.getInstance()[Calendar.YEAR]

    /**
     * 当前学期
     *
     * @return 学期（1春季、2秋季）
     */
    var term = 1

    init {
        try {
            parseSchoolYear()
            parseSemester()
            parseCourse()
        } finally {
            // no-op
        }
    }

    /**
     * 解析学年
     */
    private fun parseSchoolYear() {
        var years = document.select("select[name='year'] option[selected]")
        if (years.isEmpty()) {
            years = document.select("select[name='year'] option")
        }
        if (years.isNotEmpty()) {
            schoolYear = years[0].text().toIntOrNull() ?: schoolYear
        }
    }

    /**
     * 解析学期
     */
    private fun parseSemester() {
        var terms = document.select("select[name='term'] option[selected]")
        if (terms.isEmpty()) {
            terms = document.select("select[name='term'] option")
        }
        if (terms.isNotEmpty()) {
            term = terms[0].`val`().toIntOrNull() ?: 1
        }
    }

    /**
     * 解析课表信息
     */
    private fun parseCourse() {
        val courseElements = document.getElementsByClass("infolist_tab")
            .first()?.select(".infolist_common")
        if (courseElements.isNullOrEmpty()) {
            return
        }
        for (e in courseElements) {
            val number = e.child(0).text()
            val name = e.child(2).text()
            val courseInfo = CourseInfo()
            courseInfo.courseNum = e.child(0).text()
            courseInfo.courseName = e.child(2).text()
            courseInfo.grade = e.child(4).text()
            courseInfo.term = term
            courseInfo.schoolYear = schoolYear
            courseInfo.teacher = e.child(3).text()
            courseInfoList.add(courseInfo)

            val info = e.child(9).getElementsByTag("tr")
            var dayOf = -1
            /* 有教学时间地点的情况 */
            for (tr in info) {
                val course = Course()
                course.schoolYear = schoolYear
                course.term = term
                course.courseNum = number
                course.courseName = name
                val weeks = tr.child(0).text()
                course.week = weeks
                val dayOfWeek = tr.child(1).text()
                if (dayOfWeek.isNotBlank()) {
                    when (dayOfWeek.last()) {
                        '一' -> dayOf = 1
                        '二' -> dayOf = 2
                        '三' -> dayOf = 3
                        '四' -> dayOf = 4
                        '五' -> dayOf = 5
                        '六' -> dayOf = 6
                        '日' -> dayOf = 7
                    }
                }
                course.dayOfWeek = dayOf
                val weeksDetails = weeks.split(",")
                val smp = StringBuilder(" ")
                for (weekDetail in weeksDetails) {
                    val m = patternWeek.matcher(weekDetail)
                    if (m.find()) {
                        val weekStart = m.group(1)
                        val weekEnd = m.group(3) ?: weekStart
                        val everyWeek = m.group(4)
                        var weekType = 0
                        when (everyWeek) {
                            "单" -> weekType = 1
                            "双" -> weekType = 2
                        }
                        if (weekStart != null) {
                            val begin = weekStart.toIntOrNull() ?: 0
                            val end = weekEnd.toIntOrNull() ?: begin
                            if (weekType == 0) {
                                for (w in begin..end) {
                                    smp.append(w).append(" ")
                                }
                            } else {
                                for (w in begin..end step 2) {
                                    smp.append(w).append(" ")
                                }
                            }
                        } else {
                            smp.clear()
                        }
                        course.weekType = weekType
                    }
                    // println("周数排序是：$smp")
                    course.smartPeriod = smp.toString()
                }
                val courseArrangement = tr.child(2).text().trim()
                val arrangeValue = courseArrangeMap[courseArrangement] ?: ""
                if (arrangeValue.isNotBlank()) {
                    val courseStartAndEnd = arrangeValue.split(",")
                    course.startSection = courseStartAndEnd[0].toIntOrNull() ?: 0
                    course.endSection = courseStartAndEnd[1].toIntOrNull() ?: 0
                }
                val classRoom = tr.child(3).text()
                course.classRoom = classRoom
                courseList.add(course)
            }
        }
    }

    fun getCourseList(): List<Course> {
        return courseList
    }

    fun getCourseInfoList(): List<CourseInfo> {
        return courseInfoList
    }

}