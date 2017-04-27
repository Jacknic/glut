package com.jacknic.glut.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.Stack;

/**
 * Activity工具类
 */

public class ActivityUtil {
    /**
     * Activity栈
     */
    public static Stack<Activity> activities = new Stack<>();

    /**
     * 启动Activity
     *
     * @param context    上下文
     * @param toActivity 要启动的Activity
     */
    public static void lunchActivity(Context context, Class toActivity) {
        Intent intent = new Intent(context, toActivity);
        context.startActivity(intent);
    }

    /**
     * 清除栈
     */
    public static void cleanActivities() {
        while (!activities.empty()) {
            activities.pop().finish();
        }
    }
}
