package com.jacknic.glut.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.data.db.entity.CourseInfo
import com.jacknic.glut.data.repository.local.CourseInfoRepository
import com.jacknic.glut.data.repository.local.CourseRepository
import com.jacknic.glut.util.Preferences
import com.jacknic.glut.util.toast
import com.jacknic.glut.util.toastOnMain
import com.jacknic.glut.util.workerAction

/**
 * 课程编辑
 *
 * @author Jacknic
 */
class ModifyCourseViewModel : ViewModel() {

    private val courseInfoRepo = CourseInfoRepository()
    private val courseRepo = CourseRepository()
    val changeEvent = MutableLiveData<Boolean>()
    private val courseInfo = MutableLiveData<CourseInfo>()
    val courseList = mutableListOf<Course>()
    val editedCourseSet = mutableSetOf<Course>()
    val deletedCourseSet = mutableSetOf<Course>()
    private val prefer = Preferences.getInstance()

    fun getCourseInfo(courseNum: String?): LiveData<CourseInfo> {
        if (courseNum.isNullOrEmpty()) {
            courseInfo.postValue(CourseInfo())
            changeEvent.postValue(true)
        } else {
            workerAction {
                val info = courseInfoRepo.getByCourseNum(courseNum) ?: CourseInfo()
                val courses = courseRepo.findByCourseNum(courseNum)
                courseList.addAll(courses)
                courseInfo.postValue(info)
                changeEvent.postValue(true)
            }.start()
        }
        return courseInfo
    }

    fun save() {
        courseInfo.value?.apply {
            if (courseName.isEmpty()) {
                prefer.app.toast("课程名不能为空")
                return
            }
            val courseNumber = if (courseNum.isEmpty()) {
                val newCourseNum = System.currentTimeMillis().toString()
                schoolYear = prefer.schoolYear
                term = prefer.term
                courseNum = newCourseNum
                newCourseNum
            } else courseNum
            workerAction {
                courseInfoRepo.save(this)
                if (editedCourseSet.isNotEmpty()) {
                    editedCourseSet.forEach {
                        it.courseName = courseName
                        it.courseNum = courseNumber
                        it.schoolYear = prefer.schoolYear
                        it.term = prefer.term
                    }
                    courseRepo.saveList(editedCourseSet.toList())
                    editedCourseSet.clear()
                }
                if (deletedCourseSet.isNotEmpty()) {
                    courseRepo.deleteList(deletedCourseSet.toList())
                    deletedCourseSet.clear()
                }
                prefer.app.toastOnMain("保存成功")
            }.start()
        }
    }

}