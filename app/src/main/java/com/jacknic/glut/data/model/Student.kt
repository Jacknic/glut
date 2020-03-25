package com.jacknic.glut.data.model

import androidx.annotation.Keep


/**
 * 学生信息
 */
@Keep
data class Student(
    /**
     * 学号
     */
    var sid: String = "",

    /**
     * 姓名
     */
    var name: String = "",

    /**
     * 班级
     */
    var className: String = "",

    /**
     * 出生日期
     */
    var birthday: String = "",

    /**
     * 籍贯
     */
    var place: String = "",

    /**
     * 证件号
     */
    var id: String = "",

    /**
     * 民族
     */
    var nation: String = "",

    /**
     * 政治面貌
     */
    var role: String = "",

    /**
     * 文化程度
     */
    var level: String = "",

    /**
     * 来源地
     */
    var origin: String = "",

    /**
     * 高考分数
     */
    var score: String = ""

)