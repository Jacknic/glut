package com.jacknic.glut.data.repository.local

import com.jacknic.glut.data.db.CourseDatabase
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.util.Preferences
import com.jacknic.glut.util.TimeUtils

/**
 * 课表数据存储
 *
 * @author Jacknic
 */
class CourseRepository {

    private val prefer = Preferences.getInstance()
    private val dao = CourseDatabase.getInstance(prefer.app).getCourseDao()

    suspend fun save(course: Course) = dao.save(course)

    suspend fun saveList(courseList: List<Course>) = dao.saveAll(courseList)

    suspend fun delete(course: Course) = dao.deleteById(course.id)

    suspend fun getAll() = dao.getAll()

    suspend fun deleteList(courseList: List<Course>) = dao.delete(courseList)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun findById(courseId: Long): Course? = dao.findById(courseId)

    suspend fun getWeekCourses(year: Int, semester: Int, week: Int) =
        dao.getWeekCourses(year, semester, week)

    suspend fun getTodayCourse(): List<Course> {
        val dayOfWeek = TimeUtils.getCurrDayOfWeek()
        val schoolYear = prefer.schoolYear
        val term = prefer.term
        val currWeek = prefer.currWeek
        return dao.getDayCourses(schoolYear, term, currWeek, dayOfWeek)
    }

    suspend fun deleteInTerm(schoolYear: Int, term: Int) = dao.deleteInTerm(schoolYear, term)

    suspend fun deleteByCourseIds(courseNumList: List<String>) =
        dao.deleteByCourseIds(courseNumList)

    suspend fun findByCourseNum(courseNum: String): List<Course> {
        return dao.findByCourseNum(courseNum)
    }
}