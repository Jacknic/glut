package com.jacknic.glut.data.db.dao

import androidx.room.*
import com.jacknic.glut.data.db.entity.Course

/**
 * 课程安排数据访问
 *
 * @author Jacknic
 */
@Dao
interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(courses: Course)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(courseList: List<Course>)

    @Query("SELECT * FROM course ORDER BY id")
    suspend fun getAll(): List<Course>

    @Delete
    suspend fun delete(courseList: List<Course>)

    @Query("DELETE FROM course WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM course WHERE id=:courseId")
    suspend fun findById(courseId: Long): Course?

    /**
     * 获取指定周数课表
     *
     * @param year 学年
     * @param term 学期
     * @param week 周数
     **/
    @Query("""
        SELECT * FROM course  
            WHERE term=:term 
            AND schoolYear=:year 
            AND  smartPeriod LIKE '% ' || :week || ' %'
            ORDER BY dayOfWeek ASC,startSection ASC
        """)
    suspend fun getWeekCourses(year: Int, term: Int, week: Int): List<Course>

    /**
     * 获取指定星期几当天课表
     *
     * @param year 学年
     * @param term 学期
     * @param week 周数
     * @param dayOfWeek 星期几
     **/
    @Query("""
        SELECT * FROM course  
            WHERE term=:term 
            AND schoolYear=:year 
            AND dayOfWeek=:dayOfWeek 
            AND  smartPeriod LIKE '% ' || :week || ' %'
            ORDER BY startSection ASC
        """)
    suspend fun getDayCourses(year: Int, term: Int, week: Int, dayOfWeek: Int): List<Course>

    /**
     * 获取指定周数课表
     *
     * @param year 学年
     * @param term 学期
     * @param week 周数
     **/
    @Query("""
        SELECT * FROM course  
            WHERE term=:term 
            AND schoolYear=:year
            AND  smartPeriod LIKE '% ' || :week || ' %'
            GROUP BY schoolYear
            ORDER BY dayOfWeek,startSection ASC
        """)
    suspend fun getTermCourses(year: Int, term: Int, week: Int): List<Course>

    /**
     * 删除学期课程安排
     */
    @Query("""
        DELETE FROM course
        WHERE schoolYear=:schoolYear AND term =:term
    """)
    suspend fun deleteInTerm(schoolYear: Int, term: Int): Int

    /**
     * 按课程号删除课程安排
     */
    @Query("""
        DELETE FROM course
        WHERE courseNum in (:courseNumList)
    """)
    suspend fun deleteByCourseIds(courseNumList: List<String>): Int

    @Query("""
        SELECT * FROM course 
        WHERE courseNum=:courseNum
        ORDER BY dayOfWeek
        """)
    suspend fun findByCourseNum(courseNum: String): List<Course>
}