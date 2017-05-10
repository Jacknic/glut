package com.jacknic.glut.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jacknic.glut.beans.course.CourseInfoBean;

/**
 * 课程信息
 */

public class CourseInfoDbModel {

    private SQLiteDatabase database;
    private final String TABLE_NAME = "course_info";


    public CourseInfoDbModel(Context context) {
        this.database = new CourseDBHelper(context).getWritableDatabase();
    }

    /**
     * 插入课程
     */
    public long insertCourseInfo(CourseInfoBean courseInfo) {
        ContentValues info = new ContentValues();
        info.put("id", courseInfo.getCourseNum());
        info.put("schoolYearStart", courseInfo.getSchoolYearStart());
        info.put("semester", courseInfo.getSemester());
        info.put("courseName", courseInfo.getCourseName());
        info.put("courseNum", courseInfo.getCourseNum());
        info.put("teacher", courseInfo.getTeacher());
        info.put("grade", courseInfo.getGrade());
        return database.insert(TABLE_NAME, null, info);
    }

    /**
     * 获取指定课表信息
     */
    public CourseInfoBean getCourseInfo(String course_num) {
        CourseInfoBean courseInfoBean = new CourseInfoBean();
        Cursor cursor = database.query(TABLE_NAME, null, "courseNum = ?",
                new String[]{course_num}, null, null, null);
        while (cursor.moveToNext()) {
            courseInfoBean.setId(cursor.getInt(0));
            courseInfoBean.setSchoolYearStart(cursor.getInt(1));
            courseInfoBean.setSemester(cursor.getInt(2));
            courseInfoBean.setCourseName(cursor.getString(3));
            courseInfoBean.setCourseNum(cursor.getString(4));
            courseInfoBean.setTeacher(cursor.getString(5));
            courseInfoBean.setGrade(cursor.getString(6));
        }
        cursor.close();
        return courseInfoBean;
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
