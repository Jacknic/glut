package com.jacknic.glut.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.jacknic.glut.R
import com.jacknic.glut.data.util.FILE_NAME_STUDY_PROCESS
import com.jacknic.glut.util.request
import com.jacknic.glut.util.toastOnMain
import com.orhanobut.logger.Logger
import org.jsoup.Jsoup
import java.io.File

/**
 * 课程进度加载
 *
 * @author Jacknic
 */
class ProcessViewModel : BaseRequestViewModel() {

    private val elementList = MediatorLiveData<List<String>>()

    fun getElementList(): LiveData<List<String>> {
        if (elementList.value == null) {
            loadElementList()
        }
        return elementList
    }

    private fun loadElementList() {
        val file = File(prefer.app.filesDir, FILE_NAME_STUDY_PROCESS)
        if (file.exists()) {
            try {
                parseFile(file)
            } catch (e: Exception) {
                Logger.e(e, "解析学业进度失败")
            }
        }
    }

    private fun parseFile(file: File) {
        val mItemList = mutableListOf<String>()
        val dom = Jsoup.parse(file, Charsets.UTF_8.name())
        val trs = dom.getElementsByTag("tr")
        for (tr in trs) {
            tr.child(0).remove()
            for (element in tr.children()) {
                mItemList.add(element.text())
            }
        }
        elementList.postValue(mItemList)
    }

    fun fetchProcess() {
        checkJwRequest {
            request(
                { jwcRepo.fetchStudyProcess() },
                {
                    loadElementList()
                    val message = prefer.app.getString(R.string.data_load_success)
                    prefer.app.toastOnMain(message)
                },
                { prefer.app.toastOnMain(it.exception.message) }
            )
        }
    }

}