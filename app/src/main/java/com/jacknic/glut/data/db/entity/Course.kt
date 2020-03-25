package com.jacknic.glut.data.db.entity

import androidx.annotation.IntRange
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 课程实体类
 */
@Keep
@Entity(tableName = "course")
data class Course(

    /**
     * 主鍵，自增
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    /**
     * 学年开始年
     */
    var schoolYear: Int = 0,

    /**
     * 学期
     */
    var term: Int = 0,

    /**
     * 星期几
     *
     * 1 周一... 7周日
     */
    var dayOfWeek: Int = 0,

    /**
     * 第几节课开始
     */
    var startSection: Int = 0,

    /**
     * 第几节课结束
     */
    var endSection: Int = 0,

    /**
     * 课程名
     */
    var courseName: String = "",

    /**
     * 课程编号
     */
    var courseNum: String = "",

    /**
     * 教室
     */
    var classRoom: String = "",

    /**
     * 课程周
     */
    var week: String = "",

    /**
     * 标记是否是单双周
     * 0 每周,1 单周，2 双周
     */
    @IntRange(from = 0, to = 2)
    var weekType: Int = 0,

    /**
     * 开课周
     */
    var smartPeriod: String = ""
)
