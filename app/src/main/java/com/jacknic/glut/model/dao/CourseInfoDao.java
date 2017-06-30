package com.jacknic.glut.model.dao;

import com.jacknic.glut.model.entity.CourseInfoEntity;
import com.jacknic.glut.model.entity.CourseInfoEntityDao;
import com.jacknic.glut.util.DataBase;

import java.util.ArrayList;

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
        return courseInfoEntityDao.queryBuilder().where(CourseInfoEntityDao.Properties.CourseNum.eq(course_num)).limit(1).unique();
    }

    /**
     * 插入多个
     */
    public void insertCourseInfoList(ArrayList<CourseInfoEntity> courseInfoList) {
        courseInfoEntityDao.insertInTx(courseInfoList);
    }
}
