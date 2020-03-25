package com.jacknic.glut.data.net.parser

import com.jacknic.glut.data.model.Student
import org.jsoup.nodes.Document

/**
 * 学籍信息解析
 *
 * @author Jacknic
 */
class InfoParser(document: Document) {

    var student: Student? = null
        private set

    init {
        parseInfo(document)
    }

    private fun parseInfo(document: Document) {
        try {
            val student = Student()
            val sid = document.getElementsByAttributeValue("name", "username")[0].`val`()
            val name = document.getElementsByAttributeValue("name", "realname")[0].`val`()
            val birthday = document.getElementsByAttributeValue("name", "birthday")[0].`val`()
            val className = document.getElementById("classChange").text()
            val place = document.getElementsByAttributeValue("name", "nativePlace")[0].`val`()
            val id = document.getElementsByAttributeValue("name", "idno")[0].`val`()
            val nation = document.getElementsByAttributeValue("name", "folkid")[0].parent().text()
            val role = document.getElementsByAttributeValue("name", "politicalStatusId")[0].parent().text()
            val level = document.getElementsByAttributeValue("name", "literacyId")[0].parent().text()
            val origin = document.getElementsByAttributeValue("name", "stusourceId")[0].parent().text()
            val score = document.getElementsByAttributeValue("name", "entrExamScore")[0].`val`()
            student.also {
                it.sid = sid
                it.name = name
                it.birthday = birthday
                it.className = className
                it.place = place
                it.id = id
                it.nation = nation
                it.role = role
                it.level = level
                it.origin = origin
                it.score = score
            }
            this.student = student
        } catch (e: Exception) {
            println(e.message)
        }
    }
}