package com.jacknic.glut.model.dao;

import android.content.Context;
import android.content.SharedPreferences;

import com.jacknic.glut.model.entity.CourseEntity;
import com.jacknic.glut.model.entity.CourseEntityDao;
import com.jacknic.glut.util.Config;
import com.jacknic.glut.util.DataBase;
import com.jacknic.glut.util.Func;
import com.lzy.okgo.OkGo;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;
import java.util.Calendar;
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
        SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER, Context.MODE_PRIVATE);
        int school_year = prefer_jw.getInt(Config.JW_SCHOOL_YEAR, Calendar.getInstance().get(Calendar.YEAR));
        int term = prefer_jw.getInt(Config.JW_SEMESTER, 1);
        return courseEntityDao.queryBuilder()
                .where(CourseEntityDao.Properties.SmartPeriod.like("% " + week + " %"),
                        CourseEntityDao.Properties.SchoolStartYear.eq(school_year),
                        CourseEntityDao.Properties.StartSection.isNotNull(),
                        CourseEntityDao.Properties.EndSection.isNotNull(),
                        CourseEntityDao.Properties.Semester.eq(term))
                .orderAsc(CourseEntityDao.Properties.DayOfWeek, CourseEntityDao.Properties.StartSection)
                .list();
    }

    /**
     * 获取当日课表
     */
    public List<CourseEntity> getCourse() {
        SharedPreferences prefer_jw = OkGo.getContext().getSharedPreferences(Config.PREFER, Context.MODE_PRIVATE);
        Calendar calendar_now = Calendar.getInstance();
        int week_now = Func.getWeekNow();
        int school_year = prefer_jw.getInt(Config.JW_SCHOOL_YEAR, Calendar.getInstance().get(Calendar.YEAR));
        int term = prefer_jw.getInt(Config.JW_SEMESTER, 1);
        int weekDay = (calendar_now.get(Calendar.DAY_OF_WEEK) + 6) % 7;
        System.out.printf("当前周%d，当前学年%d,当前学期%d,当前星期%d\n", week_now, school_year, term, weekDay);
        return courseEntityDao.queryBuilder()
                .where(CourseEntityDao.Properties.SmartPeriod.like("% " + week_now + " %"),
                        CourseEntityDao.Properties.SchoolStartYear.eq(school_year),
                        CourseEntityDao.Properties.StartSection.isNotNull(),
                        CourseEntityDao.Properties.EndSection.isNotNull(),
                        CourseEntityDao.Properties.DayOfWeek.eq(weekDay == 0 ? 7 : weekDay),
                        CourseEntityDao.Properties.Semester.eq(term))
                .orderAsc(CourseEntityDao.Properties.StartSection)
                .list();
    }

    /**
     * 获取学期课表信息
     */
    public List<CourseEntity> getTermsCourse() {
        WhereCondition.StringCondition groupBy = new WhereCondition.StringCondition(" 1==1 GROUP BY "
                + CourseEntityDao.Properties.SchoolStartYear.columnName + ","
                + CourseEntityDao.Properties.Semester.columnName
        );
        List<CourseEntity> courseEntities = courseEntityDao.queryBuilder()
                .where(groupBy)
                .orderDesc(CourseEntityDao.Properties.SchoolStartYear, CourseEntityDao.Properties.Semester)
                .list();
        return courseEntities;
    }

    /**
     * 插入多个
     */
    public void insertCourses(ArrayList<CourseEntity> courses) {
        courseEntityDao.insertInTx(courses);
    }
}
