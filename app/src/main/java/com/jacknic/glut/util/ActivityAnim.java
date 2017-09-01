package com.jacknic.glut.util;

import android.app.Activity;

import com.jacknic.glut.R;

/**
 * activity场景动画工具类
 */

public class ActivityAnim {

    /**
     * 场景左出
     *
     * @param activity
     */
    public static void leftOut(Activity activity, boolean finish) {
        activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        if (finish) activity.finish();
    }

    /**
     * 场景右出
     *
     * @param activity
     */
    public static void rightOut(Activity activity, boolean finish) {
        activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        if (finish) activity.finish();
    }
}
