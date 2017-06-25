package com.jacknic.glut.model.dao;

import com.jacknic.glut.bean.course.CourseEntityDao;
import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.util.DataBase;

import java.util.List;

/**
 * 数据库交互模型
 */

public class CourseDao {
    private CourseEntityDao courseEntityDao;

    public CourseDao() {
        courseEntityDao = DataBase.getDaoSession().getCourseEntityDao();
    }

    /**
     * 插入课程
     *
     * @param course 课程对象
     */
    public void insertCourse(CourseEntity course) {
        courseEntityDao.insert(course);
    }

    /**
     * 获取指定周数课表
     *
     * @param week 周数
     * @return 课表列表
     */
    public List<CourseEntity> getCourse(int week) {
        return courseEntityDao.queryBuilder()
                .where(CourseEntityDao.Properties.SmartPeriod.like("% " + week + " %"))
                .orderAsc(CourseEntityDao.Properties.DayOfWeek, CourseEntityDao.Properties.StartSection)
                .list();
    }

}
