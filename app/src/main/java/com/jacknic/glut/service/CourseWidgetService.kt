package com.jacknic.glut.service

import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.RemoteViewsService.RemoteViewsFactory
import com.jacknic.glut.MainActivity
import com.jacknic.glut.R
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.data.repository.local.CourseRepository
import com.jacknic.glut.util.Preferences
import com.jacknic.glut.util.TimeUtils
import kotlinx.coroutines.runBlocking

/**
 * 桌面课程列表更新服务
 *
 * @author Jacknic
 */
class CourseWidgetService : RemoteViewsService(), RemoteViewsFactory {

    private val prefer = Preferences.getInstance()
    private val courseList = mutableListOf<Course>()
    private val courseRepo = CourseRepository()

    override fun onGetViewFactory(intent: Intent?) = this
    override fun getLoadingView() = null
    override fun getItemId(position: Int) = position.toLong()
    override fun onDataSetChanged() = loadCourses()
    override fun hasStableIds() = true
    override fun getCount() = courseList.size
    override fun getViewTypeCount() = 1

    private fun loadCourses() {
        runBlocking {
            courseList.clear()
            courseList.addAll(courseRepo.getTodayCourse())
        }
    }

    override fun getViewAt(position: Int): RemoteViews {
        val course = courseList[position]
        val remoteViews = RemoteViews(packageName, R.layout.item_course_widget)
        remoteViews.setTextViewText(R.id.tvCourseName, "课程：${course.courseName}")
        remoteViews.setTextViewText(R.id.tvTeacherName, "地点：${course.classRoom}")
        val startText = TimeUtils.courseIndex2Text(course.startSection)
        val endText = TimeUtils.courseIndex2Text(course.endSection)
        remoteViews.setTextViewText(R.id.tvCourseArrange, "$startText-$endText")
        val mainIntent = Intent(this, MainActivity::class.java)
        remoteViews.setOnClickFillInIntent(R.id.courseWidgetItem, mainIntent)
        return remoteViews
    }

}