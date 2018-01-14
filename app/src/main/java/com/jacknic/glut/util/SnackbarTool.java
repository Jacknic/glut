package com.jacknic.glut.util;

import android.app.Activity;
import android.support.design.widget.Snackbar;

/**
 * 提示条管理工具
 */

public class SnackbarTool {
    private static Snackbar snackbar;

    private SnackbarTool() {
    }

    /**
     * 调用前必须进行初始化
     */
    public static void init(Activity activity) {
        snackbar = Snackbar.make(activity.getWindow().getDecorView(), "", Snackbar.LENGTH_SHORT);
    }

    public static Snackbar getSnackBar() {
        snackbar.setDuration(Snackbar.LENGTH_SHORT);
        return snackbar;
    }

    /**
     * 显示类型
     */
    public static void show(String content, int durationType) {
        snackbar.setText(content).setDuration(durationType).show();
    }

    /**
     * 短时显示
     */
    public static void showShort(String content) {
        show(content, Snackbar.LENGTH_SHORT);
    }

    /**
     * 长时显示
     */
    public static void showLong(String content) {
        show(content, Snackbar.LENGTH_LONG);
    }

    /**
     * 不消失显示
     */
    public static void showIndefinite(String content) {
        show(content, Snackbar.LENGTH_INDEFINITE);
    }

    /**
     * 隐藏
     */
    public static void dismiss() {
        if (snackbar.isShown()) {
            snackbar.dismiss();
        }
    }
}
