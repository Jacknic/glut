package com.jacknic.glut.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.lzy.okgo.OkGo;

import java.util.Stack;

/**
 * Activity工具类
 */

public class ActivityUtil {

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

    private static Stack<Activity> activityStack = new Stack<>();

    /**
     * 单一实例
     */
    public static Stack<Activity> getActivityStack() {
        return activityStack;
    }

    /**
     * 添加Activity到堆栈
     */
    public static void pushActivity(Activity activity) {
        activityStack.push(activity);
    }

    /**
     * 堆栈移除Activity
     */
    public static void removeActivity(Activity activity) {
        activityStack.remove(activity);
    }


    /**
     * 结束所有Activity
     */
    public static void finishAllActivity() {
        while (!activityStack.empty()) {
            activityStack.pop().finish();
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public static void appExit(Activity activity) {
        try {
            finishAllActivity();
            //取消所有请求
            OkGo.getInstance().cancelAll();
            activity.finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
