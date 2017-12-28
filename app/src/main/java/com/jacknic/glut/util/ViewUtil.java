package com.jacknic.glut.util;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.lzy.okgo.OkGo;

/**
 * 视图工具类
 */

public class ViewUtil {

    /**
     * 设置actionBar标题
     */
    public static void setTitle(AppCompatActivity activity, String title) {
        ActionBar supportActionBar = activity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(title);
        }
    }

    /**
     * dp转换px
     */
    public static int dip2px(float dpValue) {
        float scale = OkGo.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
