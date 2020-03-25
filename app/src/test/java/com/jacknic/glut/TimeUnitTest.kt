package com.jacknic.glut

import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

/**
 */
class TimeUnitTest {

    @Test
    fun week_isCorrect() {
        val markTime = Calendar.getInstance().apply {
            set(2020, 0, 30)
        }.timeInMillis
        val markWeek = 10
        val markCalendar = Calendar.getInstance()
        markCalendar.firstDayOfWeek = Calendar.MONDAY
        println("本周第一天：${markCalendar.firstDayOfWeek}")
        markCalendar.timeInMillis = markTime
        markCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        markCalendar.clear(Calendar.HOUR)
        markCalendar.clear(Calendar.MINUTE)
        markCalendar.clear(Calendar.SECOND)
        println(markCalendar.time)
        println("所在周：${markCalendar.get(Calendar.WEEK_OF_YEAR)},时间戳：${markCalendar.timeInMillis}")
        val weekMillis = TimeUnit.DAYS.toMillis(7)
        val currCalendar = Calendar.getInstance()
        currCalendar.firstDayOfWeek = Calendar.MONDAY
        currCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val timeOffset = currCalendar.timeInMillis - markTime
        println("一周毫秒数：$weekMillis,间隔毫秒数：$timeOffset")
        val currWeek = markWeek + timeOffset / weekMillis
        println("当前周数：$currWeek")
    }
}
