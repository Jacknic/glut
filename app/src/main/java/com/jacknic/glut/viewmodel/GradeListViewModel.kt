package com.jacknic.glut.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jacknic.glut.R
import com.jacknic.glut.data.util.FILE_NAME_GRADE
import com.jacknic.glut.util.request
import com.jacknic.glut.util.toastOnMain
import com.orhanobut.logger.Logger
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.io.File

/**
 * 成绩查询
 *
 * @author Jacknic
 */
class GradeListViewModel : BaseRequestViewModel() {

    private val gradeList = MutableLiveData<Elements>()

    fun getGradeList(): LiveData<Elements> {
        if (gradeList.value == null) {
            loadGradeList()
        }
        return gradeList
    }

    private fun loadGradeList() {
        val file = File(prefer.app.filesDir, FILE_NAME_GRADE)
        if (file.exists()) {
            try {
                parseFile(file)
            } catch (e: Exception) {
                gradeList.postValue(Elements())
                Logger.e(e, "解析成绩列表失败")
            }
        } else {
            fetchGrade()
        }
    }

    private fun parseFile(file: File) {
        if (file.exists()) {
            val dom = Jsoup.parse(file, Charsets.UTF_8.name())
            val elements = dom.select("table.datalist tr")
            gradeList.postValue(elements)
        }
    }

    fun fetchGrade() {
        checkJwRequest {
            request(
                { jwcRepo.fetchAllGrade() },
                {
                    loadGradeList()
                    val message = prefer.app.getString(R.string.data_load_success)
                    prefer.app.toastOnMain(message)
                },
                { prefer.app.toastOnMain(it.exception.message) }
            )
        }
    }

}