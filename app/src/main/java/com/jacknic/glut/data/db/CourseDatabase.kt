package com.jacknic.glut.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jacknic.glut.data.db.dao.CourseDao
import com.jacknic.glut.data.db.dao.CourseInfoDao
import com.jacknic.glut.data.db.entity.Course
import com.jacknic.glut.data.db.entity.CourseInfo

/**
 * 数据库
 *
 * @author Jacknic
 */
@Database(
    version = 1,
    exportSchema = false,
    entities = [Course::class, CourseInfo::class]
)
abstract class CourseDatabase : RoomDatabase() {

    abstract fun getCourseDao(): CourseDao

    abstract fun getCourseInfoDao(): CourseInfoDao

    companion object {

        private const val DB_NAME = "course_v3"
        @Volatile
        private var instance: CourseDatabase? = null

        /**
         * 创建数据库
         */
        fun create(context: Context, memoryOnly: Boolean): CourseDatabase {
            return if (memoryOnly) {
                Room.inMemoryDatabaseBuilder(context, CourseDatabase::class.java).build()
            } else {
                Room.databaseBuilder(context, CourseDatabase::class.java, DB_NAME)
                    .allowMainThreadQueries()
                    .build()
            }
        }

        fun getInstance(context: Context): CourseDatabase {
            return instance ?: synchronized(this) {
                instance ?: create(context, false).also { instance = it }
            }
        }
    }
}