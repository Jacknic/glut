package com.jacknic.glut.data.db.dao

import androidx.room.*
import com.jacknic.glut.data.db.entity.CourseInfo

/**
 * 课程信息数据访问
 *
 * @author Jacknic
 */
@Dao
interface CourseInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(courseInfo: CourseInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(courseInfoList: List<CourseInfo>)

    @Query("SELECT * FROM course_info ORDER BY id")
    suspend fun getAll(): List<CourseInfo>

    @Delete
    suspend fun delete(courseInfoList: List<CourseInfo>)

    @Query("DELETE FROM course_info WHERE id=:id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM course_info WHERE id=:courseId")
    suspend fun findById(courseId: Long): CourseInfo?

    /**
     * 获取学期列表
     **/
    @Query("""
        SELECT * FROM course_info 
        GROUP BY schoolYear,term 
        ORDER BY schoolYear DESC,term DESC
        """)
    suspend fun getTermList(): List<CourseInfo>

    /**
     * 删除学期课程信息
     */
    @Query("""
        DELETE FROM course_info
        WHERE schoolYear=:schoolYear AND term =:term
    """)
    suspend fun deleteInTerm(schoolYear: Int, term: Int): Int

    /**
     * 获取学期课程信息
     */
    @Query("""
        SELECT * FROM course_info
        WHERE schoolYear=:schoolYear AND term =:term
    """)
    suspend fun getInTerm(schoolYear: Int, term: Int): List<CourseInfo>


    /**
     * 按课程号获取
     */
    @Query("SELECT * FROM course_info WHERE courseNum=:courseNum LIMIT 1")
    suspend fun getByCourseNum(courseNum: String): CourseInfo?

}