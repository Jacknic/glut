package com.jacknic.glut.viewmodel

import android.app.Application
import androidx.annotation.MainThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.data.repository.local.CourseRepository
import com.jacknic.glut.util.Preferences
import com.jacknic.glut.util.TimeUtils
import com.jacknic.glut.util.updateWidget
import com.jacknic.glut.util.workerAction
import java.util.*

/**
 * 课表相关
 *
 * @author Jacknic
 */
class CourseViewModel(val app: Application) : AndroidViewModel(app) {

    private val prefer = Preferences.getInstance()
    private val courseRepo = CourseRepository()
    val currWeek = MutableLiveData<Int>()
    val addonsWeek = MutableLiveData<Int>()
    val needRefresh = MutableLiveData<Boolean>()
    val endWeek = MutableLiveData<Int>()
    val schoolYear = MutableLiveData<Int>()
    val term = MutableLiveData<Int>()
    val weekCourseList = MutableLiveData<List<Course>>()
    private val todayCourseList = MutableLiveData<List<Course>>()
    val weekDayList = MutableLiveData<MutableList<String>>()

    init {
        notifyChanged()
    }

    private fun loadData() {
        endWeek.value = prefer.endWeek
        currWeek.value = prefer.currWeek
        addonsWeek.value = 0
        schoolYear.value = prefer.schoolYear
        term.value = prefer.term
    }

    fun getTodayCourseList(): LiveData<List<Course>> {
        val value = todayCourseList.value
        if (value == null) {
            loadTodayCourses()
        }
        return todayCourseList
    }

    private fun loadTodayCourses() {
        workerAction {
            val todayCourses = courseRepo.getTodayCourse()
            todayCourseList.postValue(todayCourses)
        }
    }

    private fun loadWeekCourses() {
        val year = schoolYear.value!!
        val semester = term.value!!
        val week = currWeek.value!! + addonsWeek.value!!
        workerAction {
            val weekCourses = courseRepo.getWeekCourses(year, semester, week)
            weekCourseList.postValue(weekCourses)
        }
    }

    private fun makeWeekDayList() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, addonsWeek.value!! * 7)
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, -1)
        }
        val dayList = mutableListOf<String>()
        dayList.add("${calendar.get(Calendar.MONTH) + 1}\n月")
        for (i in 0..6) {
            val dayOfWeek = TimeUtils.getFixDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK))
            val weekDay = TimeUtils.getWeekDay(dayOfWeek)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            dayList.add("周$weekDay\n$dayOfMonth")
            calendar.add(Calendar.DATE, 1)
        }
        weekDayList.postValue(dayList)
    }

    @MainThread
    fun setCurrWeek(newValue: Int) {
        if (newValue != currWeek.value) {
            prefer.markWeek = newValue
            notifyChanged()
        }
    }

    @MainThread
    fun setAddonsWeek(newValue: Int) {
        if (newValue != addonsWeek.value) {
            addonsWeek.value = newValue
            makeWeekDayList()
            loadWeekCourses()
        }
    }

    /**
     * 重新加载数据
     */
    fun notifyChanged() {
        needRefresh.postValue(false)
        loadData()
        loadTodayCourses()
        loadWeekCourses()
        makeWeekDayList()
        app.updateWidget()
    }
}