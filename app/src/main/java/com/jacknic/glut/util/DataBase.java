package com.jacknic.glut.util;

import com.jacknic.glut.model.entity.DaoMaster;
import com.jacknic.glut.model.entity.DaoSession;
import com.lzy.okgo.OkGo;

import org.greenrobot.greendao.database.Database;


/**
 * 数据库操作类
 */

public class DataBase {
    private static Database db;
    private static DaoSession mDaoSession;


    /**
     * 设置greenDao
     */
    private static void setDatabase() {
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(OkGo.getContext(), "course.db", null);
        db = mHelper.getWritableDb();
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
    public static Database getDb() {
        if (db == null) {
            setDatabase();
        }
        return db;
    }

}
