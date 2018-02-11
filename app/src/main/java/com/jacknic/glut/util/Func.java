package com.jacknic.glut.util;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.StringCallback;

import java.io.File;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * 常用函数
 */

public final class Func {
    /**
     * 获取整型数
     *
     * @param str_value
     * @param defaultValue
     * @return
     */
    public static int getInt(String str_value, int defaultValue) {
        int value = defaultValue;
        try {
            value = Integer.parseInt(str_value);
        } catch (Exception e) {
            Log.d(TAG, "getInt: " + e.getMessage());
        }
        return value;
    }

    /**
     * 课程节数转序号
     *
     * @param index
     * @return
     */
    public static String courseIndexToStr(Integer index) {
        if (index == null) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        if (index > 4) {
            switch (index) {
                case 5:
                    stringBuilder.append("中午 1");
                    break;
                case 6:
                    stringBuilder.append("中午 2");
                    break;
                default:
                    index -= 2;
                    stringBuilder.append(index);
                    break;
            }
        } else {
            stringBuilder.append(index);
        }
        return stringBuilder.toString();
    }

    /**
     * 递归删除文件
     *
     * @param file 文件
     */
    public static void deleteFile(File file) {
        if (file.isDirectory()) {
            for (File fileItem : file.listFiles()) {
                deleteFile(fileItem);
            }
            file.delete();
        } else {
            file.delete();
        }
    }

    /**
     * 检验教务用户登录状态
     *
     * @param callback 回调
     */
    public static void checkLoginStatus(@NonNull final AbsCallback callback) {
        OkGo.get("http://202.193.80.58:81/academic/showHeader.do").execute(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                callback.onSuccess(s, call, response);
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                callback.onError(call, response, e);
            }

            @Override
            public void onAfter(String s, Exception e) {
                callback.onAfter(s, e);
            }
        });
    }

    /**
     * 获取实际当前周
     */
    public static int getWeekNow() {
        SharedPreferences prefer = PreferManager.getPrefer();
        Calendar calendar_now = Calendar.getInstance();
        int select_week = prefer.getInt(Config.JW_WEEK_SELECT, 1);
        int year_week_old = prefer.getInt(Config.JW_YEAR_WEEK_OLD, calendar_now.get(Calendar.WEEK_OF_YEAR));
        int year_week_now = calendar_now.get(Calendar.WEEK_OF_YEAR);
        int week_now = select_week + (year_week_now - year_week_old);
        if (year_week_now > year_week_old && calendar_now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            week_now -= 1;
        }
        return week_now;
    }

    /**
     * 更新小部件
     */
    public static void updateWidget(Context context) {
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(intent);
    }


}
