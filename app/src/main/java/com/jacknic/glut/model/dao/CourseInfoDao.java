package com.jacknic.glut.model.dao;

import com.jacknic.glut.bean.course.CourseInfoEntityDao;
import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.util.DataBase;

/**
 * 课程信息
 */

public class CourseInfoDao {

    private CourseInfoEntityDao courseInfoEntityDao;

    public CourseInfoDao() {
        courseInfoEntityDao = DataBase.getDaoSession().getCourseInfoEntityDao();
    }

    /**
     * 插入课程信息
     */
    public long insertCourseInfo(CourseInfoEntity courseInfo) {
        return courseInfoEntityDao.insert(courseInfo);
    }

    /**
     * 获取指定课程描述信息
     */
    public CourseInfoEntity getCourseInfo(String course_num) {
        return courseInfoEntityDao.queryBuilder().where(CourseInfoEntityDao.Properties.CourseNum.eq(course_num)).unique();
    }
}
