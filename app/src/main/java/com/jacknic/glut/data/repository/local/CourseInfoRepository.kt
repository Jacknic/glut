package com.jacknic.glut.data.repository.local

import com.jacknic.glut.data.db.CourseDatabase
import com.jacknic.glut.data.db.entity.CourseInfo
import com.jacknic.glut.util.Preferences

/**
 * 课表信息访问
 *
 * @author Jacknic
 */
class CourseInfoRepository {

    private val prefer = Preferences.getInstance()
    private val courseInfoDao = CourseDatabase.getInstance(prefer.app).getCourseInfoDao()

    suspend fun getTermList() = courseInfoDao.getTermList()

    suspend fun getInTerm(year: Int? = null, term: Int? = null): List<CourseInfo> {
        val yearVal = year ?: prefer.schoolYear
        val termVal = term ?: prefer.term
        return courseInfoDao.getInTerm(yearVal, termVal)
    }

    suspend fun deleteInTerm(schoolYear: Int, term: Int) =
        courseInfoDao.deleteInTerm(schoolYear, term)

    suspend fun delete(courseInfoList: List<CourseInfo>) = courseInfoDao.delete(courseInfoList)

    suspend fun getByCourseNum(courseNum: String) = courseInfoDao.getByCourseNum(courseNum)

    suspend fun save(courseInfo: CourseInfo) = courseInfoDao.save(courseInfo)
}