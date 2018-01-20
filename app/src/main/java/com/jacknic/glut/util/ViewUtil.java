package com.jacknic.glut.util;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lzy.okgo.OkGo;

/**
 * 视图工具类
 */

public class ViewUtil {

    /**
     * 设置actionBar标题
     */
    public static void setTitle(Context context, String title) {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = ((AppCompatActivity) context);
            ActionBar supportActionBar = activity.getSupportActionBar();
            if (supportActionBar != null) {
                supportActionBar.setTitle(title);
            }
        }

    }

    /**
     * dp转换px
     */
    public static int dip2px(float dpValue) {
        float scale = OkGo.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 显示工具栏视图
     */
    public static void showToolbar(AppCompatActivity activity, boolean show) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            if (show) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }
    }

    /**
     * 设置工具栏返回按钮
     */
    public static void showBackIcon(Context context, boolean isShow) {
        if (!(context instanceof AppCompatActivity)) return;
        ActionBar supportActionBar = ((AppCompatActivity) context).getSupportActionBar();
        if (supportActionBar != null) {
            Log.d(context.getClass().getName(), "showBackIcon: " + isShow);
            supportActionBar.setDisplayHomeAsUpEnabled(isShow);
        }
    }
}
