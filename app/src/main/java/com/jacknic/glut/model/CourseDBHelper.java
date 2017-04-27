package com.jacknic.glut.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 课程数据操作类
 */

public class CourseDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "course.db";
    private static final int DATABASE_VERSION = 1;

    CourseDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //  建立课程表
        db.execSQL("CREATE TABLE [course](" +
                "    [id] int, " +
                "    [courseNum] varchar, " +
                "    [courseName] varchar, " +
                "    [semester] int, " +
                "    [dayOfWeek] int, " +
                "    [weekType] int DEFAULT 0, " +
                "    [schoolYearStart] int, " +
                "    [smartPeriod] varchar, " +
                "    [week] varchar, " +
                "    [classroom] varchar, " +
                "    [courseEndSection] int, " +
                "    [courseStartSection] int);");
        //建立课程信息表
        db.execSQL("CREATE TABLE [course_info](" +
                "    [id] int PRIMARY KEY, " +
                "    [schoolYearStart] int, " +
                "    [semester] int, " +
                "    [courseName] varchar, " +
                "    [courseNum] varchar, " +
                "    [teacher] varchar, " +
                "    [grade] varchar);" +
                "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

}
