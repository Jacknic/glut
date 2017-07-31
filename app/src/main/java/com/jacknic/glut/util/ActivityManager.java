package com.jacknic.glut.util;

import android.app.Activity;

import java.util.Stack;

/**
 * Activity管理类
 */

public class ActivityManager extends Stack<Activity> {

    private static ActivityManager activityManager = new ActivityManager();

    /**
     * 单一实例
     */
    public static ActivityManager getManager() {
        return activityManager;
    }


    /**
     * 结束所有Activity
     */
    public static void finishAll() {
        while (!activityManager.empty()) {
            activityManager.pop().finish();
        }
        activityManager.clear();
    }
}
