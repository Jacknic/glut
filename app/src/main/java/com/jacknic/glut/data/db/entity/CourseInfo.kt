package com.jacknic.glut.data.db.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 课程信息
 */
@Keep
@Entity(
    tableName = "course_info",
    indices = [Index(value = arrayOf("courseNum"), unique = true)]
)
data class CourseInfo(
    /**
     * 主鍵，自增
     */
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var term: Int = 0,
    var schoolYear: Int = 0,
    var courseName: String = "",
    var courseNum: String = "",
    var teacher: String = "",
    var grade: String = ""
)
