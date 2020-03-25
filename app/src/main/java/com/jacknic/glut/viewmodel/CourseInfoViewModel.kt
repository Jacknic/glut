package com.jacknic.glut.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.jacknic.glut.data.db.entity.CourseInfo
import com.jacknic.glut.data.repository.local.CourseInfoRepository
import com.jacknic.glut.data.repository.local.CourseRepository
import com.jacknic.glut.data.repository.remote.JwcRepository
import com.jacknic.glut.util.request
import com.jacknic.glut.util.toast
import com.jacknic.glut.util.toastOnMain
import com.jacknic.glut.util.workerAction
import com.orhanobut.logger.Logger
import kotlinx.coroutines.Job

/**
 * 课程信息
 *
 * @author Jacknic
 */
class CourseInfoViewModel(private val app: Application) : AndroidViewModel(app) {

    private val courseInfoRepo = CourseInfoRepository()
    private val courseRepo = CourseRepository()
    private val jwcRepo = JwcRepository()
    private var fetchTermJob: Job? = null
    val termCourseInfoList = MutableLiveData<List<CourseInfo>>()
    val allCourseInfoList = MutableLiveData<List<CourseInfo>>()
    val needLogin = MutableLiveData<Boolean>()

    /**
     * 获取学期列表
     */
    fun loadTermList() = workerAction {
        val termList = courseInfoRepo.getTermList()
        termCourseInfoList.postValue(termList)
    }

    fun getTermCourses() = workerAction {
        val list = courseInfoRepo.getInTerm()
        allCourseInfoList.postValue(list)
    }

    fun fetchTerm(year: Int, term: Int) {
        if (fetchTermJob?.isActive == true) {
            app.toast("已有导入任务在执行")
            return
        }
        fetchTermJob = request(
            action = {
                app.toastOnMain("正在导入课表")
                jwcRepo.fetchCourses(year, term)
            },
            success = {
                if (it.data.getCourseInfoList().isEmpty()) {
                    app.toastOnMain("导入失败，课表为空！")
                } else {
                    loadTermList()
                    app.toastOnMain("导入课表成功")
                }
            },
            error = {
                if (it.exception is IllegalAccessException) {
                    needLogin.postValue(true)
                    app.toastOnMain("请登录验证后，再进行操作")
                } else {
                    app.toastOnMain(it.exception.message)
                }
            }
        )
    }

    fun deleteTerm(courseInfo: CourseInfo) = workerAction {
        val deleteCount = courseInfoRepo.deleteInTerm(courseInfo.schoolYear, courseInfo.term)
        val deleteCourseCount = courseRepo.deleteInTerm(courseInfo.schoolYear, courseInfo.term)
        loadTermList()
        Logger.d("删除课程：$deleteCount -> $deleteCourseCount")
        app.toastOnMain("删除成功")
    }

    fun deleteCourses(courseInfoList: List<CourseInfo>) = workerAction {
        courseRepo.deleteByCourseIds(courseInfoList.map { it.courseNum })
        courseInfoRepo.delete(courseInfoList)
    }
}