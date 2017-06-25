package com.jacknic.glut.util;

import android.database.sqlite.SQLiteDatabase;

import com.jacknic.glut.bean.course.DaoMaster;
import com.jacknic.glut.bean.course.DaoSession;
import com.lzy.okgo.OkGo;


/**
 * 数据库操作类
 */

public class DataBase {
    private static SQLiteDatabase db;
    private static DaoSession mDaoSession;


    /**
     * 设置greenDao
     */
    private static void setDatabase() {
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(OkGo.getContext(), "course.db", null);
        db = mHelper.getWritableDatabase();
        DaoMaster mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 获取会话
     */
    public static DaoSession getDaoSession() {
        if (mDaoSession == null) {
            setDatabase();
        }
        return mDaoSession;
    }

    /**
     * 获取数据库对象
     */
    public static SQLiteDatabase getDb() {
        if (db == null) {
            setDatabase();
        }
        return db;
    }

}
