package com.jacknic.glut.util

import androidx.annotation.IntRange
import java.util.*

/**
 * 时间类方法
 *
 * @author Jacknic
 */
object TimeUtils {

    @JvmStatic
    fun getWeekDay(dayOfWeek: Int): String {
        return when (dayOfWeek) {
            1 -> "一"
            2 -> "二"
            3 -> "三"
            4 -> "四"
            5 -> "五"
            6 -> "六"
            7 -> "日"
            else -> "?"
        }
    }

    @JvmStatic
    fun term2text(@IntRange(from = 1, to = 2) term: Int) = if (term == 1) "春" else "秋"

    @JvmStatic
    fun courseIndex2Text(index: Int, delimiter: String = ""): String {
        return when {
            index <= 4 -> index.toString()
            index == 5 -> "中${delimiter}午${delimiter}1"
            index == 6 -> "中${delimiter}午${delimiter}2"
            index >= 7 -> (index - 2).toString()
            else -> ""
        }
    }

    /**
     * 获取当前周几
     * */
    @JvmStatic
    fun getCurrDayOfWeek(): Int {
        val calendar = Calendar.getInstance()
        val dayOf = calendar.get(Calendar.DAY_OF_WEEK)
        return getFixDayOfWeek(dayOf)
    }

    /**
     * 修正当前星期几
     * 即 1..7 为 周一..周日
     */
    @JvmStatic
    fun getFixDayOfWeek(dayOf: Int): Int {
        var dayOfWeek = dayOf - 1
        if (dayOfWeek == 0) {
            dayOfWeek = 7
        }
        return dayOfWeek
    }
}