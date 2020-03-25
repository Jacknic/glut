package com.jacknic.glut.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jacknic.glut.R
import com.jacknic.glut.data.model.Exam
import com.jacknic.glut.data.net.parser.ExamsParser
import com.jacknic.glut.data.util.FILE_NAME_ALL_EXAM
import com.jacknic.glut.util.request
import com.jacknic.glut.util.toastOnMain
import com.orhanobut.logger.Logger
import org.jsoup.Jsoup
import java.io.File

/**
 * 考试安排
 *
 * @author Jacknic
 */
class ExamListViewModel : BaseRequestViewModel() {

    private val examList = MutableLiveData<List<Exam>>()

    fun getExamList(): LiveData<List<Exam>> {
        if (examList.value == null) {
            loadExamList()
        }
        return examList
    }

    private fun loadExamList() {
        val file = File(prefer.app.filesDir, FILE_NAME_ALL_EXAM)
        if (file.exists()) {
            try {
                parseFile(file)
            } catch (e: Exception) {
                examList.postValue(emptyList())
                Logger.e(e, "解析考试安排失败")
            }
        }
    }

    private fun parseFile(file: File) {
        if (file.exists()) {
            val dom = Jsoup.parse(file, Charsets.UTF_8.name())
            val examsParser = ExamsParser(dom)
            val exams = examsParser.getExamList()
            examList.postValue(exams)
        }
    }

    fun fetchExam() {
        checkJwRequest {
            request(
                { jwcRepo.fetchAllExam() },
                {
                    loadExamList()
                    val message = prefer.app.getString(R.string.data_load_success)
                    prefer.app.toastOnMain(message)
                },
                { prefer.app.toastOnMain(it.exception.message) }
            )
        }
    }

}