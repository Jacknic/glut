package com.jacknic.glut.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jacknic.glut.beans.course.CourseBean;

import java.util.ArrayList;

/**
 * 数据库交互模型
 */

public class CourseDbModel {
    private SQLiteDatabase database;
    private final String TABLE_NAME = "course";
    public static final String
            ID = "id",
            COURSE_NUM = "courseNum",
            COURSE_NAME = "courseName",
            SEMESTER = "semester",
            DAY_OF_WEEK = "dayOfWeek",
            WEEK_TYPE = "weekType",
            SCHOOL_YEAR_START = "schoolYearStart",
            SMART_PERIOD = "smartPeriod",
            WEEK = "week",
            CLASSROOM = "classroom",
            COURSE_END_SECTION = "courseEndSection",
            COURSE_START_SECTION = "courseStartSection";
    public static final int
            ID_INDEX = 0,
            COURSE_NUM_INDEX = 1,
            COURSE_NAME_INDEX = 2,
            SEMESTER_INDEX = 3,
            DAY_OF_WEEK_INDEX = 4,
            WEEK_TYPE_INDEX = 5,
            SCHOOL_YEAR_START_INDEX = 6,
            SMART_PERIOD_INDEX = 7,
            WEEK_INDEX = 8,
            CLASSROOM_INDEX = 9,
            COURSE_END_SECTION_INDEX = 10,
            COURSE_START_SECTION_INDEX = 11;

    public CourseDbModel(Context context) {
        this.database = new CourseDBHelper(context).getWritableDatabase();
    }

    /**
     * 插入课程
     *
     * @param course 课程对象
     */
    public void insertCourse(CourseBean course) {
        ContentValues values = new ContentValues();
        values.put(COURSE_NUM, course.getCourseNum());
        values.put(COURSE_NAME, course.getCourseName());
        values.put(SEMESTER, course.getSemester());
        values.put(DAY_OF_WEEK, course.getDayOfWeek());
        values.put(WEEK_TYPE, course.getWeekType());
        values.put(SCHOOL_YEAR_START, course.getSchoolStartYear());
        values.put(SMART_PERIOD, course.getSmartPeriod());
        values.put(WEEK, course.getWeek());
        values.put(CLASSROOM, course.getClassRoom());
        values.put(COURSE_START_SECTION, course.getStartSection());
        values.put(COURSE_END_SECTION, course.getEndSection());
        database.insert(TABLE_NAME, null, values);
    }

    /**
     * 获取指定周数课表
     *
     * @param week 周数
     * @return 课表列表
     */
    public ArrayList<CourseBean> getCourse(int week) {
        ArrayList<CourseBean> courses = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, SMART_PERIOD + " like ?",
                new String[]{"% " + week + " %"}, null, null, DAY_OF_WEEK + "," + COURSE_START_SECTION);
        while (cursor.moveToNext()) {
            CourseBean course = new CourseBean();
            course.setCourseName(cursor.getString(COURSE_NAME_INDEX));
            course.setCourseNum(cursor.getString(COURSE_NUM_INDEX));
            course.setSemester(cursor.getInt(SEMESTER_INDEX));
            course.setSchoolStartYear(cursor.getInt(SCHOOL_YEAR_START_INDEX));
            course.setWeekType(cursor.getInt(WEEK_TYPE_INDEX));
            course.setWeek(cursor.getString(WEEK_INDEX));
            course.setStartSection(cursor.getInt(COURSE_START_SECTION_INDEX));
            course.setEndSection(cursor.getInt(COURSE_END_SECTION_INDEX));
            course.setDayOfWeek(cursor.getInt(DAY_OF_WEEK_INDEX));
            course.setClassRoom(cursor.getString(CLASSROOM_INDEX));
            course.setId(cursor.getInt(ID_INDEX));
            course.setSmartPeriod(cursor.getString(SMART_PERIOD_INDEX));
            courses.add(course);
        }
        cursor.close();
        return courses;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        if (database != null) {
            database.close();
        }
    }
}
