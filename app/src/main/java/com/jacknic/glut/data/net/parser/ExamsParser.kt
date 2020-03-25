package com.jacknic.glut.data.net.parser

import com.jacknic.glut.data.model.Exam
import org.jsoup.nodes.Document

/**
 * 考试安排解析
 *
 * @author Jacknic
 */
class ExamsParser(document: Document) {

    private val examList = mutableListOf<Exam>()

    init {
        try {
            parseExam(document)
        } finally {
            // no-op
        }
    }

    private fun parseExam(document: Document) {
        val elements = document.select("table.datalist tr")
        elements.removeAt(0)
        for (e in elements) {
            val time = e.child(1).text()
            val name = e.child(2).text()
            val location = e.child(3).text()
            val bean = Exam(time, name, location)
            examList.add(bean)
        }
    }

    fun getExamList(): List<Exam> {
        return examList
    }


}